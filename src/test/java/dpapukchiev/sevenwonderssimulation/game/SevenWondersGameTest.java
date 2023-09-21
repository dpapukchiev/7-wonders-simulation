package dpapukchiev.sevenwonderssimulation.game;

import jsl.simulation.Simulation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SevenWondersGameTest {
    @Test
    void play() {
        var simulation = new Simulation();
        simulation.setNumberOfReplications(1);

        var gameOptions = GameOptions.builder()
                .numberOfPlayers(7)
                .agesToSchedule(3)
                .build();

        var game = new SevenWondersGame(
                simulation.getModel(),
                gameOptions
        );

        simulation.run();

        var players = game.getPlayersFactory().getPlayers();
        assertEquals(gameOptions.numberOfPlayers(), players.size());

        // All players have 1 left over card from each age
        game.getHandsOfCardsPerAge().entrySet()
                .stream()
                .filter(e -> e.getKey() <= gameOptions.agesToSchedule())
                .forEach(e -> {
                    var handsOfCards = e.getValue();
                    assertEquals(gameOptions.numberOfPlayers(), handsOfCards.size());
                    handsOfCards.forEach(handOfCards -> assertTrue( handOfCards.getCards().size() == 1,
                            "hand %s has %s left over cards".formatted(handOfCards.getUuid(), handOfCards.getCards().size())));
                });
    }
}