package dpapukchiev.v1.cards;

import dpapukchiev.v1.cost.FreeToPlayCost;
import dpapukchiev.v1.effects.ManufacturedGoodEffect;

import java.util.List;

public class SingleManufacturedGoodCard extends Card {
    public SingleManufacturedGoodCard(int age, String name, ManufacturedGood manufacturedGood, int requiredPlayersCount) {
        super();
        this.type = CardType.MANUFACTURED_GOOD;
        this.requiredPlayersCount = requiredPlayersCount;
        this.name = name;
        this.age = age;
        this.effect = new ManufacturedGoodEffect(List.of(manufacturedGood));
        this.cost = new FreeToPlayCost();
    }
}