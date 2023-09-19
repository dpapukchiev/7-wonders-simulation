package dpapukchiev.v1.player;

import dpapukchiev.v1.cards.ManufacturedGood;
import dpapukchiev.v1.cards.ManufacturedGoodCard;
import dpapukchiev.v1.cards.SingleManufacturedGoodCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerManufacturedGoodsTest extends BasePlayerTest {

    @BeforeEach
    void init() {
        initPlayers();
    }

    @Test
    void getRawMaterialCountSingle() {
        player1.setBuiltCards(List.of(
                new SingleManufacturedGoodCard(1, "1", ManufacturedGood.GLASS, 3)
        ));

        assertEquals(1, player1.getManufacturedGoodCount(ManufacturedGood.GLASS));
    }

    @Test
    void getRawMaterialCountTwoCards() {
        player1.setBuiltCards(List.of(
                new SingleManufacturedGoodCard(1, "1", ManufacturedGood.GLASS, 3),
                new SingleManufacturedGoodCard(1, "2", ManufacturedGood.GLASS, 3)
        ));

        assertEquals(2, player1.getManufacturedGoodCount(ManufacturedGood.GLASS));
        assertEquals(0, player1.getManufacturedGoodCount(ManufacturedGood.SCRIPTS));
    }

    @Test
    void getRawMaterialCountMixed() {
        player1.setBuiltCards(List.of(
                new SingleManufacturedGoodCard(1, "1", ManufacturedGood.SCRIPTS, 3),
                new SingleManufacturedGoodCard(1, "2", ManufacturedGood.GLASS, 3),
                new SingleManufacturedGoodCard(1, "3", ManufacturedGood.TEXTILE, 3)
        ));

        assertEquals(1, player1.getManufacturedGoodCount(ManufacturedGood.SCRIPTS));
    }

    @Test
    void getRawMaterialCountDouble() {
        player1.setBuiltCards(List.of(
                // should not count because it's a wildcard
                new ManufacturedGoodCard("1", List.of(ManufacturedGood.GLASS, ManufacturedGood.SCRIPTS), 3),
                // +2
                new ManufacturedGoodCard("1", List.of(ManufacturedGood.GLASS, ManufacturedGood.GLASS), 3),
                // +1
                new SingleManufacturedGoodCard(1, "3", ManufacturedGood.GLASS, 3),
                new SingleManufacturedGoodCard(1, "3", ManufacturedGood.SCRIPTS, 3)
        ));

        assertEquals(3, player1.getManufacturedGoodCount(ManufacturedGood.GLASS));
        assertEquals(1, player1.getManufacturedGoodCountWildcard(ManufacturedGood.GLASS));
        assertEquals(1, player1.getManufacturedGoodCountWildcard(ManufacturedGood.SCRIPTS));
    }
}