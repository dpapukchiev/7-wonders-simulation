package dpapukchiev.cost;

import dpapukchiev.cards.RawMaterial;
import dpapukchiev.game.TurnContext;
import dpapukchiev.player.Player;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Log4j2
@AllArgsConstructor
public abstract class BaseResourceCost<T extends Enum<?>> implements Cost {
    protected final List<T> resourceList;

    @Override
    public CostReport generateCostReport(TurnContext turnContext) {
        var materialsNeeded = resourceList.stream()
                .collect(groupingBy(T::name));

        var finalReport = materialsNeeded.entrySet().stream()
                .map(s -> {
                    var result = createCostReport(turnContext).apply(s);
                    return result;
                })
                .reduce(CostReport.builder().affordable(true).build(), CostReport::merge);
        finalReport = finalReport.toBuilder()
                .affordable((finalReport.getToPayLeft() + finalReport.getToPayRight() <= turnContext.getPlayer().getCoins()) && finalReport.isAffordable())
                .build();
        return finalReport;
    }

    private Function<Map.Entry<String, List<T>>, CostReport> createCostReport(TurnContext turnContext) {
        return rm -> {
            var requiredCount = rm.getValue().size();
            var neededMaterial = rm.getKey();
            var player = turnContext.getPlayer();
            var currentCount = getCurrentCount(player, neededMaterial);

            if (currentCount >= requiredCount) {
                return CostReport.builder()
                        .affordable(true)
                        .resourcesIncluded(neededMaterial)
                        .build();
            }

            var diff = requiredCount - currentCount;

            var leftCount = getAvailableResourcesLeft(player, neededMaterial);
            var takeFromLeft = Math.min(leftCount, diff);

            var rightCount = getAvailableResourcesRight(player, neededMaterial);
            var takeFromRight = Math.min(rightCount, diff - takeFromLeft);

            boolean hasEnoughResources = (currentCount + leftCount + rightCount) >= requiredCount;
            if (!hasEnoughResources) {
                return CostReport.builder()
                        .affordable(false)
                        .resourcesIncluded(neededMaterial)
                        .build();
            }

            var priceLeft = takeFromLeft * getTradingPriceLeft(player, neededMaterial);
            var priceRight = takeFromRight * getTradingPriceRight(player, neededMaterial);

            boolean hasEnoughCoinsForTrade = (priceLeft + priceRight) <= player.getCoins();
            return CostReport.builder()
                    .affordable(hasEnoughCoinsForTrade)
                    .resourcesIncluded(neededMaterial)
                    .toPayLeft(priceLeft)
                    .toPayRight(priceRight)
                    .build();
        };
    }

    public abstract double getTradingPriceRight(Player player, String neededMaterial);
//    {
//        return player.getTradingPriceRight(neededMaterial);
//    }

    public abstract double getTradingPriceLeft(Player player, String neededMaterial);
//    {
//        return player.getTradingPriceLeft(neededMaterial);
//    }

    public abstract double getAvailableResourcesRight(Player player, String neededMaterial);
//    {
//        return player.getRightPlayer().getRawMaterialCount(neededMaterial);
//    }

    public abstract double getAvailableResourcesLeft(Player player, String neededMaterial);
//    {
//        return player.getLeftPlayer().getRawMaterialCount(neededMaterial);
//    }

    public abstract double getCurrentCount(Player player, String neededMaterial);
//        return player.getRawMaterialCount(neededMaterial) +
//                player.getRawMaterialCountWildcard(neededMaterial);
//    }

    @Override
    public void applyCost(TurnContext turnContext, CostReport costReport) {
        var player = turnContext.getPlayer();
        var leftPlayer = player.getLeftPlayer();
        var rightPlayer = player.getRightPlayer();

        if (costReport.getToPayLeft() == 0 && costReport.getToPayRight() == 0) {
            return;
        }

        player.removeCoins(costReport.getToPayLeft() + costReport.getToPayRight());
        leftPlayer.rewardCoins(costReport.getToPayLeft());
        rightPlayer.rewardCoins(costReport.getToPayRight());

        log.info("{} {} Player {} has paid ${} coins total. ${} => L:{}, R:${} => {}",
                turnContext.getAge(),
                turnContext.getTurnCountAge(),
                player.getName(),
                costReport.getToPayLeft() + costReport.getToPayRight(),
                costReport.getToPayLeft(),
                player.getLeftPlayer().getName(),
                costReport.getToPayRight(),
                player.getRightPlayer().getName()
        );
    }

    @Override
    public String report() {
        return "RM: " + resourceList.stream().map(T::name)
                .map(rm -> rm.substring(0, 1))
                .collect(Collectors.joining());
    }
}
