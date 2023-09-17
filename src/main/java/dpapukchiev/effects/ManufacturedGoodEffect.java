package dpapukchiev.effects;

import dpapukchiev.cards.ManufacturedGood;

public class ManufacturedGoodEffect extends CardEffect {
    public ManufacturedGoodEffect(
            ManufacturedGood manufacturedGood
    ) {
        super();
        this.providedManufacturedGood = manufacturedGood;
        this.effectUsageType = EffectUsageType.ANYTIME;
    }

}
