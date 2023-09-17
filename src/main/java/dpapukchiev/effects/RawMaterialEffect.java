package dpapukchiev.effects;

import dpapukchiev.cards.RawMaterial;

import java.util.List;

public class RawMaterialEffect extends CardEffect{
    public RawMaterialEffect(List<RawMaterial> providedMaterial){
        super();
        this.providedRawMaterials = providedMaterial;
    }

}
