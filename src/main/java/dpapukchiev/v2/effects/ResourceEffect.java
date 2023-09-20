package dpapukchiev.v2.effects;

import dpapukchiev.v2.effects.core.BaseEffect;
import dpapukchiev.v2.player.Player;
import dpapukchiev.v2.resources.ManufacturedGood;
import dpapukchiev.v2.resources.RawMaterial;
import dpapukchiev.v2.resources.ResourceBundle;
import lombok.Builder;

import java.util.List;
import java.util.Optional;

@Builder
public class ResourceEffect extends BaseEffect {
    private final List<RawMaterial>      rawMaterialList;
    private final List<ManufacturedGood> manufacturedGoodList;
    
    public static ResourceEffect of(RawMaterial... rawMaterial){
        return ResourceEffect.builder()
                .rawMaterialList(List.of(rawMaterial))
                .build();
    }
    
    public static ResourceEffect of(ManufacturedGood... manufacturedGood){
        return ResourceEffect.builder()
                .manufacturedGoodList(List.of(manufacturedGood))
                .build();
    }
    
    public static ResourceEffect of(List<RawMaterial> materialList, List<ManufacturedGood> manufacturedGoodsList){
        return ResourceEffect.builder()
                .rawMaterialList(materialList)
                .manufacturedGoodList(manufacturedGoodsList)
                .build();
    }

    public Optional<ResourceBundle> getResourceBundle(Player player) {
        return Optional.of(ResourceBundle.builder()
                .rawMaterials(rawMaterialList)
                .manufacturedGoods(manufacturedGoodList)
                .build());
    }
}
