package dpapukchiev.sevenwonderssimulation.cards;

import dpapukchiev.sevenwonderssimulation.cost.CostReport;
import dpapukchiev.sevenwonderssimulation.game.TurnContext;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.math3.util.Pair;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        cards.remove(card);
    }

    public List<Pair<Card, CostReport>> getCostReportsPerCard(TurnContext turnContext) {
        return getCards().stream()
                .map(card -> Pair.create(card, card.getCost().generateCostReport(turnContext)))
                .toList();
    }

    public List<Pair<Card, CostReport>> getAffordableNotBuiltCards(TurnContext turnContext, Predicate<Pair<Card, CostReport>> filter) {
        var builtCards = turnContext.getPlayer().getVault().getBuiltCardNames();
        return getCostReportsPerCard(turnContext).stream()
                .filter(card -> !builtCards.contains(card.getKey().getName().name()))
                .filter(cardCostReportPair -> cardCostReportPair.getValue().isAffordable())
                .filter(filter)
                .toList();
    }

    public List<Card> getCardsWithoutAlreadyBuilt(TurnContext turnContext) {
        return getCards()
                .stream()
                .filter(card -> !turnContext.getPlayer().getVault().getBuiltCardNames().contains(card.getName().name()))
                .toList();
    }

    public List<Card> getCardsWithNoCost(TurnContext turnContext) {
        return getAffordableNotBuiltCards(turnContext, entry -> entry.getValue().getToPayTotal() == 0)
                .stream()
                .map(Pair::getFirst)
                .toList();
    }

    public List<Card> getCardsWithCost(TurnContext turnContext) {
        return getAffordableNotBuiltCards(turnContext, entry -> entry.getValue().getToPayTotal() > 0)
                .stream().map(Pair::getFirst)
                .toList();
    }

    public String report() {
        return "\nHandOfCards(%s): %s \n%s".formatted(cards.size(), uuid, cards.stream()
                .map(Card::report)
                .collect(Collectors.joining("\n")));
    }

    public void remove(Card card) {
        cards.remove(card);
    }
}
