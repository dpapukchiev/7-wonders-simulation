package dpapukchiev.sevenwonderssimulation.game;

import dpapukchiev.sevenwonderssimulation.cards.Card;
import dpapukchiev.sevenwonderssimulation.cards.HandOfCards;
import dpapukchiev.sevenwonderssimulation.player.Player;
import dpapukchiev.sevenwonderssimulation.reporting.CityStatistics;
import dpapukchiev.sevenwonderssimulation.wonder.CityName;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.simulation.Simulation;
import jsl.utilities.random.rvariable.NormalRV;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static dpapukchiev.sevenwonderssimulation.cards.CardName.APOTEKE;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.BADER;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.HOLZPLATZ;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.PRESSE;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.THEATER;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.TONGRUBE;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.WACHTURM;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.ZIEGELEI;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SevenWondersGameScenarioTest {

    private CityStatistics cityStatistics = new CityStatistics();

    @ValueSource(ints = {1, 2, 3, 4, 5})
    @ParameterizedTest
    void playWithStrategy(int strategy) {
        var initializationResult = initGame(strategy, CityName.HALIKARNASSOS);

        var player = initializationResult.player();
        var playerVault = player.getVault();

        var deck = initializationResult.game().getDeck();
        var cardsInHand = new ArrayList<>(Arrays.asList(
                deck.getCardByName(APOTEKE),
                deck.getCardByName(PRESSE),
                deck.getCardByName(HOLZPLATZ),
                deck.getCardByName(THEATER),
                deck.getCardByName(TONGRUBE),
                deck.getCardByName(WACHTURM),
                deck.getCardByName(BADER)
        ));
        player.playExtraCard(deck.getCardByName(ZIEGELEI));

        var turnContext = getTurnContextWithHandOfCards(player, initializationResult, cardsInHand);

        player.executeTurn(turnContext);

        var builtCards = playerVault.getBuiltCards();
        var lastBuiltCard = builtCards.get(builtCards.size() - 1);

        assertEquals(PRESSE, lastBuiltCard.getName());
    }

    @NotNull
    private SevenWondersGameScenarioTest.InitializationResult initGame(int strategy, CityName cityToPlay) {
        var simulation = new Simulation();

        int numberOfPlayers = 3;
        var gameOptions = GameOptions.builder()
                .numberOfPlayers(numberOfPlayers)
                .agesToSchedule(numberOfPlayers)
                .runId(UUID.randomUUID())
                .logEveryNGames(1)
                .startTime(Clock.systemDefaultZone().instant())
                .cityStatistics(cityStatistics)
                .simulation(simulation.getModel())
                .citiesToPlay(List.of(cityToPlay))
                .playerRandomVariables(
                        IntStream.rangeClosed(1, numberOfPlayers)
                                .mapToObj(i -> new RandomVariable(simulation.getModel(), new NormalRV()))
                                .toList()
                )
                .build();
        cityStatistics.refreshEventTrackingService(gameOptions, 1);

        var game = new SevenWondersGame(
                simulation.getModel(),
                gameOptions
        );
        game.getPlayersFactory().initialisePlayers(gameOptions);

        var players = game.getPlayersFactory().getPlayers();
        assertEquals(gameOptions.numberOfPlayers(), players.size());

        var cityToPlayerMap = players.stream()
                .collect(Collectors.groupingBy(p -> p.getWonderContext().getCityName()));

        var player = cityToPlayerMap.get(cityToPlay).get(0);
        player.setStrategy(game.getPlayersFactory().getStrategyForPlayer(strategy));

        game.getDeck().resetDeck(gameOptions);

        return new InitializationResult(game, player);
    }

    private static TurnContext getTurnContextWithHandOfCards(Player player, InitializationResult initializationResult, ArrayList<Card> cardsInHand) {
        return TurnContext.builder()
                .age(1)
                .turnCountAge(8 - cardsInHand.size())
                .player(player)
                .simulationStep(1)
                .handOfCards(HandOfCards.builder()
                        .deck(initializationResult.game().getDeck())
                        .cards(cardsInHand)
                        .build())
                .build();
    }

    private record InitializationResult(SevenWondersGame game, Player player) {
    }
}