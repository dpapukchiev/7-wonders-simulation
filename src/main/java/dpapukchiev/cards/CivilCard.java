package dpapukchiev.cards;

import dpapukchiev.cost.Cost;
import dpapukchiev.cost.FreeToPlayCost;
import dpapukchiev.cost.RawMaterialCost;
import dpapukchiev.effects.VictoryPointEffect;

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
