package dpapukchiev.cards;

import dpapukchiev.cost.FreeToPlayCost;
import dpapukchiev.cost.RawMaterialCost;
import dpapukchiev.effects.VictoryPointEffect;

import java.util.List;

public class CivilCard extends Card {
    public CivilCard(String name, int points, int requiredPlayersCount) {
        this(name, points, requiredPlayersCount, List.of());
    }

    public CivilCard(String name, int points, int requiredPlayersCount, List<RawMaterial> requiredMaterials) {
        super();
        this.type = CardType.CIVIL;
        this.requiredPlayersCount = requiredPlayersCount;
        this.name = name;
        this.age = 1;
        this.effect = new VictoryPointEffect(points);
        this.cost = requiredMaterials.isEmpty() ? new FreeToPlayCost() : new RawMaterialCost(requiredMaterials);
    }
}
