package dpapukchiev.sevenwonderssimulation.cards;

import dpapukchiev.sevenwonderssimulation.BasePlayerTest;
import dpapukchiev.sevenwonderssimulation.cost.CoinCost;
import dpapukchiev.sevenwonderssimulation.cost.ComplexResourceCost;
import dpapukchiev.sevenwonderssimulation.cost.Cost;
import dpapukchiev.sevenwonderssimulation.cost.FreeToPlayCost;
import dpapukchiev.sevenwonderssimulation.effects.CoinRewardAndVictoryPointWithModifiersEffect;
import dpapukchiev.sevenwonderssimulation.effects.CoinRewardWithModifiersEffect;
import dpapukchiev.sevenwonderssimulation.effects.GuildVictoryPointsForCardsEffect;
import dpapukchiev.sevenwonderssimulation.effects.ResourceEffect;
import dpapukchiev.sevenwonderssimulation.effects.ScienceSymbolsEffect;
import dpapukchiev.sevenwonderssimulation.effects.VictoryPointEffect;
import dpapukchiev.sevenwonderssimulation.effects.VictoryPointWithModifiersEffect;
import dpapukchiev.sevenwonderssimulation.effects.WarShieldsEffect;
import dpapukchiev.sevenwonderssimulation.effects.core.Effect;
import jsl.simulation.ModelElement;
import jsl.simulation.Simulation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

    @ParameterizedTest
    @ValueSource(ints = {3, 4, 5, 6, 7})
    void getCardsByAge(int numberOfPlayers) {
        var deck = new Deck(modelElement);
        deck.resetDeck(numberOfPlayers);
        var maxCardsPerAge = numberOfPlayers * 7;

        var result = deck.getCardsByAge();

        IntStream.rangeClosed(1, 3)
                .forEach(age -> System.out.printf("\nAge %d %d/%d %f%%\n",
                        age,
                        Optional.ofNullable(result.get(age)).orElse(List.of()).size(),
                        maxCardsPerAge,
                        Optional.ofNullable(result.get(age)).orElse(List.of()).size() * 100.0 / maxCardsPerAge
                ));
        System.out.printf("\nTotal %d/%d %f%%\n",
                result.values().stream().mapToInt(List::size).sum(),
                3 * maxCardsPerAge,
                result.values().stream().mapToInt(List::size).sum() * 100.0 / (3 * maxCardsPerAge)
        );

        assertEquals(3, result.size());
        assertTrue(result.values().stream().allMatch(list -> list.size() == maxCardsPerAge));
    }

    // AGE 1
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

    // AGE 2
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
    void getAge2Group2() {
        var deck = new Deck(modelElement);

        var result = deck.getAge2Group2();

        assertEquals(16, result.size());
        assertTrue(result.stream().allMatch(card -> card.getAge() == 2));

        assertListContains(result, 6, CardType.CIVIL);
        assertListContains(result, 10, CardType.COMMERCIAL);

        assertCardEffect(result, 6, VictoryPointEffect.class);
        assertCardEffect(result, 6, ResourceEffect.class);
        assertCardEffect(result, 4, CoinRewardWithModifiersEffect.class);
        assertListContains(result, 6, 3);
        assertListContains(result, 1, 4);
        assertListContains(result, 1, 5);
        assertListContains(result, 4, 6);
        assertListContains(result, 4, 7);

        assertListContains(result, 4, FreeToPlayCost.class);
        assertListContains(result, 12, ComplexResourceCost.class);
    }

    @Test
    void getAge2Group3() {
        var deck = new Deck(modelElement);

        var result = deck.getAge2Group3();

        assertEquals(19, result.size());
        assertTrue(result.stream().allMatch(card -> card.getAge() == 2));

        assertListContains(result, 9, CardType.MILITARY);
        assertListContains(result, 2, CardType.CIVIL);
        assertListContains(result, 8, CardType.COMMERCIAL);

        assertCardEffect(result, 2, VictoryPointEffect.class);
        assertCardEffect(result, 8, ScienceSymbolsEffect.class);
        assertCardEffect(result, 9, WarShieldsEffect.class);
        assertListContains(result, 8, 3);
        assertListContains(result, 2, 4);
        assertListContains(result, 3, 5);
        assertListContains(result, 3, 6);
        assertListContains(result, 3, 7);

        assertListContains(result, 19, ComplexResourceCost.class);
    }

    // AGE 3
    @Test
    void guildCards() {
        var deck = new Deck(modelElement);

        var result = deck.getGuildCards();

        assertEquals(10, result.size());
        assertTrue(result.stream().allMatch(card -> card.getAge() == 3));

        assertListContains(result, 10, CardType.GUILD);

        assertCardEffect(result, 8, VictoryPointWithModifiersEffect.class);
        assertCardEffect(result, 1, GuildVictoryPointsForCardsEffect.class);
        assertCardEffect(result, 1, ScienceSymbolsEffect.class);
        assertListContains(result, 10, 3);

        assertListContains(result, 10, ComplexResourceCost.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7})
    void pickRandomGuildCards(int playerCount) {
        var deck = new Deck(modelElement);

        var result = deck.getRandomSetOfGuildCards(playerCount);
        var expectedCount = playerCount + 2;

        assertEquals(expectedCount, result.size());
        assertTrue(result.stream().allMatch(card -> card.getAge() == 3));

        assertListContains(result, expectedCount, CardType.GUILD);
        assertListContains(result, expectedCount, ComplexResourceCost.class);
    }

    @Test
    void getAge3Group2() {
        var deck = new Deck(modelElement);

        var result = deck.getAge3Group2();

        assertEquals(15, result.size());
        assertTrue(result.stream().allMatch(card -> card.getAge() == 3));

        assertListContains(result, 9, CardType.CIVIL);
        assertListContains(result, 6, CardType.COMMERCIAL);

        assertCardEffect(result, 9, VictoryPointEffect.class);
        assertCardEffect(result, 6, CoinRewardAndVictoryPointWithModifiersEffect.class);
        assertListContains(result, 6, 3);
        assertListContains(result, 3, 4);
        assertListContains(result, 1, 5);
        assertListContains(result, 4, 6);
        assertListContains(result, 1, 7);

        assertListContains(result, 15, ComplexResourceCost.class);
    }

    @Test
    void getAge3Group3() {
        var deck = new Deck(modelElement);

        var result = deck.getAge3Group3();

        assertEquals(25, result.size());
        assertTrue(result.stream().allMatch(card -> card.getAge() == 3));

        assertListContains(result, 10, CardType.MILITARY);
        assertListContains(result, 3, CardType.COMMERCIAL);
        assertListContains(result, 2, CardType.CIVIL);
        assertListContains(result, 10, CardType.SCIENCE);

        assertCardEffect(result, 10, WarShieldsEffect.class);
        assertCardEffect(result, 10, ScienceSymbolsEffect.class);
        assertCardEffect(result, 2, VictoryPointEffect.class);
        assertCardEffect(result, 3, CoinRewardAndVictoryPointWithModifiersEffect.class);
        assertListContains(result, 10, 3);
        assertListContains(result, 3, 4);
        assertListContains(result, 5, 5);
        assertListContains(result, 2, 6);
        assertListContains(result, 5, 7);

        assertListContains(result, 25, ComplexResourceCost.class);
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