package dpapukchiev.v2.effects.core;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class EffectReward {
    @Builder.Default
    private double coinReward          = 0;
    @Builder.Default
    private double shields             = 0;
    @Builder.Default
    private double victoryPointsReward = 0;

    public EffectReward merge(EffectReward other) {
        if (other == null) {
            return this;
        }

        return EffectReward.builder()
                .coinReward(coinReward + other.coinReward)
                .shields(shields + other.shields)
                .victoryPointsReward(victoryPointsReward + other.victoryPointsReward)
                .build();
    }
}
