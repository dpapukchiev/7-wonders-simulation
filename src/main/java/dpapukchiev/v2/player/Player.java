package dpapukchiev.v2.player;

import dpapukchiev.v2.cards.Card;
import dpapukchiev.v2.cost.CostReport;
import dpapukchiev.v2.effects.EffectExecutionContext;
import dpapukchiev.v2.game.TurnContext;
import dpapukchiev.v2.resources.ResourceContext;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Getter
@Builder
public class Player {
    @Builder.Default
    private EffectExecutionContext effectExecutionContext = new EffectExecutionContext();
    @Builder.Default
    private Vault                  vault                  = new Vault();
    @Builder.Default
    private List<Card>             builtCards             = new ArrayList<>();

    public ResourceContext resourceContext() {
        return new ResourceContext(this);
    }

    public void executeTurn(TurnContext turnContext) {
        // get the hand of cards
        var handOfCards = turnContext.getHandOfCards();

        var noCostCards = handOfCards.getCardsWithNoCost(turnContext);
        var someCostCards = handOfCards.getCardsWithCost(turnContext);

        // get the cards that can be built for free (effect)
        // get the cards that can be built for free (previous card)

        // if no cards can be built, discard a card
        // if a card can be built, build it
        // pay the cost
        // apply the effect
    }

    public Set<String> getBuiltCardNames() {
        return builtCards.stream().map(Card::getName).collect(Collectors.toSet());
    }
}
