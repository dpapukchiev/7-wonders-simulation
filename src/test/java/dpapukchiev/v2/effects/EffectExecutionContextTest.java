package dpapukchiev.v2.effects;

import dpapukchiev.v2.BasePlayerTest;
import dpapukchiev.v2.player.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static dpapukchiev.v2.effects.EffectDirectionConstraint.BOTH;
import static dpapukchiev.v2.effects.EffectDirectionConstraint.LEFT;
import static dpapukchiev.v2.effects.EffectDirectionConstraint.RIGHT;
import static dpapukchiev.v2.effects.PreferentialTradingContract.Type.MANUFACTURED_GOODS;
import static dpapukchiev.v2.effects.PreferentialTradingContract.Type.RAW_MATERIALS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EffectExecutionContextTest  extends BasePlayerTest{

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
        var executionContext = new EffectExecutionContext();
        var player = Player.builder().build();

        lenient().when(effect1.getState())
                .thenReturn(effect1State);
        lenient().when(effect1.getReward(player))
                .thenReturn(Optional.ofNullable(EffectReward.builder()
                        .coinReward(coinRewardEffect1)
                        .build()));

        lenient().when(effect2.getState())
                .thenReturn(effect2State);
        lenient().when(effect2.getReward(player))
                .thenReturn(Optional.ofNullable(EffectReward.builder()
                        .victoryPointsReward(victoryPointsRewardEffect2)
                        .build()));

        executionContext.addEffect(effect1, effect1Timing);
        executionContext.addEffect(effect2, effect2Timing);

        Optional<EffectReward> result = Optional.empty();
        switch (currentTiming) {
            case END_OF_TURN:
                result = executionContext.executeEffectsEndOfTurn(player);
                break;
            case END_OF_AGE:
                result = executionContext.executeEffectsEndOfAge(player);
                break;
            case END_OF_GAME:
                result = executionContext.executeEffectsEndOfGame(player);
                break;
        }

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
        var executionContext = new EffectExecutionContext();

        executionContext.addEffect(effect1, EffectTiming.ANYTIME);
        executionContext.addEffect(effect2, EffectTiming.ANYTIME);

        when(effect1.getState()).thenReturn(EffectState.AVAILABLE);
        when(effect2.getState()).thenReturn(EffectState.EXHAUSTED);

        var result = executionContext.getPermanentEffects();
        assertEquals(1, result.size());
        assertEquals(effect1, result.get(0));
    }

    @Test
    void preferentialTrading() {
        var executionContext = new EffectExecutionContext();
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