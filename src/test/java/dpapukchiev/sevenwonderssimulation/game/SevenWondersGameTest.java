package dpapukchiev.sevenwonderssimulation.game;

import dpapukchiev.sevenwonderssimulation.player.Player;
import dpapukchiev.sevenwonderssimulation.reporting.CityStatistics;
import dpapukchiev.sevenwonderssimulation.wonder.CityName;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.simulation.Simulation;
import jsl.utilities.random.rvariable.NormalRV;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.Clock;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static dpapukchiev.sevenwonderssimulation.wonder.CityName.ALEXANDRIA;
import static dpapukchiev.sevenwonderssimulation.wonder.CityName.EPHESOS;
import static dpapukchiev.sevenwonderssimulation.wonder.CityName.RHODOS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SevenWondersGameTest {

    private final static List<CityName> CITIES_TO_PLAY    = List.of(EPHESOS, ALEXANDRIA, RHODOS);
    private final static int            NUMBER_OF_PLAYERS = 7;
    private final static int            GAMES_TO_PLAY     = 1000;
    //    private final static int                   LOG_EVERY_N_GAMES = Math.round((float) GAMES_TO_PLAY / 10);
    private final static int            LOG_EVERY_N_GAMES = 1;
    private final static CityStatistics cityStatistics    = new CityStatistics();

    @Test
    void play() {
        var result = runGameSimulation();

        var players = result.game().getPlayersFactory().getPlayers();
        assertEquals(result.gameOptions().numberOfPlayers(), players.size());

        assertPlayersPlayedDistinctCards(players);

        cityStatistics.reportWinners(GAMES_TO_PLAY);
    }

    @NotNull
    private static GameResult runGameSimulation() {
        var simulation = new Simulation();
        simulation.setNumberOfReplications(GAMES_TO_PLAY);

        var gameOptions = GameOptions.builder()
                .numberOfPlayers(NUMBER_OF_PLAYERS)
                .agesToSchedule(3)
                .runId(UUID.randomUUID())
                .logEveryNGames(LOG_EVERY_N_GAMES)
                .startTime(Clock.systemDefaultZone().instant())
                .cityStatistics(cityStatistics)
                .simulation(simulation.getModel())
                .citiesToPlay(CITIES_TO_PLAY)
                .playerRandomVariables(
                        IntStream.rangeClosed(1, NUMBER_OF_PLAYERS)
                                .mapToObj(i -> new RandomVariable(simulation.getModel(), new NormalRV()))
                                .toList()
                )
                .build();

        var game = new SevenWondersGame(
                simulation.getModel(),
                gameOptions
        );

        simulation.run();
        return new GameResult(gameOptions, game);
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