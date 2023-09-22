package dpapukchiev.sevenwonderssimulation.game;

import dpapukchiev.sevenwonderssimulation.city.CityName;
import dpapukchiev.sevenwonderssimulation.player.Player;
import dpapukchiev.sevenwonderssimulation.player.ScoreCard;
import dpapukchiev.sevenwonderssimulation.reporting.CityStatistics;
import jsl.simulation.Simulation;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static dpapukchiev.sevenwonderssimulation.reporting.CityStatistics.SortBy.CITY;
import static dpapukchiev.sevenwonderssimulation.reporting.CityStatistics.SortBy.METRIC_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SevenWondersGameTest {

    private final static int                    ATTEMPTS       = 100;
    private final static CityStatistics.SortBy  SORT_BY        = METRIC_NAME;
    private final static Map<CityName, Integer> winners        = new HashMap<>();
    private final static CityStatistics         cityStatistics = new CityStatistics(SORT_BY);

    private List<Arguments> getGameTestArguments() {
        return IntStream.rangeClosed(1, ATTEMPTS).mapToObj(Arguments::of).toList();
    }

    @ParameterizedTest
    @MethodSource("getGameTestArguments")
    void play(int streamNumber) {
        var result = runGameSimulation(streamNumber);

        var players = result.game().getPlayersFactory().getPlayers();
        assertEquals(result.gameOptions().numberOfPlayers(), players.size());

        assertAllPlayersHave1LeftoverAtEndOfAge(result.game(), result.gameOptions());

        assertPlayersPlayedDistinctCards(players);

        addWinnerToWinnersList(result.game());
        collectStatistics(result.game());

        reportStatistics(streamNumber, SORT_BY);
    }

    @NotNull
    private static GameResult runGameSimulation(int streamNumber) {
        var simulation = new Simulation();
        simulation.setNumberOfReplications(1);
        simulation.setAdvanceStreamNumber(streamNumber);

        var gameOptions = GameOptions.builder()
                .numberOfPlayers(7)
                .agesToSchedule(3)
                .cityStatistics(cityStatistics)
                .build();

        var game = new SevenWondersGame(
                simulation.getModel(),
                gameOptions
        );

        simulation.run();
        return new GameResult(gameOptions, game);
    }

    private record GameResult(GameOptions gameOptions, SevenWondersGame game) {
    }

    private static void assertAllPlayersHave1LeftoverAtEndOfAge(SevenWondersGame game, GameOptions gameOptions) {
        // All players have 1 left over card from each age
        game.getHandsOfCardsPerAge().entrySet()
                .stream()
                .filter(e -> e.getKey() <= gameOptions.agesToSchedule())
                .forEach(e -> {
                    var handsOfCards = e.getValue();
                    assertEquals(gameOptions.numberOfPlayers(), handsOfCards.size());
                    handsOfCards.forEach(handOfCards -> assertTrue(handOfCards.getCards().size() == 1,
                            "hand %s has %s left over cards"
                                    .formatted(handOfCards.getUuid(), handOfCards.getCards().size())));
                });
    }

    private static void assertPlayersPlayedDistinctCards(List<Player> players) {
        // Players have distinct built cards
        players.forEach(player -> {
            var builtCards = player.getVault().getBuiltCards().size();
            var builtCardNames = player.getVault().getBuiltCardNames().stream().distinct().count();

            assertTrue(builtCards >= 9, "Player %s has %s built cards".formatted(player.getName(), builtCards));
            assertEquals(
                    builtCards,
                    builtCardNames,
                    "Player %s has %s built cards and %s distinct built cards"
                            .formatted(player.getName(), builtCards, builtCardNames)
            );
        });
    }

    private static void addWinnerToWinnersList(SevenWondersGame game) {
        Pair<Player, ScoreCard> winner = game.getResultingScoringOrder().get(0);
        var winningPlayer = winner.getLeft();
        var cityName = winningPlayer.getWonderContext().getCityName();
        winners.putIfAbsent(cityName, 0);
        winners.computeIfPresent(cityName, (k, v) -> v + 1);
    }

    private static void collectStatistics(SevenWondersGame game) {
        game.getResultingScoringOrder().forEach(playerScoreCardPair -> {
            var player = playerScoreCardPair.getLeft();
            var scoreCard = playerScoreCardPair.getRight();
            var cityName = player.getWonderContext().getCityName();

            // TODO: add tracking of all scores
            cityStatistics.collectMetric(cityName, "score-total", scoreCard.getTotalScore(), player);
            cityStatistics.collectMetric(cityName, "coins-score", scoreCard.getCoinsScore(), player);
            cityStatistics.collectMetric(cityName, "science-score", scoreCard.getScienceScore(), player);
            cityStatistics.collectMetric(cityName, "built-cards", player.getVault().getBuiltCards().size(), player);
            cityStatistics.collectMetric(cityName, "discarded-cards", player.getVault().getDiscardedCards().size(), player);
        });
    }

    private static void reportStatistics(int streamNumber, CityStatistics.SortBy sortBy) {
        cityStatistics.reportStatistics(sortBy);
        if (streamNumber == ATTEMPTS) {
            assertEquals(ATTEMPTS, winners.values().stream().mapToInt(Integer::intValue).sum());

            System.out.println(winners);
        }
    }
}