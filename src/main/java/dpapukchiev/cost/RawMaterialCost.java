package dpapukchiev.cost;

import dpapukchiev.cards.RawMaterial;
import dpapukchiev.game.TurnContext;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@AllArgsConstructor
public class RawMaterialCost implements Cost {
    private final List<RawMaterial> rawMaterialsList;

    @Override
    public CostReport generateCostReport(TurnContext turnContext) {
        var materialsNeeded = rawMaterialsList.stream()
                .collect(groupingBy(RawMaterial::name));

        return materialsNeeded.entrySet().stream()
                .map(createCostReport(turnContext))
                .reduce(CostReport.builder().affordable(true).build(), CostReport::merge);
    }

    private static Function<Map.Entry<String, List<RawMaterial>>, CostReport> createCostReport(TurnContext turnContext) {
        return rm -> {
            var requiredCount = rm.getValue().size();
            var neededMaterial = RawMaterial.valueOf(rm.getKey());
            var currentCount = turnContext.getPlayer().getRawMaterialCount(neededMaterial) +
                    turnContext.getPlayer().getRawMaterialCountWildcard(neededMaterial);

            if(currentCount >= requiredCount){
                return CostReport.builder()
                        .affordable(true)
                        .resourcesIncluded(neededMaterial.name())
                        .build();
            }

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
                    .resourcesIncluded(neededMaterial.name())
                    .toPayLeft(takeFromLeft * 2)
                    .toPayRight(takeFromRight * 2)
                    .build();
        };
    }

    @Override
    public void applyCost(TurnContext turnContext, CostReport costReport) {
        var player = turnContext.getPlayer();
        var leftPlayer = player.getLeftPlayer();
        var rightPlayer = player.getRightPlayer();

        player.removeCoins(costReport.getToPayLeft() + costReport.getToPayRight());
        leftPlayer.rewardCoins(costReport.getToPayLeft());
        rightPlayer.rewardCoins(costReport.getToPayRight());
    }

    @Override
    public String report() {
        return "RM: " + rawMaterialsList.stream().map(RawMaterial::name)
                .map(rm -> rm.substring(0, 1))
                .collect(Collectors.joining());
    }
}
