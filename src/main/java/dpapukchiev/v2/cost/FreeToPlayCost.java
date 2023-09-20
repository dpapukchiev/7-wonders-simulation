package dpapukchiev.v2.cost;

import dpapukchiev.v2.game.TurnContext;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class FreeToPlayCost implements Cost {

    @Override
    public CostReport generateCostReport(TurnContext turnContext) {
        return CostReport.builder()
                .affordable(true)
                .build();
    }

    @Override
    public String report() {
        return null;
    }
}
