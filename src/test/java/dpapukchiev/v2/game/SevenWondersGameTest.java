package dpapukchiev.v2.game;

import jsl.simulation.Simulation;
import org.junit.jupiter.api.Test;

class SevenWondersGameTest {

    @Test
    void play() {
        var simulation = new Simulation();
        var gameOptions = GameOptions.builder()
                .numberOfPlayers(7)
                .agesToSchedule(1)
                .build();

        new SevenWondersGame(
                simulation.getModel(),
                gameOptions
        );

        simulation.run();
    }
}