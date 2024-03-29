package dpapukchiev.sevenwonderssimulation.effects;

import dpapukchiev.sevenwonderssimulation.effects.core.BaseEffect;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectDirectionConstraint;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectMultiplierType;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectTiming;
import dpapukchiev.sevenwonderssimulation.game.Turn;
import dpapukchiev.sevenwonderssimulation.player.Player;

public class CoinRewardAndVictoryPointWithModifiersEffect extends BaseEffect {
    private final double                    coinReward;
    private final double                    victoryPoints;
    private final EffectDirectionConstraint directionConstraint;
    private final EffectMultiplierType      multiplierType;

    public CoinRewardAndVictoryPointWithModifiersEffect(
            EffectDirectionConstraint directionConstraint,
            EffectMultiplierType multiplierType,
            double coinReward,
            double victoryPoints
    ) {
        this.directionConstraint = directionConstraint;
        this.multiplierType = multiplierType;
        this.coinReward = coinReward;
        this.victoryPoints = victoryPoints;
    }

    public static CoinRewardAndVictoryPointWithModifiersEffect of(
            EffectDirectionConstraint directionConstraint,
            EffectMultiplierType multiplierType,
            double coinReward,
            double victoryPoints
    ) {
        return new CoinRewardAndVictoryPointWithModifiersEffect(
                directionConstraint,
                multiplierType,
                coinReward,
                victoryPoints
        );
    }

    @Override
    public void scheduleRewardEvaluationAndCollection(Player player, Turn turn) {
        player.getEffectExecutionContext()
                .scheduleRewardEvaluationAndCollection(CoinRewardWithModifiersEffect.of(
                        directionConstraint,
                        multiplierType,
                        coinReward
                ), EffectTiming.END_OF_TURN);
        player.getEffectExecutionContext()
                .scheduleRewardEvaluationAndCollection(VictoryPointWithModifiersEffect.of(
                                directionConstraint,
                                multiplierType,
                                victoryPoints
                        ), multiplierType.equals(EffectMultiplierType.WONDER_STAGE) ?
                                EffectTiming.END_OF_GAME : EffectTiming.END_OF_TURN
                );
    }

    @Override
    public String report() {
        return "VP$([$%s VP%s]x%sx%s)".formatted(coinReward, victoryPoints, directionConstraint, multiplierType);
    }
}
