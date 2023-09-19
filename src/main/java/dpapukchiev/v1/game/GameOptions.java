package dpapukchiev.v1.game;

import jsl.modeling.elements.variable.RandomVariable;
import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record GameOptions(
        int numberOfPlayers,
        int agesToSchedule,
        List<RandomVariable> playerRandomVariables
) {
}