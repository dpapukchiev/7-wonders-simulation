package dpapukchiev.effects;

import dpapukchiev.cards.ManufacturedGood;
import dpapukchiev.cards.RawMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public record PreferentialTrading(
        PreferentialTradingType type,
        List<ManufacturedGood> manufacturedGoods,
        List<RawMaterial> rawMaterials
) {
    public enum PreferentialTradingType {
        LEFT,
        RIGHT,
        BOTH
    }

    public String report() {
        var report = new ArrayList<String>();
        if (!manufacturedGoods.isEmpty()) {
            report.add("MG: " + manufacturedGoods.stream()
                    .map(ManufacturedGood::name)
                    .map(n -> n.substring(0, 2))
                    .collect(Collectors.joining("-")));
        }
        if (!rawMaterials.isEmpty()) {
            report.add("RM: " + rawMaterials.stream()
                    .map(RawMaterial::name)
                    .map(n -> n.substring(0, 2))
                    .collect(Collectors.joining("-")));
        }
        return String.format("%s[%s]",
                type,
                String.join(" ", report)
        );
    }
}
