package dpapukchiev.effects.v2;

import dpapukchiev.player.Player;

public interface Effect {
    void applyTo(Player player);

    EffectReward getReward(Player player);

    ResourceBundle getResourceBundle(Player player);

    EffectState getState();
}
