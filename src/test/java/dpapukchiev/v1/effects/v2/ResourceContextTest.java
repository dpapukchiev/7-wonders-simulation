package dpapukchiev.v1.effects.v2;

import dpapukchiev.v1.cards.ManufacturedGood;
import dpapukchiev.v1.cards.RawMaterial;
import dpapukchiev.v1.player.BasePlayerTest;
import dpapukchiev.v1.effects.v2.ManufacturedGoodEffect;
import dpapukchiev.v1.effects.v2.RawMaterialGoodEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dpapukchiev.v1.cards.ManufacturedGood.GLASS;
import static dpapukchiev.v1.cards.ManufacturedGood.SCRIPTS;
import static dpapukchiev.v1.cards.ManufacturedGood.TEXTILE;
import static dpapukchiev.v1.cards.RawMaterial.CLAY;
import static dpapukchiev.v1.cards.RawMaterial.METAL_ORE;
import static dpapukchiev.v1.cards.RawMaterial.STONE;
import static dpapukchiev.v1.cards.RawMaterial.WOOD;
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

    @Test
    void testWildcard() {
        new ManufacturedGoodEffect(ManufacturedGood.all()).applyTo(player1);
        new ManufacturedGoodEffect(List.of(SCRIPTS, SCRIPTS)).applyTo(player1);
        new ManufacturedGoodEffect(List.of(GLASS)).applyTo(player1);

        new RawMaterialGoodEffect(RawMaterial.all()).applyTo(player1);
        new RawMaterialGoodEffect(List.of(CLAY, CLAY, CLAY)).applyTo(player1);
        new RawMaterialGoodEffect(List.of(METAL_ORE, METAL_ORE)).applyTo(player1);
        new RawMaterialGoodEffect(List.of(STONE)).applyTo(player1);

        assertEquals(1, player1.getResourceContext().getManufacturedGoodCountWildcard(SCRIPTS));
        assertEquals(1, player1.getResourceContext().getManufacturedGoodCountWildcard(TEXTILE));
        assertEquals(1, player1.getResourceContext().getManufacturedGoodCountWildcard(GLASS));

        assertEquals(1, player1.getResourceContext().getRawMaterialCountWildcard(WOOD));
        assertEquals(1, player1.getResourceContext().getRawMaterialCountWildcard(CLAY));
        assertEquals(1, player1.getResourceContext().getRawMaterialCountWildcard(METAL_ORE));
        assertEquals(1, player1.getResourceContext().getRawMaterialCountWildcard(STONE));
    }

}