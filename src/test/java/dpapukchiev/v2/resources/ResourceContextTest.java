package dpapukchiev.v2.resources;

import dpapukchiev.v2.BasePlayerTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static dpapukchiev.v2.effects.core.EffectDirectionConstraint.LEFT;
import static dpapukchiev.v2.effects.core.EffectDirectionConstraint.RIGHT;
import static dpapukchiev.v2.effects.core.PreferentialTradingContract.Type.MANUFACTURED_GOODS;
import static dpapukchiev.v2.effects.core.PreferentialTradingContract.Type.RAW_MATERIALS;
import static dpapukchiev.v2.resources.ManufacturedGood.GLASS;
import static dpapukchiev.v2.resources.ManufacturedGood.SCRIPTS;
import static dpapukchiev.v2.resources.RawMaterial.CLAY;
import static dpapukchiev.v2.resources.RawMaterial.METAL_ORE;
import static dpapukchiev.v2.resources.RawMaterial.WOOD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;

class ResourceContextTest extends BasePlayerTest {
    @ParameterizedTest
    @EnumSource(RawMaterial.class)
    void getRawMaterialCount1Effect(RawMaterial material) {
        configureRawMaterialsEffect(effect1, material);

        assignPermanentEffectsToPlayer(List.of(effect1));

        double result = getMainPlayerResourceContext().getRawMaterialCount(material);

        assertEquals(1, result);
    }

    @ParameterizedTest
    @EnumSource(RawMaterial.class)
    void getRawMaterialCount2Effects(RawMaterial material) {
        configureRawMaterialsEffect(effect1, material);
        configureRawMaterialsEffect(effect2, material);

        assignPermanentEffectsToPlayer(List.of(effect1, effect2));

        double result = getMainPlayerResourceContext().getRawMaterialCount(material);

        assertEquals(2, result);
    }

    @ParameterizedTest
    @EnumSource(ManufacturedGood.class)
    void getManufacturedGoodCount1Effect(ManufacturedGood manufacturedGood) {
        configureManufacturedGoodsEffect(effect1, manufacturedGood);

        assignPermanentEffectsToPlayer(List.of(effect1));

        double result = getMainPlayerResourceContext().getManufacturedGoodCount(manufacturedGood);

        assertEquals(1, result);
    }

    @ParameterizedTest
    @EnumSource(ManufacturedGood.class)
    void getManufacturedGoodCount2Effects(ManufacturedGood manufacturedGood) {
        configureManufacturedGoodsEffect(effect1, manufacturedGood);
        configureManufacturedGoodsEffect(effect2, manufacturedGood);

        assignPermanentEffectsToPlayer(List.of(effect1, effect2));

        double result = getMainPlayerResourceContext().getManufacturedGoodCount(manufacturedGood);

        assertEquals(2, result);
    }

    @Test
    void getCountWildcard() {
        configureManufacturedGoodsEffect(effect1, ManufacturedGood.all());
        configureManufacturedGoodsEffect(effect2, List.of(SCRIPTS));
        configureRawMaterialsEffect(effect3, RawMaterial.all());

        assignPermanentEffectsToPlayer(List.of(effect1, effect2, effect3));

        ManufacturedGood.all().forEach(manufacturedGood -> {
            double result = getMainPlayerResourceContext().getManufacturedGoodCountWildcard(manufacturedGood);

            assertEquals(1, result);
        });
        RawMaterial.all().forEach(rawMaterial -> {
            double result = getMainPlayerResourceContext().getRawMaterialCountWildcard(rawMaterial);

            assertEquals(1, result);
        });
    }

    @EnumSource(RawMaterial.class)
    @ParameterizedTest
    void calculateResourcesCostSimpleResource(RawMaterial rawMaterial) {
        var result = getMainPlayerResourceContext().calculateResourcesCost(List.of(rawMaterial, rawMaterial), List.of());
        assertFalse(result.isAffordable());

        configureRawMaterialsEffect(effect1, rawMaterial);
        configureRawMaterialsEffect(effect2, rawMaterial);

        assignPermanentEffectsToPlayer(List.of(effect1, effect2));

        result = getMainPlayerResourceContext().calculateResourcesCost(List.of(rawMaterial, rawMaterial), List.of());

        assertTrue(result.isAffordable());
        assertEquals(0, result.getToPayTotal());
    }

