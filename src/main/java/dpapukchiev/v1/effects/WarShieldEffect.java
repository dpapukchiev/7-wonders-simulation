package dpapukchiev.v1.effects;

public class WarShieldEffect extends CardEffect {
    public WarShieldEffect(
            double shields
    ) {
        super();
        this.maxUsages = 1;
        this.effectUsageType = EffectUsageType.END_OF_AGE;
        this.shieldsAward = shields;
    }

}
