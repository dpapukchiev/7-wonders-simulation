package dpapukchiev.cost;

import dpapukchiev.game.TurnContext;

public class FreeToPlayCost implements Cost {
    @Override
    public boolean canBuild(TurnContext turnContext) {
        return true;
    }

    @Override
    public void applyCost(TurnContext turnContext) {

    }

    @Override
    public String report() {
        return "Free";
    }
}
