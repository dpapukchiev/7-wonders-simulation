package dpapukchiev.sevenwonderssimulation.effects.core;

import dpapukchiev.sevenwonderssimulation.player.Player;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public abstract class BaseRewardWithModifiersEffect extends BaseEffect {
    protected final EffectDirectionConstraint directionConstraint;
    protected final EffectMultiplierType      multiplierType;

    @Override
    public Optional<EffectReward> collectReward(Player player) {
        var left = player.getLeftPlayer();
        var right = player.getRightPlayer();
        var reward = 0.0;

        var selfCount = player.getVault().countMultiplier(multiplierType);
        var leftCount = left.getVault().countMultiplier(multiplierType);
        var rightCount = right.getVault().countMultiplier(multiplierType);

        reward = switch (directionConstraint) {
            case LEFT -> leftCount * getReward();
            case RIGHT -> rightCount * getReward();
            case ALL -> (leftCount + rightCount + selfCount) * getReward();
            case BOTH -> (leftCount + rightCount) * getReward();
            case SELF -> selfCount * getReward();
        };

        player.log("%s collecting reward for each %s on %s side %s (base) x %s (multiplier) = %s \n%s (self count: %s, left count: %s, right count: %s)"
                .formatted(
                        player.getName(),
                        multiplierType,
                        directionConstraint,
                        getReward(),
                        reward / getReward(),
                        reward,
                        multiplierType,
                        selfCount,
                        leftCount,
                        rightCount
                ));

        return buildEffectReward(reward);
    }

    protected abstract Optional<EffectReward> buildEffectReward(double reward);

    protected abstract double getReward();
}
