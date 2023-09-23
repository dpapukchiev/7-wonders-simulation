package dpapukchiev.sevenwonderssimulation.cards;

import dpapukchiev.sevenwonderssimulation.cards.templates.CivilCardTemplate;
import dpapukchiev.sevenwonderssimulation.cards.templates.CommercialCardTemplate;
import dpapukchiev.sevenwonderssimulation.cards.templates.GuildCardTemplate;
import dpapukchiev.sevenwonderssimulation.cards.templates.MilitaryCardTemplate;
import dpapukchiev.sevenwonderssimulation.cards.templates.ScienceCardTemplate;
import dpapukchiev.sevenwonderssimulation.cost.CoinCost;
import dpapukchiev.sevenwonderssimulation.cost.ComplexResourceCost;
import dpapukchiev.sevenwonderssimulation.cost.FreeToPlayCost;
import dpapukchiev.sevenwonderssimulation.effects.CoinRewardAndVictoryPointWithModifiersEffect;
import dpapukchiev.sevenwonderssimulation.effects.CoinRewardEffect;
import dpapukchiev.sevenwonderssimulation.effects.CoinRewardWithModifiersEffect;
import dpapukchiev.sevenwonderssimulation.effects.PreferentialTradingEffect;
import dpapukchiev.sevenwonderssimulation.effects.ResourceEffect;
import dpapukchiev.sevenwonderssimulation.effects.ScienceSymbolsEffect;
import dpapukchiev.sevenwonderssimulation.effects.VictoryPointEffect;
import dpapukchiev.sevenwonderssimulation.effects.WarShieldsEffect;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectDirectionConstraint;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectMultiplierType;
import dpapukchiev.sevenwonderssimulation.effects.core.PreferentialTradingContract;
import dpapukchiev.sevenwonderssimulation.resources.ManufacturedGood;
import dpapukchiev.sevenwonderssimulation.resources.RawMaterial;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.simulation.ModelElement;
import jsl.utilities.random.rvariable.NormalRV;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static dpapukchiev.sevenwonderssimulation.effects.core.EffectDirectionConstraint.SELF;
import static dpapukchiev.sevenwonderssimulation.effects.core.EffectMultiplierType.CIVIL_CARD;
import static dpapukchiev.sevenwonderssimulation.effects.core.EffectMultiplierType.COMMERCIAL_CARD;
import static dpapukchiev.sevenwonderssimulation.effects.core.EffectMultiplierType.MANUFACTURED_GOOD_CARD;
import static dpapukchiev.sevenwonderssimulation.effects.core.EffectMultiplierType.MILITARY_CARD;
import static dpapukchiev.sevenwonderssimulation.effects.core.EffectMultiplierType.RAW_MATERIAL_CARD;
import static dpapukchiev.sevenwonderssimulation.effects.core.EffectMultiplierType.SCIENCE_CARD;
import static dpapukchiev.sevenwonderssimulation.effects.core.EffectMultiplierType.WAR_LOSS;
import static dpapukchiev.sevenwonderssimulation.effects.core.EffectMultiplierType.WONDER_STAGE;
import static dpapukchiev.sevenwonderssimulation.resources.ManufacturedGood.GLASS;
import static dpapukchiev.sevenwonderssimulation.resources.ManufacturedGood.SCRIPTS;
import static dpapukchiev.sevenwonderssimulation.resources.ManufacturedGood.TEXTILE;
import static dpapukchiev.sevenwonderssimulation.resources.RawMaterial.CLAY;
import static dpapukchiev.sevenwonderssimulation.resources.RawMaterial.METAL_ORE;
import static dpapukchiev.sevenwonderssimulation.resources.RawMaterial.STONE;
import static dpapukchiev.sevenwonderssimulation.resources.RawMaterial.WOOD;
import static dpapukchiev.sevenwonderssimulation.resources.ScienceSymbol.COGWHEEL;
import static dpapukchiev.sevenwonderssimulation.resources.ScienceSymbol.COMPASS;
import static dpapukchiev.sevenwonderssimulation.resources.ScienceSymbol.TABLET;
import static jsl.utilities.random.rvariable.JSLRandom.randomlySelect;

@Log4j2
@Getter
@Builder
@AllArgsConstructor
public class Deck {
    @Builder.Default
    private List<Card> discardedCards = new ArrayList<>();
    @Builder.Default
    private List<Card> allCards       = new ArrayList<>();

    private final RandomVariable cardDistribution;

    public Deck(ModelElement parent) {
        cardDistribution = new RandomVariable(parent, new NormalRV());
        allCards = new ArrayList<>();
        discardedCards = new ArrayList<>();
    }

    public void discard(Card card) {
        discardedCards.add(card);
    }

