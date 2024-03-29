package dpapukchiev.sevenwonderssimulation.effects;

import dpapukchiev.sevenwonderssimulation.effects.core.BaseEffect;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectTiming;
import dpapukchiev.sevenwonderssimulation.game.Turn;
import dpapukchiev.sevenwonderssimulation.player.Player;

import java.util.ArrayList;

public class WonderMultiRewardEffect extends BaseEffect {
    private final double shields;
    private final double coins;
    private final double victoryPoints;

    public WonderMultiRewardEffect(double shields, double coins, double victoryPoints) {
        this.shields = shields;
        this.coins = coins;
        this.victoryPoints = victoryPoints;
    }

    public static WonderMultiRewardEffect of(double shields, double coins, double victoryPoints) {
        return new WonderMultiRewardEffect(shields, coins, victoryPoints);
    }

    @Override
    public void scheduleRewardEvaluationAndCollection(Player player, Turn turn) {
        player.getEffectExecutionContext()
                .scheduleRewardEvaluationAndCollection(CoinRewardEffect.of(coins), EffectTiming.END_OF_TURN);

        player.getEffectExecutionContext()
                .scheduleRewardEvaluationAndCollection(VictoryPointEffect.of(victoryPoints), EffectTiming.END_OF_TURN);

        player.getEffectExecutionContext()
                .scheduleRewardEvaluationAndCollection(WarShieldsEffect.of(shields), EffectTiming.ANYTIME);

    }

    @Override
    public String report() {
        var report = new ArrayList<String>();
        if (shields > 0) {
            report.add("SH:%s ".formatted(shields));
        }
        if (coins > 0) {
            report.add("$:%s ".formatted(coins));
        }
        if (victoryPoints > 0) {
            report.add("VP:%s ".formatted(victoryPoints));
        }
        return "WM(%s)".formatted(String.join(",", report));
    }
}
