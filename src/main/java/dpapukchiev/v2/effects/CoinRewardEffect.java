package dpapukchiev.v2.effects;

import dpapukchiev.v2.effects.core.BaseEffect;
import dpapukchiev.v2.effects.core.EffectReward;
import dpapukchiev.v2.effects.core.EffectTiming;
import dpapukchiev.v2.player.Player;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class CoinRewardEffect extends BaseEffect {
    private final double coins;

    public static CoinRewardEffect of(double coins) {
        return new CoinRewardEffect(coins);
    }

    @Override
    public void scheduleEffect(Player player) {
        player.getEffectExecutionContext()
                .addEffect(this, EffectTiming.END_OF_TURN);
    }

    @Override
    public Optional<EffectReward> getReward(Player player) {
        return Optional.of(EffectReward.builder()
                .coinReward(coins)
                .build());
    }

    @Override
    public String report() {
        return "COIN(%s)".formatted(coins);
    }
}
