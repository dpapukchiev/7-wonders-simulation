package dpapukchiev.sevenwonderssimulation.effects;

import dpapukchiev.sevenwonderssimulation.effects.core.BaseEffect;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectReward;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectTiming;
import dpapukchiev.sevenwonderssimulation.game.Turn;
import dpapukchiev.sevenwonderssimulation.player.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

@Getter
@AllArgsConstructor
public class WarShieldsEffect extends BaseEffect {

    private final double shields;
    public static WarShieldsEffect of(double shields) {
        return new WarShieldsEffect(shields);
    }

    @Override
    public void scheduleRewardEvaluationAndCollection(Player player, Turn turn) {
        player.getEffectExecutionContext()
                .scheduleRewardEvaluationAndCollection(this, EffectTiming.END_OF_TURN);
    }

    @Override
    public Optional<EffectReward> collectReward(Player player) {
        return Optional.of(EffectReward.builder()
                .shields(shields)
                .build());
    }

    @Override
    public String report() {
        return "SH(%s)".formatted(shields);
    }
}
