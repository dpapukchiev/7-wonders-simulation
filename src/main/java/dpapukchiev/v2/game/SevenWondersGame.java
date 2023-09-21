package dpapukchiev.v2.game;

import dpapukchiev.v2.cards.Deck;
import dpapukchiev.v2.cards.HandOfCards;
import dpapukchiev.v2.effects.core.EffectReward;
import dpapukchiev.v2.player.Player;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.simulation.EventAction;
import jsl.simulation.EventActionIfc;
import jsl.simulation.JSLEvent;
import jsl.simulation.ModelElement;
import jsl.simulation.SchedulingElement;
import jsl.utilities.random.rvariable.NormalRV;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

@Log4j2
public class SevenWondersGame extends SchedulingElement {
    private Deck                            deck;
    private GameOptions                     gameOptions;
    private PlayersFactory                  playersFactory;
    private Map<Integer, List<HandOfCards>> handsOfCardsPerAge = new HashMap<>();

    public SevenWondersGame(
            ModelElement parent,
            GameOptions gameOptions
    ) {
        super(parent);
        this.playersFactory = new PlayersFactory(new RandomVariable(parent, new NormalRV()));

        this.gameOptions = gameOptions.toBuilder()
                .playerRandomVariables(
                        IntStream.rangeClosed(1, gameOptions.numberOfPlayers())
                                .mapToObj(i -> new RandomVariable(parent, new NormalRV()))
                                .toList()
                )
                .build();
        this.deck = new Deck(getParentModelElement());
    }

    @Override
    public void initialize() {
        playersFactory.initialisePlayers(gameOptions);
        dealHands(gameOptions);

        var currentOffset = 1;
        for (int i = 1; i <= gameOptions.agesToSchedule(); i++) {
            currentOffset = scheduleTurns(currentOffset, i);
            currentOffset = scheduleAgeTransition(currentOffset + 1, i);
        }
    }

    class ExecutePlayerTurn implements EventActionIfc<TurnContext> {

        @Override
        public void action(JSLEvent<TurnContext> event) {
            var turnContext = event.getMessage();
            var player = turnContext.getPlayer();
            log.info("\n{}=>PlayerTurnStarted p({}) turn {}-{} {} \n{}",
                    getTime(),
                    player.getName(),
                    turnContext.getAge(),
                    turnContext.getTurnCountAge(),
                    turnContext.getHandOfCards().report(),
                    player.report()
            );
            player.executeTurn(turnContext);

            log.info("\n{}=>PlayerTurnEnded {}",getTime(), player.report());
        }
    }

    class ExecuteEndOfTurn extends EventAction {
        @Override
        public void action(JSLEvent<Object> event) {
            log.info("\n{}=>ExecuteEndOfTurn", getTime());
            playersFactory.getPlayers().forEach(player -> {
                log.info("\n{}=>ApplyingEndOfTurnEffects for player {}", getTime(), player.getName());
                var efxReportBeforeStart = player.getEffectExecutionContext().report();
                Optional<EffectReward> effectReward = player.getEffectExecutionContext()
                        .executeEffectsEndOfTurn(player);
                effectReward.ifPresent(player::applyEffectReward);

                log.info(
                        "{}=>AppliedEndOfTurnEffects \nreward: {} \nefx before: {}  \nnew state: {}",
                        getTime(),
                        effectReward.map(EffectReward::report).
                                orElse("no rewards"),
                        efxReportBeforeStart,
                        player.report()
                );
            });
        }
    }

    class ExecuteAgeTransitionTurn implements EventActionIfc<Integer> {

        @Override
        public void action(JSLEvent<Integer> event) {
            var age = event.getMessage();
            log.info("{}=>ExecuteAgeTransitionTurn {}", getTime(), age);
            playersFactory.getPlayers()
                    .forEach(player -> player
                            .getEffectExecutionContext()
                            .executeEffectsEndOfAge(player)
                    );
            playersFactory.getPlayers().forEach(player -> player.executeWar(age));
            playersFactory.getPlayers().forEach(player -> log.info(player.report()));
        }
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

    private int scheduleAgeTransition(int currentOffset, int age) {
        scheduleEvent(new ExecuteAgeTransitionTurn(), currentOffset, 0, age);
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
            scheduleEvent(new ExecuteEndOfTurn(), lastOffset);
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
