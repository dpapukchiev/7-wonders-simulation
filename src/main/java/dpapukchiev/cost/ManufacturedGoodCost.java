package dpapukchiev.cost;

import dpapukchiev.cards.ManufacturedGood;
import dpapukchiev.cards.RawMaterial;
import dpapukchiev.game.TurnContext;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@AllArgsConstructor
public class ManufacturedGoodCost implements Cost {
    private final List<ManufacturedGood> manufacturedGoodList;

    @Override
    public CostReport generateCostReport(TurnContext turnContext) {
        var materialsNeeded = manufacturedGoodList.stream()
                .collect(groupingBy(ManufacturedGood::name));

        return materialsNeeded.entrySet().stream()
                .map(createCostReport(turnContext))
                .reduce(CostReport.builder().affordable(true).build(), CostReport::merge);
    }

    private static Function<Map.Entry<String, List<ManufacturedGood>>, CostReport> createCostReport(TurnContext turnContext) {
        return rm -> {
            var requiredCount = rm.getValue().size();
            var manufacturedGood = ManufacturedGood.valueOf(rm.getKey());
            var currentCount = turnContext.getPlayer().getManufacturedGoodCount(manufacturedGood) +
                    turnContext.getPlayer().getManufacturedGoodCountWildcard(manufacturedGood);
            var diff = requiredCount - currentCount;

            var leftCount = turnContext.getPlayer().getLeftPlayer().getManufacturedGoodCount(manufacturedGood);
            var takeFromLeft = Math.min(leftCount, diff);
            var priceLeft = takeFromLeft * 2; // TODO: preferential

            var rightCount = turnContext.getPlayer().getRightPlayer().getManufacturedGoodCount(manufacturedGood);
            var takeFromRight = Math.min(rightCount, diff - takeFromLeft);
            var priceRight = takeFromRight * 2; // TODO: preferential

            boolean hasEnoughCoinsForTrade = (priceLeft + priceRight) <= turnContext.getPlayer().getCoins();
            boolean hasEnoughResources = (currentCount + leftCount + rightCount) >= requiredCount;

            boolean isAffordable = hasEnoughResources && hasEnoughCoinsForTrade;
            return CostReport.builder()
                    .affordable(isAffordable)
                    .resourcesIncluded(manufacturedGood.name())
                    .toPayLeft(takeFromLeft * 2)
                    .toPayRight(takeFromRight * 2)
                    .build();
        };
    }

    @Override
    public void applyCost(TurnContext turnContext, CostReport costReport) {

    }

    @Override
    public String report() {
        return "RM: " + manufacturedGoodList.stream().map(ManufacturedGood::name)
                .map(rm -> rm.substring(0, 1))
                .collect(Collectors.joining());
    }
}
