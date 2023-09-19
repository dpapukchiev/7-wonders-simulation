package dpapukchiev.v1.effects.v2;

import dpapukchiev.v1.cards.ManufacturedGood;
import dpapukchiev.v1.cards.RawMaterial;
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

    public boolean isWildcardManufacturedGood() {
        return manufacturedGoods.stream().distinct().count() > 1;
    }

    public boolean isWildcardRawMaterial() {
        return rawMaterials.stream().distinct().count() > 1;
    }
}
