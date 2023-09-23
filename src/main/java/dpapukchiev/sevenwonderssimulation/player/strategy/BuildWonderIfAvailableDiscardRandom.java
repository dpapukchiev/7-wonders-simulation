package dpapukchiev.sevenwonderssimulation.player.strategy;

import dpapukchiev.sevenwonderssimulation.game.TurnContext;

public class BuildWonderIfAvailableDiscardRandom implements StrategyStep {
    @Override
    public Result execute(TurnContext turnContext) {
        var player = turnContext.getPlayer();
        var randomCardToDiscard = player.selectRandomCard(turnContext.getHandOfCards().getCards());
        return player
                .getWonderContext()
                .getNextWonderStage(turnContext)
                .filter(wonderStage -> wonderStage.getCost().generateCostReport(turnContext).isAffordable())
                .map(wonderStage -> Result.wonder(randomCardToDiscard))
                .orElse(Result.skip());
    }
}
