package dpapukchiev.sevenwonderssimulation.effects.core;

import lombok.Builder;

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
        return String.format("%s[%s]",
                directionConstraint.name(),
                type.name()
        );
    }
}
