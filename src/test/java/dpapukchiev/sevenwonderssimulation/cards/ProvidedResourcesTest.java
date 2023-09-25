package dpapukchiev.sevenwonderssimulation.cards;

import org.junit.jupiter.api.Test;

import java.util.List;

import static dpapukchiev.sevenwonderssimulation.cards.CardName.APOTEKE;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.ARZNEIAUSGABE;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.PALAST;
import static dpapukchiev.sevenwonderssimulation.cards.CardName.STALLE;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProvidedResourcesTest {

    @Test
    void getCardsWithNoCost() {
        var freeUpgrades = new ProvidedResources();

        assertFalse(freeUpgrades.canBuildForFree(
                getCard(APOTEKE),
                List.of()
        ));

        assertTrue(freeUpgrades.canBuildForFree(
                getCard(STALLE),
                List.of(getCard(APOTEKE))
        ));
        assertTrue(freeUpgrades.canBuildForFree(
                getCard(ARZNEIAUSGABE),
                List.of(getCard(APOTEKE))
        ));

        assertFalse(freeUpgrades.canBuildForFree(
                getCard(ARZNEIAUSGABE),
                List.of(getCard(PALAST))
        ));
    }

    private static Card getCard(CardName cardName) {
        return Card.builder().name(cardName).build();
    }
}