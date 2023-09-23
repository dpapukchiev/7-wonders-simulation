package dpapukchiev.sevenwonderssimulation.wonder;

import dpapukchiev.sevenwonderssimulation.cost.ComplexResourceCost;
import dpapukchiev.sevenwonderssimulation.effects.CoinRewardEffect;
import dpapukchiev.sevenwonderssimulation.effects.PlayFromDiscardedWithVictoryPoints;
import dpapukchiev.sevenwonderssimulation.effects.PreferentialTradingEffect;
import dpapukchiev.sevenwonderssimulation.effects.ResourceEffect;
import dpapukchiev.sevenwonderssimulation.effects.ScienceSymbolsEffect;
import dpapukchiev.sevenwonderssimulation.effects.SpecialActionEffect;
import dpapukchiev.sevenwonderssimulation.effects.VictoryPointEffect;
import dpapukchiev.sevenwonderssimulation.effects.WarShieldsEffect;
import dpapukchiev.sevenwonderssimulation.effects.WonderMultiRewardEffect;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectDirectionConstraint;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectTiming;
import dpapukchiev.sevenwonderssimulation.effects.core.SpecialAction;
import dpapukchiev.sevenwonderssimulation.resources.ManufacturedGood;
import dpapukchiev.sevenwonderssimulation.resources.RawMaterial;
import dpapukchiev.sevenwonderssimulation.resources.ScienceSymbol;
import jsl.modeling.elements.variable.RandomVariable;
import lombok.AllArgsConstructor;

import java.util.List;

import static dpapukchiev.sevenwonderssimulation.effects.core.PreferentialTradingContract.Type.RAW_MATERIALS;
import static dpapukchiev.sevenwonderssimulation.resources.ManufacturedGood.GLASS;
import static dpapukchiev.sevenwonderssimulation.resources.ManufacturedGood.SCRIPTS;
import static dpapukchiev.sevenwonderssimulation.resources.ManufacturedGood.TEXTILE;
import static dpapukchiev.sevenwonderssimulation.resources.RawMaterial.CLAY;
import static dpapukchiev.sevenwonderssimulation.resources.RawMaterial.METAL_ORE;
import static dpapukchiev.sevenwonderssimulation.resources.RawMaterial.STONE;
import static dpapukchiev.sevenwonderssimulation.resources.RawMaterial.WOOD;
import static jsl.utilities.random.rvariable.JSLRandom.randomlySelect;

@AllArgsConstructor
public class WonderTemplate {
    private final RandomVariable pickACity;

    public WonderContext build(CityName cityName) {
        var shouldUseA = randomlySelect(List.of(true, false), pickACity.getStreamNumber());
        return switch (cityName) {
            case ALEXANDRIA -> shouldUseA ? alexandriaA() : alexandriaB();
            case BABYLON -> shouldUseA ? babylonA() : babylonB();
            case EPHESOS -> shouldUseA ? ephesosA() : ephesosB();
            case GIZAH -> shouldUseA ? gizahA() : gizahB();
            case HALIKARNASSOS -> shouldUseA ? halikarnassosA() : halikarnassosB();
            case OLIMPIA -> shouldUseA ? olympiaA() : olympiaB();
            case RHODOS -> shouldUseA ? rhodosA() : rhodosB();
        };
    }

