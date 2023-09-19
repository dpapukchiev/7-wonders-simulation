package dpapukchiev.v2.resources;

import dpapukchiev.v2.effects.Effect;
import dpapukchiev.v2.effects.EffectExecutionContext;
import dpapukchiev.v2.player.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static dpapukchiev.v2.effects.EffectState.AVAILABLE;
import static dpapukchiev.v2.resources.ManufacturedGood.SCRIPTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceContextTest {

    @Mock
    private Effect                 effect1;
    @Mock
    private Effect                 effect2;
    @Mock
    private Effect                 effect3;
    @Mock
    private EffectExecutionContext effectExecutionContext;
    private Player                 player;

    @ParameterizedTest
    @EnumSource(RawMaterial.class)
    void getRawMaterialCount1Effect(RawMaterial material) {
        init(List.of(effect1));
        initEffect(effect1, ResourceBundle.builder()
                .rawMaterials(List.of(material))
                .build());

        double result = player.resourceContext().getRawMaterialCount(material);

        assertEquals(1, result);
    }

    @ParameterizedTest
    @EnumSource(RawMaterial.class)
    void getRawMaterialCount2Effects(RawMaterial material) {
        init(List.of(effect1, effect2));
        initEffect(effect1, ResourceBundle.builder()
                .rawMaterials(List.of(material))
                .build());
        initEffect(effect2, ResourceBundle.builder()
                .rawMaterials(List.of(material))
                .build());

        double result = player.resourceContext().getRawMaterialCount(material);

        assertEquals(2, result);
    }

    @ParameterizedTest
    @EnumSource(ManufacturedGood.class)
    void getManufacturedGoodCount1Effect(ManufacturedGood manufacturedGood) {
        init(List.of(effect1));
        initEffect(effect1, ResourceBundle.builder()
                .manufacturedGoods(List.of(manufacturedGood))
                .build());

        double result = player.resourceContext().getManufacturedGoodCount(manufacturedGood);

        assertEquals(1, result);
    }

    @ParameterizedTest
    @EnumSource(ManufacturedGood.class)
    void getManufacturedGoodCount2Effects(ManufacturedGood manufacturedGood) {
        init(List.of(effect1, effect2));
        initEffect(effect1, ResourceBundle.builder()
                .manufacturedGoods(List.of(manufacturedGood))
                .build());
        initEffect(effect2, ResourceBundle.builder()
                .manufacturedGoods(List.of(manufacturedGood))
                .build());

        double result = player.resourceContext().getManufacturedGoodCount(manufacturedGood);

        assertEquals(2, result);
    }

    @Test
    void getCountWildcard() {
        init(List.of(effect1, effect2, effect3));
        initEffect(effect1, ResourceBundle.builder()
                .manufacturedGoods(ManufacturedGood.all())
                .build());
        initEffect(effect2, ResourceBundle.builder()
                .manufacturedGoods(List.of(SCRIPTS, SCRIPTS))
                .build());
        initEffect(effect3, ResourceBundle.builder()
                .rawMaterials(RawMaterial.all())
                .build());

        ManufacturedGood.all().forEach(manufacturedGood -> {
            double result = player.resourceContext().getManufacturedGoodCountWildcard(manufacturedGood);

            assertEquals(1, result);
        });
        RawMaterial.all().forEach(rawMaterial -> {
            double result = player.resourceContext().getRawMaterialCountWildcard(rawMaterial);

            assertEquals(1, result);
        });
    }

    @Test
    void getRawMaterialCountWildcard() {
    }

    private void initEffect(Effect effect, ResourceBundle bundleToReturn) {
        when(effect.getState())
                .thenReturn(AVAILABLE);
        when(effect.getResourceBundle(player))
                .thenReturn(bundleToReturn);
    }

    private void init(List<Effect> permanentEffects) {
        player = Player.builder()
                .effectExecutionContext(effectExecutionContext)
                .build();
        when(effectExecutionContext.getPermanentEffects())
                .thenReturn(permanentEffects);
    }
}