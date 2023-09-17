package dpapukchiev.player;

import dpapukchiev.cards.Card;
import dpapukchiev.city.CityName;
import dpapukchiev.game.TurnContext;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Setter
@Getter
@Builder
@ToString(exclude = {"leftPlayer", "rightPlayer"})
public class Player {
    @Builder.Default
    private double coins = 3;
    private String name;
    private CityName city;
    @Builder.Default
    private List<Card> builtCards = new ArrayList<>();
    private Player leftPlayer;
    private Player rightPlayer;

    public void executeTurn(TurnContext turnContext) {
        log.info(
//                "{} Executing turn age: {}, turn: {}, player: {}, hand: {} ({}), built: {}",
                "player: {}, hand: {}",
//                turnContext.getSimulationStep(),
//                turnContext.getAge(),
//                turnContext.getTurnCountAge(),
                turnContext.getPlayer().getName(),
                turnContext.getHandOfCards().getUuid()
//                turnContext.getHandOfCards().getCards().size(),
//                builtCards.size()
        );
    }
}