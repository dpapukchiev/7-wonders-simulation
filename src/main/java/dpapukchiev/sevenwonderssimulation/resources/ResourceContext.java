package dpapukchiev.sevenwonderssimulation.resources;

import dpapukchiev.sevenwonderssimulation.cost.CostReport;
import dpapukchiev.sevenwonderssimulation.effects.ScienceSymbolsEffect;
import dpapukchiev.sevenwonderssimulation.effects.core.Effect;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectDirectionConstraint;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectState;
import dpapukchiev.sevenwonderssimulation.effects.core.PreferentialTradingContract;
import dpapukchiev.sevenwonderssimulation.player.Player;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

        var rawMaterialsReport = createReportForRawMaterials(rawMaterials, usedEffects, costReport);
        if (rawMaterialsReport != null) return rawMaterialsReport;

        var manufacturedGoodsReport = createReportForManufacturedGoods(manufacturedGoods, usedEffects, costReport);
        if (manufacturedGoodsReport != null) return manufacturedGoodsReport;

        if (costReport.getToPayTotal() <= coins) {
            return costReport.toBuilder()
                    .affordable(true)
                    .build();
        }
        return costReport;
    }

    public double getRawMaterialCount(RawMaterial rawMaterial) {
        return countMaterial(rawMaterial, getPermanentEffectsProvidingResources(), false);
    }

    public double getRawMaterialCountWildcard(RawMaterial rawMaterial) {
        return countMaterial(rawMaterial, getPermanentEffectsProvidingResources(), true);
    }

    public double getManufacturedGoodCount(ManufacturedGood manufacturedGood) {
        return countMaterial(manufacturedGood, getPermanentEffectsProvidingResources(), false);
    }

    public double getScienceSymbolCount(ScienceSymbol scienceSymbol) {
        var scienceEffectBundles = getPermanentEffectsProvidingResources()
                .stream()
                .map(effect -> effect.getResourceBundle(player))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(bundle -> bundle.getScienceSymbols().contains(scienceSymbol))
                .toList();
        var symbolCount = scienceEffectBundles
                .stream()
                .filter(bundle -> !bundle.isWildcardScienceSymbol())
                .count();
        var wildCard = player.getEffectExecutionContext()
                .getAvailablePermanentEffects()
                .stream()
                .filter(effect -> effect.getResourceBundle(player).isPresent() &&
                        effect.getResourceBundle(player).get().isWildcardScienceSymbol())
                .filter(effect -> effect instanceof ScienceSymbolsEffect)
                .filter(effect -> Objects.equals(
                        ((ScienceSymbolsEffect) effect).getChosenSymbol(),
                        scienceSymbol
                ))
                .count();

        return symbolCount + wildCard;
    }

    public double getManufacturedGoodCountWildcard(ManufacturedGood manufacturedGood) {
        return countMaterial(manufacturedGood, getPermanentEffectsProvidingResources(), true);
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

    public String report() {
        // TODO: implement
        return "";
    }

    private CostReport createReportForRawMaterials(List<RawMaterial> rawMaterials, ArrayList<ResourceBundle> usedEffects, CostReport costReport) {
        var countPerRawMaterial = rawMaterials.stream().collect(groupingBy(Function.identity(), Collectors.counting()));
        for (var rawMaterial : countPerRawMaterial.entrySet()) {
            var count = getRawMaterialCount(rawMaterial.getKey());
            var requiredCount = rawMaterial.getValue();
            var missingCount = requiredCount - count;
            if (count >= requiredCount) {
                continue;
            }

            var wildCardBundlesToUse = getWildcardRawMaterialResourceBundles().stream()
                    .filter(bundle -> !usedEffects.contains(bundle))
                    .limit((long) missingCount)
                    .toList();

            usedEffects.addAll(wildCardBundlesToUse);
            missingCount -= wildCardBundlesToUse.size();
            if (missingCount == 0) {
                continue;
            }

            var priceLeft = player.getEffectExecutionContext()
                    .getTradingPrice(EffectDirectionConstraint.LEFT, PreferentialTradingContract.Type.RAW_MATERIALS);
            var priceRight = player.getEffectExecutionContext()
                    .getTradingPrice(EffectDirectionConstraint.RIGHT, PreferentialTradingContract.Type.RAW_MATERIALS);

            if (priceLeft < priceRight) {
                missingCount = buyMissingFromLeft(missingCount, costReport, priceLeft, rawMaterial.getKey());
                if (missingCount > 0) {
                    missingCount = buyMissingFromRight(missingCount, costReport, priceRight, rawMaterial.getKey());
                }
            } else {
                missingCount = buyMissingFromRight(missingCount, costReport, priceRight, rawMaterial.getKey());
                if (missingCount > 0) {
                    missingCount = buyMissingFromLeft(missingCount, costReport, priceLeft, rawMaterial.getKey());
                }
            }

            if (missingCount > 0) {
                return costReport.setUnaffordable();
            }

        }
        return null;
    }

    private CostReport createReportForManufacturedGoods(List<ManufacturedGood> manufacturedGoods, ArrayList<ResourceBundle> usedEffects, CostReport costReport) {
        var countPerManufacturedGood = manufacturedGoods.stream().collect(groupingBy(Function.identity(), Collectors.counting()));

        for (var manufacturedGood : countPerManufacturedGood.entrySet()) {
            var count = getManufacturedGoodCount(manufacturedGood.getKey());
            var requiredCount = manufacturedGood.getValue();
            var missingCount = requiredCount - count;
            if (count >= requiredCount) {
                continue;
            }

            var wildCardBundlesToUse = getPermanentWildcardManufacturedGoodResourceBundles().stream()
                    .filter(bundle -> !usedEffects.contains(bundle))
                    .limit((long) missingCount)
                    .toList();

            usedEffects.addAll(wildCardBundlesToUse);
            missingCount -= wildCardBundlesToUse.size();
            if (missingCount == 0) {
                continue;
            }

            var priceLeft = player.getEffectExecutionContext()
                    .getTradingPrice(EffectDirectionConstraint.LEFT, PreferentialTradingContract.Type.MANUFACTURED_GOODS);
            var priceRight = player.getEffectExecutionContext()
                    .getTradingPrice(EffectDirectionConstraint.RIGHT, PreferentialTradingContract.Type.MANUFACTURED_GOODS);

            if (priceLeft < priceRight) {
                missingCount = buyMissingFromLeft(missingCount, costReport, priceLeft, manufacturedGood.getKey());
                if (missingCount > 0) {
                    missingCount = buyMissingFromRight(missingCount, costReport, priceRight, manufacturedGood.getKey());
                }
            } else {
                missingCount = buyMissingFromRight(missingCount, costReport, priceRight, manufacturedGood.getKey());
                if (missingCount > 0) {
                    missingCount = buyMissingFromLeft(missingCount, costReport, priceLeft, manufacturedGood.getKey());
                }
            }

            if (missingCount > 0) {
                return costReport.setUnaffordable();
            }

        }
        return null;
    }

    private double buyMissingFromLeft(double missingCount, CostReport costReport, double priceLeft, RawMaterial rawMaterialKey) {
        var availableInLeft = player.getLeftPlayer().resourceContext()
                .getRawMaterialCount(rawMaterialKey);
        var toTake = Math.min(availableInLeft, missingCount);
        missingCount -= toTake;
        costReport.addToPayLeft(toTake * priceLeft);
        return missingCount;
    }

    private double buyMissingFromRight(double missingCount, CostReport costReport, double priceRight, RawMaterial rawMaterialKey) {
        var availableInRight = player.getRightPlayer().resourceContext()
                .getRawMaterialCount(rawMaterialKey);
        var toTake = Math.min(availableInRight, missingCount);
        missingCount -= toTake;
        costReport.addToPayRight(toTake * priceRight);
        return missingCount;
    }

    private double buyMissingFromLeft(double missingCount, CostReport costReport, double priceLeft, ManufacturedGood manufacturedGoodKey) {
        var availableInLeft = player.getLeftPlayer().resourceContext()
                .getManufacturedGoodCount(manufacturedGoodKey);
        var toTake = Math.min(availableInLeft, missingCount);
        missingCount -= toTake;
        costReport.addToPayLeft(toTake * priceLeft);
        return missingCount;
    }

    private double buyMissingFromRight(double missingCount, CostReport costReport, double priceRight, ManufacturedGood manufacturedGoodKey) {
        var availableInRight = player.getRightPlayer().resourceContext()
                .getManufacturedGoodCount(manufacturedGoodKey);
        var toTake = Math.min(availableInRight, missingCount);
        missingCount -= toTake;
        costReport.addToPayRight(toTake * priceRight);
        return missingCount;
    }

    private List<ResourceBundle> getResourceBundles() {
        return player.getEffectExecutionContext().getAvailablePermanentEffects()
                .stream()
                .filter(effect -> effect.getState().equals(EffectState.AVAILABLE))
                .filter(effect -> effect.getResourceBundle(player).isPresent())
                .map(effect -> effect.getResourceBundle(player))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    private List<Effect> getPermanentEffectsProvidingResources() {
        return player.getEffectExecutionContext().getAvailablePermanentEffects()
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
