package dpapukchiev.effects;

import dpapukchiev.cards.RawMaterial;

import java.util.List;

public class RawMaterialEffect extends CardEffect{
    public RawMaterialEffect(
            List<RawMaterial> providedMaterial
    ){
        super();
        this.providedRawMaterials = providedMaterial;
        this.effectUsageType = EffectUsageType.ANYTIME;
        this.wildcardRawMaterial = providedMaterial.stream().distinct().count() > 1;
    }

}
