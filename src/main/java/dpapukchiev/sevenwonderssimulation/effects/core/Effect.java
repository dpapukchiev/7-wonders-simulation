package dpapukchiev.sevenwonderssimulation.effects.core;

import dpapukchiev.sevenwonderssimulation.player.Player;
import dpapukchiev.sevenwonderssimulation.resources.ResourceBundle;

import java.util.Optional;

public interface Effect {
    void scheduleEffect(Player player);

    default EffectDirectionConstraint getDirectionConstraint() {
        return EffectDirectionConstraint.SELF;
    }

    default Optional<EffectReward> getReward(Player player) {
        return Optional.empty();
    }

    default Optional<ResourceBundle> getResourceBundle(Player player) {
        return Optional.empty();
    }

    default Optional<PreferentialTradingContract> getPreferentialTrading() {
        return Optional.empty();
    }

    default Optional<SpecialAction> getSpecialAction() {
        return Optional.empty();
    }

    EffectState getState();

    EffectState markAsExhausted();

    String report();
}
