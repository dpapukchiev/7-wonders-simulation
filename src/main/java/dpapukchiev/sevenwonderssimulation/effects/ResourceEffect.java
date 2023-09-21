package dpapukchiev.sevenwonderssimulation.effects;

import dpapukchiev.sevenwonderssimulation.effects.core.BaseEffect;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectTiming;
import dpapukchiev.sevenwonderssimulation.player.Player;
import dpapukchiev.sevenwonderssimulation.reporting.Reporters;
import dpapukchiev.sevenwonderssimulation.resources.ManufacturedGood;
import dpapukchiev.sevenwonderssimulation.resources.RawMaterial;
import dpapukchiev.sevenwonderssimulation.resources.ResourceBundle;
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

    public static ResourceEffect rawMaterialWildcard() {
        return ResourceEffect.builder()
                .rawMaterialList(RawMaterial.all())
                .build();
    }

    public static ResourceEffect manufacturedGoodWildcard() {
        return ResourceEffect.builder()
                .manufacturedGoodList(ManufacturedGood.all())
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
        return "R(%s)".formatted(Reporters.resourcesReport(rawMaterialList, manufacturedGoodList));
    }
}
