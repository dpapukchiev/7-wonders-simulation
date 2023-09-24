package dpapukchiev.sevenwonderssimulation.ui;

import com.fasterxml.jackson.databind.json.JsonMapper;
import dpapukchiev.sevenwonderssimulation.game.GamePhase;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Arrays;

@Log4j2
public class RetoolUI {
    record PhaseFilter(String displayName, String expression){

    }
    @SneakyThrows
    public static void main(String[] args) {
        var mapper = JsonMapper.builder().build();
        var filters = Arrays.stream(GamePhase.values())
                .map(phase -> new PhaseFilter(phase.getDisplayName(), phase.name()))
                .toList();
        log.info(mapper.writeValueAsString(filters));
    }
}
