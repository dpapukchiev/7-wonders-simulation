package dpapukchiev.sevenwonderssimulation.game;

import dpapukchiev.sevenwonderssimulation.cards.Deck;
import dpapukchiev.sevenwonderssimulation.cards.HandOfCards;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectReward;
import dpapukchiev.sevenwonderssimulation.player.Player;
import dpapukchiev.sevenwonderssimulation.player.ScoreCard;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.simulation.EventAction;
import jsl.simulation.EventActionIfc;
import jsl.simulation.JSLEvent;
import jsl.simulation.ModelElement;
import jsl.simulation.SchedulingElement;
import jsl.utilities.random.rvariable.NormalRV;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static dpapukchiev.sevenwonderssimulation.game.GamePhase.END_OF_GAME;
import static dpapukchiev.sevenwonderssimulation.game.GamePhase.SCORING;
import static dpapukchiev.sevenwonderssimulation.game.GamePhase.WINNERS;

@Log4j2
@Getter
public class SevenWondersGame extends SchedulingElement {
    private final List<Pair<Player, ScoreCard>>   resultingScoringOrder = new ArrayList<>();
    private final Deck                            deck;
    private final GameOptions                     gameOptions;
    private final PlayersFactory                  playersFactory;
    private final Map<Integer, List<HandOfCards>> handsOfCardsPerAge    = new HashMap<>();

    public SevenWondersGame(
            ModelElement parent,
            GameOptions gameOptions
    ) {
        super(parent);

        this.gameOptions = gameOptions.toBuilder()
                .playerRandomVariables(
                        IntStream.rangeClosed(1, gameOptions.numberOfPlayers())
                                .mapToObj(i -> new RandomVariable(parent, new NormalRV()))
                                .toList()
                )
                .build();
        this.deck = new Deck(getParentModelElement());
        this.playersFactory = new PlayersFactory(
                this.deck,
                new RandomVariable(parent, new NormalRV()),
                new RandomVariable(parent, new NormalRV()),
                gameOptions.cityStatistics()
        );
    }

    @Override
    public void initialize() {
        gameOptions.cityStatistics().refreshEventTrackingService(getGameOptions(), getCurrentReplicationNumber());
        transitionIntoTrackingPhase(GamePhase.INITIALIZE_PLAYERS);

        log.info("\n{}=>SevenWondersGame initialize game {}", getTime(), getCurrentReplicationNumber());
        playersFactory.initialisePlayers(gameOptions);

        dealHands(gameOptions);

        var nextOffset = 1;
        for (int i = 1; i <= gameOptions.agesToSchedule(); i++) {
            nextOffset = scheduleTurns(nextOffset, i);
            nextOffset = scheduleEndOfAge(nextOffset + 1, i);
        }
        scheduleEvent(new ExecuteEndOfGame(), nextOffset);
    }

    private void transitionIntoTrackingPhase(GamePhase phase) {
        getGameOptions().cityStatistics().getEventTrackingService().transitionPhase(phase);
    }

    class ExecutePlayerTurn implements EventActionIfc<TurnContext> {

        @Override
        public void action(JSLEvent<TurnContext> event) {
            transitionIntoTrackingPhase(GamePhase.playerTurn(
                    event.getMessage().getAge(),
                    event.getMessage().getTurnCountAge()
            ));

            var turnContext = event.getMessage();
            var player = turnContext.getPlayer();
            player.log("\n%s=>PlayerTurnStarted p(%s) turn %s-%s \n%s \n%s".formatted(
                    String.valueOf(getTime()),
                    player.getName(),
                    String.valueOf(turnContext.getAge()),
                    String.valueOf(turnContext.getTurnCountAge()),
                    player.report(),
                    turnContext.getHandOfCards().report()
            ));
            // TODO: add some metrics interface which can get the player and then collect metrics
            // this will be a snapshot in the simulation time which can be replayed
            player.executeTurn(turnContext);

            player.log("\n%s=>PlayerTurnEnded %s".formatted(String.valueOf(getTime()), player.report()));
        }
    }

    class ExecuteEndOfTurn implements EventActionIfc<Turn> {
        @Override
        public void action(JSLEvent<Turn> event) {
            transitionIntoTrackingPhase(GamePhase.endOfTurn(
                    event.getMessage().age(),
                    event.getMessage().turn()
            ));

            log.info("\n{}=>ExecuteEndOfTurn", getTime());
            playersFactory.getPlayers().forEach(player -> {
                log.info("\n{}=>ApplyingEndOfTurnEffects {}", getTime(), player.report());

                var efxReportBeforeStart = player.getEffectExecutionContext().report();
                Optional<EffectReward> effectReward = player.getEffectExecutionContext()
                        .executeEffectsEndOfTurn(player);
                effectReward.ifPresent(player::applyEffectReward);

                log.info(
                        "\n{}=>AppliedEndOfTurnEffects \nreward: {} \nefx before: {}  \nnew state: {}",
                        getTime(),
                        effectReward.map(EffectReward::report).
                                orElse("no rewards"),
                        efxReportBeforeStart,
                        player.report()
                );
            });
        }
    }

