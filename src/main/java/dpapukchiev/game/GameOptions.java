package dpapukchiev.game;

import jsl.simulation.ModelElement;
import lombok.Builder;

@Builder
public record GameOptions(
        int numberOfPlayers
) {
}