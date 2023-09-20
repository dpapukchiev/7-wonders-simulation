package dpapukchiev.v2.cost;

import dpapukchiev.v2.game.TurnContext;
import dpapukchiev.v2.resources.ManufacturedGood;
import dpapukchiev.v2.resources.RawMaterial;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public class CoinCost implements Cost {

    @Builder.Default
    private double requiredCoins = 1d;

    @Override
    public CostReport generateCostReport(TurnContext turnContext) {
        boolean affordable = turnContext.getPlayer().getVault().getCoins() >= requiredCoins;
        return CostReport.builder()
                .affordable(affordable)
                .toPayBank(affordable ? requiredCoins : 0d)
                .build();
    }

    @Override
    public String report() {
        return null;
    }
}
