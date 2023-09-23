package dpapukchiev.sevenwonderssimulation.effects;

import dpapukchiev.sevenwonderssimulation.effects.core.BaseEffect;
import dpapukchiev.sevenwonderssimulation.effects.core.Effect;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectTiming;
import dpapukchiev.sevenwonderssimulation.game.Turn;
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

    public static ResourceEffect ofRawMaterials(List<RawMaterial> rawMaterials) {
        return ResourceEffect.builder()
                .rawMaterialList(rawMaterials)
                .build();
    }
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

    public static Effect ofManufacturedGoods(List<ManufacturedGood> manufacturedGoods) {
        return ResourceEffect.builder()
                .manufacturedGoodList(manufacturedGoods)
                .build();
    }

    @Override
    public void scheduleRewardEvaluationAndCollection(Player player, Turn turn) {
        player.getEffectExecutionContext()
                .scheduleRewardEvaluationAndCollection(this, EffectTiming.ANYTIME);
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
