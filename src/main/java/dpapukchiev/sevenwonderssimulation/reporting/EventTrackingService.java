package dpapukchiev.sevenwonderssimulation.reporting;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;

@Log4j2
public class EventTrackingService {
    private final String runId;
    private final double replicationNumber;
    private File logFile;

    @SneakyThrows
    public EventTrackingService(String runId, double replicationNumber) {
        this.runId = runId;
        this.replicationNumber = replicationNumber;
        var getRunDirectory = new File("run-%s".formatted(runId));
        if (getRunDirectory.exists()) {
            getRunDirectory.delete();
        }
        getRunDirectory.mkdir();

        var traceId = "R" + replicationNumber;
        // check if log file exists
        // if not, create it
        // if yes, clear it
        logFile = new File(Path.of("%s/%s.log".formatted(getRunDirectory, traceId)).toString());
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
        log.info("R{} {}: {}", replicationNumber, runId, event);
        try(var fileWriter = new FileWriter(logFile, true)) {
            fileWriter.append("%s\n".formatted(event));
        }
    }

    @SneakyThrows
    public void logEvent(String event, String... parameters) {
        if (event != null && parameters != null) {
            // Replace each '{}' in the event string with corresponding parameters
            for (String parameter : parameters) {
                event = event.replaceFirst("\\{\\}", parameter);
            }
            // Print the final log message
            try(var fileWriter = new FileWriter(logFile, true)) {
                fileWriter.append("%s\n".formatted(event));
            }
        }
    }
}
