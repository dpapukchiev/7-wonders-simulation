package dpapukchiev.player;


public class BasePlayerTest {

    protected Player player1;

    protected Player player2;
    protected Player player3;

    protected void initPlayers() {
        player1 = Player.builder()
                .name("Player1")
                .build();
        player2 = Player.builder()
                .name("Player2")
                .build();
        player3 = Player.builder()
                .name("Player3")
                .build();
        player1.setLeftPlayer(player2);
        player1.setRightPlayer(player3);

        player2.setLeftPlayer(player3);
        player2.setRightPlayer(player1);

        player3.setLeftPlayer(player1);
        player3.setRightPlayer(player2);
    }
}