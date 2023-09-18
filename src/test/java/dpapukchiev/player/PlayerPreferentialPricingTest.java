package dpapukchiev.player;

import dpapukchiev.cards.CommercialTradingCard;
import dpapukchiev.cards.ManufacturedGood;
import dpapukchiev.cards.RawMaterial;
import dpapukchiev.effects.PreferentialTrading;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dpapukchiev.cards.RawMaterial.CLAY;
import static dpapukchiev.cards.RawMaterial.WOOD;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerPreferentialPricingTest extends BasePlayerTest {

    @BeforeEach
    void init() {
        initPlayers();
    }

    @Test
    void getRawMaterialCountSingle() {
        assertEquals(2, player1.getTradingPriceRight(ManufacturedGood.GLASS));
        assertEquals(2, player1.getTradingPriceLeft(ManufacturedGood.GLASS));

        assertEquals(2, player1.getTradingPriceRight(RawMaterial.METAL_ORE));
        assertEquals(2, player1.getTradingPriceLeft(RawMaterial.METAL_ORE));
    }

    @Test
    void preferentialBothManufacturedGood() {
        player1.setBuiltCards(List.of(
                new CommercialTradingCard("test", 1, new PreferentialTrading(
                        PreferentialTrading.PreferentialTradingType.BOTH,
                        List.of(ManufacturedGood.GLASS),
                        List.of()
                ))
        ));

        assertEquals(1, player1.getTradingPriceLeft(ManufacturedGood.GLASS));
        assertEquals(1, player1.getTradingPriceRight(ManufacturedGood.GLASS));

        assertEquals(2, player1.getTradingPriceLeft(ManufacturedGood.SCRIPTS));
        assertEquals(2, player1.getTradingPriceRight(ManufacturedGood.SCRIPTS));

        assertEquals(2, player1.getTradingPriceLeft(CLAY));
        assertEquals(2, player1.getTradingPriceRight(CLAY));
    }

    @Test
    void preferentialLeftManufacturedGood() {
        player1.setBuiltCards(List.of(
                new CommercialTradingCard("test", 1, new PreferentialTrading(
                        PreferentialTrading.PreferentialTradingType.LEFT,
                        List.of(ManufacturedGood.GLASS),
                        List.of()
                ))
        ));

        assertEquals(1, player1.getTradingPriceLeft(ManufacturedGood.GLASS));
        assertEquals(2, player1.getTradingPriceLeft(ManufacturedGood.SCRIPTS));

        assertEquals(2, player1.getTradingPriceRight(ManufacturedGood.GLASS));
        assertEquals(2, player1.getTradingPriceRight(ManufacturedGood.SCRIPTS));

        assertEquals(2, player1.getTradingPriceLeft(CLAY));
        assertEquals(2, player1.getTradingPriceRight(CLAY));
    }

    @Test
    void preferentialRightManufacturedGood() {
        player1.setBuiltCards(List.of(
                new CommercialTradingCard("test", 1, new PreferentialTrading(
                        PreferentialTrading.PreferentialTradingType.RIGHT,
                        List.of(ManufacturedGood.GLASS),
                        List.of()
                ))
        ));

        assertEquals(2, player1.getTradingPriceLeft(ManufacturedGood.GLASS));
        assertEquals(2, player1.getTradingPriceLeft(ManufacturedGood.SCRIPTS));

        assertEquals(1, player1.getTradingPriceRight(ManufacturedGood.GLASS));
        assertEquals(2, player1.getTradingPriceRight(ManufacturedGood.SCRIPTS));

        assertEquals(2, player1.getTradingPriceLeft(CLAY));
        assertEquals(2, player1.getTradingPriceRight(CLAY));
    }

    @Test
    void preferentialBothRawMaterial() {
        player1.setBuiltCards(List.of(
                new CommercialTradingCard("test", 1, new PreferentialTrading(
                        PreferentialTrading.PreferentialTradingType.BOTH,
                        List.of(),
                        List.of(WOOD)
                ))
        ));

        assertEquals(1, player1.getTradingPriceLeft(WOOD));
        assertEquals(1, player1.getTradingPriceRight(WOOD));

        assertEquals(2, player1.getTradingPriceLeft(CLAY));
        assertEquals(2, player1.getTradingPriceRight(CLAY));

        assertEquals(2, player1.getTradingPriceLeft(ManufacturedGood.SCRIPTS));
        assertEquals(2, player1.getTradingPriceRight(ManufacturedGood.SCRIPTS));
    }

    @Test
    void preferentialLeftRawMaterial() {
        player1.setBuiltCards(List.of(
                new CommercialTradingCard("test", 1, new PreferentialTrading(
                        PreferentialTrading.PreferentialTradingType.LEFT,
                        List.of(),
                        List.of(WOOD)
                ))
        ));

        assertEquals(1, player1.getTradingPriceLeft(WOOD));
        assertEquals(2, player1.getTradingPriceRight(WOOD));

        assertEquals(2, player1.getTradingPriceLeft(CLAY));
        assertEquals(2, player1.getTradingPriceRight(CLAY));

        assertEquals(2, player1.getTradingPriceLeft(ManufacturedGood.SCRIPTS));
        assertEquals(2, player1.getTradingPriceRight(ManufacturedGood.SCRIPTS));
    }

    @Test
    void preferentialRightRawMaterial() {
        player1.setBuiltCards(List.of(
                new CommercialTradingCard("test", 1, new PreferentialTrading(
                        PreferentialTrading.PreferentialTradingType.RIGHT,
                        List.of(),
                        List.of(WOOD)
                ))
        ));

        assertEquals(2, player1.getTradingPriceLeft(WOOD));
        assertEquals(1, player1.getTradingPriceRight(WOOD));

        assertEquals(2, player1.getTradingPriceLeft(CLAY));
        assertEquals(2, player1.getTradingPriceRight(CLAY));

        assertEquals(2, player1.getTradingPriceLeft(ManufacturedGood.SCRIPTS));
        assertEquals(2, player1.getTradingPriceRight(ManufacturedGood.SCRIPTS));
    }
}