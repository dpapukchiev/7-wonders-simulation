package dpapukchiev.v2;

import dpapukchiev.v2.effects.core.Effect;
import dpapukchiev.v2.effects.core.EffectDirectionConstraint;
import dpapukchiev.v2.effects.core.EffectExecutionContext;
import dpapukchiev.v2.effects.core.EffectState;
import dpapukchiev.v2.effects.core.EffectTiming;
import dpapukchiev.v2.effects.core.PreferentialTradingContract;
import dpapukchiev.v2.game.TurnContext;
import dpapukchiev.v2.player.Player;
import dpapukchiev.v2.player.Vault;
import dpapukchiev.v2.resources.ManufacturedGood;
import dpapukchiev.v2.resources.RawMaterial;
import dpapukchiev.v2.resources.ResourceBundle;
import dpapukchiev.v2.resources.ResourceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static dpapukchiev.v2.effects.core.EffectState.AVAILABLE;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BasePlayerTest {

    protected Player mainPlayer;
    @Mock
    protected Player leftPlayer;
    @Mock
    protected Player rightPlayer;

    @Mock
    protected Effect                 effect1;
    @Mock
    protected Effect                 effect2;
    @Mock
    protected Effect                 effect3;
    @Mock
    protected ResourceContext        resourceContextPlayerLeft;
    @Mock
    protected ResourceContext        resourceContextPlayerRight;
    @Mock
    protected EffectExecutionContext effectExecutionContext;
    @Mock
    protected Vault                  leftPlayerVault;
    @Mock
    protected Vault                  rightPlayerVault;

    @BeforeEach
    void init() {
        initPlayers();
    }

    protected TurnContext getTurnContext() {
        return TurnContext.builder()
                .player(mainPlayer)
                .build();
    }

    protected void initPlayers() {
        mainPlayer = Player.builder()
                .effectExecutionContext(effectExecutionContext)
                .name("Player")
                .build();

        mainPlayer.setLeftPlayer(leftPlayer);
        mainPlayer.setRightPlayer(rightPlayer);

        leftPlayer.setLeftPlayer(rightPlayer);
        leftPlayer.setRightPlayer(mainPlayer);

        rightPlayer.setLeftPlayer(mainPlayer);
        rightPlayer.setRightPlayer(leftPlayer);

        lenient().when(leftPlayer.resourceContext())
                .thenReturn(resourceContextPlayerLeft);
        lenient().when(rightPlayer.resourceContext())
                .thenReturn(resourceContextPlayerRight);
        lenient().when(leftPlayer.getVault())
                .thenReturn(leftPlayerVault);
        lenient().when(rightPlayer.getVault())
                .thenReturn(rightPlayerVault);
    }

    protected ResourceContext getMainPlayerResourceContext() {
        return mainPlayer.resourceContext();
    }

    protected void configureManufacturedGoodsEffect(Effect effect, List<ManufacturedGood> manufacturedGoods) {
        configureResourceBundleEffect(effect, ResourceBundle.builder()
                .manufacturedGoods(manufacturedGoods)
                .build());
    }

    protected void configureRawMaterialsEffect(Effect effect, List<RawMaterial> rawMaterials) {
        configureResourceBundleEffect(effect, ResourceBundle.builder()
                .rawMaterials(rawMaterials)
                .build());
    }

    protected void configureManufacturedGoodsEffect(Effect effect, ManufacturedGood manufacturedGood) {
        configureResourceBundleEffect(effect, ResourceBundle.builder()
                .manufacturedGoods(List.of(manufacturedGood))
                .build());
    }

    protected void configureRawMaterialsEffect(Effect effect, RawMaterial... rawMaterial) {
        configureResourceBundleEffect(effect, ResourceBundle.builder()
                .rawMaterials(List.of(rawMaterial))
                .build());
    }

    protected void assignPermanentEffectsToPlayer(Effect... permanentEffects) {
        when(effectExecutionContext.getPermanentEffects())
                .thenReturn(List.of(permanentEffects));
    }

    protected void assignPermanentEffectsToPlayer(List<Effect> permanentEffects) {
        when(effectExecutionContext.getPermanentEffects())
                .thenReturn(permanentEffects);
    }

    protected void configurePreferentialTradingEffect(
            Effect effect,
            EffectDirectionConstraint right,
            EffectExecutionContext executionContext,
            PreferentialTradingContract.Type type
    ) {
        when(effect.getState()).thenReturn(EffectState.AVAILABLE);
        when(effect.getPreferentialTrading())
                .thenReturn(Optional.ofNullable(PreferentialTradingContract.builder()
                        .type(type)
                        .directionConstraint(right)
                        .build()));
        executionContext.addEffect(effect, EffectTiming.ANYTIME);
    }

    protected void configureResourceBundleEffect(Effect effect, ResourceBundle bundleToReturn) {
        when(effect.getState())
                .thenReturn(AVAILABLE);
        when(effect.getResourceBundle(mainPlayer))
                .thenReturn(Optional.ofNullable(bundleToReturn));
    }

    protected void configureTradingPrice(
            EffectExecutionContext executionContext,
            EffectDirectionConstraint direction,
            PreferentialTradingContract.Type contractType,
            double price
    ) {
        lenient().when(executionContext.getTradingPrice(direction, contractType))
                .thenReturn(price);
    }
}