package dpapukchiev.cost;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class CoinCostTest extends BaseCostTest {
    private CoinCost testInstance;

    @BeforeEach
    void init() {
        initPlayers();
        testInstance = new CoinCost(1);
    }

    @Test
    void canBuildFalse() {
        player1.setCoins(0);

        var result = testInstance.generateCostReport(getTurnContext());
        assertFalse(result.isAffordable());
    }

    @Test
    void canBuildTrue() {
        player1.setCoins(3);

        var result = testInstance.generateCostReport(getTurnContext());
        assertTrue(result.isAffordable());
    }

    @Test
    void applyCost() {
        player1.setCoins(3);

        testInstance.applyCost(getTurnContext(), CostReport.builder()
                .affordable(true)
                .toPayBank(1)
                .build());

        assertEquals(2, player1.getCoins());
    }
}