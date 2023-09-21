package dpapukchiev.v2.effects.core;

import dpapukchiev.v2.player.Player;
import lombok.Getter;

@Getter
public abstract class BaseEffect implements Effect {
    private EffectState state = EffectState.AVAILABLE;

//    @Override
//    public void scheduleEffect(Player player) {
//        player.getEffectExecutionContext()
//                .addEffect(this, EffectTiming.ANYTIME);
//    }

    @Override
    public EffectState markAsExhausted() {
        state = EffectState.EXHAUSTED;
        return state;
    }
}
