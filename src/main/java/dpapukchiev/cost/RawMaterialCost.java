package dpapukchiev.cost;

import dpapukchiev.cards.RawMaterial;
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
            var player = turnContext.getPlayer();
            var currentCount = player.getRawMaterialCount(neededMaterial) +
                    player.getRawMaterialCountWildcard(neededMaterial);

            if (currentCount >= requiredCount) {
                return CostReport.builder()
                        .affordable(true)
                        .resourcesIncluded(neededMaterial.name())
                        .build();
            }

            var diff = requiredCount - currentCount;

            // TODO: get better price
            var leftCount = player.getLeftPlayer().getRawMaterialCount(neededMaterial);
            var takeFromLeft = Math.min(leftCount, diff);
            var priceLeft = takeFromLeft * player.getTradingPriceLeft(neededMaterial);

            var rightCount = player.getRightPlayer().getRawMaterialCount(neededMaterial);
            var takeFromRight = Math.min(rightCount, diff - takeFromLeft);
            var priceRight = takeFromRight * player.getTradingPriceRight(neededMaterial);

            boolean hasEnoughCoinsForTrade = (priceLeft + priceRight) <= player.getCoins();
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

        if (costReport.getToPayLeft() == 0 && costReport.getToPayRight() == 0) {
            return;
        }

        player.removeCoins(costReport.getToPayLeft() + costReport.getToPayRight());
        leftPlayer.rewardCoins(costReport.getToPayLeft());
        rightPlayer.rewardCoins(costReport.getToPayRight());

        log.info("{} {} Player {} has paid ${} coins total. ${} => {}, ${} => {}",
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
        return "RM: " + rawMaterialsList.stream().map(RawMaterial::name)
                .map(rm -> rm.substring(0, 1))
                .collect(Collectors.joining());
    }
}
