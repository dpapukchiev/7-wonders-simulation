package dpapukchiev.sevenwonderssimulation.effects;

import dpapukchiev.sevenwonderssimulation.effects.core.BaseEffect;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectReward;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectTiming;
import dpapukchiev.sevenwonderssimulation.game.Turn;
import dpapukchiev.sevenwonderssimulation.player.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

@Getter
@AllArgsConstructor
public class CoinRewardEffect extends BaseEffect {
    private final double coins;

    public static CoinRewardEffect of(double coins) {
        return new CoinRewardEffect(coins);
    }

    @Override
    public void scheduleRewardEvaluationAndCollection(Player player, Turn turn) {
        player.getEffectExecutionContext()
                .scheduleRewardEvaluationAndCollection(this, EffectTiming.END_OF_TURN);
    }

    @Override
    public Optional<EffectReward> collectReward(Player player) {
        return Optional.of(EffectReward.builder()
                .coinReward(coins)
                .build());
    }

    @Override
    public String report() {
        return "COIN(%s)".formatted(coins);
    }
}
