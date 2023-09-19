package dpapukchiev.v1.cost;

import dpapukchiev.v1.game.TurnContext;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@AllArgsConstructor
public class AggregateCost implements Cost {
    private final List<Cost> innerCosts;

    @Override
    public CostReport generateCostReport(TurnContext turnContext) {
        var finalReport = innerCosts.stream()
                .map(cost -> {
                    var result = cost.generateCostReport(turnContext);
                    return result;
                })
                .reduce(CostReport.builder().affordable(true).build(), CostReport::merge);

        finalReport = finalReport.toBuilder()
                .affordable((finalReport.getToPayLeft() + finalReport.getToPayRight() + finalReport.getToPayBank() <= turnContext.getPlayer().getCoins()) &&
                        finalReport.isAffordable())
                .build();
        return finalReport;
    }

    @Override
    public void applyCost(TurnContext turnContext, CostReport costReport) {
        CostExecution.applyCost(turnContext, costReport);
    }

    @Override
    public String report() {
        return innerCosts.stream().map(Cost::report).collect(Collectors.joining(", "));
    }
}
