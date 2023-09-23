package dpapukchiev.sevenwonderssimulation.cards.templates;

import dpapukchiev.sevenwonderssimulation.cards.Card;
import dpapukchiev.sevenwonderssimulation.cards.CardName;
import dpapukchiev.sevenwonderssimulation.cards.CardType;
import dpapukchiev.sevenwonderssimulation.cost.ComplexResourceCost;
import dpapukchiev.sevenwonderssimulation.cost.FreeToPlayCost;
import dpapukchiev.sevenwonderssimulation.effects.VictoryPointEffect;
import dpapukchiev.sevenwonderssimulation.resources.ManufacturedGood;
import dpapukchiev.sevenwonderssimulation.resources.RawMaterial;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class CivilCardTemplate extends BaseCardTemplate{
    private final int age;

    public static CivilCardTemplate create(int age) {
        return new CivilCardTemplate(age);
    }

    public List<Card> createCards(
            CardName name,
            List<Integer> requiredPlayerCounts,
            int victoryPoints
    ) {
        return createCards(
                name,
                requiredPlayerCounts,
                List.of(),
                List.of(),
                victoryPoints
        );
    }

    public List<Card> createCards(
            CardName name,
            List<Integer> requiredPlayerCounts,
            List<RawMaterial> rawMaterialsCost,
            List<ManufacturedGood> manufacturedGoodsCost,
            int victoryPoints
    ) {
        return requiredPlayerCounts.stream()
                .map(requiredPlayerCount ->
                        {
                            var cost = createCost(rawMaterialsCost, manufacturedGoodsCost);
                            return Card.builder()
                                    .name(name)
                                    .age(age)
                                    .requiredPlayersCount(requiredPlayerCount)
                                    .cost(cost)
                                    .effect(VictoryPointEffect.of(victoryPoints))
                                    .type(CardType.CIVIL)
                                    .build();
                        }
                ).toList();

    }
}
