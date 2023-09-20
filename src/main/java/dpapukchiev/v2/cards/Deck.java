package dpapukchiev.v2.cards;

import dpapukchiev.v2.cost.CoinCost;
import dpapukchiev.v2.cost.ComplexResourceCost;
import dpapukchiev.v2.cost.FreeToPlayCost;
import dpapukchiev.v2.effects.CoinRewardEffect;
import dpapukchiev.v2.effects.PreferentialTradingEffect;
import dpapukchiev.v2.effects.ResourceEffect;
import dpapukchiev.v2.effects.ScienceSymbolsEffect;
import dpapukchiev.v2.effects.VictoryPointEffect;
import dpapukchiev.v2.effects.WarShieldsEffect;
import dpapukchiev.v2.resources.ManufacturedGood;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.simulation.ModelElement;
import jsl.utilities.random.rvariable.NormalRV;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static dpapukchiev.v2.effects.core.EffectDirectionConstraint.BOTH;
import static dpapukchiev.v2.effects.core.EffectDirectionConstraint.LEFT;
import static dpapukchiev.v2.effects.core.EffectDirectionConstraint.RIGHT;
import static dpapukchiev.v2.effects.core.PreferentialTradingContract.Type.MANUFACTURED_GOODS;
import static dpapukchiev.v2.effects.core.PreferentialTradingContract.Type.RAW_MATERIALS;
import static dpapukchiev.v2.resources.ManufacturedGood.GLASS;
import static dpapukchiev.v2.resources.ManufacturedGood.SCRIPTS;
import static dpapukchiev.v2.resources.ManufacturedGood.TEXTILE;
import static dpapukchiev.v2.resources.RawMaterial.CLAY;
import static dpapukchiev.v2.resources.RawMaterial.METAL_ORE;
import static dpapukchiev.v2.resources.RawMaterial.STONE;
import static dpapukchiev.v2.resources.RawMaterial.WOOD;
import static dpapukchiev.v2.resources.ScienceSymbol.COGWHEEL;
import static dpapukchiev.v2.resources.ScienceSymbol.COMPASS;
import static dpapukchiev.v2.resources.ScienceSymbol.TABLET;

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

    public void resetDeck() {
        discardedCards.clear();
        allCards.clear();

        allCards.addAll(getAge1Group1());
        allCards.addAll(getAge1Group2());
        allCards.addAll(getAge1Group3());

        allCards.addAll(getAge2Group1());
    }

    public Map<Integer, List<Card>> getCardsByAge() {
        return allCards.stream()
                .collect(Collectors.groupingBy(Card::getAge));
    }

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
                        .effect(ResourceEffect.of(WOOD))
                        .name(CardName.HOLZPLATZ)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(4)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(ResourceEffect.of(WOOD))
                        .name(CardName.HOLZPLATZ)
                        .build(),

                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(ResourceEffect.of(STONE))
                        .name(CardName.STEINBRUCH)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(5)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(ResourceEffect.of(STONE))
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
                        .effect(ResourceEffect.of(METAL_ORE))
                        .name(CardName.ERZBERGWERK)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(4)
                        .cost(FreeToPlayCost.newInstance())
                        .effect(ResourceEffect.of(METAL_ORE))
                        .name(CardName.ERZBERGWERK)
                        .build(),

                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(6)
                        .cost(CoinCost.of(1))
                        .effect(ResourceEffect.of(WOOD, CLAY))
                        .name(CardName.BAUMSCHULE)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(4)
                        .cost(CoinCost.of(1))
                        .effect(ResourceEffect.of(STONE, CLAY))
                        .name(CardName.AUSGRABUNGSTTATTE)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(CoinCost.of(1))
                        .effect(ResourceEffect.of(METAL_ORE, CLAY))
                        .name(CardName.TONGRUBE)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(CoinCost.of(1))
                        .effect(ResourceEffect.of(STONE, WOOD))
                        .name(CardName.FORSTWIRTSCHAFT)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(5)
                        .cost(CoinCost.of(1))
                        .effect(ResourceEffect.of(METAL_ORE, WOOD))
                        .name(CardName.WALDHOLE)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(6)
                        .cost(CoinCost.of(1))
                        .effect(ResourceEffect.of(METAL_ORE, STONE))
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
        var oneStoneCost = ComplexResourceCost.builder()
                .rawMaterialList(List.of(STONE))
                .build();
        var freeToPlayCost = FreeToPlayCost
                .builder()
                .build();

        return List.of(
                // CIVIL
                Card.builder()
                        .type(civil)
                        .age(age)
                        .requiredPlayersCount(4)
                        .cost(freeToPlayCost)
                        .effect(VictoryPointEffect.of(3))
                        .name(CardName.PFANDHOUSE)
                        .build(),
                Card.builder()
                        .type(civil)
                        .age(age)
                        .requiredPlayersCount(7)
                        .cost(freeToPlayCost)
                        .effect(VictoryPointEffect.of(3))
                        .name(CardName.PFANDHOUSE)
                        .build(),

                Card.builder()
                        .type(civil)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(oneStoneCost)
                        .effect(VictoryPointEffect.of(3))
                        .name(CardName.BADER)
                        .build(),
                Card.builder()
                        .type(civil)
                        .age(age)
                        .requiredPlayersCount(7)
                        .cost(oneStoneCost)
                        .effect(VictoryPointEffect.of(3))
                        .name(CardName.BADER)
                        .build(),

                Card.builder()
                        .type(civil)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(freeToPlayCost)
                        .effect(VictoryPointEffect.of(2))
                        .name(CardName.ALTAR)
                        .build(),
                Card.builder()
                        .type(civil)
                        .age(age)
                        .requiredPlayersCount(5)
                        .cost(freeToPlayCost)
                        .effect(VictoryPointEffect.of(2))
                        .name(CardName.ALTAR)
                        .build(),

                Card.builder()
                        .type(civil)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(freeToPlayCost)
                        .effect(VictoryPointEffect.of(2))
                        .name(CardName.THEATER)
                        .build(),
                Card.builder()
                        .type(civil)
                        .age(age)
                        .requiredPlayersCount(6)
                        .cost(freeToPlayCost)
                        .effect(VictoryPointEffect.of(2))
                        .name(CardName.THEATER)
                        .build(),

                // CIVIL
                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(4)
                        .cost(freeToPlayCost)
                        .effect(CoinRewardEffect.of(5))
                        .name(CardName.TAVERNE)
                        .build(),
                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(5)
                        .cost(freeToPlayCost)
                        .effect(CoinRewardEffect.of(5))
                        .name(CardName.TAVERNE)
                        .build(),
                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(7)
                        .cost(freeToPlayCost)
                        .effect(CoinRewardEffect.of(5))
                        .name(CardName.TAVERNE)
                        .build(),

                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(freeToPlayCost)
                        .effect(PreferentialTradingEffect.forDirectionAndType(RIGHT, RAW_MATERIALS))
                        .name(CardName.KONTOR_OST)
                        .build(),
                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(7)
                        .cost(freeToPlayCost)
                        .effect(PreferentialTradingEffect.forDirectionAndType(RIGHT, RAW_MATERIALS))
                        .name(CardName.KONTOR_OST)
                        .build(),

                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(freeToPlayCost)
                        .effect(PreferentialTradingEffect.forDirectionAndType(LEFT, RAW_MATERIALS))
                        .name(CardName.KONTOR_WEST)
                        .build(),
                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(7)
                        .cost(freeToPlayCost)
                        .effect(PreferentialTradingEffect.forDirectionAndType(LEFT, RAW_MATERIALS))
                        .name(CardName.KONTOR_WEST)
                        .build(),

                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(freeToPlayCost)
                        .effect(PreferentialTradingEffect.forDirectionAndType(BOTH, MANUFACTURED_GOODS))
                        .name(CardName.MARKT)
                        .build(),
                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(6)
                        .cost(freeToPlayCost)
                        .effect(PreferentialTradingEffect.forDirectionAndType(BOTH, MANUFACTURED_GOODS))
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
                        .cost(ComplexResourceCost.of(WOOD))
                        .effect(WarShieldsEffect.of(1))
                        .name(CardName.BEFESTIGUNSANLAGE)
                        .build(),
                Card.builder()
                        .type(military)
                        .age(age)
                        .requiredPlayersCount(7)
                        .cost(ComplexResourceCost.of(WOOD))
                        .effect(WarShieldsEffect.of(1))
                        .name(CardName.BEFESTIGUNSANLAGE)
                        .build(),

                Card.builder()
                        .type(military)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(ComplexResourceCost.of(METAL_ORE))
                        .effect(WarShieldsEffect.of(1))
                        .name(CardName.KASERNE)
                        .build(),
                Card.builder()
                        .type(military)
                        .age(age)
                        .requiredPlayersCount(5)
                        .cost(ComplexResourceCost.of(METAL_ORE))
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
                        .cost(ComplexResourceCost.of(TEXTILE))
                        .effect(ScienceSymbolsEffect.of(COMPASS))
                        .name(CardName.APHOTEKE)
                        .build(),
                Card.builder()
                        .type(science)
                        .age(age)
                        .requiredPlayersCount(5)
                        .cost(ComplexResourceCost.of(TEXTILE))
                        .effect(ScienceSymbolsEffect.of(COMPASS))
                        .name(CardName.APHOTEKE)
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
                        .cost(ComplexResourceCost.of(SCRIPTS))
                        .effect(ScienceSymbolsEffect.of(TABLET))
                        .name(CardName.SKRIPTORIUM)
                        .build(),
                Card.builder()
                        .type(science)
                        .age(age)
                        .requiredPlayersCount(4)
                        .cost(ComplexResourceCost.of(SCRIPTS))
                        .effect(ScienceSymbolsEffect.of(TABLET))
                        .name(CardName.SKRIPTORIUM)
                        .build()
        );
    }

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
                        .effect(ResourceEffect.of(WOOD))
                        .name(CardName.HOLZPLATZ)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(4)
                        .cost(CoinCost.of(1))
                        .effect(ResourceEffect.of(WOOD))
                        .name(CardName.HOLZPLATZ)
                        .build(),

                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(CoinCost.of(1))
                        .effect(ResourceEffect.of(STONE, STONE))
                        .name(CardName.BILDHAUEREI)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(4)
                        .cost(CoinCost.of(1))
                        .effect(ResourceEffect.of(STONE, STONE))
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
                        .effect(ResourceEffect.of(METAL_ORE, METAL_ORE))
                        .name(CardName.GIESSEREI)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(4)
                        .cost(CoinCost.of(1))
                        .effect(ResourceEffect.of(METAL_ORE, METAL_ORE))
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
}