    private static WonderContext alexandriaA() {
        return WonderContext.builder()
                .cityName(CityName.ALEXANDRIA)
                .side("A")
                .wonderStages(List.of(
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(STONE, STONE))
                                .effect(VictoryPointEffect.of(3))
                                .stageNumber(1)
                                .build(),
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(METAL_ORE, METAL_ORE))
                                .effect(ResourceEffect.ofRawMaterials(RawMaterial.all()))
                                .stageNumber(2)
                                .build(),
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(GLASS, GLASS))
                                .effect(VictoryPointEffect.of(7))
                                .stageNumber(3)
                                .build()
                ))
                .build();
    }

    private static WonderContext alexandriaB() {
        return WonderContext.builder()
                .cityName(CityName.ALEXANDRIA)
                .side("B")
                .wonderStages(List.of(
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(CLAY, CLAY))
                                .effect(ResourceEffect.ofRawMaterials(RawMaterial.all()))
                                .stageNumber(1)
                                .build(),
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(WOOD, WOOD))
                                .effect(ResourceEffect.ofManufacturedGoods(ManufacturedGood.all()))
                                .stageNumber(2)
                                .build(),
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(STONE, STONE, STONE))
                                .effect(VictoryPointEffect.of(7))
                                .stageNumber(3)
                                .build()
                ))
                .build();
    }

    private static WonderContext babylonA() {
        return WonderContext.builder()
                .cityName(CityName.BABYLON)
                .side("A")
                .wonderStages(List.of(
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(CLAY, CLAY))
                                .effect(VictoryPointEffect.of(3))
                                .stageNumber(1)
                                .build(),
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(WOOD, WOOD, WOOD))
                                .effect(ScienceSymbolsEffect.of(ScienceSymbol.all()))
                                .stageNumber(2)
                                .build(),
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(CLAY, CLAY, CLAY))
                                .effect(VictoryPointEffect.of(7))
                                .stageNumber(3)
                                .build()
                ))
                .build();
    }

    private static WonderContext babylonB() {
        return WonderContext.builder()
                .cityName(CityName.BABYLON)
                .side("B")
                .wonderStages(List.of(
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(CLAY, TEXTILE))
                                .effect(VictoryPointEffect.of(3))
                                .stageNumber(1)
                                .build(),
                        WonderStage.builder()
                                .cost(ComplexResourceCost.builder()
                                        .manufacturedGoodsList(List.of(GLASS))
                                        .rawMaterialList(List.of(WOOD, WOOD))
                                        .build()
                                )
                                .effect(SpecialActionEffect.of(
                                        SpecialAction.PLAY_BOTH_CARDS_AT_LAST_TURN_IN_AGE,
                                        EffectTiming.END_OF_AGE
                                ))
                                .stageNumber(2)
                                .build(),
                        WonderStage.builder()
                                .cost(ComplexResourceCost.builder()
                                        .manufacturedGoodsList(List.of(SCRIPTS))
                                        .rawMaterialList(List.of(CLAY, CLAY, CLAY))
                                        .build()
                                )
                                .effect(ScienceSymbolsEffect.of(ScienceSymbol.all()))
                                .stageNumber(3)
                                .build()
                ))
                .build();
    }

    private static WonderContext ephesosA() {
        return WonderContext.builder()
                .cityName(CityName.EPHESOS)
                .side("A")
                .wonderStages(List.of(
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(STONE, STONE))
                                .effect(VictoryPointEffect.of(3))
                                .stageNumber(1)
                                .build(),
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(WOOD, WOOD))
                                .effect(CoinRewardEffect.of(9))
                                .stageNumber(2)
                                .build(),
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(SCRIPTS, SCRIPTS))
                                .effect(VictoryPointEffect.of(7))
                                .stageNumber(3)
                                .build()
                ))
                .build();
    }

    private static WonderContext ephesosB() {
        return WonderContext.builder()
                .cityName(CityName.EPHESOS)
                .side("B")
                .wonderStages(List.of(
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(STONE, STONE))
                                .effect(WonderMultiRewardEffect.of(
                                        0,
                                        4,
                                        2
                                ))
                                .stageNumber(1)
                                .build(),
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(WOOD, WOOD))
                                .effect(WonderMultiRewardEffect.of(
                                        0,
                                        4,
                                        3
                                ))
                                .stageNumber(2)
                                .build(),
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(ManufacturedGood.all()))
                                .effect(WonderMultiRewardEffect.of(
                                        0,
                                        4,
                                        5
                                ))
                                .stageNumber(3)
                                .build()
                ))
                .build();
    }

    private static WonderContext gizahA() {
        return WonderContext.builder()
                .cityName(CityName.GIZAH)
                .side("A")
                .wonderStages(List.of(
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(STONE, STONE))
                                .effect(VictoryPointEffect.of(3))
                                .stageNumber(1)
                                .build(),
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(WOOD, WOOD, WOOD))
                                .effect(VictoryPointEffect.of(5))
                                .stageNumber(2)
                                .build(),
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(STONE, STONE, STONE, STONE))
                                .effect(VictoryPointEffect.of(7))
                                .stageNumber(3)
                                .build()
                ))
                .build();
    }

    private static WonderContext gizahB() {
        return WonderContext.builder()
                .cityName(CityName.GIZAH)
                .side("B")
                .wonderStages(List.of(
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(WOOD, WOOD))
                                .effect(VictoryPointEffect.of(3))
                                .stageNumber(1)
                                .build(),
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(STONE, STONE, STONE))
                                .effect(VictoryPointEffect.of(5))
                                .stageNumber(2)
                                .build(),
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(CLAY, CLAY, CLAY))
                                .effect(VictoryPointEffect.of(5))
                                .stageNumber(3)
                                .build(),
                        WonderStage.builder()
                                .cost(ComplexResourceCost.builder()
                                        .rawMaterialList(List.of(STONE, STONE, STONE, STONE))
                                        .manufacturedGoodsList(List.of(SCRIPTS))
                                        .build())
                                .effect(VictoryPointEffect.of(3))
                                .stageNumber(4)
                                .build()
                ))
                .build();
    }

    private static WonderContext halikarnassosA() {
        return WonderContext.builder()
                .cityName(CityName.HALIKARNASSOS)
                .side("A")
                .wonderStages(List.of(
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(CLAY, CLAY))
                                .effect(VictoryPointEffect.of(3))
                                .stageNumber(1)
                                .build(),
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(METAL_ORE, METAL_ORE, METAL_ORE))
                                .effect(PlayFromDiscardedWithVictoryPoints.of(0))
                                .stageNumber(2)
                                .build(),
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(List.of(TEXTILE, TEXTILE)))
                                .effect(VictoryPointEffect.of(7))
                                .stageNumber(3)
                                .build()
                ))
                .build();
    }

    private static WonderContext halikarnassosB() {
        return WonderContext.builder()
                .cityName(CityName.HALIKARNASSOS)
                .side("B")
                .wonderStages(List.of(
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(METAL_ORE, METAL_ORE))
                                .effect(PlayFromDiscardedWithVictoryPoints.of(2))
                                .stageNumber(1)
                                .build(),
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(CLAY, CLAY, CLAY))
                                .effect(PlayFromDiscardedWithVictoryPoints.of(2))
                                .stageNumber(2)
                                .build(),
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(ManufacturedGood.all()))
                                .effect(SpecialActionEffect.of(
                                        SpecialAction.PLAY_CARD_FROM_DISCARD,
                                        EffectTiming.ANYTIME
                                ))
                                .stageNumber(3)
                                .build()
                ))
                .build();
    }

    private static WonderContext olympiaA() {
        return WonderContext.builder()
                .cityName(CityName.OLIMPIA)
                .side("A")
                .wonderStages(List.of(
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(WOOD, WOOD))
                                .effect(VictoryPointEffect.of(3))
                                .stageNumber(1)
                                .build(),
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(STONE, STONE))
                                .effect(SpecialActionEffect.of(
                                        SpecialAction.PLAY_CARD_WITHOUT_COST,
                                        EffectTiming.ANYTIME
                                ))
                                .stageNumber(2)
                                .build(),
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(METAL_ORE, METAL_ORE))
                                .effect(VictoryPointEffect.of(7))
                                .stageNumber(3)
                                .build()
                ))
                .build();
    }

    private static WonderContext olympiaB() {
        return WonderContext.builder()
                .cityName(CityName.OLIMPIA)
                .side("B")
                .wonderStages(List.of(
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(WOOD, WOOD))
                                .effect(PreferentialTradingEffect.forDirectionAndType(
                                        EffectDirectionConstraint.BOTH,
                                        RAW_MATERIALS
                                ))
                                .stageNumber(1)
                                .build(),
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(STONE, STONE))
                                .effect(VictoryPointEffect.of(5))
                                .stageNumber(2)
                                .build(),
                        WonderStage.builder()
                                .cost(ComplexResourceCost.builder()
                                        .manufacturedGoodsList(List.of(TEXTILE))
                                        .rawMaterialList(List.of(METAL_ORE, METAL_ORE))
                                        .build()
                                )
                                .effect(SpecialActionEffect.of(
                                        SpecialAction.COPY_GUILD_CARD,
                                        EffectTiming.END_OF_TURN
                                ))
                                .stageNumber(3)
                                .build()
                ))
                .build();
    }

    private static WonderContext rhodosA() {
        return WonderContext.builder()
                .cityName(CityName.RHODOS)
                .side("A")
                .wonderStages(List.of(
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(WOOD, WOOD))
                                .effect(VictoryPointEffect.of(3))
                                .stageNumber(1)
                                .build(),
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(CLAY, CLAY, CLAY))
                                .effect(WarShieldsEffect.of(2))
                                .stageNumber(2)
                                .build(),
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(METAL_ORE, METAL_ORE, METAL_ORE, METAL_ORE))
                                .effect(VictoryPointEffect.of(7))
                                .stageNumber(3)
                                .build()
                ))
                .build();
    }

    private static WonderContext rhodosB() {
        return WonderContext.builder()
                .cityName(CityName.RHODOS)
                .side("B")
                .wonderStages(List.of(
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(STONE, STONE, STONE))
                                .effect(WonderMultiRewardEffect.of(
                                        1,
                                        3,
                                        3
                                ))
                                .stageNumber(1)
                                .build(),
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(METAL_ORE, METAL_ORE, METAL_ORE, METAL_ORE))
                                .effect(WonderMultiRewardEffect.of(
                                        1,
                                        4,
                                        4
                                ))
                                .stageNumber(2)
                                .build()
                ))
                .build();
    }
}
