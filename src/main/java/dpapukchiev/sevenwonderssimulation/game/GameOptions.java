package dpapukchiev.sevenwonderssimulation.game;

import dpapukchiev.sevenwonderssimulation.reporting.CityStatistics;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.simulation.ModelElement;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true)
public record GameOptions(
        int numberOfPlayers,
        int agesToSchedule,
        UUID runId,
        Instant startTime,
        List<RandomVariable> playerRandomVariables,
        CityStatistics cityStatistics,
        ModelElement simulation

) {
}