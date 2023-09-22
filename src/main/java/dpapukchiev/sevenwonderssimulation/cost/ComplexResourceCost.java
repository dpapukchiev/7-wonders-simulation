package dpapukchiev.sevenwonderssimulation.cost;

import dpapukchiev.sevenwonderssimulation.game.TurnContext;
import dpapukchiev.sevenwonderssimulation.resources.ManufacturedGood;
import dpapukchiev.sevenwonderssimulation.resources.RawMaterial;
import dpapukchiev.sevenwonderssimulation.reporting.Reporters;
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

    public static ComplexResourceCost of(List<ManufacturedGood> manufacturedGoodList) {
        return ComplexResourceCost.builder()
                .manufacturedGoodsList(manufacturedGoodList)
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
