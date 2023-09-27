package dpapukchiev.sevenwonderssimulation.reporting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dpapukchiev.sevenwonderssimulation.game.GamePhase;
import jsl.simulation.ModelElement;
import jsl.utilities.statistic.Statistic;
import lombok.Builder;
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
    public void logStatistic(Statistic statistic) {
        if (replicationNumber % logEveryNGames != 0) {
            return;
        }

        currentLogNumber++;
        try (var fileWriter = new FileWriter(logFile, true)) {
            fileWriter.append("%s\n".formatted(objectMapper.writeValueAsString(
                    StatisticLog.builder()
                            .phase(phase.name())
                            .statisticName(statistic.getName())
                            .count(statistic.getCount())
                            .average(statistic.getAverage())
                            .standardDeviation(statistic.getStandardDeviation())
                            .min(statistic.getMin())
                            .max(statistic.getMax())
                            .logNumber(currentLogNumber)
                            .replicationNumber(simulation.getCurrentReplicationNumber())
                            .time(simulation.getTime())
                            .build()
            )));
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
                    Event.builder()
                            .phase(phase.name())
                            .event(event)
                            .logNumber(currentLogNumber)
                            .replicationNumber(simulation.getCurrentReplicationNumber())
                            .time(simulation.getTime())
                            .build()
            )));
        }
    }

    public void transitionPhase(GamePhase phase) {
        this.phase = phase;
    }

    @Builder
    @JsonSerialize
    record Event(String phase, String event, double logNumber, double replicationNumber, double time) {
    }

    @Builder
    @JsonSerialize
    record StatisticLog(
            String phase,
            String statisticName,
            double count,
            double average,
            double standardDeviation,
            double min,
            double max,
            double logNumber,
            double replicationNumber,
            double time
    ) {
    }
}
