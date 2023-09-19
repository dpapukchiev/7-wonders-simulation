package dpapukchiev.v1.effects.v2;

import dpapukchiev.v1.player.BasePlayerTest;
import dpapukchiev.v1.effects.v2.AggregateEffect;
import dpapukchiev.v1.effects.v2.CoinRewardEffect;
import dpapukchiev.v1.effects.v2.EffectState;
import dpapukchiev.v1.effects.v2.VictoryPointEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CoinRewardEffectTest extends BasePlayerTest {

    @BeforeEach
    void setUp() {
        initPlayers();
    }

    @Test
    void applyCoinRewardEffect() {
        var effect = new CoinRewardEffect(5);
        assertEquals(EffectState.AVAILABLE, effect.getState());

        // when card is played
        effect.applyTo(player1);

        // then effect is added to player's effect execution context
        var result = player1.getEffectExecutionContext()
                .executeEffectsEndOfTurn(player1);
        assertEquals(5, result.getCoinReward());

        var result2 = player1.getEffectExecutionContext()
                .executeEffectsEndOfTurn(player1);
        assertEquals(0, result2.getCoinReward());
    }

    //    @Test
//    void applyVictoryPointEffect() {
//        IntStream.rangeClosed(0, 100).forEach(i -> {
//            var effect = new VictoryPointEffect(i, EffectTiming.END_OF_TURN);
//            assertEquals(EffectState.AVAILABLE, effect.getState());
//
//            var result = effect.getReward(EffectContext.builder()
//                    .player(player1)
//                    .build());
//
//            assertEquals(i, result.getVictoryPointsReward());
//            assertEquals(EffectState.EXHAUSTED, effect.getState());
//        });
//    }
//
    @Test
    void applyAggregatedVictoryPointAndCoinRewardEffect() {
        var coinRewardEffect = new CoinRewardEffect(5);
        var victoryPointEffect = new VictoryPointEffect(3);
        var aggregateEffect = new AggregateEffect(
                coinRewardEffect, coinRewardEffect.getEffectTiming(),
                victoryPointEffect, victoryPointEffect.getEffectTiming()
        );
        assertEquals(EffectState.AVAILABLE, aggregateEffect.getState());

        // when card is played
        aggregateEffect.applyTo(player1);

        // then effect is added to player's effect execution context
        var rewardEndOfTurn = player1.getEffectExecutionContext()
                .executeEffectsEndOfTurn(player1);
        assertEquals(5, rewardEndOfTurn.getCoinReward());

        var rewardEndOfTurn2 = player1.getEffectExecutionContext()
                .executeEffectsEndOfTurn(player1);
        assertEquals(0, rewardEndOfTurn2.getCoinReward());

        // execute end of game effects
        var rewardEndOfGame = player1.getEffectExecutionContext()
                .executeEffectsEndOfGame(player1);
        assertEquals(3, rewardEndOfGame.getVictoryPointsReward());

        var rewardEndOfGame2 = player1.getEffectExecutionContext()
                .executeEffectsEndOfGame(player1);
        assertEquals(0, rewardEndOfGame2.getVictoryPointsReward());
    }
}