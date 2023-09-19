package dpapukchiev.v1.cost;

import dpapukchiev.v1.game.TurnContext;

public interface Cost {
    CostReport generateCostReport(TurnContext turnContext);

    void applyCost(TurnContext turnContext, CostReport costReport);

    String report();
}
