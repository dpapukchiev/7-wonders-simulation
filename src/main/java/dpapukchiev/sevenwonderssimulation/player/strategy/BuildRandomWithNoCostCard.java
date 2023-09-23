package dpapukchiev.sevenwonderssimulation.player.strategy;

import dpapukchiev.sevenwonderssimulation.game.TurnContext;

public class BuildRandomWithNoCostCard implements StrategyStep {
    @Override
    public Result execute(TurnContext turnContext) {
        var player = turnContext.getPlayer();
        var cardsWithNoCost = turnContext.getHandOfCards().getCardsWithNoCost(turnContext);
        if (cardsWithNoCost.isEmpty()) {
            return Result.skip();
        }
        var randomCardToBuild = player.selectRandomCard(cardsWithNoCost);
        return Result.buildForFree(randomCardToBuild);
    }
}
