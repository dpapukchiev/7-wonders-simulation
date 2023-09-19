package dpapukchiev.v2.cost;

import dpapukchiev.v2.game.TurnContext;

public interface Cost {
    CostReport generateCostReport(TurnContext turnContext);

    void applyCost(TurnContext turnContext, CostReport costReport);

    String report();
}