    public void resetDeck(int numberOfPlayers) {
        discardedCards.clear();
        allCards.clear();

        allCards.addAll(getAge1Group1());
        allCards.addAll(getAge1Group2());
        allCards.addAll(getAge1Group3());

        allCards.addAll(getAge2Group1());
        allCards.addAll(getAge2Group2());
        allCards.addAll(getAge2Group3());

        var randomSetOfGuildCards = getRandomSetOfGuildCards(numberOfPlayers);
        log.info("Random set of guild cards: {}", randomSetOfGuildCards.stream()
                .map(Card::getName)
                .map(Enum::name)
                .collect(Collectors.joining(", ")));

        allCards.addAll(randomSetOfGuildCards);
        allCards.addAll(getAge3Group2());
        allCards.addAll(getAge3Group3());

        allCards = new ArrayList<>(allCards.stream()
                .filter(card -> card.getRequiredPlayersCount() <= numberOfPlayers)
                .toList());
    }

    public List<Card> getRandomSetOfGuildCards(int playerCount) {
        var guildCards = getGuildCards();
        var cardsToReturn = new ArrayList<Card>();
        for (int i = 0; i < playerCount + 2; i++) {
            var selected = randomlySelect(guildCards, cardDistribution.getRandomNumberStream());
            cardsToReturn.add(selected);
            guildCards.remove(selected);
        }
        return cardsToReturn;
    }

    public Map<Integer, List<Card>> getCardsByAge() {
        return allCards.stream()
                .collect(Collectors.groupingBy(Card::getAge));
    }

    // AGE 1
    public List<Card> getAge1Group1() {
        var rawMaterial = CardType.RAW_MATERIAL;
        var manufacturedGood = CardType.MANUFACTURED_GOOD;
        var age = 1;

        return List.of(
                // RAW MATERIALS
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(ResourceEffect.of(RawMaterial.WOOD))
                        .name(CardName.HOLZPLATZ)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(4)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(ResourceEffect.of(RawMaterial.WOOD))
                        .name(CardName.HOLZPLATZ)
                        .build(),

                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(ResourceEffect.of(RawMaterial.STONE))
                        .name(CardName.STEINBRUCH)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(5)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(ResourceEffect.of(RawMaterial.STONE))
                        .name(CardName.STEINBRUCH)
                        .build(),

                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(ResourceEffect.of(CLAY))
                        .name(CardName.ZIEGELEI)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(5)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(ResourceEffect.of(CLAY))
                        .name(CardName.ZIEGELEI)
                        .build(),

                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(ResourceEffect.of(RawMaterial.METAL_ORE))
                        .name(CardName.ERZBERGWERK)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(4)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(ResourceEffect.of(RawMaterial.METAL_ORE))
                        .name(CardName.ERZBERGWERK)
                        .build(),

                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(6)
                        .cost(CoinCost.of(1))
                        .effect(ResourceEffect.of(RawMaterial.WOOD, CLAY))
                        .name(CardName.BAUMSCHULE)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(4)
                        .cost(CoinCost.of(1))
                        .effect(ResourceEffect.of(RawMaterial.STONE, CLAY))
                        .name(CardName.AUSGRABUNGSTTATTE)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(CoinCost.of(1))
                        .effect(ResourceEffect.of(RawMaterial.METAL_ORE, CLAY))
                        .name(CardName.TONGRUBE)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(CoinCost.of(1))
                        .effect(ResourceEffect.of(RawMaterial.STONE, RawMaterial.WOOD))
                        .name(CardName.FORSTWIRTSCHAFT)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(5)
                        .cost(CoinCost.of(1))
                        .effect(ResourceEffect.of(RawMaterial.METAL_ORE, RawMaterial.WOOD))
                        .name(CardName.WALDHOLE)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(6)
                        .cost(CoinCost.of(1))
                        .effect(ResourceEffect.of(RawMaterial.METAL_ORE, RawMaterial.STONE))
                        .name(CardName.MINE)
                        .build(),

                // MANUFACTURED GOODS
                Card.builder()
                        .type(manufacturedGood)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(ResourceEffect.of(ManufacturedGood.TEXTILE))
                        .name(CardName.WEBSTUHL)
                        .build(),
                Card.builder()
                        .type(manufacturedGood)
                        .age(age)
                        .requiredPlayersCount(6)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(ResourceEffect.of(ManufacturedGood.TEXTILE))
                        .name(CardName.WEBSTUHL)
                        .build(),
                Card.builder()
                        .type(manufacturedGood)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(ResourceEffect.of(GLASS))
                        .name(CardName.GLASHUTTE)
                        .build(),
                Card.builder()
                        .type(manufacturedGood)
                        .age(age)
                        .requiredPlayersCount(6)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(ResourceEffect.of(GLASS))
                        .name(CardName.GLASHUTTE)
                        .build(),
                Card.builder()
                        .type(manufacturedGood)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(ResourceEffect.of(ManufacturedGood.SCRIPTS))
                        .name(CardName.PRESSE)
                        .build(),
                Card.builder()
                        .type(manufacturedGood)
                        .age(age)
                        .requiredPlayersCount(6)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(ResourceEffect.of(ManufacturedGood.SCRIPTS))
                        .name(CardName.PRESSE)
                        .build()
        );
    }

