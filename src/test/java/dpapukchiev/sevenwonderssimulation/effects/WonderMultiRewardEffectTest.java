package dpapukchiev.sevenwonderssimulation.effects;

import dpapukchiev.sevenwonderssimulation.BasePlayerTest;
import dpapukchiev.sevenwonderssimulation.effects.core.Effect;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectTiming;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class WonderMultiRewardEffectTest extends BasePlayerTest {

    @Captor
    private ArgumentCaptor<Effect> effectArgumentCaptorPermanent;
    @Captor
    private ArgumentCaptor<Effect> effectArgumentCaptorEndOfTurn;

    @Test
    void scheduleEffect() {
        var wonderMultiRewardEffect = WonderMultiRewardEffect.of(1, 3, 4);

        wonderMultiRewardEffect.scheduleRewardEvaluationAndCollection(mainPlayer);

        assertCoinAndVictoryPointsEffectsScheduled();
        assertWarShieldsEffectScheduled();
    }

    private void assertCoinAndVictoryPointsEffectsScheduled() {
        verify(effectExecutionContext, times(2))
                .scheduleRewardEvaluationAndCollection(effectArgumentCaptorEndOfTurn.capture(), eq(EffectTiming.END_OF_TURN));
        var endOfTurnEffects = effectArgumentCaptorEndOfTurn.getAllValues();
        assertEquals(2, endOfTurnEffects.size());
        endOfTurnEffects.stream().filter(effect -> effect instanceof CoinRewardEffect)
                .map(effect -> (CoinRewardEffect) effect)
                .forEach(effect -> assertEquals(3, effect.getCoins()));
        endOfTurnEffects.stream().filter(effect -> effect instanceof VictoryPointEffect)
                .map(effect -> (VictoryPointEffect) effect)
                .forEach(effect -> assertEquals(4, effect.getVictoryPoints()));
    }

    private void assertWarShieldsEffectScheduled() {
        verify(effectExecutionContext, times(1))
                .scheduleRewardEvaluationAndCollection(effectArgumentCaptorPermanent.capture(), eq(EffectTiming.ANYTIME));
        var permanentEffects = effectArgumentCaptorPermanent.getAllValues();
        assertEquals(1, permanentEffects.size());
        permanentEffects.stream().filter(effect -> effect instanceof WarShieldsEffect)
                .map(effect -> (WarShieldsEffect) effect)
                .forEach(effect -> assertEquals(1, effect.getShields()));
    }
}