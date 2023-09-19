package dpapukchiev.v1.effects.v2;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class EffectReward {
    @Builder.Default
    private double      coinReward          = 0;
    @Builder.Default
    private double      victoryPointsReward = 0;
}
