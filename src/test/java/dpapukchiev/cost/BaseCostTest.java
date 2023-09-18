package dpapukchiev.cost;

import dpapukchiev.game.TurnContext;
import dpapukchiev.player.BasePlayerTest;

public class BaseCostTest extends BasePlayerTest {
    protected TurnContext getTurnContext() {
        return TurnContext.builder()
                .player(player1)
                .build();
    }
}
