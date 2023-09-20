package dpapukchiev.v2.cost;

import dpapukchiev.v2.game.TurnContext;
import dpapukchiev.v2.resources.ManufacturedGood;
import dpapukchiev.v2.resources.RawMaterial;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        var report = new StringBuilder();
        report.append("C(");
        if (!rawMaterialList.isEmpty()) {
            report.append("RM:");
            report.append(rawMaterialList.stream().map(RawMaterial::name)
                    .map(name -> name.substring(0, 2))
                    .collect(Collectors.joining("-")));
        }
        if (!manufacturedGoodsList.isEmpty()) {
            report.append("MG:");
            report.append(manufacturedGoodsList.stream().map(ManufacturedGood::name)
                    .map(name -> name.substring(0, 2))
                    .collect(Collectors.joining("-")));
        }
        report.append(")");
        return report.toString();
    }
}
