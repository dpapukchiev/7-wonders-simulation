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
        return countMaterial(rawMaterial, validEffects);
    }

    public double getManufacturedGoodCount(ManufacturedGood manufacturedGood) {
        var validEffects = player.getEffectExecutionContext().getPermanentEffects();
        return countMaterial(manufacturedGood, validEffects);
    }

    public double getManufacturedGoodCountWildcard(ManufacturedGood manufacturedGood) {
        var validEffects = player.getEffectExecutionContext().getPermanentEffects();
        return countMaterial(manufacturedGood, validEffects);
    }

    public double getRawMaterialCountWildcard(RawMaterial rawMaterial) {
        var validEffects = player.getEffectExecutionContext().getPermanentEffects();
        return countMaterial(rawMaterial, validEffects);
    }

    private int countMaterial(RawMaterial rawMaterial, Stream<Effect> validEffects) {
        return validEffects
                .filter(effect -> effect.getResourceBundle(player).getRawMaterials().contains(rawMaterial))
                .map(effect -> effect.getResourceBundle(player).getRawMaterials()
                        .stream()
                        .filter(m -> m.equals(rawMaterial))
                        .count()
                )
                .mapToInt(Long::intValue)
                .sum();
    }

    private int countMaterial(ManufacturedGood manufacturedGood, Stream<Effect> validEffects) {
        return validEffects
                .filter(effect -> effect.getResourceBundle(player).getManufacturedGoods().contains(manufacturedGood))
                .map(effect -> effect.getResourceBundle(player).getManufacturedGoods()
                        .stream()
                        .filter(m -> m.equals(manufacturedGood))
                        .count()
                )
                .mapToInt(Long::intValue)
                .sum();
    }
}
