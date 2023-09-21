package dpapukchiev.sevenwonderssimulation.effects.core;

import lombok.Builder;

import java.util.ArrayList;

@Builder
public record PreferentialTradingContract(
        EffectDirectionConstraint directionConstraint, // TODO: replace with separate enum
        Type type
) {

    public enum Type {
        MANUFACTURED_GOODS,
        RAW_MATERIALS
    }

    public String report() {
        var report = new ArrayList<String>();
        return String.format("%s[%s]",
                directionConstraint.name(),
                type.name()
        );
    }
}
