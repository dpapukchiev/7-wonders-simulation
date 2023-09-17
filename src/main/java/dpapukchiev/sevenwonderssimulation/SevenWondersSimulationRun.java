package dpapukchiev.sevenwonderssimulation;

import dpapukchiev.game.GameOptions;
import dpapukchiev.game.SevenWondersGame;
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
                .numberOfPlayers(3)
                .build();

        var game = new SevenWondersGame(
                simulation.getModel(),
                gameOptions
        );

        simulation.run();

        log.info(game);
    }
}
