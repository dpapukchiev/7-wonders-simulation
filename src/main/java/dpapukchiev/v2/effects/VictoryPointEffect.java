package dpapukchiev.v2.effects;

import dpapukchiev.v2.effects.core.BaseEffect;
import dpapukchiev.v2.effects.core.EffectReward;
import dpapukchiev.v2.effects.core.EffectTiming;
import dpapukchiev.v2.player.Player;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class VictoryPointEffect extends BaseEffect {
    private final double victoryPoints;

    public static VictoryPointEffect of(double victoryPoints) {
        return new VictoryPointEffect(victoryPoints);
    }

    @Override
    public void scheduleEffect(Player player) {
        player.getEffectExecutionContext()
                .addEffect(this, EffectTiming.END_OF_AGE);
    }

    @Override
    public Optional<EffectReward> getReward(Player player) {
        return Optional.of(EffectReward.builder()
                .victoryPointsReward(victoryPoints)
                .build());
    }

    @Override
    public String report() {
        return "VP(%s)".formatted(victoryPoints);
    }
}
