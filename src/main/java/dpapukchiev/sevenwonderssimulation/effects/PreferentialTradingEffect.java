package dpapukchiev.sevenwonderssimulation.effects;

import dpapukchiev.sevenwonderssimulation.effects.core.BaseEffect;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectDirectionConstraint;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectTiming;
import dpapukchiev.sevenwonderssimulation.effects.core.PreferentialTradingContract;
import dpapukchiev.sevenwonderssimulation.player.Player;
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
    public void scheduleRewardEvaluationAndCollection(Player player) {
        player.getEffectExecutionContext()
                .scheduleRewardEvaluationAndCollection(this, EffectTiming.ANYTIME);
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