    @Test
    void calculateResourcesCostDoubleResource() {
        configureRawMaterialsEffect(effect1, CLAY);
        configureRawMaterialsEffect(effect2, WOOD);
        configureManufacturedGoodsEffect(effect3, SCRIPTS);

        assignPermanentEffectsToPlayer(List.of(effect1, effect2));
        assignPermanentEffectsToPlayer(List.of(effect1, effect2, effect3));

        assertPrice(List.of(WOOD, CLAY), List.of(), 0);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    void calculateResourcesCostDoubleResourceWithTrading(int availableCoins) {
        mainPlayer.getVault().setCoins(availableCoins);
        configureTradingPrice(effectExecutionContext, LEFT, RAW_MATERIALS, 1);
        configureAvailableRawMaterial(resourceContextPlayerLeft, WOOD, 1);
        configureTradingPrice(effectExecutionContext, LEFT, MANUFACTURED_GOODS, 2);
        configureAvailableManufacturedGood(resourceContextPlayerLeft, SCRIPTS, 2);

        configureTradingPrice(effectExecutionContext, RIGHT, RAW_MATERIALS, 2);
        configureAvailableRawMaterial(resourceContextPlayerRight, WOOD, 1);
        configureTradingPrice(effectExecutionContext, RIGHT, MANUFACTURED_GOODS, 1);
        configureAvailableManufacturedGood(resourceContextPlayerRight, SCRIPTS, 1);

        configureRawMaterialsEffect(effect1, CLAY);
        assignPermanentEffectsToPlayer(List.of(effect1));

        assertPrice(List.of(CLAY), List.of(), 0);

        if (availableCoins >= 2) {
            // buy from left: 1x$1 wood
            // buy from right: 1x$1 scripts
            assertPrice(List.of(WOOD, CLAY), List.of(SCRIPTS), 2);
        }
        if (availableCoins >= 4) {
            // buy from left: 1x$1 wood
            // buy from right: 1x$2 wood 1x$1 scripts
            assertPrice(List.of(WOOD, WOOD, CLAY), List.of(SCRIPTS), 4);
        }
        if (availableCoins >= 6) {
            // buy from left: 1x$1 wood 1x$2 scripts
            // buy from right: 1x$2 wood 1x$1 scripts
            assertPrice(List.of(WOOD, WOOD, CLAY), List.of(SCRIPTS, SCRIPTS), 6);
        }
        if (availableCoins >= 8) {
            // buy from left: 1x$1 wood 2x$2 scripts
            // buy from right: 1x$2 wood 1x$1 scripts
            assertPrice(List.of(WOOD, WOOD, CLAY), List.of(SCRIPTS, SCRIPTS, SCRIPTS), 8);
        }

        // anything else or more than provided is not affordable
        assertPrice(List.of(METAL_ORE, CLAY), List.of(GLASS), -1);
        assertPrice(List.of(WOOD, WOOD, WOOD, CLAY), List.of(SCRIPTS), -1);
        assertPrice(List.of(WOOD, WOOD, CLAY), List.of(SCRIPTS, SCRIPTS, SCRIPTS, SCRIPTS), -1);
        assertPrice(List.of(WOOD, WOOD, WOOD, WOOD, CLAY), List.of(SCRIPTS, SCRIPTS, SCRIPTS, SCRIPTS), -1);

    }

    @Test
    void unaffordable() {
        assertPrice(List.of(WOOD, CLAY), List.of(SCRIPTS), -1);
    }

    private void configureAvailableRawMaterial(ResourceContext context, RawMaterial rawMaterial, double count) {
        lenient().when(context.getRawMaterialCount(eq(rawMaterial)))
                .thenReturn(count);
    }

    private void configureAvailableManufacturedGood(ResourceContext context, ManufacturedGood manufacturedGood, double count) {
        lenient().when(context.getManufacturedGoodCount(manufacturedGood)).thenReturn(count);
    }

    private void assertPrice(List<RawMaterial> rawMaterials, List<ManufacturedGood> manufacturedGoods, int expectedPrice) {
        var result = getMainPlayerResourceContext().calculateResourcesCost(
                rawMaterials,
                manufacturedGoods
        );

        if (expectedPrice < 0) {
            assertFalse(result.isAffordable());
            assertEquals(0, result.getToPayTotal());
            return;
        }
        assertTrue(result.isAffordable());
        assertEquals(expectedPrice, result.getToPayTotal());
    }

}