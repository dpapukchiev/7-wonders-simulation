package dpapukchiev.sevenwonderssimulation.effects.core;

import lombok.Getter;

@Getter
public abstract class BaseEffect implements Effect {
    private EffectState state = EffectState.AVAILABLE;

    @Override
    public EffectState markAsExhausted() {
        state = EffectState.EXHAUSTED;
        return state;
    }
}
