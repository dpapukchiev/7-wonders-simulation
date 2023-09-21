package dpapukchiev.v2.effects;

import dpapukchiev.v2.effects.core.BaseEffect;
import dpapukchiev.v2.effects.core.EffectReward;
import dpapukchiev.v2.effects.core.EffectTiming;
import dpapukchiev.v2.player.Player;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class WarShieldsEffect extends BaseEffect {

    private final int shields;
    public static WarShieldsEffect of(int shields) {
        return new WarShieldsEffect(shields);
    }

    @Override
    public void scheduleEffect(Player player) {
        player.getEffectExecutionContext()
                .addEffect(this, EffectTiming.ANYTIME);
    }

    @Override
    public Optional<EffectReward> getReward(Player player) {
        return Optional.of(EffectReward.builder()
                .shields(shields)
                .build());
    }

    @Override
    public String report() {
        return "SH(%s)".formatted(shields);
    }
}
