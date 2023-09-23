package dpapukchiev.sevenwonderssimulation.game;

import dpapukchiev.sevenwonderssimulation.cards.HandOfCards;
import dpapukchiev.sevenwonderssimulation.player.Player;
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

    public Turn getTurn() {
        return new Turn(age, turnCountAge);
    }

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
