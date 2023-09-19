package dpapukchiev.v2.cards;

import dpapukchiev.v2.cost.CostReport;
import dpapukchiev.v2.game.TurnContext;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.math3.util.Pair;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

@Getter
@Builder
@ToString(onlyExplicitlyIncluded = true)
public class HandOfCards {
    @ToString.Include
    private UUID       uuid;
    private List<Card> cards;
    private Deck       deck;

    public void discard(Card card) {
        deck.discard(card);
    }

    public List<Pair<Card, CostReport>> getCostReportsPerCard(TurnContext turnContext) {
        return getCards().stream()
                .map(card -> Pair.create(card, card.getCost().generateCostReport(turnContext)))
                .toList();
    }

    public List<Pair<Card, CostReport>> filterCards(TurnContext turnContext, Predicate<Pair<Card, CostReport>> filter) {
        return getCostReportsPerCard(turnContext).stream()
                .filter(filter)
                .toList();
    }

    public List<Card> getCardsWithNoCost(TurnContext turnContext) {
        return filterCards(turnContext, entry -> entry.getValue().getToPayTotal() == 0)
                .stream().map(Pair::getFirst)
                .toList();
    }

    public List<Card> getCardsWithCost(TurnContext turnContext) {
        return filterCards(turnContext, entry -> entry.getValue().getToPayTotal() > 0)
                .stream().map(Pair::getFirst)
                .toList();
    }
}
