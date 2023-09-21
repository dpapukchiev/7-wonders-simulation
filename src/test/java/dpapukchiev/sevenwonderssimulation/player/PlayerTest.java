package dpapukchiev.sevenwonderssimulation.player;

import dpapukchiev.sevenwonderssimulation.BasePlayerTest;
import dpapukchiev.sevenwonderssimulation.effects.ScienceSymbolsEffect;
import dpapukchiev.sevenwonderssimulation.resources.ScienceSymbol;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dpapukchiev.sevenwonderssimulation.player.WarPoint.FIVE;
import static dpapukchiev.sevenwonderssimulation.player.WarPoint.MINUS_ONE;
import static dpapukchiev.sevenwonderssimulation.player.WarPoint.THREE;
import static dpapukchiev.sevenwonderssimulation.resources.ScienceSymbol.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerTest extends BasePlayerTest {

    @Test
    void score() {
        assertEquals(1, mainPlayer.score().getTotalScore());

        var vault = mainPlayer.getVault();
        vault.setCoins(11); // 3
        vault.setWarPoints(List.of(MINUS_ONE, MINUS_ONE, THREE, THREE, FIVE)); // 9
        vault.setVictoryPoints(20); // 20

        assertEquals(32, mainPlayer.score().getTotalScore());

        configureScienceEffect(effect1, COGWHEEL); // 1
        configureScienceEffect(effect2, COGWHEEL); // 1
        configureScienceEffect(effect3, TABLET); // 1

        assignPermanentEffectsToPlayer(List.of(effect1, effect2, effect3));
        assertEquals(37, mainPlayer.score().getTotalScore());

        var wildCard = ScienceSymbolsEffect.of(ScienceSymbol.all());
        assignPermanentEffectsToPlayer(List.of(wildCard, effect1, effect2, effect3));

        assertEquals(37, mainPlayer.score().getTotalScore());

        wildCard.setChosenSymbol(COMPASS); // 1 + 7 (combo)

        assertEquals(45, mainPlayer.score().getTotalScore());
    }
}