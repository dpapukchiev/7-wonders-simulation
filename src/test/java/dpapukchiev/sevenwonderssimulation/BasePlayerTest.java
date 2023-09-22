package dpapukchiev.sevenwonderssimulation;

import dpapukchiev.sevenwonderssimulation.effects.core.Effect;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectDirectionConstraint;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectExecutionContext;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectState;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectTiming;
import dpapukchiev.sevenwonderssimulation.effects.core.PreferentialTradingContract;
import dpapukchiev.sevenwonderssimulation.game.TurnContext;
import dpapukchiev.sevenwonderssimulation.player.Player;
import dpapukchiev.sevenwonderssimulation.player.Vault;
import dpapukchiev.sevenwonderssimulation.resources.ManufacturedGood;
import dpapukchiev.sevenwonderssimulation.resources.RawMaterial;
import dpapukchiev.sevenwonderssimulation.resources.ResourceBundle;
import dpapukchiev.sevenwonderssimulation.resources.ResourceContext;
import dpapukchiev.sevenwonderssimulation.resources.ScienceSymbol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static dpapukchiev.sevenwonderssimulation.effects.core.EffectState.AVAILABLE;
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

        mainPlayer.initVault();

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

    protected void configureScienceEffect(Effect effect, ScienceSymbol scienceSymbols) {
        configureResourceBundleEffect(effect, ResourceBundle.builder()
                .scienceSymbols(List.of(scienceSymbols))
                .build());
    }

    protected void assignPermanentEffectsToPlayer(List<Effect> permanentEffects) {
        when(effectExecutionContext.getAvailablePermanentEffects())
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
        executionContext.scheduleRewardEvaluationAndCollection(effect, EffectTiming.ANYTIME);
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