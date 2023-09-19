package dpapukchiev.v2.player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VaultTest {

    private Vault vault;

    @BeforeEach
    void setUp() {
        vault = new Vault();
    }

    @Test
    void addWarPoint() {
        vault.addWarPoint(WarPoint.MINUS_ONE);
        vault.addWarPoint(WarPoint.MINUS_ONE);
        vault.addWarPoint(WarPoint.MINUS_ONE);
        vault.addWarPoint(WarPoint.THREE);
        vault.addWarPoint(WarPoint.FIVE);

        assertEquals(5, vault.getWarPoints());
    }

    @Test
    void addVictoryPoints() {
        vault.addVictoryPoints(2);
        vault.addVictoryPoints(2);
        vault.addVictoryPoints(5);

        assertEquals(9, vault.getVictoryPoints());
    }

    @Test
    void addShields() {
        vault.addShields(2);
        vault.addShields(3);

        assertEquals(5, vault.getShields());
    }

    @Test
    void addCoins() {
        vault.addCoins(2);
        vault.addCoins(3);

        assertEquals(8, vault.getCoins());
    }

    @Test
    void removeCoins() {
        vault.addCoins(2);
        vault.addCoins(3);
        vault.removeCoins(2);

        assertEquals(6, vault.getCoins());
    }
}