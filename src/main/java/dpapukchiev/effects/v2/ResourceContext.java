package dpapukchiev.effects.v2;

import dpapukchiev.cards.ManufacturedGood;
import dpapukchiev.cards.RawMaterial;
import dpapukchiev.player.Player;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public class ResourceContext {
    private final Player player;

    public double getRawMaterialCount(RawMaterial rawMaterial) {
        var validEffects = player.getEffectExecutionContext().getPermanentEffects();
        return countMaterial(rawMaterial, validEffects, false);
    }

    public double getManufacturedGoodCount(ManufacturedGood manufacturedGood) {
        var validEffects = player.getEffectExecutionContext().getPermanentEffects();
        return countMaterial(manufacturedGood, validEffects, false);
    }

    public double getManufacturedGoodCountWildcard(ManufacturedGood manufacturedGood) {
        var validEffects = player.getEffectExecutionContext().getPermanentEffects();
        return countMaterial(manufacturedGood, validEffects, true);
    }

    public double getRawMaterialCountWildcard(RawMaterial rawMaterial) {
        var validEffects = player.getEffectExecutionContext().getPermanentEffects();
        return countMaterial(rawMaterial, validEffects, true);
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
