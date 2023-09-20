package dpapukchiev.v2.effects;

import dpapukchiev.v2.player.Player;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class VictoryPointEffect extends BaseEffect {
    private final double victoryPoints;

    @Override
    public Optional<EffectReward> getReward(Player player) {
        return Optional.of(EffectReward.builder()
                .victoryPointsReward(victoryPoints)
                .build());
    }
}
