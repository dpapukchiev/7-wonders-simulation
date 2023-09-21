package dpapukchiev.v2.cost;

import dpapukchiev.v2.game.TurnContext;
import dpapukchiev.v2.reporting.Reporters;
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

    public static ComplexResourceCost of(RawMaterial... rawMaterial) {
        return ComplexResourceCost.builder()
                .rawMaterialList(List.of(rawMaterial))
                .build();
    }

    public static ComplexResourceCost of(ManufacturedGood... manufacturedGood) {
        return ComplexResourceCost.builder()
                .manufacturedGoodsList(List.of(manufacturedGood))
                .build();
    }

    public static ComplexResourceCost of(RawMaterial rawMaterial, ManufacturedGood manufacturedGood) {
        return ComplexResourceCost.builder()
                .rawMaterialList(List.of(rawMaterial))
                .manufacturedGoodsList(List.of(manufacturedGood))
                .build();
    }

    @Override
    public CostReport generateCostReport(TurnContext turnContext) {
        return turnContext.getPlayer()
                .resourceContext()
                .calculateResourcesCost(rawMaterialList, manufacturedGoodsList);
    }

    @Override
    public String report() {
        return "C(%s)".formatted(Reporters.resourcesReport(rawMaterialList, manufacturedGoodsList));
    }
}
