package dpapukchiev.v2.effects;

import dpapukchiev.v2.player.Player;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class CoinRewardEffect extends BaseEffect {
    private final double coins;

    @Override
    public Optional<EffectReward> getReward(Player player) {
        return Optional.of(EffectReward.builder()
                .coinReward(coins)
                .build());
    }
}
