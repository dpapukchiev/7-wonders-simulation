package dpapukchiev.cards;

import dpapukchiev.cost.FreeToPlayCost;
import dpapukchiev.effects.ManufacturedGoodEffect;

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
