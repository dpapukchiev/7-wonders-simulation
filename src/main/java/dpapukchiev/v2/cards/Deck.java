package dpapukchiev.v2.cards;

import dpapukchiev.v2.cost.CoinCost;
import dpapukchiev.v2.cost.ComplexResourceCost;
import dpapukchiev.v2.cost.FreeToPlayCost;
import dpapukchiev.v2.effects.CoinRewardEffect;
import dpapukchiev.v2.effects.EffectDirectionConstraint;
import dpapukchiev.v2.effects.PreferentialTradingContract;
import dpapukchiev.v2.effects.PreferentialTradingEffect;
import dpapukchiev.v2.effects.ResourceEffect;
import dpapukchiev.v2.effects.VictoryPointEffect;
import dpapukchiev.v2.resources.ManufacturedGood;
import dpapukchiev.v2.resources.RawMaterial;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.simulation.ModelElement;
import jsl.utilities.random.rvariable.NormalRV;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static dpapukchiev.v2.effects.EffectDirectionConstraint.BOTH;
import static dpapukchiev.v2.effects.EffectDirectionConstraint.LEFT;
import static dpapukchiev.v2.effects.EffectDirectionConstraint.RIGHT;
import static dpapukchiev.v2.effects.PreferentialTradingContract.Type.MANUFACTURED_GOODS;
import static dpapukchiev.v2.effects.PreferentialTradingContract.Type.RAW_MATERIALS;

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
        var freeToPlayCost = FreeToPlayCost
                .builder()
                .build();
        var pay1CoinCost = CoinCost.builder()
                .requiredCoins(1)
                .build();

        return List.of(
                // RAW MATERIALS
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(freeToPlayCost)
                        .effect(getResourceEffect(RawMaterial.WOOD))
                        .name(CardName.HOLZPLATZ)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(4)
                        .cost(freeToPlayCost)
                        .effect(getResourceEffect(RawMaterial.WOOD))
                        .name(CardName.HOLZPLATZ)
                        .build(),

                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(freeToPlayCost)
                        .effect(getResourceEffect(RawMaterial.STONE))
                        .name(CardName.STEINBRUCH)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(5)
                        .cost(freeToPlayCost)
                        .effect(getResourceEffect(RawMaterial.STONE))
                        .name(CardName.STEINBRUCH)
                        .build(),

                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(freeToPlayCost)
                        .effect(getResourceEffect(RawMaterial.CLAY))
                        .name(CardName.ZIEGELEI)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(5)
                        .cost(freeToPlayCost)
                        .effect(getResourceEffect(RawMaterial.CLAY))
                        .name(CardName.ZIEGELEI)
                        .build(),

                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(freeToPlayCost)
                        .effect(getResourceEffect(RawMaterial.METAL_ORE))
                        .name(CardName.ERZBERGWERK)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(4)
                        .cost(freeToPlayCost)
                        .effect(getResourceEffect(RawMaterial.METAL_ORE))
                        .name(CardName.ERZBERGWERK)
                        .build(),

                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(6)
                        .cost(pay1CoinCost)
                        .effect(getResourceEffect(RawMaterial.WOOD, RawMaterial.CLAY))
                        .name(CardName.BAUMSCHULE)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(4)
                        .cost(pay1CoinCost)
                        .effect(getResourceEffect(RawMaterial.STONE, RawMaterial.CLAY))
                        .name(CardName.AUSGRABUNGSTTATTE)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(pay1CoinCost)
                        .effect(getResourceEffect(RawMaterial.METAL_ORE, RawMaterial.CLAY))
                        .name(CardName.TONGRUBE)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(pay1CoinCost)
                        .effect(getResourceEffect(RawMaterial.STONE, RawMaterial.WOOD))
                        .name(CardName.FORSTWIRTSCHAFT)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(5)
                        .cost(pay1CoinCost)
                        .effect(getResourceEffect(RawMaterial.METAL_ORE, RawMaterial.WOOD))
                        .name(CardName.WALDHOLE)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(6)
                        .cost(pay1CoinCost)
                        .effect(getResourceEffect(RawMaterial.METAL_ORE, RawMaterial.STONE))
                        .name(CardName.MINE)
                        .build(),

                // MANUFACTURED GOODS
                Card.builder()
                        .type(manufacturedGood)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(freeToPlayCost)
                        .effect(getResourceEffect(ManufacturedGood.TEXTILE))
                        .name(CardName.WEBSTUHL)
                        .build(),
                Card.builder()
                        .type(manufacturedGood)
                        .age(age)
                        .requiredPlayersCount(6)
                        .cost(freeToPlayCost)
                        .effect(getResourceEffect(ManufacturedGood.TEXTILE))
                        .name(CardName.WEBSTUHL)
                        .build(),
                Card.builder()
                        .type(manufacturedGood)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(freeToPlayCost)
                        .effect(getResourceEffect(ManufacturedGood.GLASS))
                        .name(CardName.GLASHUTTE)
                        .build(),
                Card.builder()
                        .type(manufacturedGood)
                        .age(age)
                        .requiredPlayersCount(6)
                        .cost(freeToPlayCost)
                        .effect(getResourceEffect(ManufacturedGood.GLASS))
                        .name(CardName.GLASHUTTE)
                        .build(),
                Card.builder()
                        .type(manufacturedGood)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(freeToPlayCost)
                        .effect(getResourceEffect(ManufacturedGood.SCRIPTS))
                        .name(CardName.PRESSE)
                        .build(),
                Card.builder()
                        .type(manufacturedGood)
                        .age(age)
                        .requiredPlayersCount(6)
                        .cost(freeToPlayCost)
                        .effect(getResourceEffect(ManufacturedGood.SCRIPTS))
                        .name(CardName.PRESSE)
                        .build()
        );
    }

    public List<Card> getAge2Group1() {
        var rawMaterial = CardType.RAW_MATERIAL;
        var manufacturedGood = CardType.MANUFACTURED_GOOD;
        var age = 2;
        var freeToPlayCost = FreeToPlayCost
                .builder()
                .build();
        var pay1CoinCost = CoinCost.builder()
                .requiredCoins(1)
                .build();

        return List.of(
                // RAW MATERIALS
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(pay1CoinCost)
                        .effect(getResourceEffect(RawMaterial.WOOD))
                        .name(CardName.HOLZPLATZ)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(4)
                        .cost(pay1CoinCost)
                        .effect(getResourceEffect(RawMaterial.WOOD))
                        .name(CardName.HOLZPLATZ)
                        .build(),

                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(pay1CoinCost)
                        .effect(getResourceEffect(RawMaterial.STONE, RawMaterial.STONE))
                        .name(CardName.BILDHAUEREI)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(4)
                        .cost(pay1CoinCost)
                        .effect(getResourceEffect(RawMaterial.STONE, RawMaterial.STONE))
                        .name(CardName.BILDHAUEREI)
                        .build(),

                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(pay1CoinCost)
                        .effect(getResourceEffect(RawMaterial.CLAY, RawMaterial.CLAY))
                        .name(CardName.ZIEGELBRENNERI)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(4)
                        .cost(pay1CoinCost)
                        .effect(getResourceEffect(RawMaterial.CLAY, RawMaterial.CLAY))
                        .name(CardName.ZIEGELBRENNERI)
                        .build(),

                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(pay1CoinCost)
                        .effect(getResourceEffect(RawMaterial.METAL_ORE, RawMaterial.METAL_ORE))
                        .name(CardName.GIESSEREI)
                        .build(),
                Card.builder()
                        .type(rawMaterial)
                        .age(age)
                        .requiredPlayersCount(4)
                        .cost(pay1CoinCost)
                        .effect(getResourceEffect(RawMaterial.METAL_ORE, RawMaterial.METAL_ORE))
                        .name(CardName.GIESSEREI)
                        .build(),

                // MANUFACTURED GOODS
                Card.builder()
                        .type(manufacturedGood)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(freeToPlayCost)
                        .effect(getResourceEffect(ManufacturedGood.TEXTILE))
                        .name(CardName.WEBSTUHL)
                        .build(),
                Card.builder()
                        .type(manufacturedGood)
                        .age(age)
                        .requiredPlayersCount(5)
                        .cost(freeToPlayCost)
                        .effect(getResourceEffect(ManufacturedGood.TEXTILE))
                        .name(CardName.WEBSTUHL)
                        .build(),
                Card.builder()
                        .type(manufacturedGood)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(freeToPlayCost)
                        .effect(getResourceEffect(ManufacturedGood.GLASS))
                        .name(CardName.GLASHUTTE)
                        .build(),
                Card.builder()
                        .type(manufacturedGood)
                        .age(age)
                        .requiredPlayersCount(5)
                        .cost(freeToPlayCost)
                        .effect(getResourceEffect(ManufacturedGood.GLASS))
                        .name(CardName.GLASHUTTE)
                        .build(),
                Card.builder()
                        .type(manufacturedGood)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(freeToPlayCost)
                        .effect(getResourceEffect(ManufacturedGood.SCRIPTS))
                        .name(CardName.PRESSE)
                        .build(),
                Card.builder()
                        .type(manufacturedGood)
                        .age(age)
                        .requiredPlayersCount(5)
                        .cost(freeToPlayCost)
                        .effect(getResourceEffect(ManufacturedGood.SCRIPTS))
                        .name(CardName.PRESSE)
                        .build()
        );
    }

    public List<Card> getAge1Group2() {
        var civil = CardType.CIVIL;
        var commercial = CardType.COMMERCIAL;
        var age = 1;
        var oneStoneCost = ComplexResourceCost.builder()
                .rawMaterialList(List.of(RawMaterial.STONE))
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
                        .effect(getVictoryPointEffect(3))
                        .name(CardName.PFANDHOUSE)
                        .build(),
                Card.builder()
                        .type(civil)
                        .age(age)
                        .requiredPlayersCount(7)
                        .cost(freeToPlayCost)
                        .effect(getVictoryPointEffect(3))
                        .name(CardName.PFANDHOUSE)
                        .build(),

                Card.builder()
                        .type(civil)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(oneStoneCost)
                        .effect(getVictoryPointEffect(3))
                        .name(CardName.BADER)
                        .build(),
                Card.builder()
                        .type(civil)
                        .age(age)
                        .requiredPlayersCount(7)
                        .cost(oneStoneCost)
                        .effect(getVictoryPointEffect(3))
                        .name(CardName.BADER)
                        .build(),

                Card.builder()
                        .type(civil)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(freeToPlayCost)
                        .effect(getVictoryPointEffect(2))
                        .name(CardName.ALTAR)
                        .build(),
                Card.builder()
                        .type(civil)
                        .age(age)
                        .requiredPlayersCount(5)
                        .cost(freeToPlayCost)
                        .effect(getVictoryPointEffect(2))
                        .name(CardName.ALTAR)
                        .build(),

                Card.builder()
                        .type(civil)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(freeToPlayCost)
                        .effect(getVictoryPointEffect(2))
                        .name(CardName.THEATER)
                        .build(),
                Card.builder()
                        .type(civil)
                        .age(age)
                        .requiredPlayersCount(6)
                        .cost(freeToPlayCost)
                        .effect(getVictoryPointEffect(2))
                        .name(CardName.THEATER)
                        .build(),

                // CIVIL
                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(4)
                        .cost(freeToPlayCost)
                        .effect(getCoinRewardEffect(5))
                        .name(CardName.TAVERNE)
                        .build(),
                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(5)
                        .cost(freeToPlayCost)
                        .effect(getCoinRewardEffect(5))
                        .name(CardName.TAVERNE)
                        .build(),
                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(7)
                        .cost(freeToPlayCost)
                        .effect(getCoinRewardEffect(5))
                        .name(CardName.TAVERNE)
                        .build(),

                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(freeToPlayCost)
                        .effect(getPreferentialTradingEffect(RIGHT, RAW_MATERIALS))
                        .name(CardName.KONTOR_OST)
                        .build(),
                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(7)
                        .cost(freeToPlayCost)
                        .effect(getPreferentialTradingEffect(RIGHT, RAW_MATERIALS))
                        .name(CardName.KONTOR_OST)
                        .build(),

                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(freeToPlayCost)
                        .effect(getPreferentialTradingEffect(LEFT, RAW_MATERIALS))
                        .name(CardName.KONTOR_WEST)
                        .build(),
                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(7)
                        .cost(freeToPlayCost)
                        .effect(getPreferentialTradingEffect(LEFT, RAW_MATERIALS))
                        .name(CardName.KONTOR_WEST)
                        .build(),

                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(3)
                        .cost(freeToPlayCost)
                        .effect(getPreferentialTradingEffect(BOTH, MANUFACTURED_GOODS))
                        .name(CardName.MARKT)
                        .build(),
                Card.builder()
                        .type(commercial)
                        .age(age)
                        .requiredPlayersCount(6)
                        .cost(freeToPlayCost)
                        .effect(getPreferentialTradingEffect(BOTH, MANUFACTURED_GOODS))
                        .name(CardName.MARKT)
                        .build()
        );
    }

    private static VictoryPointEffect getVictoryPointEffect(int points) {
        return new VictoryPointEffect(points);
    }

    private static CoinRewardEffect getCoinRewardEffect(double coins) {
        return new CoinRewardEffect(coins);
    }

    private static PreferentialTradingEffect getPreferentialTradingEffect(
            EffectDirectionConstraint effectDirectionConstraint,
            PreferentialTradingContract.Type contractType
    ) {
        return new PreferentialTradingEffect(
                effectDirectionConstraint,
                contractType
        );
    }

    private static ResourceEffect getResourceEffect(RawMaterial... rawMaterial) {
        return ResourceEffect.builder()
                .rawMaterialList(List.of(rawMaterial))
                .build();
    }

    private static ResourceEffect getResourceEffect(ManufacturedGood... manufacturedGoods) {
        return ResourceEffect.builder()
                .manufacturedGoodList(List.of(manufacturedGoods))
                .build();
    }
}
