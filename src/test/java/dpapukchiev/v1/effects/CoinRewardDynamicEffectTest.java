package dpapukchiev.v1.effects;

import dpapukchiev.v1.cards.SingleManufacturedGoodCard;
import dpapukchiev.v1.cards.SingleResourceCard;
import dpapukchiev.v1.player.BasePlayerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static dpapukchiev.v1.cards.ManufacturedGood.GLASS;
import static dpapukchiev.v1.cards.ManufacturedGood.SCRIPTS;
import static dpapukchiev.v1.cards.ManufacturedGood.TEXTILE;
import static dpapukchiev.v1.cards.RawMaterial.CLAY;
import static dpapukchiev.v1.cards.RawMaterial.WOOD;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CoinRewardDynamicEffectTest extends BasePlayerTest {

    @BeforeEach
    void setUp() {
        initPlayers();
    }

    @Test
    void applyEffect() {
        player1.setCoins(0);

        var coinRewardDynamicEffect = new CoinRewardDynamicEffect(1,
                CoinRewardDynamicEffect.DirectionModifier.LEFT,
                CoinRewardDynamicEffect.CardTypeModifier.RAW_MATERIAL
        );
        coinRewardDynamicEffect.applyEffect(player1);
        assertEquals(0, player1.getCoins());
    }

    @CsvSource({
            "1,1,1",
            "1,2,2",
            "2,1,2",
            "2,2,4",
    })
    @ParameterizedTest
    void applyEffectLeft(int leftCards, int coinReward, int expectedCoins) {
        player1.setCoins(0);

        var coinRewardDynamicEffect = new CoinRewardDynamicEffect(coinReward,
                CoinRewardDynamicEffect.DirectionModifier.LEFT,
                CoinRewardDynamicEffect.CardTypeModifier.RAW_MATERIAL
        );

        for (int i = 0; i < leftCards; i++) {
            player1.getLeftPlayer().getBuiltCards()
                    .add(new SingleResourceCard(1, "name", WOOD, 1));
        }

        coinRewardDynamicEffect.applyEffect(player1);
        assertEquals(expectedCoins, player1.getCoins());
    }

    @CsvSource({
            "1,1,1",
            "1,2,2",
            "2,1,2",
            "2,2,4",
    })
    @ParameterizedTest
    void applyEffectRight(int rightCards, int coinReward, int expectedCoins) {
        player1.setCoins(0);

        var coinRewardDynamicEffect = new CoinRewardDynamicEffect(coinReward,
                CoinRewardDynamicEffect.DirectionModifier.RIGHT,
                CoinRewardDynamicEffect.CardTypeModifier.RAW_MATERIAL
        );

        for (int i = 0; i < rightCards; i++) {
            player1.getRightPlayer().getBuiltCards()
                    .add(new SingleResourceCard(1, "name", WOOD, 1));
        }

        coinRewardDynamicEffect.applyEffect(player1);
        assertEquals(expectedCoins, player1.getCoins());
    }

    @CsvSource({
            "1,0,1,1",
            "1,0,2,2",
            "2,0,1,2",
            "2,0,2,4",

            "1,1,1,2",
            "1,1,2,4",
            "2,1,1,3",
            "2,1,2,6",
    })
    @ParameterizedTest
    void applyEffectAll(int rightCards, int ownCards, int coinReward, int expectedCoins) {
        player1.setCoins(0);

        var coinRewardDynamicEffect = new CoinRewardDynamicEffect(coinReward,
                CoinRewardDynamicEffect.DirectionModifier.ALL,
                CoinRewardDynamicEffect.CardTypeModifier.RAW_MATERIAL
        );
        for (int i = 0; i < ownCards; i++) {
            player1.getBuiltCards()
                    .add(new SingleResourceCard(1, "name", CLAY, 1));
        }

        for (int i = 0; i < rightCards; i++) {
            player1.getRightPlayer().getBuiltCards()
                    .add(new SingleResourceCard(1, "name", WOOD, 1));
        }

        coinRewardDynamicEffect.applyEffect(player1);
        assertEquals(expectedCoins, player1.getCoins());
    }

    @CsvSource({
            "1,1,1",
            "1,2,2",
            "2,1,2",
            "2,2,4",
    })
    @ParameterizedTest
    void applyEffectLeftGoods(int leftCards, int coinReward, int expectedCoins) {
        player1.setCoins(0);

        var coinRewardDynamicEffect = new CoinRewardDynamicEffect(coinReward,
                CoinRewardDynamicEffect.DirectionModifier.LEFT,
                CoinRewardDynamicEffect.CardTypeModifier.MANUFACTURED_GOOD
        );

        for (int i = 0; i < leftCards; i++) {
            player1.getLeftPlayer().getBuiltCards()
                    .add(new SingleManufacturedGoodCard(1, "name", GLASS, 1));
        }

        coinRewardDynamicEffect.applyEffect(player1);
        assertEquals(expectedCoins, player1.getCoins());
    }

    @CsvSource({
            "1,1,1",
            "1,2,2",
            "2,1,2",
            "2,2,4",
    })
    @ParameterizedTest
    void applyEffectRightGoods(int rightCards, int coinReward, int expectedCoins) {
        player1.setCoins(0);

        var coinRewardDynamicEffect = new CoinRewardDynamicEffect(coinReward,
                CoinRewardDynamicEffect.DirectionModifier.RIGHT,
                CoinRewardDynamicEffect.CardTypeModifier.MANUFACTURED_GOOD
        );

        for (int i = 0; i < rightCards; i++) {
            player1.getRightPlayer().getBuiltCards()
                    .add(new SingleManufacturedGoodCard(1, "name", TEXTILE, 1));
        }

        coinRewardDynamicEffect.applyEffect(player1);
        assertEquals(expectedCoins, player1.getCoins());
    }

    @CsvSource({
            "1,0,1,1",
            "1,0,2,2",
            "2,0,1,2",
            "2,0,2,4",

            "1,1,1,2",
            "1,1,2,4",
            "2,1,1,3",
            "2,1,2,6",
    })
    @ParameterizedTest
    void applyEffectAllGoods(int rightCards, int ownCards, int coinReward, int expectedCoins) {
        player1.setCoins(0);

        var coinRewardDynamicEffect = new CoinRewardDynamicEffect(coinReward,
                CoinRewardDynamicEffect.DirectionModifier.ALL,
                CoinRewardDynamicEffect.CardTypeModifier.MANUFACTURED_GOOD
        );
        for (int i = 0; i < ownCards; i++) {
            player1.getBuiltCards()
                    .add(new SingleManufacturedGoodCard(1, "name", SCRIPTS, 1));
        }

        for (int i = 0; i < rightCards; i++) {
            player1.getRightPlayer().getBuiltCards()
                    .add(new SingleManufacturedGoodCard(1, "name", TEXTILE, 1));
        }

        coinRewardDynamicEffect.applyEffect(player1);
        assertEquals(expectedCoins, player1.getCoins());
    }
}