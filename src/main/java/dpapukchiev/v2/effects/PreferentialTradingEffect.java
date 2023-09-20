package dpapukchiev.v2.effects;

import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class PreferentialTradingEffect extends BaseEffect {

    private final EffectDirectionConstraint        effectDirectionConstraint;
    private final PreferentialTradingContract.Type contractType;

    @Override
    public Optional<PreferentialTradingContract> getPreferentialTrading() {
        return Optional.of(PreferentialTradingContract.builder()
                .directionConstraint(effectDirectionConstraint)
                .type(contractType)
                .build());
    }
}
