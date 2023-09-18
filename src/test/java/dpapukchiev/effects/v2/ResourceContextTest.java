package dpapukchiev.effects.v2;

import dpapukchiev.cards.RawMaterial;
import dpapukchiev.effects.RawMaterialEffect;
import dpapukchiev.player.BasePlayerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dpapukchiev.cards.ManufacturedGood.GLASS;
import static dpapukchiev.cards.ManufacturedGood.SCRIPTS;
import static dpapukchiev.cards.ManufacturedGood.TEXTILE;
import static dpapukchiev.cards.RawMaterial.CLAY;
import static dpapukchiev.cards.RawMaterial.METAL_ORE;
import static dpapukchiev.cards.RawMaterial.STONE;
import static dpapukchiev.cards.RawMaterial.WOOD;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ResourceContextTest extends BasePlayerTest {

    @BeforeEach
    void setUp() {
        initPlayers();
    }

    @Test
    void test() {
        new ManufacturedGoodEffect(List.of(SCRIPTS, SCRIPTS, SCRIPTS)).applyTo(player1);
        new ManufacturedGoodEffect(List.of(TEXTILE, TEXTILE)).applyTo(player1);
        new ManufacturedGoodEffect(List.of(GLASS)).applyTo(player1);

        new RawMaterialGoodEffect(List.of(WOOD, WOOD, WOOD, WOOD)).applyTo(player1);
        new RawMaterialGoodEffect(List.of(CLAY, CLAY, CLAY)).applyTo(player1);
        new RawMaterialGoodEffect(List.of(METAL_ORE, METAL_ORE)).applyTo(player1);
        new RawMaterialGoodEffect(List.of(STONE)).applyTo(player1);

        assertEquals(3, player1.getResourceContext().getManufacturedGoodCount(SCRIPTS));
        assertEquals(2, player1.getResourceContext().getManufacturedGoodCount(TEXTILE));
        assertEquals(1, player1.getResourceContext().getManufacturedGoodCount(GLASS));

        assertEquals(4, player1.getResourceContext().getRawMaterialCount(WOOD));
        assertEquals(3, player1.getResourceContext().getRawMaterialCount(CLAY));
        assertEquals(2, player1.getResourceContext().getRawMaterialCount(METAL_ORE));
        assertEquals(1, player1.getResourceContext().getRawMaterialCount(STONE));
    }

}