package dpapukchiev.game;

import dpapukchiev.cards.Deck;
import dpapukchiev.cards.HandOfCards;
import dpapukchiev.player.Player;
import jsl.modeling.elements.variable.RandomVariable;
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
    private Deck        deck;
    private GameOptions gameOptions;

    private PlayersFactory playersFactory;

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
        int agesToSchedule = 3;
        for (int i = 1; i <= agesToSchedule; i++) {
            currentOffset = scheduleTurns(currentOffset, i);
            currentOffset = scheduleAgeTransition(currentOffset + 1, i);
        }
    }

    class ExecutePlayerTurn implements EventActionIfc<TurnContext> {

        @Override
        public void action(JSLEvent<TurnContext> event) {
            var turnContext = event.getMessage();
            turnContext.getPlayer().executeTurn(turnContext);
        }
    }

    class ExecuteAgeTransitionTurn implements EventActionIfc<Integer> {

        @Override
        public void action(JSLEvent<Integer> event) {
            var age = event.getMessage();
            log.info("{} ExecuteAgeTransitionTurn {}", getTime(), age);
            playersFactory.getPlayers().forEach(player -> player.executeEndOfAge(age));
            playersFactory.getPlayers().forEach(player -> log.info(player.report(age)));
        }
    }

    public void dealHands(GameOptions options) {
        deck.resetDeck(options.numberOfPlayers());
        IntStream.rangeClosed(1, 3)
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
        for (int j = 0; j < 6; j++) {
            for (int i = 0; i < players.size(); i++) {
                var player = players.get(i);
                var handToAssign = getHandOfCards(age, currentHandIndexPerPlayer, i, player);

                int newOffset = ageStartingOffset + j;
                lastOffset = newOffset;
                var turnContext = TurnContext.builder()
                        .turnCountAge(j + 1)
                        .simulationStep(newOffset)
                        .age(age)
                        .player(player)
                        .handOfCards(handToAssign)
                        .build();
                scheduleEvent(new ExecutePlayerTurn(), newOffset, i, turnContext);
            }
        }
        return lastOffset;
    }

    private HandOfCards getHandOfCards(int age, HashMap<Player, Integer> currentIndexPerPlayer, int i, Player player) {
        var playerCount = playersFactory.getPlayers().size();
        var handsPerAge = handsOfCardsPerAge.get(age);

        int currentHandIndex = Optional.ofNullable(currentIndexPerPlayer.get(player))
                .orElse(i);
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
