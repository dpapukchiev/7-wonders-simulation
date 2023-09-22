package dpapukchiev.sevenwonderssimulation.effects;

import dpapukchiev.sevenwonderssimulation.BasePlayerTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VictoryPointEffectTest extends BasePlayerTest {

    @ParameterizedTest
    @ValueSource(doubles = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    void getReward(double i) {
        var effect = new VictoryPointEffect(i);

        var reward = effect.collectReward(mainPlayer);

        assertTrue(reward.isPresent());
        assertEquals(i, reward.get().getVictoryPointsReward());
    }
}