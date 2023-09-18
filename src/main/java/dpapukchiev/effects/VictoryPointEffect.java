package dpapukchiev.effects;

public class VictoryPointEffect extends CardEffect {
    public VictoryPointEffect(
            double points
    ) {
        super();
        this.maxUsages = 1;
        this.effectUsageType = EffectUsageType.END_OF_GAME;
        this.pointsAward = points;
    }

}
