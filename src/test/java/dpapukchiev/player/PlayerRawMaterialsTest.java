package dpapukchiev.player;

import dpapukchiev.cards.DoubleResourceCard;
import dpapukchiev.cards.RawMaterial;
import dpapukchiev.cards.SingleResourceCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerRawMaterialsTest extends BasePlayerTest {

    @BeforeEach
    void init() {
        initPlayers();
    }

    @Test
    void getRawMaterialCountSingle() {
        player1.setBuiltCards(List.of(
                new SingleResourceCard(1, "1", RawMaterial.METAL_ORE, 3)
        ));

        assertEquals(1, player1.getRawMaterialCount(RawMaterial.METAL_ORE));
    }

    @Test
    void getRawMaterialCountTwoCards() {
        player1.setBuiltCards(List.of(
                new SingleResourceCard(1, "1", RawMaterial.METAL_ORE, 3),
                new SingleResourceCard(1, "2", RawMaterial.METAL_ORE, 3)
        ));

        assertEquals(2, player1.getRawMaterialCount(RawMaterial.METAL_ORE));
        assertEquals(0, player1.getRawMaterialCount(RawMaterial.CLAY));
    }

    @Test
    void getRawMaterialCountMixed() {
        player1.setBuiltCards(List.of(
                new SingleResourceCard(1, "1", RawMaterial.WOOD, 3),
                new SingleResourceCard(1, "2", RawMaterial.METAL_ORE, 3),
                new SingleResourceCard(1, "3", RawMaterial.CLAY, 3),
                new SingleResourceCard(1, "3", RawMaterial.STONE, 3)
        ));

        assertEquals(1, player1.getRawMaterialCount(RawMaterial.METAL_ORE));
    }

    @Test
    void getRawMaterialCountDouble() {
        player1.setBuiltCards(List.of(
                // should not count because it's a wildcard
                new DoubleResourceCard(1, "1", RawMaterial.WOOD, RawMaterial.STONE, 3),
                // +2
                new DoubleResourceCard(1, "1", RawMaterial.WOOD, RawMaterial.WOOD, 3),
                new SingleResourceCard(1, "2", RawMaterial.METAL_ORE, 3),
                // +1
                new SingleResourceCard(1, "3", RawMaterial.WOOD, 3),
                new SingleResourceCard(1, "3", RawMaterial.STONE, 3)
        ));

        assertEquals(3, player1.getRawMaterialCount(RawMaterial.WOOD));
        assertEquals(1, player1.getRawMaterialCountWildcard(RawMaterial.WOOD));
        assertEquals(1, player1.getRawMaterialCountWildcard(RawMaterial.STONE));
    }
}