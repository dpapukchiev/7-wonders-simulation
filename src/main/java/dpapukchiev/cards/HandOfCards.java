package dpapukchiev.cards;

import dpapukchiev.game.TurnContext;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@ToString(onlyExplicitlyIncluded = true)
public class HandOfCards {
    @ToString.Include
    private UUID       uuid;
    private List<Card> cards;

    public List<Card> getBuildableCards(TurnContext turnContext) {
        return getCards().stream()
                .filter(c -> !turnContext.getPlayer().getBuiltCardNames().contains(c.getName()))
                .filter(c -> c.getCost().generateCostReport(turnContext).isAffordable())
                .toList();
    }
}
