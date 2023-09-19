package dpapukchiev.v2.effects;

import dpapukchiev.v1.player.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class CoinRewardEffect implements Effect {

    private final double coinReward;

    private EffectState  state        = EffectState.AVAILABLE;
    private EffectTiming effectTiming = EffectTiming.END_OF_TURN;

    @Override
    public void applyTo(Player player) {
        player.getEffectExecutionContext()
                .addEffect(this, effectTiming);
    }

    @Override
    public EffectReward getReward(Player player) {
        state = EffectState.EXHAUSTED;
        return EffectReward.builder()
                .coinReward(coinReward)
                .build();
    }

    @Override
    public ResourceBundle getResourceBundle(Player player) {
        return ResourceBundle.builder()
                .build();
    }
}
