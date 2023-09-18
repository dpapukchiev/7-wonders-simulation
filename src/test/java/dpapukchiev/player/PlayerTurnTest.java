package dpapukchiev.player;

import dpapukchiev.cards.RawMaterial;
import dpapukchiev.cards.WarCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerTurnTest extends BasePlayerTest {

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
}