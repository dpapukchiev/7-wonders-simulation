package dpapukchiev.v2.effects;

import dpapukchiev.v2.effects.core.BaseRewardWithModifiersEffect;
import dpapukchiev.v2.effects.core.EffectDirectionConstraint;
import dpapukchiev.v2.effects.core.EffectMultiplierType;
import dpapukchiev.v2.effects.core.EffectReward;
import dpapukchiev.v2.effects.core.EffectTiming;
import dpapukchiev.v2.player.Player;

import java.util.Optional;

public class VictoryPointWithModifiersEffect extends BaseRewardWithModifiersEffect {
    private final double victoryPoints;

    public VictoryPointWithModifiersEffect(EffectDirectionConstraint directionConstraint, EffectMultiplierType multiplierType, double victoryPoints) {
        super(directionConstraint, multiplierType);
        this.victoryPoints = victoryPoints;
    }

    @Override
    protected Optional<EffectReward> buildEffectReward(double reward) {
        markAsExhausted();
        return Optional.of(EffectReward.builder()
                .victoryPointsReward(reward)
                .build());
    }

    @Override
    protected double getReward() {
        return victoryPoints;
    }

    @Override
    public void scheduleEffect(Player player) {
        player.getEffectExecutionContext()
                .addEffect(this, EffectTiming.END_OF_TURN);
    }

    @Override
    public String report() {
        return "VP(%s %s %s)".formatted(victoryPoints, directionConstraint, multiplierType);
    }
}
