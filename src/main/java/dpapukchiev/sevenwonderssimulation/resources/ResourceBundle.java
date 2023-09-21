package dpapukchiev.sevenwonderssimulation.resources;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class ResourceBundle {
    @Builder.Default
    private List<ManufacturedGood> manufacturedGoods = new ArrayList<>();
    @Builder.Default
    private List<RawMaterial>      rawMaterials      = new ArrayList<>();
    @Builder.Default
    private List<ScienceSymbol>    scienceSymbols    = new ArrayList<>();

    public boolean isWildcardManufacturedGood() {
        return manufacturedGoods.stream().distinct().count() > 1;
    }

    public boolean isWildcardRawMaterial() {
        return rawMaterials.stream().distinct().count() > 1;
    }

    public boolean isWildcardScienceSymbol() {
        return scienceSymbols.stream().distinct().count() > 1;
    }
}
