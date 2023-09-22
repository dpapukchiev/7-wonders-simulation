package dpapukchiev.sevenwonderssimulation.wonder;

import dpapukchiev.sevenwonderssimulation.cost.ComplexResourceCost;
import dpapukchiev.sevenwonderssimulation.effects.CoinRewardEffect;
import dpapukchiev.sevenwonderssimulation.effects.VictoryPointEffect;

import java.util.List;

import static dpapukchiev.sevenwonderssimulation.resources.ManufacturedGood.SCRIPTS;
import static dpapukchiev.sevenwonderssimulation.resources.RawMaterial.STONE;
import static dpapukchiev.sevenwonderssimulation.resources.RawMaterial.WOOD;

public class WonderTemplate {
    public static WonderContext build(CityName cityName) {
        var defaultCity = WonderContext.builder()
                .cityName(cityName)
                .build();
        return switch (cityName) {
            case EPHESOS_A -> ephesosA(cityName);
            case EPHESOS_B -> defaultCity;
            case BABYLON_A -> defaultCity;
            case RHODOS_A -> defaultCity;
            case OLIMPIA_A -> defaultCity;
            case ALEXANDRIA_A -> defaultCity;
            case HALIKARNASSOS_A -> defaultCity;
            case GIZAH_A -> defaultCity;
            case BABYLON_B -> defaultCity;
            case RHODOS_B -> defaultCity;
            case OLIMPIA_B -> defaultCity;
            case ALEXANDRIA_B -> defaultCity;
            case HALIKARNASSOS_B -> defaultCity;
            case GIZAH_B -> defaultCity;
        };
    }

    private static WonderContext ephesosA(CityName cityName) {
        return WonderContext.builder()
                .cityName(cityName)
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
}
