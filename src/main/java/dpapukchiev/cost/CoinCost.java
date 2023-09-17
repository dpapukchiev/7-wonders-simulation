package dpapukchiev.cost;

import dpapukchiev.game.TurnContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CoinCost implements Cost {
    private final int cost;

    @Override
    public boolean canBuild(TurnContext turnContext) {
        return turnContext.getPlayer().getCoins() >= cost;
    }
}
