package dpapukchiev.sevenwonderssimulation.player.strategy;

import dpapukchiev.sevenwonderssimulation.game.TurnContext;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.math3.util.Pair;

import java.util.Comparator;

import static dpapukchiev.sevenwonderssimulation.effects.core.SpecialAction.PLAY_CARD_WITHOUT_COST;

@Log4j2
public class BuildMostExpensiveUsingSpecialActionPlayWithoutCost implements StrategyStep {
    @Override
    public Result execute(TurnContext turnContext) {
        var player = turnContext.getPlayer();
        var specialAction = player.getVault().getSpecialAction(PLAY_CARD_WITHOUT_COST);
        if (specialAction.isEmpty()) {
            return Result.skip();
        }

        var cardToPlay = turnContext.getHandOfCards().getCostReportsPerCard(turnContext)
                .stream()
                .sorted(Comparator.comparingDouble(c -> c.getValue().getToPayTotal()))
                .filter(c -> !player.getVault().getBuiltCardNames().contains(c.getKey().getName().name()))
                .map(Pair::getKey)
                .findFirst();
        if (cardToPlay.isEmpty()) {
            return Result.skip();
        }

        player.log("Player %s used special action %s to build card %s".formatted(
                player.getName(), specialAction.get(), cardToPlay.get().getName())
        );
        player.getVault().useSpecialAction(PLAY_CARD_WITHOUT_COST);

        return Result.buildWithSpecialEffect(cardToPlay.get());
    }
}
