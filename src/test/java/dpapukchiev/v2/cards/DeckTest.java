package dpapukchiev.v2.cards;

import dpapukchiev.v2.BasePlayerTest;
import dpapukchiev.v2.cost.CoinCost;
import dpapukchiev.v2.cost.ComplexResourceCost;
import dpapukchiev.v2.cost.Cost;
import dpapukchiev.v2.cost.FreeToPlayCost;
import dpapukchiev.v2.effects.ScienceSymbolsEffect;
import dpapukchiev.v2.effects.WarShieldsEffect;
import dpapukchiev.v2.effects.core.Effect;
import dpapukchiev.v2.effects.ResourceEffect;
import dpapukchiev.v2.effects.VictoryPointEffect;
import jsl.simulation.ModelElement;
import jsl.simulation.Simulation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeckTest extends BasePlayerTest {
    private ModelElement modelElement;

    @BeforeEach
    void setUp() {
        modelElement = new Simulation().getModel();
    }

    @Test
    void getCardsByAge() {
        var deck = new Deck(modelElement);
        deck.resetDeck();

        var result = deck.getCardsByAge();

        assertEquals(2, result.size());
        IntStream.rangeClosed(1, 3)
                .forEach(age -> System.out.printf("\nAge %d %d/49 %f%%\n",
                        age,
                        Optional.ofNullable(result.get(age)).orElse(List.of()).size(),
                        Optional.ofNullable(result.get(age)).orElse(List.of()).size() * 100.0 / 49
                ));
        System.out.printf("\nTotal %d/%d %f%%\n",
                result.values().stream().mapToInt(List::size).sum(),
                3 * 49,
                result.values().stream().mapToInt(List::size).sum() * 100.0 / (3 * 49)
        );
    }

    @Test
    void getAge1Group1() {
        var deck = new Deck(modelElement);

        var result = deck.getAge1Group1();

        assertEquals(20, result.size());
        assertTrue(result.stream().allMatch(card -> card.getAge() == 1));

        assertListContains(result, 6, CardType.MANUFACTURED_GOOD);
        assertListContains(result, 14, CardType.RAW_MATERIAL);

        assertCardEffect(result, ResourceEffect.class);
        assertListContains(result, 9, 3);
        assertListContains(result, 3, 4);
        assertListContains(result, 3, 5);
        assertListContains(result, 5, 6);

        assertListContains(result, 14, FreeToPlayCost.class);
        assertListContains(result, 6, CoinCost.class);
    }

    @Test
    void getAge2Group1() {
        var deck = new Deck(modelElement);

        var result = deck.getAge2Group1();

        assertEquals(14, result.size());
        assertTrue(result.stream().allMatch(card -> card.getAge() == 2));

        assertListContains(result, 6, CardType.MANUFACTURED_GOOD);
        assertListContains(result, 8, CardType.RAW_MATERIAL);

        assertCardEffect(result, ResourceEffect.class);
        assertListContains(result, 7, 3);
        assertListContains(result, 4, 4);
        assertListContains(result, 3, 5);
        assertListContains(result, 0, 6);

        assertListContains(result, 6, FreeToPlayCost.class);
        assertListContains(result, 8, CoinCost.class);
    }

    @Test
    void getAge1Group2() {
        var deck = new Deck(modelElement);

        var result = deck.getAge1Group2();

        assertEquals(17, result.size());
        assertTrue(result.stream().allMatch(card -> card.getAge() == 1));

        assertListContains(result, 8, CardType.CIVIL);
        assertListContains(result, 9, CardType.COMMERCIAL);

        assertCardEffect(result, 8, VictoryPointEffect.class);
        assertListContains(result, 6, 3);
        assertListContains(result, 2, 4);
        assertListContains(result, 2, 5);
        assertListContains(result, 2, 6);
        assertListContains(result, 5, 7);

        assertListContains(result, 15, FreeToPlayCost.class);
        assertListContains(result, 2, ComplexResourceCost.class);
    }

    @Test
    void getAge1Group3() {
        var deck = new Deck(modelElement);

        var result = deck.getAge1Group3();

        assertEquals(12, result.size());
        assertTrue(result.stream().allMatch(card -> card.getAge() == 1));

        assertListContains(result, 6, CardType.MILITARY);
        assertListContains(result, 6, CardType.SCIENCE);

        assertCardEffect(result, 6, WarShieldsEffect.class);
        assertCardEffect(result, 6, ScienceSymbolsEffect.class);
        assertListContains(result, 6, 3);
        assertListContains(result, 2, 4);
        assertListContains(result, 2, 5);
        assertListContains(result, 0, 6);
        assertListContains(result, 2, 7);

        assertListContains(result, 12, ComplexResourceCost.class);
    }

    private void assertListContains(List<Card> result, int expected, CardType cardType) {
        assertEquals(expected, result.stream().filter(card -> card.getType() == cardType).count());
    }

    private void assertListContains(List<Card> result, int expected, Class<? extends Cost> costClass) {
        assertEquals(expected, result.stream().filter(card -> card.getCost().getClass() == costClass).count());
    }

    private void assertListContains(List<Card> result, int expected, int minPlayerCount) {
        assertEquals(expected, result.stream().filter(card -> card.getRequiredPlayersCount() == minPlayerCount).count());
    }

    private void assertCardEffect(List<Card> result, Class<? extends Effect> effectClass) {
        assertTrue(result.stream()
                .allMatch(card -> card.getEffect().getClass().equals(effectClass)));
    }

    private void assertCardEffect(List<Card> result, int expectedCount, Class<? extends Effect> effectClass) {
        assertEquals(expectedCount, result.stream()
                .filter(card -> card.getEffect().getClass().equals(effectClass))
                .count());
    }
}