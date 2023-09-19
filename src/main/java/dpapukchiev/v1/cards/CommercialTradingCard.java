package dpapukchiev.v1.cards;

import dpapukchiev.v1.cost.Cost;
import dpapukchiev.v1.cost.FreeToPlayCost;
import dpapukchiev.v1.effects.CardEffect;
import dpapukchiev.v1.effects.PreferentialTrading;
import dpapukchiev.v1.effects.PreferentialTradingEffect;

public class CommercialTradingCard extends Card {
    public CommercialTradingCard(
            int age,
            String name,
            int requiredPlayersCount,
            PreferentialTrading preferentialTrading
    ) {
        super();
        this.type = CardType.COMMERCIAL;
        this.requiredPlayersCount = requiredPlayersCount;
        this.name = name;
        this.age = age;
        this.effect = new PreferentialTradingEffect(preferentialTrading);
        this.cost = new FreeToPlayCost();
    }

    public CommercialTradingCard(
            int age,
            String name,
            int requiredPlayersCount,
            CardEffect effect,
            Cost cost
    ) {
        super();
        this.type = CardType.COMMERCIAL;
        this.requiredPlayersCount = requiredPlayersCount;
        this.name = name;
        this.age = age;
        this.effect = effect;
        this.cost = cost;
    }
}
