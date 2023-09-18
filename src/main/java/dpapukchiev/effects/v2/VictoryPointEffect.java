package dpapukchiev.effects.v2;

import dpapukchiev.player.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class VictoryPointEffect implements Effect {

    private final double victoryPoint;

    private EffectState  state        = EffectState.AVAILABLE;
    private EffectTiming effectTiming = EffectTiming.END_OF_GAME;

    @Override
    public EffectReward getReward(Player player) {
        state = EffectState.EXHAUSTED;
        return EffectReward.builder()
                .victoryPointsReward(victoryPoint)
                .build();
    }

    @Override
    public ResourceBundle getResourceBundle(Player player) {
        return ResourceBundle.builder()
                .build();
    }

    @Override
    public void applyTo(Player player) {
        player.getEffectExecutionContext()
                .addEffect(this, effectTiming);
    }
}
