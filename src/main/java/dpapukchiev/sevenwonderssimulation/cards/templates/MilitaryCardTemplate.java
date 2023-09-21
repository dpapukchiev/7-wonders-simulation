package dpapukchiev.sevenwonderssimulation.cards.templates;

import dpapukchiev.sevenwonderssimulation.cards.Card;
import dpapukchiev.sevenwonderssimulation.cards.CardName;
import dpapukchiev.sevenwonderssimulation.cards.CardType;
import dpapukchiev.sevenwonderssimulation.effects.WarShieldsEffect;
import dpapukchiev.sevenwonderssimulation.resources.ManufacturedGood;
import dpapukchiev.sevenwonderssimulation.resources.RawMaterial;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class MilitaryCardTemplate extends BaseCardTemplate {
    private final int age;

    public static MilitaryCardTemplate create(int age) {
        return new MilitaryCardTemplate(age);
    }

    public List<Card> createCards(
            CardName name,
            List<Integer> requiredPlayerCounts,
            int shields
    ) {
        return createCards(
                name,
                requiredPlayerCounts,
                List.of(),
                List.of(),
                shields
        );
    }

    public List<Card> createCards(
            CardName name,
            List<Integer> requiredPlayerCounts,
            List<RawMaterial> rawMaterialsCost,
            List<ManufacturedGood> manufacturedGoodsCost,
            int shields
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
                                    .effect(WarShieldsEffect.of(shields))
                                    .type(CardType.MILITARY)
                                    .freeUpgrades(List.of()) // TODO
                                    .build();
                        }
                ).toList();

    }
}
