package dpapukchiev.cards;

import dpapukchiev.cost.AggregateCost;
import dpapukchiev.cost.FreeToPlayCost;
import dpapukchiev.cost.ManufacturedGoodCost;
import dpapukchiev.cost.RawMaterialCost;
import dpapukchiev.effects.CoinRewardDynamicEffect;
import dpapukchiev.effects.PreferentialTrading;
import dpapukchiev.effects.RawMaterialEffect;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.simulation.ModelElement;
import jsl.utilities.random.rvariable.NormalRV;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static dpapukchiev.cards.ManufacturedGood.GLASS;
import static dpapukchiev.cards.ManufacturedGood.SCRIPTS;
import static dpapukchiev.cards.ManufacturedGood.TEXTILE;
import static dpapukchiev.cards.RawMaterial.CLAY;
import static dpapukchiev.cards.RawMaterial.METAL_ORE;
import static dpapukchiev.cards.RawMaterial.STONE;
import static dpapukchiev.cards.RawMaterial.WOOD;
import static java.util.stream.Collectors.groupingBy;
import static jsl.utilities.random.rvariable.JSLRandom.randomlySelect;

@Log4j2
public class Deck {
    private final RandomVariable cardDistribution;
    public        List<Card>     allCards       = new ArrayList<>();
    public        List<Card>     discardedCards = new ArrayList<>();

    public Deck(ModelElement parent) {
        cardDistribution = new RandomVariable(parent, new NormalRV());
    }

    public void discard(Card card) {
        discardedCards.add(card);
    }

    public void resetDeck(int playerCount) {
        allCards.clear();
        discardedCards.clear();

        // TODO: replace with hard coded cards

        addAge1();
        addAge2();

        // TODO: fix this with all cards
        var totalCardsPerAge = 5 * 7;

        var cardsPerAge = allCards.stream().collect(groupingBy(Card::getAge));
        IntStream.rangeClosed(1, 3).forEach((age) -> {
            int cardsInAge = cardsPerAge.getOrDefault(age, List.of()).size();
            int cardsToFill = totalCardsPerAge - cardsInAge;
            log.info("Filling deck {}/{} (age: {}) with {} cards", cardsInAge, totalCardsPerAge, age, cardsToFill);
            for (int i = 0; i < cardsToFill; i++) {
                allCards.add(Card.builder()
                        .name("test-card-" + i)
                        .requiredPlayersCount(3)
                        .type(CardType.RAW_MATERIAL)
                        .effect(new RawMaterialEffect(List.of()))
                        .cost(new FreeToPlayCost())
                        .age(age)
                        .build()
                );
            }
        });

        allCards = allCards.stream()
                .filter(card -> card.getRequiredPlayersCount() <= playerCount)
                .collect(Collectors.toList());
    }

