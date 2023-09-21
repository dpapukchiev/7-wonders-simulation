package dpapukchiev.sevenwonderssimulation.cost;

import dpapukchiev.sevenwonderssimulation.BasePlayerTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoinCostTest extends BasePlayerTest {

    @Test
    void generateCostReport() {
        mainPlayer.getVault().setCoins(0);
        assertToPayBank(2, -1);

        mainPlayer.getVault().setCoins(2);
        assertToPayBank(2, 2);
        assertToPayBank(3, -1);

        mainPlayer.getVault().setCoins(3);
        assertToPayBank(3, 3);
        assertToPayBank(4, -1);

        mainPlayer.getVault().setCoins(4);
        assertToPayBank(4, 4);
    }

    private void assertToPayBank(int requiredCoins, int expectedToPayBank) {
        var cost = CoinCost.builder()
                .requiredCoins(requiredCoins)
                .build();

        var result = cost.generateCostReport(getTurnContext());

        if (expectedToPayBank < 0) {
            assertFalse(result.isAffordable());
            assertEquals(0, result.getToPayBank());
            return;
        }

        assertTrue(result.isAffordable());
        assertEquals(expectedToPayBank, result.getToPayBank());
    }
}