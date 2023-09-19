package dpapukchiev.v1.cards;

import dpapukchiev.v1.cost.FreeToPlayCost;
import dpapukchiev.v1.effects.ManufacturedGoodEffect;

import java.util.List;

public class ManufacturedGoodCard extends Card {
    public ManufacturedGoodCard(String name, List<ManufacturedGood> manufacturedGoods, int requiredPlayersCount) {
        super();
        this.type = CardType.MANUFACTURED_GOOD;
        this.requiredPlayersCount = requiredPlayersCount;
        this.name = name;
        this.age = 1;
        this.effect = new ManufacturedGoodEffect(manufacturedGoods);
        this.cost = new FreeToPlayCost();
    }
}
