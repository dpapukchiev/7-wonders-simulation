package dpapukchiev.sevenwonderssimulation.cards.templates;

import dpapukchiev.sevenwonderssimulation.cards.Card;
import dpapukchiev.sevenwonderssimulation.cards.CardName;
import dpapukchiev.sevenwonderssimulation.cards.CardType;
import dpapukchiev.sevenwonderssimulation.effects.core.Effect;
import dpapukchiev.sevenwonderssimulation.resources.ManufacturedGood;
import dpapukchiev.sevenwonderssimulation.resources.RawMaterial;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class CommercialCardTemplate extends BaseCardTemplate {
    private final int age;

    public static CommercialCardTemplate create(int age) {
        return new CommercialCardTemplate(age);
    }

    public List<Card> createCards(
            CardName name,
            List<Integer> requiredPlayerCounts,
            List<RawMaterial> rawMaterialsCost,
            List<ManufacturedGood> manufacturedGoodsCost,
            Effect effect
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
                                    .effect(effect)
                                    .type(CardType.COMMERCIAL)
                                    .build();
                        }
                ).toList();

    }
}
