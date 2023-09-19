package dpapukchiev.v1.effects;

import dpapukchiev.v1.cards.ManufacturedGood;

import java.util.List;

public class ManufacturedGoodEffect extends CardEffect {
    public ManufacturedGoodEffect(
            List<ManufacturedGood> manufacturedGoods
    ) {
        super();
        this.providedManufacturedGood = manufacturedGoods;
        this.effectUsageType = EffectUsageType.ANYTIME;
        this.wildcardManufacturedGood = manufacturedGoods.stream().distinct().count() > 1;
    }

}
