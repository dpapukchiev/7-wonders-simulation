package dpapukchiev.effects.v2;

import dpapukchiev.cards.ManufacturedGood;
import dpapukchiev.player.Player;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ManufacturedGoodEffect implements Effect {

    private final List<ManufacturedGood> manufacturedGoods;

    @Override
    public void applyTo(Player player) {
        player.getEffectExecutionContext()
                .addEffect(this, EffectTiming.ANYTIME);
    }

    @Override
    public EffectReward getReward(Player player) {
        return EffectReward.builder()
                .build();
    }

    @Override
    public ResourceBundle getResourceBundle(Player player) {
        return ResourceBundle.builder()
                .manufacturedGoods(manufacturedGoods)
                .build();
    }

    @Override
    public EffectState getState() {
        return EffectState.AVAILABLE;
    }
}