    public List<Card> getAge1Group2() {
        var civil = CardType.CIVIL;
        var commercial = CardType.COMMERCIAL;
        var age = 1;

        return List.of(
                // CIVIL
                Card.builder()
                        .type(civil)
                        .age(age)
                        .requiredPlayersCount(4)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(VictoryPointEffect.of(3))
                        .name(CardName.PFANDHOUSE)
                        .build(),
                Card.builder()
                        .type(civil)
                        .age(age)
                        .requiredPlayersCount(7)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(VictoryPointEffect.of(3))
                        .name(CardName.PFANDHOUSE)
                        .build(),

                Card.builder()
                        .type(civil)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(ComplexResourceCost.of(RawMaterial.STONE))
                        .effect(VictoryPointEffect.of(3))
                        .name(CardName.BADER)
                        .build(),
                Card.builder()
                        .type(civil)
                        .age(age)
                        .requiredPlayersCount(7)
                        .cost(ComplexResourceCost.of(RawMaterial.STONE))
                        .effect(VictoryPointEffect.of(3))
                        .name(CardName.BADER)
                        .build(),

                Card.builder()
                        .type(civil)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(VictoryPointEffect.of(2))
                        .name(CardName.ALTAR)
                        .build(),
                Card.builder()
                        .type(civil)
                        .age(age)
                        .requiredPlayersCount(5)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(VictoryPointEffect.of(2))
                        .name(CardName.ALTAR)
                        .build(),

                Card.builder()
                        .type(civil)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(VictoryPointEffect.of(2))
                        .name(CardName.THEATER)
                        .build(),
                Card.builder()
                        .type(civil)
                        .age(age)
                        .requiredPlayersCount(6)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(VictoryPointEffect.of(2))
                        .name(CardName.THEATER)
                        .build(),

                // CIVIL
                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(4)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(CoinRewardEffect.of(5))
                        .name(CardName.TAVERNE)
                        .build(),
                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(5)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(CoinRewardEffect.of(5))
                        .name(CardName.TAVERNE)
                        .build(),
                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(7)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(CoinRewardEffect.of(5))
                        .name(CardName.TAVERNE)
                        .build(),

                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(PreferentialTradingEffect.forDirectionAndType(EffectDirectionConstraint.RIGHT, PreferentialTradingContract.Type.RAW_MATERIALS))
                        .name(CardName.KONTOR_OST)
                        .build(),
                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(7)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(PreferentialTradingEffect.forDirectionAndType(EffectDirectionConstraint.RIGHT, PreferentialTradingContract.Type.RAW_MATERIALS))
                        .name(CardName.KONTOR_OST)
                        .build(),

                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(PreferentialTradingEffect.forDirectionAndType(EffectDirectionConstraint.LEFT, PreferentialTradingContract.Type.RAW_MATERIALS))
                        .name(CardName.KONTOR_WEST)
                        .build(),
                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(7)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(PreferentialTradingEffect.forDirectionAndType(EffectDirectionConstraint.LEFT, PreferentialTradingContract.Type.RAW_MATERIALS))
                        .name(CardName.KONTOR_WEST)
                        .build(),

                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(PreferentialTradingEffect.forDirectionAndType(EffectDirectionConstraint.BOTH, PreferentialTradingContract.Type.MANUFACTURED_GOODS))
                        .name(CardName.MARKT)
                        .build(),
                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(6)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(PreferentialTradingEffect.forDirectionAndType(EffectDirectionConstraint.BOTH, PreferentialTradingContract.Type.MANUFACTURED_GOODS))
                        .name(CardName.MARKT)
                        .build()
        );
    }