    class ExecuteEndOfGame extends EventAction {
        @Override
        public void action(JSLEvent<Object> event) {
            transitionIntoTrackingPhase(END_OF_GAME);

            log.info("\n{}=>ExecuteEndOfGame", getTime());
            var players = applyEndOfGameEffects();

            transitionIntoTrackingPhase(SCORING);
            var scoreReports = players.stream()
                    .sorted((p1, p2) -> Double.compare(p2.score().getTotalScore(), p1.score().getTotalScore()))
                    .peek(p -> resultingScoringOrder.add(Pair.of(p, p.score())))
                    .peek(Player::collectFinalMetrics)
                    .toList();
            var winner = scoreReports.get(0);
            var scoreReportsStrings = scoreReports.stream()
                    .map(p -> "\n%s %s %s\n".formatted(
                            p.getName(),
                            p.getWonderContext().report(),
                            p.score().report()
                    ))
                    .collect(Collectors.joining());

            transitionIntoTrackingPhase(WINNERS);
            logEvent("\n%s=>ScoreReport \n%s".formatted(getTime(), scoreReportsStrings));
            logEvent("\n%s=>Winner is %s %s with score %s".formatted(
                    getTime(),
                    winner.getName(),
                    winner.getWonderContext().report(),
                    winner.score().getTotalScore()
            ));

            addWinnerToWinnersList(winner);
        }
    }

    private void logEvent(String scoreReportsStrings) {
        getGameOptions().cityStatistics().getEventTrackingService().logEvent(scoreReportsStrings);
    }

    class ExecuteEndOfAge implements EventActionIfc<Integer> {

        @Override
        public void action(JSLEvent<Integer> event) {
            var age = event.getMessage();
            transitionIntoTrackingPhase(GamePhase.endOfAge(age));

            log.info("{}=>ExecuteEndOfAge {}", getTime(), age);
            playersFactory.getPlayers()
                    .forEach(player -> player
                            .getEffectExecutionContext()
                            .executeEffectsEndOfAge(player)
                    );
            playersFactory.getPlayers().forEach(player -> player.executeWar(age));
            playersFactory.getPlayers()
                    .forEach(player -> player.log("EndOfAgePlayerState %s".formatted(player.report())));
        }
    }

    private void addWinnerToWinnersList(Player winner) {
        gameOptions.cityStatistics().addWinner(winner.getWonderContext());
    }

    private List<Player> applyEndOfGameEffects() {
        var players = playersFactory.getPlayers();
        players.forEach(player -> {
            log.info("\n{}=>ApplyingEndOfGameEffects for player {}", getTime(), player.getName());

            var efxReportBeforeStart = player.getEffectExecutionContext().report();
            Optional<EffectReward> effectReward = player.getEffectExecutionContext()
                    .executeEffectsEndOfGame(player);
            effectReward.ifPresent(er -> {
                player.log("\nEndOfGame: Player %s gets reward %s".formatted(player.getName(), er.report()));
                player.applyEffectReward(er);
            });

            player.copyGuildCardEffectIfHasSpecialAction(players);

            player.log(
                    "%s=>FinishedEndOfGameEffects \nreward: %s \nefx before: %s  \nnew state: %s".formatted(
                            String.valueOf(getTime()),
                            effectReward.map(EffectReward::report).
                                    orElse("no rewards"),
                            efxReportBeforeStart,
                            player.report()
                    ));
        });
        return players;
    }

    public void dealHands(GameOptions options) {
        deck.resetDeck(options.numberOfPlayers());
        IntStream.rangeClosed(1, options.agesToSchedule())
                .forEach(age -> handsOfCardsPerAge.put(age, new ArrayList<>()));

        handsOfCardsPerAge.forEach((age, hands) -> {
            for (int i = 0; i < options.numberOfPlayers(); i++) {
                var handOfCards = deck.prepareHandOfCards(age);
                hands.add(handOfCards);
            }
        });
    }

    private int scheduleEndOfAge(int currentOffset, int age) {
        scheduleEvent(new ExecuteEndOfAge(), currentOffset, 0, age);
        currentOffset++;
        return currentOffset;
    }

    private int scheduleTurns(int ageStartingOffset, int age) {
        int lastOffset = ageStartingOffset;
        var currentHandIndexPerPlayer = new HashMap<Player, Integer>();
        var players = playersFactory.getPlayers();

        // Every age starts with 7 cards and ends with 1 discarded
        int turnsPerAge = 6;
        for (int turnInAge = 0; turnInAge < turnsPerAge; turnInAge++) {
            for (int playerIndex = 0; playerIndex < players.size(); playerIndex++) {
                var player = players.get(playerIndex);
                var handToAssign = getHandOfCards(age, currentHandIndexPerPlayer, playerIndex, player);

                var turnContext = TurnContext.builder()
                        .turnCountAge(turnInAge + 1)
                        .simulationStep(lastOffset)
                        .age(age)
                        .player(player)
                        .handOfCards(handToAssign)
                        .build();
                scheduleEvent(new ExecutePlayerTurn(), lastOffset, playerIndex, turnContext);
                lastOffset++;
            }
            scheduleEvent(new ExecuteEndOfTurn(), lastOffset, 0, new Turn(age, turnInAge + 1));
            lastOffset++;
        }
        return lastOffset;
    }

    private HandOfCards getHandOfCards(int age, HashMap<Player, Integer> currentIndexPerPlayer, int playerIndex, Player player) {
        var playerCount = playersFactory.getPlayers().size();
        var handsPerAge = handsOfCardsPerAge.get(age);

        int currentHandIndex = Optional.ofNullable(currentIndexPerPlayer.get(player))
                .orElse(playerIndex);
        var handToAssign = handsPerAge.get(currentHandIndex);

        // CARD ROTATION PER AGE
        if (age == 2) {
            if (currentHandIndex - 1 < 0) {
                currentIndexPerPlayer.put(player, playerCount - 1);
            } else {
                currentIndexPerPlayer.put(player, currentHandIndex - 1);
            }
        } else {
            if (currentHandIndex + 1 > (playerCount - 1)) {
                currentIndexPerPlayer.put(player, 0);
            } else {
                currentIndexPerPlayer.put(player, currentHandIndex + 1);
            }
        }
        return handToAssign;
    }
}