    private void addAge2() {
        // RESOURCES
        allCards.add(new SingleManufacturedGoodCard(2, "Glashutte", GLASS, 4));
        allCards.add(new SingleManufacturedGoodCard(2, "Glashutte", GLASS, 3));
        allCards.add(new SingleManufacturedGoodCard(2, "Presse", SCRIPTS, 5));
        allCards.add(new SingleManufacturedGoodCard(2, "Presse", SCRIPTS, 3));
        allCards.add(new SingleManufacturedGoodCard(2, "Webstuhl", TEXTILE, 5));
        allCards.add(new SingleManufacturedGoodCard(2, "Webstuhl", TEXTILE, 3));

        allCards.add(new DoubleResourceCard(2, "Sagewerk", WOOD, WOOD, 4));
        allCards.add(new DoubleResourceCard(2, "Sagewerk", WOOD, WOOD, 3));

        allCards.add(new DoubleResourceCard(2, "Ziegelbrenneri", CLAY, CLAY, 4));
        allCards.add(new DoubleResourceCard(2, "Ziegelbrenneri", CLAY, CLAY, 3));

        allCards.add(new DoubleResourceCard(2, "Bildhauerei", STONE, STONE, 4));
        allCards.add(new DoubleResourceCard(2, "Bildhauerei", STONE, STONE, 3));

        allCards.add(new DoubleResourceCard(2, "Giesserei", METAL_ORE, METAL_ORE, 4));
        allCards.add(new DoubleResourceCard(2, "Giesserei", METAL_ORE, METAL_ORE, 3));

        // CIIVL
        allCards.add(new CivilCard(2, "Aquadukt", 5, 3, List.of(STONE, STONE, STONE)));
        allCards.add(new CivilCard(2, "Statue", 4, 3, List.of(METAL_ORE, METAL_ORE, WOOD)));
        allCards.add(new CivilCard(2, "Gericht", 4, 5, new AggregateCost(
                List.of(
                        new RawMaterialCost(List.of(CLAY, CLAY)),
                        new ManufacturedGoodCost(List.of(TEXTILE))
                )
        )));
        allCards.add(new CivilCard(2, "Gericht", 4, 3, new AggregateCost(
                List.of(
                        new RawMaterialCost(List.of(CLAY, CLAY)),
                        new ManufacturedGoodCost(List.of(TEXTILE))
                )
        )));
        allCards.add(new CivilCard(2, "Tempel", 3, 3, new AggregateCost(
                List.of(
                        new RawMaterialCost(List.of(WOOD, CLAY)),
                        new ManufacturedGoodCost(List.of(GLASS))
                )
        )));

        // SCIENCE
        allCards.add(new ScienceCard(2, "Arzneiausgabe", 4, List.of(ScienceSymbol.COMPASS), new AggregateCost(
                List.of(
                        new RawMaterialCost(List.of(METAL_ORE, METAL_ORE)),
                        new ManufacturedGoodCost(List.of(GLASS))
                )
        )));
        allCards.add(new ScienceCard(2, "Arzneiausgabe", 3, List.of(ScienceSymbol.COMPASS), new AggregateCost(
                List.of(
                        new RawMaterialCost(List.of(METAL_ORE, METAL_ORE)),
                        new ManufacturedGoodCost(List.of(GLASS))
                )
        )));
        allCards.add(new ScienceCard(2, "Bibliothek", 3, List.of(ScienceSymbol.TABLET), new AggregateCost(
                List.of(
                        new RawMaterialCost(List.of(STONE, STONE)),
                        new ManufacturedGoodCost(List.of(TEXTILE))
                )
        )));
        allCards.add(new ScienceCard(2, "Schule", 3, List.of(ScienceSymbol.TABLET), new AggregateCost(
                List.of(
                        new RawMaterialCost(List.of(WOOD)),
                        new ManufacturedGoodCost(List.of(SCRIPTS))
                )
        )));

        allCards.add(new ScienceCard(2, "Laboratorium", 5, List.of(ScienceSymbol.COGWHEEL), new AggregateCost(
                List.of(
                        new RawMaterialCost(List.of(CLAY, CLAY)),
                        new ManufacturedGoodCost(List.of(SCRIPTS))
                )
        )));
        allCards.add(new ScienceCard(2, "Laboratorium", 3, List.of(ScienceSymbol.COGWHEEL), new AggregateCost(
                List.of(
                        new RawMaterialCost(List.of(CLAY, CLAY)),
                        new ManufacturedGoodCost(List.of(SCRIPTS))
                )
        )));

        // WAR
        allCards.add(new WarCard(2, "Stalle", 2, 3, List.of(CLAY, WOOD, METAL_ORE)));
        allCards.add(new WarCard(2, "Stalle", 2, 5, List.of(CLAY, WOOD, METAL_ORE)));
        allCards.add(new WarCard(2, "Trainingsgelande", 2, 4, List.of(METAL_ORE, METAL_ORE, WOOD)));
        allCards.add(new WarCard(2, "Mauern", 2, 3, List.of(STONE, STONE, STONE)));
        allCards.add(new WarCard(2, "Schiessplatz", 2, 3, List.of(WOOD, WOOD, METAL_ORE)));

        // COMMERCIAL
        var wildCardRawMaterial = new RawMaterialEffect(
                List.of(CLAY, METAL_ORE, STONE, WOOD)
        );
        var wildCardManufacturedGood = new RawMaterialEffect(
                List.of(CLAY, METAL_ORE, STONE, WOOD)
        );
        var twoWood = new RawMaterialCost(List.of(WOOD, WOOD));
        var twoClay = new RawMaterialCost(List.of(CLAY, CLAY));

        allCards.add(new CommercialTradingCard(2, "Karawanserei", 5, wildCardRawMaterial, twoWood));
        allCards.add(new CommercialTradingCard(2, "Karawanserei", 3, wildCardRawMaterial, twoWood));
        allCards.add(new CommercialTradingCard(2, "Forum", 3, wildCardManufacturedGood, twoClay));

        allCards.add(new CommercialTradingCard(2, "Weinberg", 3, new CoinRewardDynamicEffect(1,
                CoinRewardDynamicEffect.DirectionModifier.ALL,
                CoinRewardDynamicEffect.CardTypeModifier.RAW_MATERIAL
        ), new FreeToPlayCost()));
        allCards.add(new CommercialTradingCard(2, "Basar", 4, new CoinRewardDynamicEffect(1,
                CoinRewardDynamicEffect.DirectionModifier.ALL,
                CoinRewardDynamicEffect.CardTypeModifier.MANUFACTURED_GOOD
        ), new FreeToPlayCost()));
    }

