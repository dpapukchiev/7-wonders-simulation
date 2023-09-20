package dpapukchiev.v2.effects;

import lombok.Getter;

@Getter
public class BaseEffect implements Effect {
    private EffectState state = EffectState.AVAILABLE;

    @Override
    public EffectState markAsExhausted() {
        state = EffectState.EXHAUSTED;
        return state;
    }
}
