package dpapukchiev.sevenwonderssimulation.player.strategy;

import dpapukchiev.sevenwonderssimulation.game.TurnContext;

public class DiscardRandom implements StrategyStep {
    @Override
    public Result execute(TurnContext turnContext) {
        var player = turnContext.getPlayer();
        var randomCardToDiscard = player.selectRandomCard(turnContext.getHandOfCards().getCards());
        return Result.discard(randomCardToDiscard);
    }
}
