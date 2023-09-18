package dpapukchiev.cards;

import dpapukchiev.cost.Cost;
import dpapukchiev.effects.CardEffect;

public class GuildCard extends Card {

    public GuildCard(int age, String name, int requiredPlayersCount, Cost cost, CardEffect cardEffect) {
        super();
        this.type = CardType.GUILD;
        this.requiredPlayersCount = requiredPlayersCount;
        this.name = name;
        this.age = age;
        this.effect = cardEffect;
        this.cost = cost;
    }
}
