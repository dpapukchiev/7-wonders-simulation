package dpapukchiev.effects.v2;

import dpapukchiev.cards.ManufacturedGood;
import dpapukchiev.cards.RawMaterial;
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
}
