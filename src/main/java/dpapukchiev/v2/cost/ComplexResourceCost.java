package dpapukchiev.v2.cost;

import dpapukchiev.v2.game.TurnContext;
import dpapukchiev.v2.resources.ManufacturedGood;
import dpapukchiev.v2.resources.RawMaterial;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public class ComplexResourceCost implements Cost {

    @Builder.Default
    private List<RawMaterial>      rawMaterialList       = new ArrayList<>();
    @Builder.Default
    private List<ManufacturedGood> manufacturedGoodsList = new ArrayList<>();

    @Override
    public CostReport generateCostReport(TurnContext turnContext) {
        return null;
    }

    @Override
    public void applyCost(TurnContext turnContext, CostReport costReport) {

    }

    @Override
    public String report() {
        return null;
    }
}
