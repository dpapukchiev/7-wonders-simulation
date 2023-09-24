package dpapukchiev.sevenwonderssimulation.ui;

import com.fasterxml.jackson.databind.json.JsonMapper;
import dpapukchiev.sevenwonderssimulation.game.GamePhase;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

@Log4j2
public class RetoolUI {
    record PhaseFilter(String displayName, String expression) {

    }

    @SneakyThrows
    public static void main(String[] args) {
        var mapper = JsonMapper.builder().build();
        var filters = new ArrayList<PhaseFilter>();
        filters.add(new PhaseFilter("Player discards card", "discards card"));
        filters.add(new PhaseFilter("Player pays for card", "pays for"));
        filters.add(new PhaseFilter("Player builds wonder", "builds wonder stage"));
        filters.add(new PhaseFilter("Player used special action", "used special action"));

        filters.addAll(Arrays.stream(GamePhase.values())
                .map(phase -> new PhaseFilter(phase.getDisplayName(), phase.name()))
                .toList());

        var phaseFilters = new File("phase-filters.json");
        if (phaseFilters.exists()) {
            phaseFilters.delete();
        } else {
            phaseFilters.createNewFile();
        }
        try (var writer = new FileWriter(phaseFilters)) {
            mapper.writeValue(writer, filters);
        }
    }
}
