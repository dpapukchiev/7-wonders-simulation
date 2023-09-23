package dpapukchiev.sevenwonderssimulation.effects.core;

import dpapukchiev.sevenwonderssimulation.game.Turn;
import dpapukchiev.sevenwonderssimulation.player.Player;
import dpapukchiev.sevenwonderssimulation.resources.ResourceBundle;

import java.util.Optional;

public interface Effect {
    void scheduleRewardEvaluationAndCollection(Player player, Turn turn);

    default Optional<EffectReward> collectReward(Player player) {
        return Optional.empty();
    }

    default Optional<ResourceBundle> getResourceBundle(Player player) {
        return Optional.empty();
    }

    default Optional<PreferentialTradingContract> getPreferentialTrading() {
        return Optional.empty();
    }

    EffectState getState();

    EffectState markAsExhausted();

    String report();
}
