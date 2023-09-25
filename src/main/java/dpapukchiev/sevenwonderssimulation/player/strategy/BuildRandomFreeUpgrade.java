package dpapukchiev.sevenwonderssimulation.player.strategy;

import dpapukchiev.sevenwonderssimulation.cards.ProvidedResources;
import dpapukchiev.sevenwonderssimulation.game.TurnContext;

public class BuildRandomFreeUpgrade implements StrategyStep {
    private final ProvidedResources freeUpgrades = new ProvidedResources();

    @Override
    public Result execute(TurnContext turnContext) {
        var cardsToPickFrom = turnContext.getHandOfCards().getCardsWithoutAlreadyBuilt(turnContext)
                .stream()
                .filter(c -> freeUpgrades.canBuildForFree(c, turnContext.getPlayer().getVault().getBuiltCards()))
                .toList();
        if (cardsToPickFrom.isEmpty()) {
            return Result.skip();
        }
        var cardToBuild = turnContext.getPlayer().selectRandomCard(cardsToPickFrom);
        return Result.buildForFree(cardToBuild);
    }
}
