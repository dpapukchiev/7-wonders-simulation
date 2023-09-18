package dpapukchiev.cost;

import dpapukchiev.game.TurnContext;

public interface Cost {
    CostReport generateCostReport(TurnContext turnContext);

    void applyCost(TurnContext turnContext, CostReport costReport);

    String report();
}
