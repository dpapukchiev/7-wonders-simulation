package dpapukchiev.sevenwonderssimulation.player.strategy;

import dpapukchiev.sevenwonderssimulation.game.TurnContext;

public class BuildRandomWithCostCardIfAffordable implements StrategyStep {
    @Override
    public Result execute(TurnContext turnContext) {
        var player = turnContext.getPlayer();
        var cardsWithNoCost = turnContext.getHandOfCards().getCardsWithCost(turnContext);
        if (cardsWithNoCost.isEmpty()) {
            return Result.skip();
        }
        var randomCardToBuild = player.selectRandomCard(cardsWithNoCost);
        return Result.buildWithCost(randomCardToBuild);
    }
}
