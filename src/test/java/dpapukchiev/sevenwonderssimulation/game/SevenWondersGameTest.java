package dpapukchiev.sevenwonderssimulation.game;

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

import java.util.List;
import java.util.stream.IntStream;

import static dpapukchiev.sevenwonderssimulation.reporting.CityStatistics.SortBy.METRIC_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SevenWondersGameTest {

    private final static int                   ATTEMPTS       = 200;
    private final static CityStatistics.SortBy SORT_BY        = METRIC_NAME;
    private final static CityStatistics        cityStatistics = new CityStatistics(SORT_BY);

    private List<Arguments> getGameTestArguments() {
        return IntStream.rangeClosed(1, ATTEMPTS).mapToObj(Arguments::of).toList();
    }

    @ParameterizedTest
    @MethodSource("getGameTestArguments")
    void play(int streamNumber) {
        var result = runGameSimulation(streamNumber);

        var players = result.game().getPlayersFactory().getPlayers();
        assertEquals(result.gameOptions().numberOfPlayers(), players.size());

        assertPlayersPlayedDistinctCards(players);

        addWinnerToWinnersList(result.game());

        reportStatistics(streamNumber);
    }

    @NotNull
    private static GameResult runGameSimulation(int streamNumber) {
        var simulation = new Simulation();
        simulation.setNumberOfReplications(1);
        simulation.setAdvanceStreamNumber(streamNumber);

        var gameOptions = GameOptions.builder()
                .numberOfPlayers(3)
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

    private static void assertPlayersPlayedDistinctCards(List<Player> players) {
        // Players have distinct built cards
        players.forEach(player -> {
            var builtCards = player.getVault().getBuiltCards().size();
            var builtCardNames = player.getVault().getBuiltCardNames().stream().distinct().count();

            assertTrue(builtCards >= 8, "Player %s has %s built cards".formatted(player.getName(), builtCards));
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
        cityStatistics.addWinner(winningPlayer.getWonderContext());
    }

    private static void reportStatistics(int streamNumber) {
        if (streamNumber == ATTEMPTS) {
            cityStatistics.reportStatistics(SORT_BY);
            cityStatistics.reportWinners(ATTEMPTS);
        }
    }
}