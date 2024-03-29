package dpapukchiev.sevenwonderssimulation.cost;

import dpapukchiev.sevenwonderssimulation.game.TurnContext;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class FreeToPlayCost implements Cost {

    public static FreeToPlayCost newInstance() {
        return new FreeToPlayCost();
    }

    @Override
    public CostReport generateCostReport(TurnContext turnContext) {
        return CostReport.builder()
                .affordable(true)
                .build();
    }

    @Override
    public String report() {
        return "C(FREE)";
    }
}
