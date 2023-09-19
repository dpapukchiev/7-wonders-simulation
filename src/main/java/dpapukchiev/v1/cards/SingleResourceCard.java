package dpapukchiev.v1.cards;

import dpapukchiev.v1.cost.FreeToPlayCost;
import dpapukchiev.v1.effects.RawMaterialEffect;

import java.util.List;

public class SingleResourceCard extends Card {
    public SingleResourceCard(int age, String name, RawMaterial rawMaterial, int requiredPlayersCount) {
        super();
        this.type = CardType.RAW_MATERIAL;
        this.requiredPlayersCount = requiredPlayersCount;
        this.name = name;
        this.age = age;
        this.effect = new RawMaterialEffect(List.of(rawMaterial));
        this.cost = new FreeToPlayCost();
    }
}
