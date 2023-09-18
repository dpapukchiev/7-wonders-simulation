package dpapukchiev.cost;

import dpapukchiev.cards.ManufacturedGood;
import dpapukchiev.cards.SingleManufacturedGoodCard;
import dpapukchiev.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ManufacturedGoodCostTest extends BaseCostTest {
    private ManufacturedGoodCost testInstance;

    @BeforeEach
    void init() {
        initPlayers();
        testInstance = new ManufacturedGoodCost(List.of(
                ManufacturedGood.GLASS,
                ManufacturedGood.GLASS,
                ManufacturedGood.GLASS
        ));
    }

    @Test
    void canBuildFalse() {
        var result = testInstance.generateCostReport(getTurnContext());
        assertFalse(result.isAffordable());
    }

    @Test
    void canBuildNotEnoughFalse() {
        setManufacturedGoodCount(player1, ManufacturedGood.GLASS, 2);

        var result = testInstance.generateCostReport(getTurnContext());
        assertFalse(result.isAffordable());
    }

    @Test
    void canBuildOffOwnResourcesFalse() {
        setManufacturedGoodCount(player1, ManufacturedGood.GLASS, 1);
        setManufacturedGoodCount(player1, ManufacturedGood.SCRIPTS, 3);
        setManufacturedGoodCount(player1, ManufacturedGood.TEXTILE, 2);

        var result = testInstance.generateCostReport(getTurnContext());
        assertFalse(result.isAffordable());
    }

    @Test
    void canBuildOffOwnResources() {
        setManufacturedGoodCount(player1, ManufacturedGood.GLASS, 3);

        var result = testInstance.generateCostReport(getTurnContext());
        assertTrue(result.isAffordable());
    }

    @Test
    void canBuildOffOwnResourcesAndTradeLeft() {
        setManufacturedGoodCount(player1, ManufacturedGood.GLASS, 2);
        setManufacturedGoodCount(player2, ManufacturedGood.GLASS, 1);

        var result = testInstance.generateCostReport(getTurnContext());
        assertTrue(result.isAffordable());
        assertEquals(2, result.getToPayLeft());
    }

    @Test
    void canBuildOffOwnResourcesAndTradeRight() {
        setManufacturedGoodCount(player1, ManufacturedGood.GLASS, 2);
        setManufacturedGoodCount(player3, ManufacturedGood.GLASS, 1);

        var result = testInstance.generateCostReport(getTurnContext());
        assertTrue(result.isAffordable());
        assertEquals(2, result.getToPayRight());
    }

    @Test
    void canBuildOffTradeLeftRight() {
        player1.setCoins(6);

        setManufacturedGoodCount(player3, ManufacturedGood.GLASS, 1);
        setManufacturedGoodCount(player2, ManufacturedGood.GLASS, 2);

        var result = testInstance.generateCostReport(getTurnContext());

        assertTrue(result.isAffordable());
        assertEquals(ManufacturedGood.GLASS.name(), result.getResourcesIncluded());
        assertEquals(2, result.getToPayRight());
        assertEquals(4, result.getToPayLeft());
    }

    @Test
    void canBuildOffTradeLeftRightNotEnoughCoins() {
        player1.setCoins(2);

        setManufacturedGoodCount(player3, ManufacturedGood.GLASS, 1);
        setManufacturedGoodCount(player2, ManufacturedGood.GLASS, 2);

        var result = testInstance.generateCostReport(getTurnContext());

        assertFalse(result.isAffordable());
    }

    @Test
    void canBuildOffTradeLeftRightComplex() {
        player1.setCoins(6);

        testInstance = new ManufacturedGoodCost(List.of(
                ManufacturedGood.GLASS,
                ManufacturedGood.GLASS,
                ManufacturedGood.SCRIPTS
        ));

        setManufacturedGoodCount(player3, ManufacturedGood.SCRIPTS, 1);
        setManufacturedGoodCount(player2, ManufacturedGood.GLASS, 2);

        var result = testInstance.generateCostReport(getTurnContext());

        assertTrue(result.isAffordable());
        assertEquals(ManufacturedGood.GLASS.name() + "," + ManufacturedGood.SCRIPTS.name(), result.getResourcesIncluded());
        assertEquals(2, result.getToPayRight());
        assertEquals(4, result.getToPayLeft());
    }

    @Test
    void canBuildOffOwnComplex() {
        testInstance = new ManufacturedGoodCost(List.of(
                ManufacturedGood.GLASS,
                ManufacturedGood.GLASS,
                ManufacturedGood.SCRIPTS
        ));

        setManufacturedGoodCount(player1, ManufacturedGood.SCRIPTS, 1);
        setManufacturedGoodCount(player1, ManufacturedGood.GLASS, 2);

        var result = testInstance.generateCostReport(getTurnContext());

        assertTrue(result.isAffordable());
        assertEquals(ManufacturedGood.GLASS.name() + "," + ManufacturedGood.SCRIPTS.name(), result.getResourcesIncluded());
        assertEquals(0, result.getToPayRight());
        assertEquals(0, result.getToPayLeft());
    }

    @Test
    void canBuildOffOwnComplexFalse() {
        testInstance = new ManufacturedGoodCost(List.of(
                ManufacturedGood.GLASS,
                ManufacturedGood.GLASS,
                ManufacturedGood.SCRIPTS
        ));

        setManufacturedGoodCount(player1, ManufacturedGood.TEXTILE, 1);
        setManufacturedGoodCount(player1, ManufacturedGood.GLASS,1);

        var result = testInstance.generateCostReport(getTurnContext());

        assertFalse(result.isAffordable());
        assertEquals(ManufacturedGood.GLASS.name() + "," + ManufacturedGood.SCRIPTS.name(), result.getResourcesIncluded());
        assertEquals(0, result.getToPayRight());
        assertEquals(0, result.getToPayLeft());
    }

    private void setManufacturedGoodCount(Player player, ManufacturedGood good, int count) {
        for (int i = 0; i < count; i++) {
            var card = new SingleManufacturedGoodCard("test-" + i, good, 1);
            player.getBuiltCards().add(card);
        }
    }
}