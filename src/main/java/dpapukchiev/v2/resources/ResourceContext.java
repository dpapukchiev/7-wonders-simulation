package dpapukchiev.v2.resources;

import dpapukchiev.v2.effects.Effect;
import dpapukchiev.v2.effects.EffectState;
import dpapukchiev.v2.player.Player;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public class ResourceContext {
    private final Player player;

    public double getRawMaterialCount(RawMaterial rawMaterial) {
        return countMaterial(rawMaterial, getPermanentEffects(), false);
    }

    public double getManufacturedGoodCount(ManufacturedGood manufacturedGood) {
        return countMaterial(manufacturedGood, getPermanentEffects(), false);
    }

    public double getManufacturedGoodCountWildcard(ManufacturedGood manufacturedGood) {
        return countMaterial(manufacturedGood, getPermanentEffects(), true);
    }

    public double getRawMaterialCountWildcard(RawMaterial rawMaterial) {
        return countMaterial(rawMaterial, getPermanentEffects(), true);
    }

    private Stream<Effect> getPermanentEffects() {
        return player.getEffectExecutionContext().getPermanentEffects()
                .filter(effect -> effect.getState().equals(EffectState.AVAILABLE));
    }

    private int countMaterial(RawMaterial rawMaterial, Stream<Effect> validEffects, boolean wildCardRawMaterial) {
        var resourceBundles = validEffects
                .map(effect -> effect.getResourceBundle(player))
                .filter(bundle -> bundle.getRawMaterials().contains(rawMaterial))
                .filter(bundle -> (wildCardRawMaterial && bundle.isWildcardRawMaterial()) || (!wildCardRawMaterial && !bundle.isWildcardRawMaterial()))
                .toList();
        return resourceBundles.stream()
                .map(bundle -> bundle.getRawMaterials()
                        .stream()
                        .filter(m -> m.equals(rawMaterial))
                        .count()
                )
                .mapToInt(Long::intValue)
                .sum();
    }

    private int countMaterial(ManufacturedGood manufacturedGood, Stream<Effect> validEffects, boolean wildcardManufacturedGood) {
        var resourceBundles = validEffects
                .map(effect -> effect.getResourceBundle(player))
                .filter(bundle -> bundle.getManufacturedGoods().contains(manufacturedGood))
                .filter(bundle -> (wildcardManufacturedGood && bundle.isWildcardManufacturedGood()) || (!wildcardManufacturedGood && !bundle.isWildcardManufacturedGood()))
                .toList();
        return resourceBundles.stream()
                .map(bundle -> bundle.getManufacturedGoods()
                        .stream()
                        .filter(m -> m.equals(manufacturedGood))
                        .count()
                )
                .mapToInt(Long::intValue)
                .sum();
    }
}
