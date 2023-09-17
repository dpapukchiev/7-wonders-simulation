package dpapukchiev.game;

import dpapukchiev.cards.Deck;
import dpapukchiev.cards.HandOfCards;
import dpapukchiev.city.CityName;
import dpapukchiev.player.Player;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.simulation.EventActionIfc;
import jsl.simulation.JSLEvent;
import jsl.simulation.ModelElement;
import jsl.simulation.SchedulingElement;
import jsl.utilities.random.rvariable.NormalRV;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static jsl.utilities.random.rvariable.JSLRandom.randomlySelect;

@Log4j2
public class SevenWondersGame extends SchedulingElement {
    private Deck deck;
    private List<Player> players = new ArrayList<>();
    private RandomVariable cityDistribution;
    private GameOptions gameOptions;

    private Map<Integer, List<HandOfCards>> handsOfCardsPerAge = new HashMap<>();

    public SevenWondersGame(
            ModelElement parent,
            GameOptions gameOptions
    ) {
        super(parent);
        this.gameOptions = gameOptions;
        this.cityDistribution = new RandomVariable(parent, new NormalRV());
        this.deck = new Deck(getParentModelElement());
    }

    @Override
    public void initialize() {
        initialisePlayers(gameOptions);
        dealHands(gameOptions);

        var currentOffset = 1;
        for (int i = 1; i <= 3; i++) {
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
            log.info("{} ExecuteAgeTransitionTurn {}", getTime(), event.getMessage());
        }
    }


    public void initialisePlayers(GameOptions options) {
        players.clear();

        var cities = selectRandomCities(options);
        for (int i = 0; i < options.numberOfPlayers(); i++) {
            players.add(Player.builder()
                    .name("Player-" + i)
                    .city(cities.get(i))
                    .build());
        }

        for (int i = 0; i < players.size(); i++) {
            var player = players.get(i);
            var previousPlayer = i == 0 ? players.size() - 1 : i - 1;
            var nextPlayer = i == (players.size() - 1) ? 0 : i + 1;

            player.setLeftPlayer(players.get(previousPlayer));
            player.setRightPlayer(players.get(nextPlayer));
            log.info("{} left: {}, right: {}",
                    player.getName(),
                    player.getLeftPlayer().getName(),
                    player.getRightPlayer().getName()
            );
        }
        log.info("Initialised Players {}", players);
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

    private List<CityName> selectRandomCities(GameOptions options) {
        var usedCities = new ArrayList<CityName>();

        for (int i = 0; i < options.numberOfPlayers(); i++) {

            var cityName = randomlySelect(Stream.of(CityName.values())
                            .filter(city -> !usedCities.contains(city))
                            .toList(),
                    cityDistribution.getStreamNumber()
            );
            usedCities.add(cityName);
        }

        return usedCities;
    }

    private int scheduleAgeTransition(int currentOffset, int age) {
        scheduleEvent(new ExecuteAgeTransitionTurn(), currentOffset, 0, age);
        currentOffset++;
        return currentOffset;
    }

    private int scheduleTurns(int startingOffset, int age) {
        int lastOffset = startingOffset;
        var currentIndexPerPlayer = new HashMap<Player, Integer>();
        for (int j = 0; j < 7; j++) {
            for (int i = 0; i < players.size(); i++) {
                var player = players.get(i);
                var handToAssign = getHandOfCards(age, currentIndexPerPlayer, i, player);


                int newOffset = startingOffset + j;
                lastOffset = newOffset;
                scheduleEvent(new ExecutePlayerTurn(), newOffset, i, TurnContext.builder()
                        .turnCountAge(j + 1)
                        .simulationStep(newOffset)
                        .age(age)
                        .player(player)
                        .handOfCards(handToAssign)
                        .build()
                );
            }
        }
        return lastOffset;
    }

    private HandOfCards getHandOfCards(int age, HashMap<Player, Integer> currentIndexPerPlayer, int i, Player player) {
        var handsPerAge = handsOfCardsPerAge.get(age);

        int currentHandIndex = Optional.ofNullable(currentIndexPerPlayer.get(player))
                .orElse(i);
        var handToAssign = handsPerAge.get(currentHandIndex);


        // CARD ROTATION PER TURN
        if (age == 2) {
            if (currentHandIndex - 1 < 0) {
                currentIndexPerPlayer.put(player, players.size() - 1);
            } else {
                currentIndexPerPlayer.put(player, currentHandIndex - 1);
            }
        } else {
            if (currentHandIndex + 1 > (players.size() - 1)) {
                currentIndexPerPlayer.put(player, 0);
            } else {
                currentIndexPerPlayer.put(player, currentHandIndex + 1);
            }
        }
        return handToAssign;
    }
}
