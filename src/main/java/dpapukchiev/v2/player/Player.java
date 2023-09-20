package dpapukchiev.v2.player;

import dpapukchiev.v2.cards.Card;
import dpapukchiev.v2.cards.CardType;
import dpapukchiev.v2.effects.EffectExecutionContext;
import dpapukchiev.v2.effects.EffectMultiplierType;
import dpapukchiev.v2.game.TurnContext;
import dpapukchiev.v2.resources.ResourceContext;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static dpapukchiev.v2.cards.CardType.MANUFACTURED_GOOD;
import static dpapukchiev.v2.cards.CardType.RAW_MATERIAL;

@Getter
@Builder
public class Player {
    private String                 name;
    @Setter
    private Player                 leftPlayer;
    @Setter
    private Player                 rightPlayer;
    @Builder.Default
    private EffectExecutionContext effectExecutionContext = new EffectExecutionContext();
    @Builder.Default
    private Vault                  vault                  = new Vault();

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


}
