package dpapukchiev.cards;

import dpapukchiev.cost.FreeToPlayCost;
import dpapukchiev.effects.RawMaterialEffect;

import java.util.List;

public class SingleResourceCard extends Card {
    public SingleResourceCard(String name, RawMaterial rawMaterial, int requiredPlayersCount) {
        super();
        this.type = CardType.RAW_MATERIAL;
        this.requiredPlayersCount = requiredPlayersCount;
        this.name = name;
        this.age = 1;
        this.effect = new RawMaterialEffect(List.of(rawMaterial));
        this.cost = new FreeToPlayCost();
    }
}
