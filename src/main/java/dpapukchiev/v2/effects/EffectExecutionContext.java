package dpapukchiev.v2.effects;

import dpapukchiev.v2.player.Player;

import java.util.ArrayList;
import java.util.List;

public class EffectExecutionContext {
    private final List<Effect> effectsEndOfTurn = new ArrayList<>();
    private final List<Effect> effectsEndOfAge  = new ArrayList<>();
    private final List<Effect> effectsEndOfGame = new ArrayList<>();
    private final List<Effect> permanentEffects = new ArrayList<>();

    public List<Effect> getPermanentEffects() {
        return permanentEffects.stream()
                .filter(effect -> effect.getState().equals(EffectState.AVAILABLE))
                .toList();
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

    public EffectReward executeEffectsEndOfAge(Player player) {
        return getEffectReward(player, effectsEndOfAge);
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
