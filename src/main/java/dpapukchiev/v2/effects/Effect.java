package dpapukchiev.v2.effects;

import dpapukchiev.v2.player.Player;
import dpapukchiev.v2.resources.ResourceBundle;

public interface Effect {
    EffectReward getReward(Player player);

    ResourceBundle getResourceBundle(Player player);

    EffectState getState();
}
