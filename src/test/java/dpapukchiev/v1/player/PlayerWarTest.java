package dpapukchiev.v1.player;

import dpapukchiev.v1.cards.RawMaterial;
import dpapukchiev.v1.cards.WarCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerWarTest extends BasePlayerTest {

    @BeforeEach
    void init() {
        initPlayers();
    }

    @Test
    void calculateShields() {
        player1.setBuiltCards(List.of(
                new WarCard(1, "1", 1, 3, List.of(RawMaterial.WOOD)),
                new WarCard(1, "2", 1, 3, List.of(RawMaterial.WOOD))
        ));

        assertEquals(2, player1.getShieldCount());
    }

    @Test
    void calculateShieldsEmpty() {
        player1.setBuiltCards(List.of());

        assertEquals(0, player1.getShieldCount());
    }

    // Players sitting in a circle
    @CsvSource({
            "1,0,0,0,0,0,0",
            "2,0,0,0,0,0,0",
            "3,0,0,0,0,0,0",

            "1,0,2,0,-1,2,-1",
            "2,0,2,0,-1,6,-1",
            "3,0,2,0,-1,10,-1",

            "1,3,3,0,1,1,-2",
            "2,3,3,0,3,3,-2",
            "3,3,3,0,5,5,-2",

            "1,0,3,3,-2,1,1",
            "2,0,3,3,-2,3,3",
            "3,0,3,3,-2,5,5",
    })
    @ParameterizedTest
    void executeWar1(int age, int p3Shields, int p2Shields, int p1Shields, int p3Expected, int p2Expected, int p1Expected) {
        setShieldsCount(player1, p1Shields);
        setShieldsCount(player2, p2Shields);
        setShieldsCount(player3, p3Shields);

        player1.executeWar(age);
        player2.executeWar(age);
        player3.executeWar(age);

        if (p1Expected < 0) {
            assertEquals(p1Expected, player1.getWarLossPoints());
        } else {
            assertEquals(p1Expected, player1.getWarWinPoints());
        }

        if (p2Expected < 0) {
            assertEquals(p2Expected, player2.getWarLossPoints());
        } else {
            assertEquals(p2Expected, player2.getWarWinPoints());
        }

        if (p3Expected < 0) {
            assertEquals(p3Expected, player3.getWarLossPoints());
        } else {
            assertEquals(p3Expected, player3.getWarWinPoints());
        }
    }

    private void setShieldsCount(Player player, int shieldsCount) {
        if (shieldsCount != 0) {
            player.setBuiltCards(List.of(
                    new WarCard(1, "1", shieldsCount, 3, List.of(RawMaterial.WOOD))
            ));
            return;
        }
        assertEquals(shieldsCount, player.getShieldCount());
    }
}