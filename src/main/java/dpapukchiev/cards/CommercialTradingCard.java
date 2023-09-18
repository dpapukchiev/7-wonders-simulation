package dpapukchiev.cards;

import dpapukchiev.cost.FreeToPlayCost;
import dpapukchiev.effects.PreferentialTrading;
import dpapukchiev.effects.PreferentialTradingEffect;

public class CommercialTradingCard extends Card {
    public CommercialTradingCard(String name, int requiredPlayersCount, PreferentialTrading preferentialTrading) {
        super();
        this.type = CardType.COMMERCIAL;
        this.requiredPlayersCount = requiredPlayersCount;
        this.name = name;
        this.age = 1;
        this.effect = new PreferentialTradingEffect(preferentialTrading);
        this.cost = new FreeToPlayCost();
    }
}
