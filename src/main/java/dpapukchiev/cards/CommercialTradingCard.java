package dpapukchiev.cards;

import dpapukchiev.cost.Cost;
import dpapukchiev.cost.FreeToPlayCost;
import dpapukchiev.effects.CardEffect;
import dpapukchiev.effects.PreferentialTrading;
import dpapukchiev.effects.PreferentialTradingEffect;

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
