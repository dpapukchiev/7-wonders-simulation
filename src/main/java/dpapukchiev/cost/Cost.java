package dpapukchiev.cost;

import dpapukchiev.game.TurnContext;

public interface Cost {
    boolean canBuild(TurnContext turnContext);

    void applyCost(TurnContext turnContext);

    String report();
}
