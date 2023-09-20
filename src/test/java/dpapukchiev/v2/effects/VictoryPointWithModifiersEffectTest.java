package dpapukchiev.v2.effects;

import dpapukchiev.v2.BasePlayerTest;
import dpapukchiev.v2.cards.Card;
import dpapukchiev.v2.cards.CardType;
import dpapukchiev.v2.player.WarPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class VictoryPointWithModifiersEffectTest extends BasePlayerTest {
    private VictoryPointWithModifiersEffect effect;

    @BeforeEach
    void setUp() {

    }

    @CsvSource({
            "1,1,0,BOTH,RAW_MATERIAL_CARD,4",
            "1,1,1,BOTH,RAW_MATERIAL_CARD,4",
            "1,1,1,ALL,RAW_MATERIAL_CARD,6",
            "1,3,1,ALL,RAW_MATERIAL_CARD,10",
            "1,3,1,LEFT,RAW_MATERIAL_CARD,2",
            "1,3,1,RIGHT,RAW_MATERIAL_CARD,6",
            "1,3,1,BOTH,RAW_MATERIAL_CARD,8",

            "1,1,0,BOTH,WAR_WIN,4",
            "1,1,1,BOTH,WAR_WIN,4",
            "1,1,1,ALL,WAR_WIN,6",
            "1,3,1,ALL,WAR_WIN,10",
            "1,3,1,LEFT,WAR_WIN,2",
            "1,3,1,RIGHT,WAR_WIN,6",
            "1,3,1,BOTH,WAR_WIN,8",
    })
    @ParameterizedTest
    void getEffectReward(
            long leftCount,
            long rightCount,
            int selfCount,
            EffectDirectionConstraint directionConstraint,
            EffectMultiplierType effectMultiplierType,
            long expectedPoints
    ) {
        effect = new VictoryPointWithModifiersEffect(
                directionConstraint,
                effectMultiplierType,
                2
        );
        when(leftPlayerVault.countMultiplier(effectMultiplierType))
                .thenReturn(leftCount);
        when(rightPlayerVault.countMultiplier(effectMultiplierType))
                .thenReturn(rightCount);
        IntStream.range(0, selfCount)
                .forEach(i -> {
                    mainPlayer.getVault()
                            .addBuiltCard(Card.builder()
                                    .type(CardType.RAW_MATERIAL)
                                    .build()
                            );
                    mainPlayer.getVault().addWarPoint(WarPoint.FIVE);
                });

        mainPlayer.getVault().addWarPoint(WarPoint.MINUS_ONE);

        var reward = effect.getReward(mainPlayer);
        assertTrue(reward.isPresent());
        assertEquals(expectedPoints, reward.get().getVictoryPointsReward());
    }

    @Test
    void getReward() {
        effect = new VictoryPointWithModifiersEffect(
                EffectDirectionConstraint.LEFT,
                EffectMultiplierType.WAR_WIN,
                2
        );
        assertEquals(2, effect.getReward());
    }
}