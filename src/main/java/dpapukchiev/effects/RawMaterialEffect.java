package dpapukchiev.effects;

import dpapukchiev.cards.RawMaterial;

import java.util.List;

public class RawMaterialEffect extends CardEffect{
    public RawMaterialEffect(
            List<RawMaterial> providedMaterial,
            boolean wildcardRawMaterial
    ){
        super();
        this.providedRawMaterials = providedMaterial;
        this.effectUsageType = EffectUsageType.ANYTIME;
        this.wildcardRawMaterial = wildcardRawMaterial;
    }

}
