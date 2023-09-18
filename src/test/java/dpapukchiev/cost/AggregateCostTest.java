package dpapukchiev.cost;

import dpapukchiev.cards.CommercialTradingCard;
import dpapukchiev.cards.ManufacturedGood;
import dpapukchiev.cards.RawMaterial;
import dpapukchiev.cards.SingleManufacturedGoodCard;
import dpapukchiev.cards.SingleResourceCard;
import dpapukchiev.effects.PreferentialTrading;
import dpapukchiev.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static dpapukchiev.cards.ManufacturedGood.GLASS;
import static dpapukchiev.cards.RawMaterial.WOOD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AggregateCostTest extends BaseCostTest {

    @BeforeEach
    void init() {
        initPlayers();
    }

    @Test
    void generateCostReport() {
        var manufacturedGoodCost = new ManufacturedGoodCost(List.of(GLASS));
        var coinCost = new CoinCost(1);
        var testInstance = new AggregateCost(List.of(coinCost, manufacturedGoodCost));

        setManufacturedGoodCount(player1, GLASS, 3);

        var costReport = testInstance.generateCostReport(getTurnContext());

        assertTrue(costReport.isAffordable());
    }

    @Test
    void generateCostReportMixedResources() {
        var manufacturedGoodCost = new ManufacturedGoodCost(List.of(GLASS));
        var rawMaterialCost = new RawMaterialCost(List.of(WOOD));
        var testInstance = new AggregateCost(List.of(rawMaterialCost, manufacturedGoodCost));

        setManufacturedGoodCount(player1, GLASS, 1);
        setRawMaterialCount(player1, WOOD, 1);

        var costReport = testInstance.generateCostReport(getTurnContext());

        assertTrue(costReport.isAffordable());
    }

    @CsvSource({
            "1,1,1,1,true",
            "1,1,1,2,true",
            "1,1,2,1,true",
            "1,1,2,2,true",
            "0,1,0,0,false",
            "1,0,0,0,false",
            "2,2,1,1,false",
    })
    @ParameterizedTest
    void generateCostReportMixedResourcesFalse(double woodCost, double glassCost, int playerWood, int playerGlass, boolean isAffordable) {
        List<RawMaterial> wood = new ArrayList<>();
        for (int i = 0; i < woodCost; i++) {
            wood.add(WOOD);
        }
        List<ManufacturedGood> glass = new ArrayList<>();
        for (int i = 0; i < glassCost; i++) {
            glass.add(GLASS);
        }

        var manufacturedGoodCost = new ManufacturedGoodCost(glass);
        var rawMaterialCost = new RawMaterialCost(wood);
        var testInstance = new AggregateCost(List.of(rawMaterialCost, manufacturedGoodCost));

        setManufacturedGoodCount(player1, GLASS, playerGlass);
        setRawMaterialCount(player1, WOOD, playerWood);

        var costReport = testInstance.generateCostReport(getTurnContext());

        if (isAffordable) {
            assertTrue(costReport.isAffordable());
        } else {
            assertFalse(costReport.isAffordable());
        }
    }

    @Test
    void generateCostReportTwoCoins() {
        var coinCost = new CoinCost(1);
        var testInstance = new AggregateCost(List.of(coinCost, coinCost));

        setManufacturedGoodCount(player1, GLASS, 3);

        var costReport = testInstance.generateCostReport(getTurnContext());

        assertTrue(costReport.isAffordable());
        assertEquals(2, costReport.getToPayBank());
    }

    @Test
    void canBuildOffTradeLeftRightComplexWithPreferentialLeft() {
        // player 2 on the left
        // player 3 on the right
        player1.setCoins(4);
        player1.setBuiltCards(Collections.singletonList(new CommercialTradingCard(
                1, "test", 1, new PreferentialTrading(
                PreferentialTrading.PreferentialTradingType.LEFT,
                List.of(ManufacturedGood.GLASS),
                List.of()
        ))));

        var manufacturedGoodCost = new ManufacturedGoodCost(List.of(
                ManufacturedGood.GLASS,
                ManufacturedGood.GLASS,
                ManufacturedGood.SCRIPTS
        ));
        var coinCost = new CoinCost(1);
        var testInstance = new AggregateCost(List.of(coinCost, manufacturedGoodCost));

        setManufacturedGoodCount(player2, ManufacturedGood.SCRIPTS, 1);
        setManufacturedGoodCount(player3, ManufacturedGood.GLASS, 2);

        var result = testInstance.generateCostReport(getTurnContext());

        assertFalse(result.isAffordable());
        assertEquals(ManufacturedGood.GLASS.name() + "," + ManufacturedGood.SCRIPTS.name(), result.getResourcesIncluded());
    }

    @CsvSource({
            "1,false",
            "2,false",
            "3,false",
            "4,false",
            "5,false",
            "6,false",
            "7,true",
            "9,true",
    })
    @ParameterizedTest
    void canBuildOffTradeLeftRightComplexWithPreferentialLeftCanAfford(double playerCoins, boolean isAffordable) {
        // player 2 on the left
        // player 3 on the right
        player1.setCoins(playerCoins);
        player1.setBuiltCards(Collections.singletonList(new CommercialTradingCard(
                1, "test", 1, new PreferentialTrading(
                PreferentialTrading.PreferentialTradingType.LEFT,
                List.of(ManufacturedGood.GLASS),
                List.of()
        ))));

        var manufacturedGoodCost = new ManufacturedGoodCost(List.of(
                ManufacturedGood.GLASS,
                ManufacturedGood.GLASS,
                ManufacturedGood.SCRIPTS
        ));
        var coinCost = new CoinCost(1);
        var testInstance = new AggregateCost(List.of(coinCost, manufacturedGoodCost));

        setManufacturedGoodCount(player2, ManufacturedGood.SCRIPTS, 1);
        setManufacturedGoodCount(player3, ManufacturedGood.GLASS, 2);

        var result = testInstance.generateCostReport(getTurnContext());

        assertEquals(ManufacturedGood.GLASS.name() + "," + ManufacturedGood.SCRIPTS.name(), result.getResourcesIncluded());
        if (isAffordable) {
            assertTrue(result.isAffordable());
        } else {
            assertFalse(result.isAffordable());
        }

        assertEquals(2, result.getToPayLeft());
        assertEquals(4, result.getToPayRight());
        assertEquals(1, result.getToPayBank());
    }

    @Test
    void applyCost() {
    }

    private void setManufacturedGoodCount(Player player, ManufacturedGood good, int count) {
        for (int i = 0; i < count; i++) {
            var card = new SingleManufacturedGoodCard(1, "test-" + i, good, 1);
            player.getBuiltCards().add(card);
        }
    }

    private void setRawMaterialCount(Player player, RawMaterial material, int count) {
        for (int i = 0; i < count; i++) {
            var card = new SingleResourceCard(1, "test-" + i, material, 1);
            player.getBuiltCards().add(card);
        }
    }
}