package dpapukchiev.v2.effects;

import dpapukchiev.v2.player.Player;
import dpapukchiev.v2.resources.ResourceBundle;

import java.util.Optional;

public interface Effect {
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

    default String report() {
        // TODO: implement
        return "";
    }
}
