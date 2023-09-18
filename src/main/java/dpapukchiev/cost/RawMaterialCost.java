package dpapukchiev.cost;

import dpapukchiev.cards.RawMaterial;
import dpapukchiev.game.TurnContext;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@AllArgsConstructor
public class RawMaterialCost implements Cost {
    private final List<RawMaterial> rawMaterialsList;

    @Override
    public CostReport generateCostReport(TurnContext turnContext) {
        var materialsNeeded = rawMaterialsList.stream()
                .collect(groupingBy(RawMaterial::name));

        return materialsNeeded.entrySet().stream().map(rm -> {
            var requiredCount = rm.getValue().size();
            var neededMaterial = RawMaterial.valueOf(rm.getKey());
            var currentCount = turnContext.getPlayer().getRawMaterialCount(neededMaterial);
            var diff = requiredCount - currentCount;

            var leftCount = turnContext.getPlayer().getLeftPlayer().getRawMaterialCount(neededMaterial);
            var takeFromLeft = Math.min(leftCount, diff);
            var priceLeft = takeFromLeft * 2; // TODO: preferential

            var rightCount = turnContext.getPlayer().getRightPlayer().getRawMaterialCount(neededMaterial);
            var takeFromRight = Math.min(rightCount, diff - takeFromLeft);
            var priceRight = takeFromRight * 2; // TODO: preferential

            boolean hasEnoughCoinsForTrade = (priceLeft + priceRight) <= turnContext.getPlayer().getCoins();
            boolean hasEnoughResources = (currentCount + leftCount + rightCount) >= requiredCount;

            boolean isAffordable = hasEnoughResources && hasEnoughCoinsForTrade;
            return CostReport.builder()
                    .affordable(isAffordable)
                    .missingResource(neededMaterial.name())
                    .toPayLeft(takeFromLeft * 2)
                    .toPayRight(takeFromRight * 2)
                    .build();
        }).reduce(CostReport.builder().affordable(true).build(), CostReport::merge);
    }

    @Override
    public void applyCost(TurnContext turnContext, CostReport costReport) {

    }

    @Override
    public String report() {
        return "RM: " + rawMaterialsList.stream().map(RawMaterial::name)
                .map(rm -> rm.substring(0, 1))
                .collect(Collectors.joining());
    }
}
