package dpapukchiev.v2.reporting;

import dpapukchiev.v2.resources.ManufacturedGood;
import dpapukchiev.v2.resources.RawMaterial;

import java.util.List;
import java.util.stream.Collectors;

public class Reporters {
    public static String resourcesReport(List<RawMaterial> rawMaterialList, List<ManufacturedGood> manufacturedGoodList) {
        var report = new StringBuilder();
        report.append("R(");
        if (!rawMaterialList.isEmpty()) {
            report.append("RM:");
            report.append(rawMaterialList.stream().map(RawMaterial::name)
                    .map(name -> name.substring(0, 2))
                    .collect(Collectors.joining("-")));
        }
        if (!manufacturedGoodList.isEmpty()) {
            report.append("MG:");
            report.append(manufacturedGoodList.stream().map(ManufacturedGood::name)
                    .map(name -> name.substring(0, 2))
                    .collect(Collectors.joining("-")));
        }
        report.append(")");
        return report.toString();
    }
}
