package dpapukchiev.v2.effects;

import dpapukchiev.v2.effects.core.BaseEffect;
import dpapukchiev.v2.effects.core.EffectDirectionConstraint;
import dpapukchiev.v2.effects.core.EffectTiming;
import dpapukchiev.v2.effects.core.PreferentialTradingContract;
import dpapukchiev.v2.player.Player;
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
    public void scheduleEffect(Player player) {
        player.getEffectExecutionContext()
                .addEffect(this, EffectTiming.ANYTIME);
    }

    @Override
    public Optional<PreferentialTradingContract> getPreferentialTrading() {
        return Optional.of(PreferentialTradingContract.builder()
                .directionConstraint(effectDirectionConstraint)
                .type(contractType)
                .build());
    }

    @Override
    public String report() {
        return "PT(" +
                effectDirectionConstraint.name() +
                "," + contractType.name() +
                ')';
    }
}
