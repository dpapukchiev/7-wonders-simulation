package dpapukchiev.cost;

import dpapukchiev.cards.ManufacturedGood;
import dpapukchiev.game.TurnContext;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Log4j2
@AllArgsConstructor
public class ManufacturedGoodCost implements Cost {
    private final List<ManufacturedGood> manufacturedGoodList;

    @Override
    public CostReport generateCostReport(TurnContext turnContext) {
        var materialsNeeded = manufacturedGoodList.stream()
                .collect(groupingBy(ManufacturedGood::name));

        return materialsNeeded.entrySet().stream()
                .map(s -> {
                    var result = createCostReport(turnContext).apply(s);
                    return result;
                })
                .reduce(CostReport.builder().affordable(true).build(), CostReport::merge);
    }

    private static Function<Map.Entry<String, List<ManufacturedGood>>, CostReport> createCostReport(TurnContext turnContext) {
        return rm -> {
            var requiredCount = rm.getValue().size();
            var manufacturedGood = ManufacturedGood.valueOf(rm.getKey());
            var player = turnContext.getPlayer();
            var currentCount = player.getManufacturedGoodCount(manufacturedGood) +
                    player.getManufacturedGoodCountWildcard(manufacturedGood);

            if (currentCount >= requiredCount) {
                return CostReport.builder()
                        .affordable(true)
                        .resourcesIncluded(manufacturedGood.name())
                        .build();
            }

            var diff = requiredCount - currentCount;

            // TODO: get better price
            var leftCount = player.getLeftPlayer().getManufacturedGoodCount(manufacturedGood);
            var takeFromLeft = Math.min(leftCount, diff);

            var rightCount = player.getRightPlayer().getManufacturedGoodCount(manufacturedGood);
            var takeFromRight = Math.min(rightCount, diff - takeFromLeft);

            boolean hasEnoughResources = (currentCount + leftCount + rightCount) >= requiredCount;
            if (!hasEnoughResources) {
                return CostReport.builder()
                        .affordable(false)
                        .resourcesIncluded(manufacturedGood.name())
                        .build();
            }

            var priceLeft = takeFromLeft * player.getTradingPriceLeft(manufacturedGood);
            var priceRight = takeFromRight * player.getTradingPriceRight(manufacturedGood);

            boolean hasEnoughCoinsForTrade = (priceLeft + priceRight) <= player.getCoins();
            if (hasEnoughCoinsForTrade) {
                log.info("Player {} has {}/{} {}. Buying {}x{} from left and {}x{} from right.",
                        player.getName(),
                        currentCount,
                        requiredCount,
                        manufacturedGood.name(),
                        takeFromLeft,
                        player.getTradingPriceLeft(manufacturedGood),
                        takeFromRight,
                        player.getTradingPriceRight(manufacturedGood)
                );
            }
            return CostReport.builder()
                    .affordable(hasEnoughCoinsForTrade)
                    .resourcesIncluded(manufacturedGood.name())
                    .toPayLeft(priceLeft)
                    .toPayRight(priceRight)
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
        return "RM: " + manufacturedGoodList.stream().map(ManufacturedGood::name)
                .map(rm -> rm.substring(0, 1))
                .collect(Collectors.joining());
    }
}
