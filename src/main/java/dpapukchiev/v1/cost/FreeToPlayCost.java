package dpapukchiev.v1.cost;

import dpapukchiev.v1.game.TurnContext;

public class FreeToPlayCost implements Cost {
    @Override
    public CostReport generateCostReport(TurnContext turnContext) {
        return CostReport.builder()
                .affordable(true)
                .build();
    }

    @Override
    public void applyCost(TurnContext turnContext, CostReport costReport) {

    }

    @Override
    public String report() {
        return "Free";
    }
}
