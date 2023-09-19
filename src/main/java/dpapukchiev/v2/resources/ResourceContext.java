package dpapukchiev.v2.resources;

import dpapukchiev.v2.cost.CostReport;
import dpapukchiev.v2.effects.Effect;
import dpapukchiev.v2.effects.EffectState;
import dpapukchiev.v2.player.Player;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@RequiredArgsConstructor
public class ResourceContext {
    private final Player player;

    public CostReport calculateResourcesCost(List<RawMaterial> rawMaterials, List<ManufacturedGood> manufacturedGoods) {
        var costReport = CostReport.builder().build();
        var coins = player.getVault().getCoins();
        var usedEffects = new ArrayList<ResourceBundle>();

        var countPerRawMaterial = rawMaterials.stream().collect(groupingBy(Function.identity(), Collectors.counting()));
        for (var rawMaterial : countPerRawMaterial.entrySet()) {
            var count = getRawMaterialCount(rawMaterial.getKey());
            var requiredCount = rawMaterial.getValue();
            var missingCount = requiredCount - count;
            if(missingCount == 0) {
                continue;
            }

            var wildCardBundlesToUse = getWildcardRawMaterialResourceBundles().stream()
                    .filter(bundle -> !usedEffects.contains(bundle))
                    .toList()
                    .subList(0, (int) missingCount);
            usedEffects.addAll(wildCardBundlesToUse);
            missingCount -= wildCardBundlesToUse.size();
            if(missingCount == 0) {
                continue;
            }


        }

        return costReport;
    }

    public double getRawMaterialCount(RawMaterial rawMaterial) {
        return countMaterial(rawMaterial, getPermanentEffectsProvidingResources(), false);
    }

    public double getManufacturedGoodCount(ManufacturedGood manufacturedGood) {
        return countMaterial(manufacturedGood, getPermanentEffectsProvidingResources(), false);
    }

    public double getManufacturedGoodCountWildcard(ManufacturedGood manufacturedGood) {
        return countMaterial(manufacturedGood, getPermanentEffectsProvidingResources(), true);
    }

    public double getRawMaterialCountWildcard(RawMaterial rawMaterial) {
        return countMaterial(rawMaterial, getPermanentEffectsProvidingResources(), true);
    }

    public String report() {
        // TODO: implement
        return "";
    }

    public List<ResourceBundle> getWildcardRawMaterialResourceBundles() {
        return getResourceBundles()
                .stream()
                .filter(ResourceBundle::isWildcardRawMaterial)
                .toList();
    }

    public List<ResourceBundle> getPermanentWildcardManufacturedGoodResourceBundles() {
        return getResourceBundles()
                .stream()
                .filter(ResourceBundle::isWildcardManufacturedGood)
                .toList();
    }

    private List<ResourceBundle> getResourceBundles() {
        return player.getEffectExecutionContext().getPermanentEffects()
                .stream()
                .filter(effect -> effect.getState().equals(EffectState.AVAILABLE))
                .filter(effect -> effect.getResourceBundle(player).isPresent())
                .map(effect -> effect.getResourceBundle(player))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    private List<Effect> getPermanentEffectsProvidingResources() {
        return player.getEffectExecutionContext().getPermanentEffects()
                .stream()
                .filter(effect -> effect.getState().equals(EffectState.AVAILABLE))
                .filter(effect -> effect.getResourceBundle(player).isPresent())
                .toList();
    }

    private int countMaterial(RawMaterial rawMaterial, List<Effect> validEffects, boolean wildCardRawMaterial) {
        var resourceBundles = validEffects
                .stream()
                .map(effect -> effect.getResourceBundle(player))
                .filter(Optional::isPresent)
                .map(Optional::get)
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

    private int countMaterial(ManufacturedGood manufacturedGood, List<Effect> validEffects, boolean wildcardManufacturedGood) {
        var resourceBundles = validEffects
                .stream()
                .map(effect -> effect.getResourceBundle(player))
                .filter(Optional::isPresent)
                .map(Optional::get)
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