    public List<Card> getAge1Group3() {
        var military = CardType.MILITARY;
        var science = CardType.SCIENCE;
        var age = 1;

        return List.of(
                // WAR
                Card.builder()
                        .type(military)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(ComplexResourceCost.of(RawMaterial.WOOD))
                        .effect(WarShieldsEffect.of(1))
                        .name(CardName.BEFESTIGUNSANLAGE)
                        .build(),
                Card.builder()
                        .type(military)
                        .age(age)
                        .requiredPlayersCount(7)
                        .cost(ComplexResourceCost.of(RawMaterial.WOOD))
                        .effect(WarShieldsEffect.of(1))
                        .name(CardName.BEFESTIGUNSANLAGE)
                        .build(),

                Card.builder()
                        .type(military)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(ComplexResourceCost.of(RawMaterial.METAL_ORE))
                        .effect(WarShieldsEffect.of(1))
                        .name(CardName.KASERNE)
                        .build(),
                Card.builder()
                        .type(military)
                        .age(age)
                        .requiredPlayersCount(5)
                        .cost(ComplexResourceCost.of(RawMaterial.METAL_ORE))
                        .effect(WarShieldsEffect.of(1))
                        .name(CardName.KASERNE)
                        .build(),

                Card.builder()
                        .type(military)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(ComplexResourceCost.of(CLAY))
                        .effect(WarShieldsEffect.of(1))
                        .name(CardName.WACHTURM)
                        .build(),
                Card.builder()
                        .type(military)
                        .age(age)
                        .requiredPlayersCount(4)
                        .cost(ComplexResourceCost.of(CLAY))
                        .effect(WarShieldsEffect.of(1))
                        .name(CardName.WACHTURM)
                        .build(),

                // SCIENCE
                Card.builder()
                        .type(science)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(ComplexResourceCost.of(ManufacturedGood.TEXTILE))
                        .effect(ScienceSymbolsEffect.of(COMPASS))
                        .name(CardName.APOTEKE)
                        .build(),
                Card.builder()
                        .type(science)
                        .age(age)
                        .requiredPlayersCount(5)
                        .cost(ComplexResourceCost.of(ManufacturedGood.TEXTILE))
                        .effect(ScienceSymbolsEffect.of(COMPASS))
                        .name(CardName.APOTEKE)
                        .build(),

                Card.builder()
                        .type(science)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(ComplexResourceCost.of(GLASS))
                        .effect(ScienceSymbolsEffect.of(COGWHEEL))
                        .name(CardName.WERKSTAT)
                        .build(),
                Card.builder()
                        .type(science)
                        .age(age)
                        .requiredPlayersCount(7)
                        .cost(ComplexResourceCost.of(GLASS))
                        .effect(ScienceSymbolsEffect.of(COGWHEEL))
                        .name(CardName.WERKSTAT)
                        .build(),

                Card.builder()
                        .type(science)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(ComplexResourceCost.of(ManufacturedGood.SCRIPTS))
                        .effect(ScienceSymbolsEffect.of(TABLET))
                        .name(CardName.SKRIPTORIUM)
                        .build(),
                Card.builder()
                        .type(science)
                        .age(age)
                        .requiredPlayersCount(4)
                        .cost(ComplexResourceCost.of(ManufacturedGood.SCRIPTS))
                        .effect(ScienceSymbolsEffect.of(TABLET))
                        .name(CardName.SKRIPTORIUM)
                        .build()
        );
    }

    // AGE 2
    public List<Card> getAge2Group1() {
        var rawMaterial = CardType.RAW_MATERIAL;
        var manufacturedGood = CardType.MANUFACTURED_GOOD;
        var age = 2;

        return List.of(
                // RAW MATERIALS
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(CoinCost.of(1))
                        .effect(ResourceEffect.of(WOOD, WOOD))
                        .name(CardName.SAGEWERK)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(4)
                        .cost(CoinCost.of(1))
                        .effect(ResourceEffect.of(WOOD, WOOD))
                        .name(CardName.SAGEWERK)
                        .build(),

                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(CoinCost.of(1))
                        .effect(ResourceEffect.of(RawMaterial.STONE, RawMaterial.STONE))
                        .name(CardName.BILDHAUEREI)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(4)
                        .cost(CoinCost.of(1))
                        .effect(ResourceEffect.of(RawMaterial.STONE, RawMaterial.STONE))
                        .name(CardName.BILDHAUEREI)
                        .build(),

                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(CoinCost.of(1))
                        .effect(ResourceEffect.of(CLAY, CLAY))
                        .name(CardName.ZIEGELBRENNERI)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(4)
                        .cost(CoinCost.of(1))
                        .effect(ResourceEffect.of(CLAY, CLAY))
                        .name(CardName.ZIEGELBRENNERI)
                        .build(),

                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(CoinCost.of(1))
                        .effect(ResourceEffect.of(RawMaterial.METAL_ORE, RawMaterial.METAL_ORE))
                        .name(CardName.GIESSEREI)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(4)
                        .cost(CoinCost.of(1))
                        .effect(ResourceEffect.of(RawMaterial.METAL_ORE, RawMaterial.METAL_ORE))
                        .name(CardName.GIESSEREI)
                        .build(),

                // MANUFACTURED GOODS
                Card.builder()
                        .type(manufacturedGood)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(ResourceEffect.of(ManufacturedGood.TEXTILE))
                        .name(CardName.WEBSTUHL)
                        .build(),
                Card.builder()
                        .type(manufacturedGood)
                        .age(age)
                        .requiredPlayersCount(5)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(ResourceEffect.of(ManufacturedGood.TEXTILE))
                        .name(CardName.WEBSTUHL)
                        .build(),
                Card.builder()
                        .type(manufacturedGood)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(ResourceEffect.of(GLASS))
                        .name(CardName.GLASHUTTE)
                        .build(),
                Card.builder()
                        .type(manufacturedGood)
                        .age(age)
                        .requiredPlayersCount(5)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(ResourceEffect.of(GLASS))
                        .name(CardName.GLASHUTTE)
                        .build(),
                Card.builder()
                        .type(manufacturedGood)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(ResourceEffect.of(ManufacturedGood.SCRIPTS))
                        .name(CardName.PRESSE)
                        .build(),
                Card.builder()
                        .type(manufacturedGood)
                        .age(age)
                        .requiredPlayersCount(5)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(ResourceEffect.of(ManufacturedGood.SCRIPTS))
                        .name(CardName.PRESSE)
                        .build()
        );
    }

