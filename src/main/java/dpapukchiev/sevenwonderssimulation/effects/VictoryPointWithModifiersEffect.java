package dpapukchiev.sevenwonderssimulation.effects;

import dpapukchiev.sevenwonderssimulation.effects.core.BaseRewardWithModifiersEffect;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectDirectionConstraint;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectMultiplierType;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectReward;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectTiming;
import dpapukchiev.sevenwonderssimulation.game.Turn;
import dpapukchiev.sevenwonderssimulation.player.Player;

import java.util.Optional;

public class VictoryPointWithModifiersEffect extends BaseRewardWithModifiersEffect {
    private final double victoryPoints;

    public VictoryPointWithModifiersEffect(
            EffectDirectionConstraint directionConstraint,
            EffectMultiplierType multiplierType,
            double victoryPoints
    ) {
        super(directionConstraint, multiplierType);
        this.victoryPoints = victoryPoints;
    }

    public static VictoryPointWithModifiersEffect of(
            EffectDirectionConstraint directionConstraint,
            EffectMultiplierType multiplierType,
            double victoryPoints
    ) {
        return new VictoryPointWithModifiersEffect(directionConstraint, multiplierType, victoryPoints);
    }

    @Override
    protected Optional<EffectReward> buildEffectReward(double reward) {
        return Optional.of(EffectReward.builder()
                .victoryPointsReward(reward)
                .build());
    }

    @Override
    protected double getReward() {
        return victoryPoints;
    }

    @Override
    public void scheduleRewardEvaluationAndCollection(Player player, Turn turn) {
        player.getEffectExecutionContext()
                .scheduleRewardEvaluationAndCollection(this, EffectTiming.END_OF_TURN);
    }

    @Override
    public String report() {
        return "VP(%s %s %s)".formatted(victoryPoints, directionConstraint, multiplierType);
    }
}
