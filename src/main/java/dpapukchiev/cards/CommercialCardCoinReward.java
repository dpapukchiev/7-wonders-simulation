package dpapukchiev.cards;

import dpapukchiev.cost.FreeToPlayCost;
import dpapukchiev.effects.CoinRewardEffect;

public class CommercialCardCoinReward extends Card {
    public CommercialCardCoinReward(String name, int coins, int requiredPlayersCount) {
        super();
        this.type = CardType.COMMERCIAL;
        this.requiredPlayersCount = requiredPlayersCount;
        this.name = name;
        this.age = 1;
        this.effect = new CoinRewardEffect(coins);
        this.cost = new FreeToPlayCost();
    }
}
