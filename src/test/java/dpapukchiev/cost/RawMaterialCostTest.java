package dpapukchiev.cost;

import dpapukchiev.cards.RawMaterial;
import dpapukchiev.cards.SingleResourceCard;
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
class RawMaterialCostTest extends BaseCostTest {
    private RawMaterialCost testInstance;

    @BeforeEach
    void init() {
        initPlayers();
        testInstance = new RawMaterialCost(List.of(
                RawMaterial.WOOD,
                RawMaterial.WOOD,
                RawMaterial.WOOD
        ));
    }

    @Test
    void canBuildFalse() {
        var result = testInstance.generateCostReport(getTurnContext());
        assertFalse(result.isAffordable());
    }

    @Test
    void canBuildNotEnoughFalse() {
        setRawMaterialCount(player1, RawMaterial.WOOD, 2);

        var result = testInstance.generateCostReport(getTurnContext());
        assertFalse(result.isAffordable());
    }

    @Test
    void canBuildOffOwnResourcesFalse() {
        setRawMaterialCount(player1, RawMaterial.CLAY, 1);
        setRawMaterialCount(player1, RawMaterial.STONE, 3);
        setRawMaterialCount(player1, RawMaterial.METAL_ORE, 2);

        var result = testInstance.generateCostReport(getTurnContext());
        assertFalse(result.isAffordable());
    }

    @Test
    void canBuildOffOwnResources() {
        setRawMaterialCount(player1, RawMaterial.WOOD, 3);

        var result = testInstance.generateCostReport(getTurnContext());
        assertTrue(result.isAffordable());
    }

    @Test
    void canBuildOffOwnResourcesAndTradeLeft() {
        setRawMaterialCount(player1, RawMaterial.WOOD, 2);
        setRawMaterialCount(player2, RawMaterial.WOOD, 1);

        var result = testInstance.generateCostReport(getTurnContext());
        assertTrue(result.isAffordable());
        assertEquals(2, result.getToPayLeft());
    }

    @Test
    void canBuildOffOwnResourcesAndTradeRight() {
        setRawMaterialCount(player1, RawMaterial.WOOD, 2);
        setRawMaterialCount(player3, RawMaterial.WOOD, 1);

        var result = testInstance.generateCostReport(getTurnContext());
        assertTrue(result.isAffordable());
        assertEquals(2, result.getToPayRight());
    }

    @Test
    void canBuildOffTradeLeftRight() {
        player1.setCoins(6);

        setRawMaterialCount(player3, RawMaterial.WOOD, 1);
        setRawMaterialCount(player2, RawMaterial.WOOD, 2);

        var result = testInstance.generateCostReport(getTurnContext());

        assertTrue(result.isAffordable());
        assertEquals(RawMaterial.WOOD.name(), result.getMissingResource());
        assertEquals(2, result.getToPayRight());
        assertEquals(4, result.getToPayLeft());
    }

    @Test
    void canBuildOffTradeLeftRightNotEnoughCoins() {
        player1.setCoins(2);

        setRawMaterialCount(player3, RawMaterial.WOOD, 1);
        setRawMaterialCount(player2, RawMaterial.WOOD, 2);

        var result = testInstance.generateCostReport(getTurnContext());

        assertFalse(result.isAffordable());
    }

    @Test
    void canBuildOffTradeLeftRightComplex() {
        player1.setCoins(6);

        testInstance = new RawMaterialCost(List.of(
                RawMaterial.WOOD,
                RawMaterial.WOOD,
                RawMaterial.CLAY
        ));

        setRawMaterialCount(player3, RawMaterial.CLAY, 1);
        setRawMaterialCount(player2, RawMaterial.WOOD, 2);

        var result = testInstance.generateCostReport(getTurnContext());

        assertTrue(result.isAffordable());
        assertEquals(RawMaterial.WOOD.name() + "," + RawMaterial.CLAY.name(), result.getMissingResource());
        assertEquals(2, result.getToPayRight());
        assertEquals(4, result.getToPayLeft());
    }

    private void setRawMaterialCount(Player player, RawMaterial material, int count) {
        for (int i = 0; i < count; i++) {
            var card = new SingleResourceCard("test-" + i, material, 1);
            player.getBuiltCards().add(card);
        }
    }
}