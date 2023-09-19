package dpapukchiev.v2.player;

import dpapukchiev.v2.effects.EffectExecutionContext;
import dpapukchiev.v2.resources.ResourceContext;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Player {
    private EffectExecutionContext effectExecutionContext;

    public ResourceContext resourceContext() {
        return new ResourceContext(this);
    }
}
