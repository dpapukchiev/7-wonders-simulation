package dpapukchiev.sevenwonderssimulation.wonder;

import dpapukchiev.sevenwonderssimulation.cost.ComplexResourceCost;
import dpapukchiev.sevenwonderssimulation.effects.CoinRewardAndVictoryPointWithModifiersEffect;
import dpapukchiev.sevenwonderssimulation.effects.CoinRewardEffect;
import dpapukchiev.sevenwonderssimulation.effects.VictoryPointEffect;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectDirectionConstraint;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectMultiplierType;
import dpapukchiev.sevenwonderssimulation.resources.ManufacturedGood;
import jsl.modeling.elements.variable.RandomVariable;
import lombok.AllArgsConstructor;

import java.util.List;

import static dpapukchiev.sevenwonderssimulation.resources.ManufacturedGood.SCRIPTS;
import static dpapukchiev.sevenwonderssimulation.resources.RawMaterial.STONE;
import static dpapukchiev.sevenwonderssimulation.resources.RawMaterial.WOOD;
import static jsl.utilities.random.rvariable.JSLRandom.randomlySelect;

@AllArgsConstructor
public class WonderTemplate {
    private final RandomVariable pickACity;
    public WonderContext build(CityName cityName) {
        var defaultCity = WonderContext.builder()
                .cityName(cityName)
                .build();
        var shouldUseA = randomlySelect(List.of(true, false), pickACity.getStreamNumber());
        return switch (cityName) {
            case BABYLON -> defaultCity;
            case RHODOS -> defaultCity;
            case OLIMPIA -> defaultCity;
            case ALEXANDRIA -> defaultCity;
            case HALIKARNASSOS -> defaultCity;
            case GIZAH -> defaultCity;
            case EPHESOS -> shouldUseA ? ephesosA() : ephesosB();
        };
    }

    private static WonderContext ephesosA() {
        return WonderContext.builder()
                .cityName(CityName.EPHESOS)
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
                .wonderStages(List.of(
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(STONE, STONE))
                                .effect(CoinRewardAndVictoryPointWithModifiersEffect.of(
                                        EffectDirectionConstraint.SELF,
                                        EffectMultiplierType.ONE,
                                        4,
                                        2
                                ))
                                .stageNumber(1)
                                .build(),
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(WOOD, WOOD))
                                .effect(CoinRewardAndVictoryPointWithModifiersEffect.of(
                                        EffectDirectionConstraint.SELF,
                                        EffectMultiplierType.ONE,
                                        4,
                                        3
                                ))
                                .stageNumber(2)
                                .build(),
                        WonderStage.builder()
                                .cost(ComplexResourceCost.of(ManufacturedGood.all()))
                                .effect(CoinRewardAndVictoryPointWithModifiersEffect.of(
                                        EffectDirectionConstraint.SELF,
                                        EffectMultiplierType.ONE,
                                        4,
                                        5
                                ))
                                .stageNumber(3)
                                .build()
                ))
                .build();
    }
}
