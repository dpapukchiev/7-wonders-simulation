package dpapukchiev.game;

import dpapukchiev.cards.HandOfCards;
import dpapukchiev.player.Player;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class TurnContext {
    private Player player;
    private HandOfCards handOfCards;
    private int simulationStep;
    private int age;
    private int turnCountAge;
}
