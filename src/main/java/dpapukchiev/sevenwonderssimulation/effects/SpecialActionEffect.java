package dpapukchiev.sevenwonderssimulation.effects;

import dpapukchiev.sevenwonderssimulation.effects.core.BaseEffect;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectReward;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectTiming;
import dpapukchiev.sevenwonderssimulation.effects.core.SpecialAction;
import dpapukchiev.sevenwonderssimulation.player.Player;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class SpecialActionEffect extends BaseEffect {
    private final SpecialAction specialAction;
    private final EffectTiming  eventTiming;

    public static SpecialActionEffect of(SpecialAction specialAction, EffectTiming eventTiming) {
        return new SpecialActionEffect(specialAction, eventTiming);
    }

    @Override
    public void scheduleRewardEvaluationAndCollection(Player player) {
        if(eventTiming.equals(EffectTiming.ANYTIME)){
            player.getVault().addSpecialActions(specialAction);
            return;
        }

        player.getEffectExecutionContext()
                .scheduleRewardEvaluationAndCollection(this, eventTiming);
    }

    @Override
    public Optional<SpecialAction> getSpecialAction() {
        return Optional.of(specialAction);
    }

    @Override
    public Optional<EffectReward> collectReward(Player player) {
        player.getVault().addSpecialActions(specialAction);
        return Optional.of(EffectReward.builder()
                .build());
    }

    @Override
    public String report() {
        return "SA(%s)".formatted(specialAction.name());
    }
}
