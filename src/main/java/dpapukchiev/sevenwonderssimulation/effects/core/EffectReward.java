package dpapukchiev.sevenwonderssimulation.effects.core;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder(toBuilder = true)
public class EffectReward {
    @Builder.Default
    private double              coinReward          = 0;
    @Builder.Default
    private double              shields             = 0;
    @Builder.Default
    private double              victoryPointsReward = 0;

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

    public String report() {
        var report = new StringBuilder();
        if (coinReward > 0) {
            report.append("$:").append(coinReward);
        }
        if (shields > 0) {
            report.append("S:").append(shields);
        }
        if (victoryPointsReward > 0) {
            report.append("V:").append(victoryPointsReward);
        }
        return report.toString();
    }
}
