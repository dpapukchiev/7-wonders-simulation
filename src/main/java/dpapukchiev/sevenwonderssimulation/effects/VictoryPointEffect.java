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
public class VictoryPointEffect extends BaseEffect {
    private final double victoryPoints;

    public static VictoryPointEffect of(double victoryPoints) {
        return new VictoryPointEffect(victoryPoints);
    }

    @Override
    public void scheduleRewardEvaluationAndCollection(Player player, Turn turn) {
        player.getEffectExecutionContext()
                .scheduleRewardEvaluationAndCollection(this, EffectTiming.END_OF_TURN);
    }

    @Override
    public Optional<EffectReward> collectReward(Player player) {
        return Optional.of(EffectReward.builder()
                .victoryPointsReward(victoryPoints)
                .build());
    }

    @Override
    public String report() {
        return "VP(%s)".formatted(victoryPoints);
    }
}
