package dpapukchiev.v1.effects.v2;

import dpapukchiev.v1.player.Player;

public interface Effect {
    void applyTo(Player player);

    EffectReward getReward(Player player);

    ResourceBundle getResourceBundle(Player player);

    EffectState getState();
}