    public List<Card> getAge2Group2() {
        var civil = CardType.CIVIL;
        var commercial = CardType.COMMERCIAL;
        var age = 2;

        return List.of(
                // CIVIL
                Card.builder()
                        .type(civil)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(ComplexResourceCost.of(RawMaterial.STONE, RawMaterial.STONE, RawMaterial.STONE))
                        .effect(VictoryPointEffect.of(5))
                        .name(CardName.AQUADUKT)
                        .build(),
                Card.builder()
                        .type(civil)
                        .age(age)
                        .requiredPlayersCount(7)
                        .cost(ComplexResourceCost.of(RawMaterial.STONE, RawMaterial.STONE, RawMaterial.STONE))
                        .effect(VictoryPointEffect.of(5))
                        .name(CardName.AQUADUKT)
                        .build(),

                Card.builder()
                        .type(civil)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(ComplexResourceCost.builder()
                                .rawMaterialList(List.of(RawMaterial.WOOD, CLAY))
                                .manufacturedGoodsList(List.of(GLASS))
                                .build()
                        )
                        .effect(VictoryPointEffect.of(3))
                        .name(CardName.TEMPEL)
                        .build(),
                Card.builder()
                        .type(civil)
                        .age(age)
                        .requiredPlayersCount(6)
                        .cost(ComplexResourceCost.builder()
                                .rawMaterialList(List.of(RawMaterial.WOOD, CLAY))
                                .manufacturedGoodsList(List.of(GLASS))
                                .build()
                        )
                        .effect(VictoryPointEffect.of(3))
                        .name(CardName.TEMPEL)
                        .build(),

                Card.builder()
                        .type(civil)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(ComplexResourceCost.of(RawMaterial.METAL_ORE, RawMaterial.METAL_ORE, RawMaterial.WOOD))
                        .effect(VictoryPointEffect.of(4))
                        .name(CardName.STATUE)
                        .build(),
                Card.builder()
                        .type(civil)
                        .age(age)
                        .requiredPlayersCount(7)
                        .cost(ComplexResourceCost.of(RawMaterial.METAL_ORE, RawMaterial.METAL_ORE, RawMaterial.WOOD))
                        .effect(VictoryPointEffect.of(4))
                        .name(CardName.STATUE)
                        .build(),

                // CIVIL
                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(ComplexResourceCost.of(CLAY, CLAY))
                        .effect(ResourceEffect.manufacturedGoodWildcard())
                        .name(CardName.FORUM)
                        .build(),
                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(6)
                        .cost(ComplexResourceCost.of(CLAY, CLAY))
                        .effect(ResourceEffect.manufacturedGoodWildcard())
                        .name(CardName.FORUM)
                        .build(),
                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(7)
                        .cost(ComplexResourceCost.of(CLAY, CLAY))
                        .effect(ResourceEffect.manufacturedGoodWildcard())
                        .name(CardName.FORUM)
                        .build(),

                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(ComplexResourceCost.of(RawMaterial.WOOD, RawMaterial.WOOD))
                        .effect(ResourceEffect.rawMaterialWildcard())
                        .name(CardName.KARAWANSEREI)
                        .build(),
                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(5)
                        .cost(ComplexResourceCost.of(RawMaterial.WOOD, RawMaterial.WOOD))
                        .effect(ResourceEffect.rawMaterialWildcard())
                        .name(CardName.KARAWANSEREI)
                        .build(),
                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(6)
                        .cost(ComplexResourceCost.of(RawMaterial.WOOD, RawMaterial.WOOD))
                        .effect(ResourceEffect.rawMaterialWildcard())
                        .name(CardName.KARAWANSEREI)
                        .build(),

                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(CoinRewardWithModifiersEffect.of(
                                EffectDirectionConstraint.ALL,
                                RAW_MATERIAL_CARD,
                                1
                        ))
                        .name(CardName.WEINBERG)
                        .build(),
                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(6)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(CoinRewardWithModifiersEffect.of(
                                EffectDirectionConstraint.ALL,
                                RAW_MATERIAL_CARD,
                                1
                        ))
                        .name(CardName.WEINBERG)
                        .build(),

                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(4)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(CoinRewardWithModifiersEffect.of(
                                EffectDirectionConstraint.ALL,
                                EffectMultiplierType.MANUFACTURED_GOOD_CARD,
                                2
                        ))
                        .name(CardName.BASAR)
                        .build(),
                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(7)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(CoinRewardWithModifiersEffect.of(
                                EffectDirectionConstraint.ALL,
                                EffectMultiplierType.MANUFACTURED_GOOD_CARD,
                                2
                        ))
                        .name(CardName.BASAR)
                        .build()
        );
    }

