package dpapukchiev.sevenwonderssimulation.wonder;

import dpapukchiev.sevenwonderssimulation.cost.ComplexResourceCost;
import dpapukchiev.sevenwonderssimulation.effects.CoinRewardEffect;
import dpapukchiev.sevenwonderssimulation.effects.SpecialActionEffect;
import dpapukchiev.sevenwonderssimulation.effects.VictoryPointEffect;
import dpapukchiev.sevenwonderssimulation.effects.WarShieldsEffect;
import dpapukchiev.sevenwonderssimulation.effects.WonderMultiRewardEffect;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectTiming;
import dpapukchiev.sevenwonderssimulation.effects.core.SpecialAction;
import dpapukchiev.sevenwonderssimulation.resources.ManufacturedGood;
import jsl.modeling.elements.variable.RandomVariable;
import lombok.AllArgsConstructor;

import java.util.List;

import static dpapukchiev.sevenwonderssimulation.resources.ManufacturedGood.SCRIPTS;
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
        var defaultCity = WonderContext.builder()
                .cityName(cityName)
                .side("D")
                .build();
        return switch (cityName) {
            case BABYLON -> defaultCity;
            case RHODOS -> shouldUseA ? rhodosA() : rhodosB();
//            case RHODOS -> defaultCity;
            case OLIMPIA -> shouldUseA ? olympiaA() : defaultCity;
            case ALEXANDRIA -> defaultCity;
            case HALIKARNASSOS -> defaultCity;
            case GIZAH -> shouldUseA ? gizahA() : gizahB();
//            case GIZAH -> defaultCity;
            case EPHESOS -> shouldUseA ? ephesosA() : ephesosB();
//            case EPHESOS -> defaultCity;
        };
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
                                .stageNumber(7)
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
}
