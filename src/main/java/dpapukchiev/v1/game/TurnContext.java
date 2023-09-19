package dpapukchiev.v1.game;

import dpapukchiev.v1.cards.HandOfCards;
import dpapukchiev.v1.player.Player;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class TurnContext {
    private Player      player;
    private HandOfCards handOfCards;
    private int         simulationStep;
    private int age;
    private int turnCountAge;
}
