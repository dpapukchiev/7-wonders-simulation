package dpapukchiev.sevenwonderssimulation.cards.templates;

import dpapukchiev.sevenwonderssimulation.cards.Card;
import dpapukchiev.sevenwonderssimulation.cards.CardName;
import dpapukchiev.sevenwonderssimulation.cards.CardType;
import dpapukchiev.sevenwonderssimulation.cost.ComplexResourceCost;
import dpapukchiev.sevenwonderssimulation.cost.FreeToPlayCost;
import dpapukchiev.sevenwonderssimulation.effects.ScienceSymbolsEffect;
import dpapukchiev.sevenwonderssimulation.resources.ManufacturedGood;
import dpapukchiev.sevenwonderssimulation.resources.RawMaterial;
import dpapukchiev.sevenwonderssimulation.resources.ScienceSymbol;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ScienceCardTemplate extends BaseCardTemplate {
    private final int age;

    public static ScienceCardTemplate create(int age) {
        return new ScienceCardTemplate(age);
    }

    public List<Card> createCards(
            CardName name,
            List<Integer> requiredPlayerCounts,
            ScienceSymbol scienceSymbol
    ) {
        return createCards(
                name,
                requiredPlayerCounts,
                List.of(),
                List.of(),
                scienceSymbol
        );
    }

    public List<Card> createCards(
            CardName name,
            List<Integer> requiredPlayerCounts,
            List<RawMaterial> rawMaterialsCost,
            List<ManufacturedGood> manufacturedGoodsCost,
            ScienceSymbol scienceSymbol
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
                                    .effect(ScienceSymbolsEffect.of(scienceSymbol))
                                    .type(CardType.SCIENCE)
                                    .freeUpgrades(List.of()) // TODO
                                    .build();
                        }
                ).toList();

    }
}
