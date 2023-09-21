package dpapukchiev.sevenwonderssimulation.cost;

import dpapukchiev.sevenwonderssimulation.BasePlayerTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FreeToPlayCostTest extends BasePlayerTest {

    @Test
    void generateCostReport() {
        var cost = new FreeToPlayCost();

        assertTrue(cost.generateCostReport(getTurnContext())
                .isAffordable());
    }
}