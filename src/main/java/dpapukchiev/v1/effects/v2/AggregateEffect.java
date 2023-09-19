package dpapukchiev.v1.effects.v2;

import dpapukchiev.v1.player.Player;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AggregateEffect implements Effect {
    private final Effect       effect1;
    private final EffectTiming effect1Timing;
    private final Effect       effect2;
    private final EffectTiming effect2Timing;

    @Override
    public void applyTo(Player player) {
        player.getEffectExecutionContext().addEffect(effect1, effect1Timing);
        player.getEffectExecutionContext().addEffect(effect2, effect2Timing);
    }

    @Override
    public EffectReward getReward(Player player) {
        var reward1 = effect1.getReward(player);
        var reward2 = effect2.getReward(player);

        return EffectReward.builder()
                .coinReward(reward1.getCoinReward() + reward2.getCoinReward())
                .victoryPointsReward(reward1.getVictoryPointsReward() + reward2.getVictoryPointsReward())
                .build();
    }

    @Override
    public ResourceBundle getResourceBundle(Player player) {
        return ResourceBundle.builder()
                .build();
    }

    @Override
    public EffectState getState() {
        return effect1.getState().equals(EffectState.AVAILABLE) && effect2.getState().equals(EffectState.AVAILABLE)
                ? EffectState.AVAILABLE
                : EffectState.EXHAUSTED;
    }
}
