package dpapukchiev.v2.game;

import dpapukchiev.v2.cards.HandOfCards;
import dpapukchiev.v2.player.Player;
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
    private int         age;
    private int         turnCountAge;
}
