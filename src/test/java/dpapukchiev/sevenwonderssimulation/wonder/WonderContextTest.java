package dpapukchiev.sevenwonderssimulation.wonder;

import dpapukchiev.sevenwonderssimulation.BasePlayerTest;
import dpapukchiev.sevenwonderssimulation.cost.CoinCost;
import dpapukchiev.sevenwonderssimulation.cost.Cost;
import dpapukchiev.sevenwonderssimulation.effects.VictoryPointEffect;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WonderContextTest extends BasePlayerTest {

    @Test
    void getNextAffordableWonderStage() {
        var stage1 = WonderStage.builder()
                .cost(CoinCost.of(4))
                .effect(VictoryPointEffect.of(3))
                .stageNumber(1)
                .build();
        var stage2 = WonderStage.builder()
                .cost(CoinCost.of(6))
                .effect(VictoryPointEffect.of(4))
                .stageNumber(2)
                .build();
        var stage3 = WonderStage.builder()
                .cost(CoinCost.of(8))
                .effect(VictoryPointEffect.of(5))
                .stageNumber(3)
                .build();

        var wonderContext = WonderContext.builder()
                .cityName(CityName.ALEXANDRIA)
                .wonderStages(List.of(
                        stage1,
                        stage2,
                        stage3
                ))
                .build();

        assertNextAvailableStage(wonderContext, Optional.empty());
        mainPlayer.getVault().setCoins(8);
        assertNextAvailableStage(wonderContext, Optional.of(stage1));
        stage1.setBuilt(true);

        mainPlayer.getVault().setCoins(5);
        assertNextAvailableStage(wonderContext, Optional.empty());
        mainPlayer.getVault().setCoins(6);
        assertNextAvailableStage(wonderContext, Optional.of(stage2));
        stage2.setBuilt(true);

        assertNextAvailableStage(wonderContext, Optional.empty());
        mainPlayer.getVault().setCoins(8);
        assertNextAvailableStage(wonderContext, Optional.of(stage3));
        stage3.setBuilt(true);

        assertNextAvailableStage(wonderContext, Optional.empty());
    }

    private void assertNextAvailableStage(WonderContext wonderContext, Optional<WonderStage> expectedStage) {
        var result = wonderContext.getNextAffordableWonderStage(getTurnContext());
        assertEquals(result.isPresent(), expectedStage.isPresent());

        expectedStage.ifPresent(wonderStage -> assertEquals(wonderStage, result.get().getLeft()));
    }
}