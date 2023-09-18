package dpapukchiev.cards;

import dpapukchiev.cost.CoinCost;
import dpapukchiev.effects.RawMaterialEffect;

import java.util.List;

public class DoubleResourceCard extends Card {
    public DoubleResourceCard(
            int age,
            String name,
            RawMaterial rawMaterial1,
            RawMaterial rawMaterial2,
            int requiredPlayersCount
    ) {
        super();
        this.type = CardType.RAW_MATERIAL;
        this.requiredPlayersCount = requiredPlayersCount;
        this.name = name;
        this.age = age;
        this.effect = new RawMaterialEffect(List.of(rawMaterial1, rawMaterial2));
        this.cost = new CoinCost(1);
    }
}
