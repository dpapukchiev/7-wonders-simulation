package dpapukchiev.v2.effects;

import dpapukchiev.v2.effects.core.BaseRewardWithModifiersEffect;
import dpapukchiev.v2.effects.core.EffectDirectionConstraint;
import dpapukchiev.v2.effects.core.EffectMultiplierType;
import dpapukchiev.v2.effects.core.EffectReward;

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
}