    private void addAge1() {
        // Resources and goods
        allCards.add(new SingleResourceCard(1, "Erzbergwerk", METAL_ORE, 3));
        allCards.add(new SingleResourceCard(1, "Ziegelei", CLAY, 3));
        allCards.add(new SingleResourceCard(1, "Steinbruch", STONE, 3));
        allCards.add(new SingleResourceCard(1, "Holzplatz", WOOD, 3));
        allCards.add(new DoubleResourceCard(1, "Forstwirtschaft", STONE, WOOD, 3));
        allCards.add(new DoubleResourceCard(1, "Tongrube", METAL_ORE, CLAY, 3));
        allCards.add(new SingleManufacturedGoodCard(1, "Glashutte", GLASS, 3));
        allCards.add(new SingleManufacturedGoodCard(1, "Presse", SCRIPTS, 3));
        allCards.add(new SingleManufacturedGoodCard(1, "Webstuhl", TEXTILE, 3));

        allCards.add(new SingleResourceCard(1, "Erzbergwerk", METAL_ORE, 4));
        allCards.add(new SingleResourceCard(1, "Holzplatz", WOOD, 4));
        allCards.add(new DoubleResourceCard(1, "Ausgrabungsttatte", STONE, CLAY, 4));

        allCards.add(new SingleResourceCard(1, "Ziegelei", CLAY, 5));
        allCards.add(new SingleResourceCard(1, "Steinbruch", STONE, 5));

        allCards.add(new DoubleResourceCard(1, "Waldhole", METAL_ORE, WOOD, 5));

        // CIVIL
        allCards.add(new CivilCard(1, "Theater", 2, 3));
        allCards.add(new CivilCard(1, "Altar", 2, 3));
        allCards.add(new CivilCard(1, "Altar", 2, 5));
        allCards.add(new CivilCard(1, "Bader", 3, 3, List.of(STONE)));
        allCards.add(new CivilCard(1, "Pfandhouse", 3, 4));

        // MILITARY
        allCards.add(new WarCard(1, "Wachturm", 1, 4, List.of(CLAY)));
        allCards.add(new WarCard(1, "Wachturm", 1, 3, List.of(CLAY)));
        allCards.add(new WarCard(1, "Kaserne", 1, 3, List.of(METAL_ORE)));
        allCards.add(new WarCard(1, "Kaserne", 1, 5, List.of(METAL_ORE)));
        allCards.add(new WarCard(1, "Befestigunsanlage", 1, 3, List.of(WOOD)));

        // COMMERCIAL
        allCards.add(new CommercialCardCoinReward(1, "Taverne", 5, 4));
        allCards.add(new CommercialCardCoinReward(1, "Taverne", 5, 5));
        allCards.add(new CommercialTradingCard(1, "Kontor Ost", 0, new PreferentialTrading(
                PreferentialTrading.PreferentialTradingType.RIGHT,
                List.of(),
                List.of(CLAY, METAL_ORE, STONE, WOOD)
        )));
        allCards.add(new CommercialTradingCard(1, "Kontor West", 0, new PreferentialTrading(
                PreferentialTrading.PreferentialTradingType.LEFT,
                List.of(),
                List.of(CLAY, METAL_ORE, STONE, WOOD)
        )));
        allCards.add(new CommercialTradingCard(1, "Markt", 0, new PreferentialTrading(
                PreferentialTrading.PreferentialTradingType.BOTH,
                List.of(GLASS, SCRIPTS, TEXTILE),
                List.of()
        )));

        // SCIENCE
        allCards.add(new ScienceCard(1, "Aphoteke", 3, List.of(ScienceSymbol.COMPASS), List.of(TEXTILE)));
        allCards.add(new ScienceCard(1, "Aphoteke", 5, List.of(ScienceSymbol.COMPASS), List.of(TEXTILE)));
        allCards.add(new ScienceCard(1, "Werkstat", 3, List.of(ScienceSymbol.COGWHEEL), List.of(GLASS)));
        allCards.add(new ScienceCard(1, "Skriptorium", 3, List.of(ScienceSymbol.TABLET), List.of(SCRIPTS)));
        allCards.add(new ScienceCard(1, "Skriptorium", 4, List.of(ScienceSymbol.TABLET), List.of(SCRIPTS)));
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

        return new HandOfCards(UUID.randomUUID(), cards, this);
    }
}
