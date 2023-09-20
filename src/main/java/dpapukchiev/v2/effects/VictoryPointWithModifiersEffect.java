package dpapukchiev.v2.effects;

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
