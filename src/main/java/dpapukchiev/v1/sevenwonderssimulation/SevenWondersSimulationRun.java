package dpapukchiev.v1.sevenwonderssimulation;

import dpapukchiev.v1.game.GameOptions;
import dpapukchiev.v1.game.SevenWondersGame;
import jakarta.annotation.PostConstruct;
import jsl.simulation.Simulation;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class SevenWondersSimulationRun {

    @PostConstruct
    public void run() {
        var simulation = new Simulation();
        simulation.setNumberOfReplications(1);

        var gameOptions = GameOptions.builder()
                .numberOfPlayers(5)
                .agesToSchedule(2)
                .build();

        var game = new SevenWondersGame(
                simulation.getModel(),
                gameOptions
        );

        simulation.run();

        log.info(game);
    }
}
