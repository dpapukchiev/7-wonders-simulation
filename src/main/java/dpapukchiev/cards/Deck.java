package dpapukchiev.cards;

import dpapukchiev.cost.CoinCost;
import dpapukchiev.effects.RawMaterialEffect;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.simulation.ModelElement;
import jsl.utilities.random.rvariable.NormalRV;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.groupingBy;
import static jsl.utilities.random.rvariable.JSLRandom.randomlySelect;

public class Deck {
    private final RandomVariable cardDistribution;
    public List<Card> allCards = new ArrayList<>();
    public List<Card> discardedCards = new ArrayList<>();

    public Deck(ModelElement parent) {
        cardDistribution = new RandomVariable(parent, new NormalRV());
    }

    public void resetDeck(int playerCount) {
        allCards.clear();
        discardedCards.clear();

        // TODO: replace with hard coded cards

        // Resources
        allCards.add(new SingleResourceCard("Erzbergwerk", RawMaterial.METAL_ORE, 3));
        allCards.add(new SingleResourceCard("Ziegelei", RawMaterial.CLAY, 3));
        allCards.add(new SingleResourceCard("Steinbruch", RawMaterial.STONE, 3));
        allCards.add(new SingleResourceCard("Holzplatz", RawMaterial.WOOD, 3));

        allCards.add(new SingleResourceCard("Erzbergwerk", RawMaterial.METAL_ORE, 4));
        allCards.add(new SingleResourceCard("Holzplatz", RawMaterial.WOOD, 4));

        allCards.add(new SingleResourceCard("Ziegelei", RawMaterial.CLAY, 5));
        allCards.add(new SingleResourceCard("Steinbruch", RawMaterial.STONE, 5));

        allCards.add(new DoubleResourceCard("Forstwirtschaft", RawMaterial.STONE, RawMaterial.WOOD, 3));
        allCards.add(new DoubleResourceCard("Waldhole", RawMaterial.METAL_ORE, RawMaterial.WOOD, 5));
        allCards.add(new DoubleResourceCard("Tongrube", RawMaterial.METAL_ORE, RawMaterial.CLAY, 3));
        allCards.add(new DoubleResourceCard("Ausgrabungsttatte", RawMaterial.STONE, RawMaterial.CLAY, 4));

        allCards.add(new SingleManufacturedGoodCard("Glashutte", ManufacturedGood.GLASS, 3));
        allCards.add(new SingleManufacturedGoodCard("Presse", ManufacturedGood.SCRIPTS, 3));
        allCards.add(new SingleManufacturedGoodCard("Webstuhl", ManufacturedGood.TEXTILE, 3));

        // CIVIL
        allCards.add(new CivilCard("Theater", 2, 3));
        allCards.add(new CivilCard("Altar", 2, 3));
        allCards.add(new CivilCard("Altar", 2, 5));
        allCards.add(new CivilCard("Bader", 3, 3, List.of(RawMaterial.STONE)));
        allCards.add(new CivilCard("Pfandhouse", 3, 4));
        allCards.add(new CivilCard("Webstuhl", 2, 3));

        // MILITARY
        allCards.add(new WarCard("Wachturm", 1, 4, List.of(RawMaterial.CLAY)));
        allCards.add(new WarCard("Wachturm", 1, 3, List.of(RawMaterial.CLAY)));
        allCards.add(new WarCard("Kaserne", 1, 3, List.of(RawMaterial.METAL_ORE)));
        allCards.add(new WarCard("Kaserne", 1, 5, List.of(RawMaterial.METAL_ORE)));
        allCards.add(new WarCard("Befestigunsanlage", 1, 3, List.of(RawMaterial.WOOD)));

        var totalCardsPerAge = 7 * 7;

        var cardsPerAge = allCards.stream().collect(groupingBy(Card::getAge));
        IntStream.rangeClosed(1, 3).forEach((age) -> {
            for (int i = 0; i < totalCardsPerAge - cardsPerAge.getOrDefault(age, List.of()).size(); i++) {
                allCards.add(Card.builder()
                        .name("test-card-" + i)
                        .requiredPlayersCount(3)
                        .type(CardType.RAW_MATERIAL)
                        .effect(new RawMaterialEffect(List.of(RawMaterial.STONE), false))
                        .cost(new CoinCost(10))
                        .age(age)
                        .build()
                );
            }
        });


        allCards = allCards.stream()
                .filter(card -> card.getRequiredPlayersCount() >= playerCount)
                .collect(Collectors.toList());
    }

    public HandOfCards prepareHandOfCards(int age) {
        var cards = new ArrayList<Card>();
        var availableCards = allCards.stream()
                .filter(card -> card.getAge() == age)
                .collect(Collectors.toList());

        for (int i = 0; i < 7; i++) {
            var card = randomlySelect(availableCards, cardDistribution.getStreamNumber());
            cards.add(card);
            availableCards.remove(card);
            allCards.remove(card);
        }


        return new HandOfCards(UUID.randomUUID(), cards);
    }
}
