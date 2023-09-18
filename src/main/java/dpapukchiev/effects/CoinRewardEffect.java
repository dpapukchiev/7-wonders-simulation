package dpapukchiev.effects;

public class CoinRewardEffect extends CardEffect {
    public CoinRewardEffect(
            double coinReward
    ) {
        super();
        this.effectUsageType = EffectUsageType.DIRECT;
        this.coinReward = coinReward;
        this.maxUsages = 1;
    }
}
