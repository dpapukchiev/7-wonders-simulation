package dpapukchiev.sevenwonderssimulation.cost;

import dpapukchiev.sevenwonderssimulation.game.TurnContext;
import lombok.Builder;

@Builder
public class CoinCost implements Cost {

    @Builder.Default
    private double requiredCoins = 1d;

    public static CoinCost of(double requiredCoins){
        return CoinCost.builder()
                .requiredCoins(requiredCoins)
                .build();
    }

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
        return "C($%s)".formatted(requiredCoins);
    }
}
