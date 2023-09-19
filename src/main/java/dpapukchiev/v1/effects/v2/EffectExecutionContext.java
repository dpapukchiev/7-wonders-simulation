package dpapukchiev.v1.effects.v2;

import dpapukchiev.v1.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class EffectExecutionContext {
    private final List<Effect> effectsEndOfTurn = new ArrayList<>();
    private final List<Effect> effectsEndOfAge  = new ArrayList<>();
    private final List<Effect> effectsEndOfGame = new ArrayList<>();
    private final List<Effect> permanentEffects = new ArrayList<>();

    public Stream<Effect> getPermanentEffects() {
        return permanentEffects.stream()
                .filter(effect -> effect.getState().equals(EffectState.AVAILABLE));
    }

    public void addEffect(Effect effect, EffectTiming timing) {
        switch (timing) {
            case END_OF_TURN:
                effectsEndOfTurn.add(effect);
                break;
            case END_OF_AGE:
                effectsEndOfAge.add(effect);
                break;
            case END_OF_GAME:
                effectsEndOfGame.add(effect);
                break;
            case ANYTIME:
                permanentEffects.add(effect);
                break;
        }
    }

    public EffectReward executeEffectsEndOfTurn(Player player) {
        return getEffectReward(player, effectsEndOfTurn);
    }

    public EffectReward executeEffectsEndOfGame(Player player) {
        return getEffectReward(player, effectsEndOfGame);
    }

    private EffectReward getEffectReward(Player player, List<Effect> effectsToEvaluate) {
        return effectsToEvaluate.stream()
                .filter(effect -> effect.getState().equals(EffectState.AVAILABLE))
                .map(effect -> effect.getReward(player))
                .reduce((effectReward, effectReward2) -> EffectReward.builder()
                        .coinReward(effectReward.getCoinReward() + effectReward2.getCoinReward())
                        .victoryPointsReward(effectReward.getVictoryPointsReward() + effectReward2.getVictoryPointsReward())
                        .build())
                .orElse(EffectReward.builder().build());
    }
}