    public List<Card> getAge2Group3() {
        var military = CardType.MILITARY;
        var science = CardType.SCIENCE;
        var age = 2;

        return List.of(
                // MILITARY
                Card.builder()
                        .type(military)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(ComplexResourceCost.of(RawMaterial.STONE, RawMaterial.STONE, RawMaterial.STONE))
                        .effect(WarShieldsEffect.of(2))
                        .name(CardName.MAUERN)
                        .build(),
                Card.builder()
                        .type(military)
                        .age(age)
                        .requiredPlayersCount(7)
                        .cost(ComplexResourceCost.of(RawMaterial.STONE, RawMaterial.STONE, RawMaterial.STONE))
                        .effect(WarShieldsEffect.of(2))
                        .name(CardName.MAUERN)
                        .build(),

                Card.builder()
                        .type(military)
                        .age(age)
                        .requiredPlayersCount(4)
                        .cost(ComplexResourceCost.of(RawMaterial.WOOD, RawMaterial.METAL_ORE, RawMaterial.METAL_ORE))
                        .effect(WarShieldsEffect.of(2))
                        .name(CardName.TRAININGSGELANDE)
                        .build(),
                Card.builder()
                        .type(military)
                        .age(age)
                        .requiredPlayersCount(6)
                        .cost(ComplexResourceCost.of(RawMaterial.WOOD, RawMaterial.METAL_ORE, RawMaterial.METAL_ORE))
                        .effect(WarShieldsEffect.of(2))
                        .name(CardName.TRAININGSGELANDE)
                        .build(),
                Card.builder()
                        .type(military)
                        .age(age)
                        .requiredPlayersCount(7)
                        .cost(ComplexResourceCost.of(RawMaterial.WOOD, RawMaterial.METAL_ORE, RawMaterial.METAL_ORE))
                        .effect(WarShieldsEffect.of(2))
                        .name(CardName.TRAININGSGELANDE)
                        .build(),

                Card.builder()
                        .type(military)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(ComplexResourceCost.of(RawMaterial.METAL_ORE, CLAY, RawMaterial.WOOD))
                        .effect(WarShieldsEffect.of(2))
                        .name(CardName.STALLE)
                        .build(),
                Card.builder()
                        .type(military)
                        .age(age)
                        .requiredPlayersCount(5)
                        .cost(ComplexResourceCost.of(RawMaterial.METAL_ORE, CLAY, RawMaterial.WOOD))
                        .effect(WarShieldsEffect.of(2))
                        .name(CardName.STALLE)
                        .build(),

                Card.builder()
                        .type(science)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(ComplexResourceCost.builder()
                                .rawMaterialList(List.of(RawMaterial.METAL_ORE, RawMaterial.METAL_ORE))
                                .manufacturedGoodsList(List.of(GLASS))
                                .build()
                        )
                        .effect(ScienceSymbolsEffect.of(COMPASS))
                        .name(CardName.ARZNEIAUSGABE)
                        .build(),
                Card.builder()
                        .type(science)
                        .age(age)
                        .requiredPlayersCount(4)
                        .cost(ComplexResourceCost.builder()
                                .rawMaterialList(List.of(RawMaterial.METAL_ORE, RawMaterial.METAL_ORE))
                                .manufacturedGoodsList(List.of(GLASS))
                                .build()
                        )
                        .effect(ScienceSymbolsEffect.of(COMPASS))
                        .name(CardName.ARZNEIAUSGABE)
                        .build(),

                Card.builder()
                        .type(military)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(ComplexResourceCost.of(RawMaterial.WOOD, RawMaterial.WOOD, RawMaterial.METAL_ORE))
                        .effect(WarShieldsEffect.of(2))
                        .name(CardName.SCHIESSPLATZ)
                        .build(),
                Card.builder()
                        .type(military)
                        .age(age)
                        .requiredPlayersCount(6)
                        .cost(ComplexResourceCost.of(RawMaterial.WOOD, RawMaterial.WOOD, RawMaterial.METAL_ORE))
                        .effect(WarShieldsEffect.of(2))
                        .name(CardName.SCHIESSPLATZ)
                        .build(),

                Card.builder()
                        .type(science)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(ComplexResourceCost.builder()
                                .rawMaterialList(List.of(CLAY, CLAY))
                                .manufacturedGoodsList(List.of(ManufacturedGood.SCRIPTS))
                                .build()
                        )
                        .effect(ScienceSymbolsEffect.of(COGWHEEL))
                        .name(CardName.LABORATORIUM)
                        .build(),
                Card.builder()
                        .type(science)
                        .age(age)
                        .requiredPlayersCount(5)
                        .cost(ComplexResourceCost.builder()
                                .rawMaterialList(List.of(CLAY, CLAY))
                                .manufacturedGoodsList(List.of(ManufacturedGood.SCRIPTS))
                                .build()
                        )
                        .effect(ScienceSymbolsEffect.of(COGWHEEL))
                        .name(CardName.LABORATORIUM)
                        .build(),

                Card.builder()
                        .type(CardType.CIVIL)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(ComplexResourceCost.builder()
                                .rawMaterialList(List.of(CLAY, CLAY))
                                .manufacturedGoodsList(List.of(ManufacturedGood.TEXTILE))
                                .build()
                        )
                        .effect(VictoryPointEffect.of(4))
                        .name(CardName.GERICHT)
                        .build(),
                Card.builder()
                        .type(CardType.CIVIL)
                        .age(age)
                        .requiredPlayersCount(5)
                        .cost(ComplexResourceCost.builder()
                                .rawMaterialList(List.of(CLAY, CLAY))
                                .manufacturedGoodsList(List.of(ManufacturedGood.TEXTILE))
                                .build()
                        )
                        .effect(VictoryPointEffect.of(4))
                        .name(CardName.GERICHT)
                        .build(),

                Card.builder()
                        .type(science)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(ComplexResourceCost.builder()
                                .rawMaterialList(List.of(RawMaterial.STONE, RawMaterial.STONE))
                                .manufacturedGoodsList(List.of(ManufacturedGood.TEXTILE))
                                .build()
                        )
                        .effect(ScienceSymbolsEffect.of(TABLET))
                        .name(CardName.BIBLIOTHEK)
                        .build(),
                Card.builder()
                        .type(science)
                        .age(age)
                        .requiredPlayersCount(6)
                        .cost(ComplexResourceCost.builder()
                                .rawMaterialList(List.of(RawMaterial.STONE, RawMaterial.STONE))
                                .manufacturedGoodsList(List.of(ManufacturedGood.TEXTILE))
                                .build()
                        )
                        .effect(ScienceSymbolsEffect.of(TABLET))
                        .name(CardName.BIBLIOTHEK)
                        .build(),

                Card.builder()
                        .type(science)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(ComplexResourceCost.of(RawMaterial.WOOD, ManufacturedGood.SCRIPTS))
                        .effect(ScienceSymbolsEffect.of(TABLET))
                        .name(CardName.SCHULE)
                        .build(),
                Card.builder()
                        .type(science)
                        .age(age)
                        .requiredPlayersCount(7)
                        .cost(ComplexResourceCost.of(RawMaterial.WOOD, ManufacturedGood.SCRIPTS))
                        .effect(ScienceSymbolsEffect.of(TABLET))
                        .name(CardName.SCHULE)
                        .build()
        );
    }

