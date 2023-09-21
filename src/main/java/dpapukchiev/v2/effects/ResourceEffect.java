package dpapukchiev.v2.effects;

import dpapukchiev.v2.effects.core.BaseEffect;
import dpapukchiev.v2.effects.core.EffectTiming;
import dpapukchiev.v2.player.Player;
import dpapukchiev.v2.reporting.Reporters;
import dpapukchiev.v2.resources.ManufacturedGood;
import dpapukchiev.v2.resources.RawMaterial;
import dpapukchiev.v2.resources.ResourceBundle;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Builder
public class ResourceEffect extends BaseEffect {
    @Builder.Default
    private final List<RawMaterial>      rawMaterialList      = new ArrayList<>();
    @Builder.Default
    private final List<ManufacturedGood> manufacturedGoodList = new ArrayList<>();

    public static ResourceEffect of(RawMaterial... rawMaterial) {
        return ResourceEffect.builder()
                .rawMaterialList(List.of(rawMaterial))
                .build();
    }

    public static ResourceEffect of(ManufacturedGood... manufacturedGood) {
        return ResourceEffect.builder()
                .manufacturedGoodList(List.of(manufacturedGood))
                .build();
    }

    public static ResourceEffect of(List<RawMaterial> materialList, List<ManufacturedGood> manufacturedGoodsList) {
        return ResourceEffect.builder()
                .rawMaterialList(materialList)
                .manufacturedGoodList(manufacturedGoodsList)
                .build();
    }

    @Override
    public void scheduleEffect(Player player) {
        player.getEffectExecutionContext()
                .addEffect(this, EffectTiming.ANYTIME);
    }

    public Optional<ResourceBundle> getResourceBundle(Player player) {
        return Optional.of(ResourceBundle.builder()
                .rawMaterials(rawMaterialList)
                .manufacturedGoods(manufacturedGoodList)
                .build());
    }

    @Override
    public String report() {
        return Reporters.resourcesReport(rawMaterialList, manufacturedGoodList);
    }
}
