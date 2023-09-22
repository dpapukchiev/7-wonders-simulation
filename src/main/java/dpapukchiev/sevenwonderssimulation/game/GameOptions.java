package dpapukchiev.sevenwonderssimulation.game;

import dpapukchiev.sevenwonderssimulation.reporting.CityStatistics;
import jsl.modeling.elements.variable.RandomVariable;
import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record GameOptions(
        int numberOfPlayers,
        int agesToSchedule,
        List<RandomVariable> playerRandomVariables,
        CityStatistics cityStatistics
) {
}