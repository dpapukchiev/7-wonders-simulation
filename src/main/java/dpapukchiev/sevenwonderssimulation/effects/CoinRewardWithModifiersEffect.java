package dpapukchiev.sevenwonderssimulation.effects;

import dpapukchiev.sevenwonderssimulation.effects.core.BaseRewardWithModifiersEffect;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectDirectionConstraint;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectMultiplierType;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectReward;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectTiming;
import dpapukchiev.sevenwonderssimulation.player.Player;

import java.util.Optional;

public class CoinRewardWithModifiersEffect extends BaseRewardWithModifiersEffect {
    private final double coinReward;

    public CoinRewardWithModifiersEffect(EffectDirectionConstraint directionConstraint, EffectMultiplierType multiplierType, double coinReward) {
        super(directionConstraint, multiplierType);
        this.coinReward = coinReward;
    }

    public static CoinRewardWithModifiersEffect of(EffectDirectionConstraint directionConstraint, EffectMultiplierType multiplierType, double coinReward) {
        return new CoinRewardWithModifiersEffect(directionConstraint, multiplierType, coinReward);
    }

    @Override
    protected Optional<EffectReward> buildEffectReward(double reward) {
        markAsExhausted();
        return Optional.of(
                EffectReward.builder()
                        .coinReward(reward)
                        .build()
        );
    }

    @Override
    protected double getReward() {
        return coinReward;
    }

    @Override
    public void scheduleEffect(Player player) {
        player.getEffectExecutionContext()
                .addEffect(this, EffectTiming.END_OF_TURN);
    }

    @Override
    public String report() {
        return "COIN(%sx%sx%s)".formatted(coinReward, directionConstraint, multiplierType);
    }
}
