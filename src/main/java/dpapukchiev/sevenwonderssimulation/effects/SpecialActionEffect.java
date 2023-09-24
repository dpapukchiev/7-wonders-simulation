package dpapukchiev.sevenwonderssimulation.effects;

import dpapukchiev.sevenwonderssimulation.effects.core.BaseEffect;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectReward;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectTiming;
import dpapukchiev.sevenwonderssimulation.effects.core.SpecialAction;
import dpapukchiev.sevenwonderssimulation.game.Turn;
import dpapukchiev.sevenwonderssimulation.player.Player;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class SpecialActionEffect extends BaseEffect {
    private final SpecialAction specialAction;
    private final EffectTiming  eventTiming;

    public static SpecialActionEffect of(SpecialAction specialAction, EffectTiming eventTiming) {
        return new SpecialActionEffect(specialAction, eventTiming);
    }

    @Override
    public void scheduleRewardEvaluationAndCollection(Player player, Turn turn) {
        if (specialAction.equals(SpecialAction.PLAY_CARD_WITHOUT_COST)) {
            var times = 4 - turn.age();
            for (int i = 0; i < times; i++) {
                player.getVault().addSpecialActions(specialAction);
            }
            player.log("Player %s got %s uses of the action %s".formatted(player.getName(), times, specialAction.name()));
            return;
        }

        player.getEffectExecutionContext()
                .scheduleRewardEvaluationAndCollection(this, eventTiming);
    }

    @Override
    public Optional<EffectReward> collectReward(Player player) {
        player.collectMetric("collected-special-action-" + specialAction.name(), 1);
        player.getVault().addSpecialActions(specialAction);
        return Optional.of(EffectReward.builder()
                .build());
    }

    @Override
    public String report() {
        return "SA(%s)".formatted(specialAction.name());
    }
}
