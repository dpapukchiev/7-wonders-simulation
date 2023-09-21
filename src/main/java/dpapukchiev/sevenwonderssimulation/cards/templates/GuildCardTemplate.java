package dpapukchiev.sevenwonderssimulation.cards.templates;

import dpapukchiev.sevenwonderssimulation.cards.Card;
import dpapukchiev.sevenwonderssimulation.cards.CardName;
import dpapukchiev.sevenwonderssimulation.cards.CardType;
import dpapukchiev.sevenwonderssimulation.effects.GuildVictoryPointsForCardsEffect;
import dpapukchiev.sevenwonderssimulation.effects.ScienceSymbolsEffect;
import dpapukchiev.sevenwonderssimulation.effects.VictoryPointWithModifiersEffect;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectDirectionConstraint;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectMultiplierType;
import dpapukchiev.sevenwonderssimulation.resources.ManufacturedGood;
import dpapukchiev.sevenwonderssimulation.resources.RawMaterial;
import dpapukchiev.sevenwonderssimulation.resources.ScienceSymbol;
import lombok.AllArgsConstructor;

import java.util.List;

import static dpapukchiev.sevenwonderssimulation.resources.ManufacturedGood.GLASS;
import static dpapukchiev.sevenwonderssimulation.resources.ManufacturedGood.SCRIPTS;
import static dpapukchiev.sevenwonderssimulation.resources.RawMaterial.METAL_ORE;
import static dpapukchiev.sevenwonderssimulation.resources.RawMaterial.WOOD;

@AllArgsConstructor
public class GuildCardTemplate extends BaseCardTemplate {
    private final int age;

    public static GuildCardTemplate create() {
        return new GuildCardTemplate(3);
    }

    public Card createGildeDerWissenschaftlerCard() {
        var cost = createCost(List.of(WOOD, WOOD, METAL_ORE, METAL_ORE), List.of(SCRIPTS, GLASS));
        return Card.builder()
                .name(CardName.GILDE_DER_WISSENSCHAFTLER)
                .age(age)
                .requiredPlayersCount(3)
                .cost(cost)
                .effect(ScienceSymbolsEffect.of(ScienceSymbol.all()))
                .type(CardType.GUILD)
                .build();

    }

    public Card createGildeDerReederCard(){
        var cost = createCost(List.of(WOOD, WOOD, WOOD), List.of(SCRIPTS, GLASS));
        return Card.builder()
                .name(CardName.GILDE_DER_REEDER)
                .age(age)
                .requiredPlayersCount(3)
                .cost(cost)
                .effect(GuildVictoryPointsForCardsEffect.newInstance())
                .type(CardType.GUILD)
                .build();

    }

    public Card createCardsForTypeAndDirection(
            CardName name,
            List<RawMaterial> rawMaterialsCost,
            List<ManufacturedGood> manufacturedGoodsCost,
            EffectMultiplierType effectMultiplierType,
            EffectDirectionConstraint effectDirectionConstraint,
            int victoryPoints
    ) {
        var cost = createCost(rawMaterialsCost, manufacturedGoodsCost);
        return Card.builder()
                .name(name)
                .age(age)
                .requiredPlayersCount(3)
                .cost(cost)
                .effect(VictoryPointWithModifiersEffect.of(
                        effectDirectionConstraint,
                        effectMultiplierType,
                        victoryPoints
                ))
                .type(CardType.GUILD)
                .build();

    }
}
