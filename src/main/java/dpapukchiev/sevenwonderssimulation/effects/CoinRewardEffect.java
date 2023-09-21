package dpapukchiev.sevenwonderssimulation.effects;

import dpapukchiev.sevenwonderssimulation.effects.core.BaseEffect;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectReward;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectTiming;
import dpapukchiev.sevenwonderssimulation.player.Player;
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
