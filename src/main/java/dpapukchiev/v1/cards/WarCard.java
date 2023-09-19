package dpapukchiev.v1.cards;

import dpapukchiev.v1.cost.RawMaterialCost;
import dpapukchiev.v1.effects.WarShieldEffect;

import java.util.List;

public class WarCard extends Card {

    public WarCard(
            int age,
            String name,
            int shield,
            int requiredPlayersCount,
            List<RawMaterial> requiredMaterials
    ) {
        super();
        this.type = CardType.MILITARY;
        this.requiredPlayersCount = requiredPlayersCount;
        this.name = name;
        this.age = age;
        this.effect = new WarShieldEffect(shield);
        this.cost = new RawMaterialCost(requiredMaterials);
    }
}
