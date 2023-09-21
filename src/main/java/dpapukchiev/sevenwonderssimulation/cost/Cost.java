package dpapukchiev.sevenwonderssimulation.cost;

import dpapukchiev.sevenwonderssimulation.game.TurnContext;

public interface Cost {
    CostReport generateCostReport(TurnContext turnContext);

    default void applyCost(TurnContext turnContext, CostReport costReport){
        var player = turnContext.getPlayer();
        if(costReport.getToPayTotal() > 0){
            player.getVault().removeCoins(costReport.getToPayTotal());
            player.getLeftPlayer().getVault().addCoins(costReport.getToPayLeft());
            player.getRightPlayer().getVault().addCoins(costReport.getToPayRight());
        }
    }

    String report();
}
