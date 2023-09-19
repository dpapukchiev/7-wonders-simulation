package dpapukchiev.v1.cards;

import dpapukchiev.v1.game.TurnContext;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@ToString(onlyExplicitlyIncluded = true)
public class HandOfCards {
    @ToString.Include
    private UUID       uuid;
    private List<Card> cards;
    private Deck deck;

    public List<Card> getBuildableCards(TurnContext turnContext) {
        var builtCards = turnContext.getPlayer().getBuiltCards();
        var cardsToReturn = new HashMap<String, Card>();

        getCards().stream()
                .filter(c -> builtCards.stream().anyMatch(bc -> bc.getFreeUpgrades().contains(c.getName())))
                .filter(c -> !cardsToReturn.containsKey(c.getName()))
                .forEach(c -> cardsToReturn.put(c.getName(), c));
        getCards().stream()
                .filter(c -> !turnContext.getPlayer().getBuiltCardNames().contains(c.getName()))
                .filter(c -> c.getCost().generateCostReport(turnContext).isAffordable())
                .filter(c -> !cardsToReturn.containsKey(c.getName()))
                .forEach(c -> cardsToReturn.put(c.getName(), c));

        return cardsToReturn.values().stream().toList();
    }

    public void discard(Card card) {
        deck.discard(card);
    }
}
