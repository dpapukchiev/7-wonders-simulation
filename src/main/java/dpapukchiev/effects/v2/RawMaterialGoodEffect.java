package dpapukchiev.effects.v2;

import dpapukchiev.cards.RawMaterial;
import dpapukchiev.player.Player;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class RawMaterialGoodEffect implements Effect {

    private final List<RawMaterial> rawMaterials;

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
                .rawMaterials(rawMaterials)
                .build();
    }

    @Override
    public EffectState getState() {
        return EffectState.AVAILABLE;
    }
}
