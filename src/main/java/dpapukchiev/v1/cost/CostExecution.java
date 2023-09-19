package dpapukchiev.v1.cost;

import dpapukchiev.v1.game.TurnContext;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class CostExecution {
    public static void applyCost(TurnContext turnContext, CostReport costReport) {
        var player = turnContext.getPlayer();
        var leftPlayer = player.getLeftPlayer();
        var rightPlayer = player.getRightPlayer();

        if (costReport.getToPayLeft() == 0 && costReport.getToPayRight() == 0) {
            return;
        }

        player.removeCoins(costReport.getToPayLeft() + costReport.getToPayRight() + costReport.getToPayBank());
        leftPlayer.rewardCoins(costReport.getToPayLeft());
        rightPlayer.rewardCoins(costReport.getToPayRight());

        log.info("{} {} Player {} has paid ${} coins total. ${} => L:{}, R:${} => {}, B${}",
                turnContext.getAge(),
                turnContext.getTurnCountAge(),
                player.getName(),
                costReport.getToPayLeft() + costReport.getToPayRight(),
                costReport.getToPayLeft(),
                player.getLeftPlayer().getName(),
                costReport.getToPayRight(),
                player.getRightPlayer().getName(),
                costReport.getToPayBank()
        );
    }
}
