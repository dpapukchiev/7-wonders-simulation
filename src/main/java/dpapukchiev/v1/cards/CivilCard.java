package dpapukchiev.v1.cards;

import dpapukchiev.v1.cost.Cost;
import dpapukchiev.v1.cost.FreeToPlayCost;
import dpapukchiev.v1.cost.RawMaterialCost;
import dpapukchiev.v1.effects.VictoryPointEffect;

import java.util.List;

public class CivilCard extends Card {
    public CivilCard(int age, String name, int points, int requiredPlayersCount) {
        this(age, name, points, requiredPlayersCount, List.of());
    }

    public CivilCard(int age, String name, int points, int requiredPlayersCount, Cost cost) {
        super();
        this.type = CardType.CIVIL;
        this.requiredPlayersCount = requiredPlayersCount;
        this.name = name;
        this.age = age;
        this.effect = new VictoryPointEffect(points);
        this.cost = cost;
    }

    public CivilCard(int age, String name, int points, int requiredPlayersCount, List<RawMaterial> requiredMaterials) {
        this(
                age,
                name,
                points,
                requiredPlayersCount,
                requiredMaterials.isEmpty() ? new FreeToPlayCost() : new RawMaterialCost(requiredMaterials)
        );
    }
}
