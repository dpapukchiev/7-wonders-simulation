package dpapukchiev.cost;

import dpapukchiev.game.TurnContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CoinCost implements Cost {
    private final int cost;

    @Override
    public CostReport generateCostReport(TurnContext turnContext) {
        return CostReport.builder()
                .affordable(turnContext.getPlayer().getCoins() >= cost)
                .toPayBank(cost)
                .build();
    }

    @Override
    public void applyCost(TurnContext turnContext, CostReport costReport) {
        turnContext.getPlayer().removeCoins(costReport.getToPayBank());
    }

    @Override
    public String report() {
        return "$" + cost;
    }
}
