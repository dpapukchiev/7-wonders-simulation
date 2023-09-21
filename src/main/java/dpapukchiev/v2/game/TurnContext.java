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
    private double      simulationStep;
    private int         age;
    private int         turnCountAge;

    public String report() {
        return String.format("\nTurnContext: simStep=%s age=%s turn=%s %s \n%s\n",
                simulationStep,
                age,
                turnCountAge,
                handOfCards.report(),
                player.report()
        );
    }
}
