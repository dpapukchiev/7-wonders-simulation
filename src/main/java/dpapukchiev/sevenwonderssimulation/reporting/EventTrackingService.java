package dpapukchiev.sevenwonderssimulation.reporting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dpapukchiev.sevenwonderssimulation.game.GamePhase;
import jsl.simulation.ModelElement;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;

import static dpapukchiev.sevenwonderssimulation.game.GamePhase.INITIALIZATION;

@Log4j2
public class EventTrackingService {
    private final static ObjectMapper objectMapper = new ObjectMapper();
    private final        String       runId;
    private final        double       replicationNumber;
    private              int          logEveryNGames;
    private              double       currentLogNumber;
    private              File         logFile;
    private final        ModelElement simulation;
    private              GamePhase    phase        = INITIALIZATION;

    @SneakyThrows
    public EventTrackingService(String runId, double replicationNumber, int logEveryNGames, ModelElement simulation) {
        this.runId = runId;
        this.replicationNumber = replicationNumber;
        this.simulation = simulation;
        this.logEveryNGames = logEveryNGames;
        this.currentLogNumber = 0;
//        var getRunDirectory = new File("run-%s".formatted(runId));
        var getRunDirectory = new File("./run-log");
        if (getRunDirectory.exists()) {
            getRunDirectory.delete();
        }
        getRunDirectory.mkdir();

        var traceId = "R" + replicationNumber;
        if (replicationNumber % logEveryNGames != 0) {
            return;
        }
        logFile = new File(Path.of("%s/%s.json".formatted(getRunDirectory, traceId)).toString());
        if (logFile.exists()) {
            logFile.delete();
        }
        var newFileCreated = logFile.createNewFile();
        if (!newFileCreated) {
            throw new IllegalStateException("Could not create log file for run %s".formatted(traceId));
        }
    }

    @SneakyThrows
    public void logEvent(String event) {
        if (replicationNumber % logEveryNGames != 0) {
            return;
        }

        currentLogNumber++;
        try (var fileWriter = new FileWriter(logFile, true)) {
            fileWriter.append("%s\n".formatted(objectMapper.writeValueAsString(
                    new Event(phase.name(), event, currentLogNumber, simulation.getCurrentReplicationNumber(), simulation.getTime())
            )));
        }
    }

    public void transitionPhase(GamePhase phase) {
        this.phase = phase;
    }

    @JsonSerialize
    record Event(String phase, String event, double logNumber, double replicationNumber, double time) {
    }
}
