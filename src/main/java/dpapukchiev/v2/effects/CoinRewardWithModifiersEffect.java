package dpapukchiev.v2.effects;

import dpapukchiev.v2.effects.core.BaseRewardWithModifiersEffect;
import dpapukchiev.v2.effects.core.EffectDirectionConstraint;
import dpapukchiev.v2.effects.core.EffectMultiplierType;
import dpapukchiev.v2.effects.core.EffectReward;
import dpapukchiev.v2.effects.core.EffectTiming;
import dpapukchiev.v2.player.Player;

import java.util.Optional;

public class CoinRewardWithModifiersEffect extends BaseRewardWithModifiersEffect {
    private final double coinReward;

    public CoinRewardWithModifiersEffect(EffectDirectionConstraint directionConstraint, EffectMultiplierType multiplierType, double coinReward) {
        super(directionConstraint, multiplierType);
        this.coinReward = coinReward;
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
        return "COIN(%s %s %s)".formatted(coinReward, directionConstraint, multiplierType);
    }
}
