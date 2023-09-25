package dpapukchiev.sevenwonderssimulation.game;

import dpapukchiev.sevenwonderssimulation.player.Player;
import dpapukchiev.sevenwonderssimulation.reporting.CityStatistics;
import jsl.simulation.Simulation;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.Clock;
import java.util.List;
import java.util.UUID;

import static dpapukchiev.sevenwonderssimulation.reporting.CityStatistics.SortBy.METRIC_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SevenWondersGameTest {

    private final static int                   ATTEMPTS       = 10000;
    private final static CityStatistics.SortBy SORT_BY        = METRIC_NAME;
    private final static CityStatistics        cityStatistics = new CityStatistics(SORT_BY);

    @Test
    void play() {
        var result = runGameSimulation();

        var players = result.game().getPlayersFactory().getPlayers();
        assertEquals(result.gameOptions().numberOfPlayers(), players.size());

        assertPlayersPlayedDistinctCards(players);

        cityStatistics.reportStatistics(SORT_BY);
        cityStatistics.reportWinners(ATTEMPTS);
    }

    @NotNull
    private static GameResult runGameSimulation() {
        var simulation = new Simulation();
        simulation.setNumberOfReplications(ATTEMPTS);

        var gameOptions = GameOptions.builder()
                .numberOfPlayers(7)
                .agesToSchedule(3)
                .runId(UUID.randomUUID())
                .startTime(Clock.systemDefaultZone().instant())
                .cityStatistics(cityStatistics)
                .simulation(simulation.getModel())
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
}