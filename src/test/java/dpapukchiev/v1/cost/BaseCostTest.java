package dpapukchiev.v1.cost;

import dpapukchiev.v1.game.TurnContext;
import dpapukchiev.v1.player.BasePlayerTest;

public class BaseCostTest extends BasePlayerTest {
    protected TurnContext getTurnContext() {
        return TurnContext.builder()
                .player(player1)
                .build();
    }
}