    // AGE 3
    public List<Card> getGuildCards() {
        var cards = new ArrayList<Card>();
        var guildCardTemplate = GuildCardTemplate.create();

        cards.add(guildCardTemplate.createCardsForTypeAndDirection(
                CardName.GILDE_DER_ARBEITER,
                List.of(METAL_ORE, METAL_ORE, CLAY, STONE, WOOD),
                List.of(),
                RAW_MATERIAL_CARD,
                EffectDirectionConstraint.BOTH,
                2
        ));

        cards.add(guildCardTemplate.createCardsForTypeAndDirection(
                CardName.GILDE_DER_KUNSTLER,
                List.of(METAL_ORE, METAL_ORE, STONE, STONE),
                List.of(),
                MANUFACTURED_GOOD_CARD,
                EffectDirectionConstraint.BOTH,
                1
        ));

        cards.add(guildCardTemplate.createCardsForTypeAndDirection(
                CardName.GILDE_DER_HANDELER,
                List.of(),
                ManufacturedGood.all(),
                COMMERCIAL_CARD,
                EffectDirectionConstraint.BOTH,
                1
        ));

        cards.add(guildCardTemplate.createCardsForTypeAndDirection(
                CardName.GILDE_DER_PHILOSOPHEN,
                List.of(CLAY, CLAY, CLAY),
                List.of(TEXTILE, SCRIPTS),
                SCIENCE_CARD,
                EffectDirectionConstraint.BOTH,
                1
        ));

        cards.add(guildCardTemplate.createCardsForTypeAndDirection(
                CardName.GILDE_DER_SPIONE,
                List.of(CLAY, CLAY, CLAY),
                List.of(GLASS),
                MILITARY_CARD,
                EffectDirectionConstraint.BOTH,
                1
        ));

        cards.add(guildCardTemplate.createCardsForTypeAndDirection(
                CardName.GILDE_DER_STRATEGEN,
                List.of(METAL_ORE, METAL_ORE, STONE),
                List.of(TEXTILE),
                WAR_LOSS,
                EffectDirectionConstraint.BOTH,
                1
        ));

        cards.add(guildCardTemplate.createGildeDerReederCard());
        cards.add(guildCardTemplate.createGildeDerWissenschaftlerCard());

        cards.add(guildCardTemplate.createCardsForTypeAndDirection(
                CardName.GILDE_DER_BEAMTEN,
                List.of(WOOD, WOOD, WOOD, STONE),
                List.of(TEXTILE),
                CIVIL_CARD,
                EffectDirectionConstraint.BOTH,
                1
        ));

        cards.add(guildCardTemplate.createCardsForTypeAndDirection(
                CardName.GILDE_DER_BAUMEISTER,
                List.of(STONE, STONE, CLAY, CLAY),
                List.of(GLASS),
                WONDER_STAGE,
                EffectDirectionConstraint.ALL,
                1
        ));

        return cards;
    }

