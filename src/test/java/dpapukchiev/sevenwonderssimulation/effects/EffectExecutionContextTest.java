package dpapukchiev.sevenwonderssimulation.effects;

import dpapukchiev.sevenwonderssimulation.BasePlayerTest;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectExecutionContext;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectReward;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectState;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectTiming;
import dpapukchiev.sevenwonderssimulation.game.Turn;
import dpapukchiev.sevenwonderssimulation.player.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Optional;

import static dpapukchiev.sevenwonderssimulation.effects.core.EffectDirectionConstraint.BOTH;
import static dpapukchiev.sevenwonderssimulation.effects.core.EffectDirectionConstraint.LEFT;
import static dpapukchiev.sevenwonderssimulation.effects.core.EffectDirectionConstraint.RIGHT;
import static dpapukchiev.sevenwonderssimulation.effects.core.PreferentialTradingContract.Type.MANUFACTURED_GOODS;
import static dpapukchiev.sevenwonderssimulation.effects.core.PreferentialTradingContract.Type.RAW_MATERIALS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

class EffectExecutionContextTest extends BasePlayerTest {

    @CsvSource({
            "AVAILABLE,END_OF_AGE,1,AVAILABLE,END_OF_AGE,2,END_OF_AGE,1,2",
            "AVAILABLE,END_OF_GAME,1,AVAILABLE,END_OF_GAME,2,END_OF_GAME,1,2",
            // Effect exhaustion
            "EXHAUSTED,END_OF_TURN,1,EXHAUSTED,END_OF_TURN,2,END_OF_TURN,0,0",
            "EXHAUSTED,END_OF_TURN,1,AVAILABLE,END_OF_TURN,2,END_OF_TURN,0,2",
            "AVAILABLE,END_OF_TURN,1,EXHAUSTED,END_OF_TURN,2,END_OF_TURN,1,0",

            // Wrong timing
            "AVAILABLE,END_OF_TURN,1,AVAILABLE,END_OF_AGE,2,END_OF_TURN,1,0",
            "AVAILABLE,END_OF_AGE,1,AVAILABLE,END_OF_TURN,2,END_OF_TURN,0,2",
            "AVAILABLE,END_OF_AGE,1,AVAILABLE,END_OF_AGE,2,END_OF_TURN,0,0",
            "AVAILABLE,END_OF_AGE,1,AVAILABLE,END_OF_AGE,2,END_OF_GAME,0,0",
    })
    @ParameterizedTest
    void addAndExecuteEffectsEndOfTurn(
            EffectState effect1State,
            EffectTiming effect1Timing,
            int coinRewardEffect1,
            EffectState effect2State,
            EffectTiming effect2Timing,
            int victoryPointsRewardEffect2,
            EffectTiming currentTiming,
            int finalCoinReward,
            int finalVictoryPointsReward
    ) {
        var executionContext = new EffectExecutionContext(null, null);
        var player = Player.builder().build();

        lenient().when(effect1.getState())
                .thenReturn(effect1State);
        lenient().when(effect1.collectReward(player))
                .thenReturn(Optional.ofNullable(EffectReward.builder()
                        .coinReward(coinRewardEffect1)
                        .build()));

        lenient().when(effect2.getState())
                .thenReturn(effect2State);
        lenient().when(effect2.collectReward(player))
                .thenReturn(Optional.ofNullable(EffectReward.builder()
                        .victoryPointsReward(victoryPointsRewardEffect2)
                        .build()));

        executionContext.scheduleRewardEvaluationAndCollection(effect1, effect1Timing);
        executionContext.scheduleRewardEvaluationAndCollection(effect2, effect2Timing);

        Optional<EffectReward> result = switch (currentTiming) {
            case END_OF_TURN -> executionContext.executeEffectsEndOfTurn(player);
            case END_OF_AGE -> executionContext.executeEffectsEndOfAge(player);
            case END_OF_GAME -> executionContext.executeEffectsEndOfGame(player);
            default -> Optional.empty();
        };

        if (finalCoinReward + finalVictoryPointsReward == 0) {
            assertNotNull(result);
            assertTrue(result.isEmpty());
            return;
        }

        assertTrue(result.isPresent());
        assertEquals(finalCoinReward, result.get().getCoinReward());
        assertEquals(finalVictoryPointsReward, result.get().getVictoryPointsReward());
    }

    @Test
    void getPermanentEffects() {
        var executionContext = new EffectExecutionContext(null, null);

        executionContext.scheduleRewardEvaluationAndCollection(effect1, EffectTiming.ANYTIME);
        executionContext.scheduleRewardEvaluationAndCollection(effect2, EffectTiming.ANYTIME);

        when(effect1.getState()).thenReturn(EffectState.AVAILABLE);
        when(effect2.getState()).thenReturn(EffectState.EXHAUSTED);

        var result = executionContext.getAvailablePermanentEffects();
        assertEquals(2, result.size());
        assertEquals(effect1, result.get(0));
    }

    @Test
    void preferentialTrading() {
        var executionContext = new EffectExecutionContext(null, null);
        assertEquals(2, executionContext.getTradingPrice(LEFT, RAW_MATERIALS));
        assertEquals(2, executionContext.getTradingPrice(RIGHT, RAW_MATERIALS));

        configurePreferentialTradingEffect(effect1, LEFT, executionContext, RAW_MATERIALS);

        assertEquals(1, executionContext.getTradingPrice(LEFT, RAW_MATERIALS));
        assertEquals(2, executionContext.getTradingPrice(RIGHT, RAW_MATERIALS));

        configurePreferentialTradingEffect(effect2, RIGHT, executionContext, RAW_MATERIALS);

        assertEquals(1, executionContext.getTradingPrice(LEFT, RAW_MATERIALS));
        assertEquals(1, executionContext.getTradingPrice(RIGHT, RAW_MATERIALS));

        assertEquals(2, executionContext.getTradingPrice(LEFT, MANUFACTURED_GOODS));
        assertEquals(2, executionContext.getTradingPrice(RIGHT, MANUFACTURED_GOODS));

        configurePreferentialTradingEffect(effect3, BOTH, executionContext, MANUFACTURED_GOODS);

        assertEquals(1, executionContext.getTradingPrice(LEFT, MANUFACTURED_GOODS));
        assertEquals(1, executionContext.getTradingPrice(RIGHT, MANUFACTURED_GOODS));
    }

}