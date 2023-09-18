package dpapukchiev.effects;

public class PreferentialTradingEffect extends CardEffect{
    public PreferentialTradingEffect(
            PreferentialTrading options
    ) {
        super();
        this.effectUsageType = EffectUsageType.ANYTIME;
        this.preferentialTrading = options;
    }
}