    public List<Card> getAge3Group2() {
        var cards = new ArrayList<Card>();
        var civilFactory = CivilCardTemplate.create(3);
        var commercialFactory = CommercialCardTemplate.create(3);

        cards.addAll(civilFactory.createCards(
                CardName.PANTHENON,
                List.of(3, 6),
                List.of(CLAY, CLAY, METAL_ORE),
                ManufacturedGood.all(),
                7
        ));

        cards.addAll(civilFactory.createCards(
                CardName.GARTEN,
                List.of(3, 4),
                List.of(WOOD, CLAY, CLAY),
                List.of(),
                5
        ));

        cards.addAll(civilFactory.createCards(
                CardName.RATHAUS,
                List.of(3, 5, 6),
                List.of(METAL_ORE, STONE, STONE),
                List.of(GLASS),
                6
        ));

        cards.addAll(civilFactory.createCards(
                CardName.PALAST,
                List.of(3, 7),
                RawMaterial.all(),
                ManufacturedGood.all(),
                8
        ));

        cards.addAll(commercialFactory.createCards(
                CardName.HAFEN,
                List.of(3, 4),
                List.of(METAL_ORE, WOOD),
                List.of(TEXTILE),
                CoinRewardAndVictoryPointWithModifiersEffect.of(
                        SELF,
                        RAW_MATERIAL_CARD,
                        1,
                        1
                )
        ));

        cards.addAll(commercialFactory.createCards(
                CardName.LEUCHTTURM,
                List.of(3, 6),
                List.of(STONE),
                List.of(GLASS),
                CoinRewardAndVictoryPointWithModifiersEffect.of(
                        SELF,
                        COMMERCIAL_CARD,
                        1,
                        1
                )
        ));
        cards.addAll(commercialFactory.createCards(
                CardName.HANDELSKAMMER,
                List.of(4, 6),
                List.of(CLAY, CLAY),
                List.of(SCRIPTS),
                CoinRewardAndVictoryPointWithModifiersEffect.of(
                        SELF,
                        MANUFACTURED_GOOD_CARD,
                        2,
                        2
                )
        ));

        return cards;
    }

    public List<Card> getAge3Group3() {
        var cards = new ArrayList<Card>();
        var scienceCardTemplate = ScienceCardTemplate.create(3);
        var commercialCardTemplate = CommercialCardTemplate.create(3);
        var civilCardTemplate = CivilCardTemplate.create(3);
        var militaryCardTemplate = MilitaryCardTemplate.create(3);

        cards.addAll(militaryCardTemplate.createCards(
                CardName.VERTEIDIGUNGSANLAGE,
                List.of(3, 7),
                List.of(STONE, METAL_ORE, METAL_ORE, METAL_ORE),
                List.of(),
                3
        ));

        cards.addAll(militaryCardTemplate.createCards(
                CardName.ZIRKUS,
                List.of(4, 5, 6),
                List.of(STONE, STONE, STONE, METAL_ORE),
                List.of(),
                3
        ));

        cards.addAll(militaryCardTemplate.createCards(
                CardName.WAFFENLAGER,
                List.of(3, 4, 7),
                List.of(METAL_ORE, WOOD, WOOD),
                List.of(TEXTILE),
                3
        ));

        cards.addAll(commercialCardTemplate.createCards(
                CardName.ARENA,
                List.of(3, 5, 7),
                List.of(METAL_ORE, STONE, STONE),
                List.of(),
                CoinRewardAndVictoryPointWithModifiersEffect.of(
                        SELF,
                        WONDER_STAGE,
                        3,
                        1
                )
        ));

        cards.addAll(scienceCardTemplate.createCards(
                CardName.LOGE,
                List.of(3, 6),
                List.of(CLAY, CLAY),
                List.of(TEXTILE, SCRIPTS),
                COMPASS
        ));

        cards.addAll(militaryCardTemplate.createCards(
                CardName.BELAGERUNGSMASCHINEN,
                List.of(3, 5),
                List.of(WOOD, CLAY, CLAY, CLAY),
                List.of(),
                3
        ));

        cards.addAll(scienceCardTemplate.createCards(
                CardName.OBSERVATORIUM,
                List.of(3, 7),
                List.of(METAL_ORE, METAL_ORE),
                List.of(GLASS, TEXTILE),
                COMPASS
        ));

        cards.addAll(civilCardTemplate.createCards(
                CardName.SENAT,
                List.of(3, 5),
                List.of(METAL_ORE, STONE, WOOD, WOOD),
                List.of(),
                6
        ));

        cards.addAll(scienceCardTemplate.createCards(
                CardName.UNIVERSTAT,
                List.of(3, 4),
                List.of(WOOD, WOOD),
                List.of(SCRIPTS, GLASS),
                TABLET
        ));

        cards.addAll(scienceCardTemplate.createCards(
                CardName.AKADEMIE,
                List.of(3, 7),
                List.of(STONE, STONE, STONE),
                List.of(GLASS),
                COMPASS
        ));

        cards.addAll(scienceCardTemplate.createCards(
                CardName.STUDIENZIMMER,
                List.of(3, 5),
                List.of(WOOD),
                List.of(SCRIPTS, TEXTILE),
                COGWHEEL
        ));

        return cards;
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
