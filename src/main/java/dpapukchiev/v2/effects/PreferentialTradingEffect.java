package dpapukchiev.v2.effects;

import dpapukchiev.v2.effects.core.BaseEffect;
import dpapukchiev.v2.effects.core.EffectDirectionConstraint;
import dpapukchiev.v2.effects.core.PreferentialTradingContract;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class PreferentialTradingEffect extends BaseEffect {

    private final EffectDirectionConstraint        effectDirectionConstraint;
    private final PreferentialTradingContract.Type contractType;

    public static PreferentialTradingEffect forDirectionAndType(
            EffectDirectionConstraint effectDirectionConstraint,
            PreferentialTradingContract.Type contractType
    ) {
        return new PreferentialTradingEffect(effectDirectionConstraint, contractType);
    }

    @Override
    public Optional<PreferentialTradingContract> getPreferentialTrading() {
        return Optional.of(PreferentialTradingContract.builder()
                .directionConstraint(effectDirectionConstraint)
                .type(contractType)
                .build());
    }
}
