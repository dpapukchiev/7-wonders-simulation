package dpapukchiev.v2.player;

import dpapukchiev.v2.cards.Card;
import dpapukchiev.v2.cards.HandOfCards;
import dpapukchiev.v2.effects.core.EffectExecutionContext;
import dpapukchiev.v2.effects.core.EffectReward;
import dpapukchiev.v2.game.TurnContext;
import dpapukchiev.v2.resources.ResourceContext;
import jsl.modeling.elements.variable.RandomVariable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.Comparator;

import static jsl.utilities.random.rvariable.JSLRandom.randomlySelect;

@Log4j2
@Getter
@Builder
public class Player {
    private String                 name;
    private WonderContext          wonderContext;
    private RandomVariable         pickACard;
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
        if (!noCostCards.isEmpty()) {
            // pick a card
            var card = randomlySelect(noCostCards, pickACard.getRandomNumberStream());
            // build the card
            playCard(turnContext, handOfCards, card);
            return;
        }
        var someCostCards = handOfCards.getCardsWithCost(turnContext)
                .stream()
                .sorted(Comparator.comparingDouble(c ->
                        c.getCost().generateCostReport(turnContext).getToPayTotal())
                )
                .toList();
        if (!someCostCards.isEmpty()) {
            // pick a card
            var card = randomlySelect(someCostCards, pickACard.getRandomNumberStream());
            // build the card
            playCard(turnContext, handOfCards, card);
            return;
        }

        var cardToDiscard = randomlySelect(handOfCards.getCards(), pickACard.getStreamNumber());
        turnContext.getPlayer().getVault().addCoins(3);

        playCard(turnContext, handOfCards, cardToDiscard);

        // get the cards that can be built for free (effect)
        // get the cards that can be built for free (previous card)

        // if no cards can be built, discard a card
        // if a card can be built, build it
        // pay the cost
        // apply the effect
    }

    public void executeWar(int age) {
        var warPoint = age != 1 ? (age != 2 ? WarPoint.FIVE : WarPoint.THREE) : WarPoint.ONE;
        var myShields = getVault().getShields();
        var leftShields = getLeftPlayer().getVault().getShields();
        var rightShields = getRightPlayer().getVault().getShields();
        log.info("Evaluating war for player {} has {} shields, left {} and right {}",
                name, myShields, leftShields, rightShields);

        if (myShields > leftShields) {
            getVault().addWarPoint(warPoint);
        } else if (myShields < leftShields) {
            getVault().addWarPoint(WarPoint.MINUS_ONE);
        }

        if (myShields > rightShields) {
            getVault().addWarPoint(warPoint);
        } else if (myShields < rightShields) {
            getVault().addWarPoint(WarPoint.MINUS_ONE);
        }
    }

    private void playCard(TurnContext turnContext, HandOfCards handOfCards, Card card) {
        log.info("\nPlayer {} plays from hand {} card {}", turnContext.getPlayer().getName(), handOfCards.getUuid(), card.report());
        handOfCards.getCards().remove(card);
        turnContext.getPlayer().getVault().getBuiltCards().add(card);

        card.getEffect().scheduleEffect(turnContext.getPlayer());
    }

    public String report() {
        return String.format("\n%s: %s %s %s",
                name,
                wonderContext.report(),
                effectExecutionContext.report(),
                vault.report()
        );
    }

    public void applyEffectReward(EffectReward effectReward) {
        log.info("Applying effect reward to player {} ({})", name, effectReward.report());
        vault.addCoins(effectReward.getCoinReward());
        vault.addVictoryPoints(effectReward.getVictoryPointsReward());
        vault.addShields(effectReward.getShields());
    }
}
