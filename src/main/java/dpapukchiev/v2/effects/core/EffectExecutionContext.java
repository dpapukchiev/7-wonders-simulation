package dpapukchiev.v2.effects.core;

import dpapukchiev.v2.player.Player;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
public class EffectExecutionContext {
    private final List<Effect> effectsEndOfTurn = new ArrayList<>();
    private final List<Effect> effectsEndOfAge  = new ArrayList<>();
    private final List<Effect> effectsEndOfGame = new ArrayList<>();
    private final List<Effect> permanentEffects = new ArrayList<>();

    // TODO: replace with separate enum
    public double getTradingPrice(EffectDirectionConstraint direction, PreferentialTradingContract.Type contractType) {
        if (!List.of(EffectDirectionConstraint.LEFT, EffectDirectionConstraint.RIGHT).contains(direction)) {
            throw new IllegalArgumentException("Direction must be LEFT or RIGHT");
        }

        return getPermanentEffects().stream()
                .map(Effect::getPreferentialTrading)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(contract -> contract.type().equals(contractType))
                .filter(contract -> contract.directionConstraint().equals(direction) ||
                        contract.directionConstraint().equals(EffectDirectionConstraint.BOTH)
                )
                .map(c -> 1)
                .findFirst()
                .orElse(2);
    }

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

    public Optional<EffectReward> executeEffectsEndOfTurn(Player player) {
        return getEffectReward(player, effectsEndOfTurn);
    }

    public Optional<EffectReward> executeEffectsEndOfGame(Player player) {
        return getEffectReward(player, effectsEndOfGame);
    }

    public Optional<EffectReward> executeEffectsEndOfAge(Player player) {
        return getEffectReward(player, effectsEndOfAge);
    }

    public String report() {
        var report = new ArrayList<String>();
        if (!effectsEndOfTurn.isEmpty()) {
            report.add("EOT:%s/%s".formatted(countAvailable(effectsEndOfTurn), effectsEndOfTurn.size()));
        }
        if (!effectsEndOfAge.isEmpty()) {
            report.add("EOA:%s/%s".formatted(countAvailable(effectsEndOfAge), effectsEndOfAge.size()));
        }
        if (!effectsEndOfGame.isEmpty()) {
            report.add("EOG:%s/%s".formatted(countAvailable(effectsEndOfGame), effectsEndOfGame.size()));
        }
        if (!permanentEffects.isEmpty()) {
            report.add("P:%s".formatted(permanentEffects.size()));
        }
        return "Efx(%s)".formatted(String.join(" ", report));
    }

    private int countAvailable(List<Effect> effects) {
        return (int) effects.stream()
                .filter(effect -> effect.getState().equals(EffectState.AVAILABLE))
                .count();
    }

    private Optional<EffectReward> getEffectReward(Player player, List<Effect> effectsToEvaluate) {
        return effectsToEvaluate.stream()
                .filter(effect -> effect.getState().equals(EffectState.AVAILABLE))
                .map(effect -> {
                    var reward = effect.getReward(player);
                    if (reward.isPresent()) {
                        effect.markAsExhausted();
                        log.info("Player {} exhausted effect {} reward {}",
                                player.getName(), effect.report(), reward.get().report());
                    }
                    return reward;
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .reduce(EffectReward::merge);
    }
}
