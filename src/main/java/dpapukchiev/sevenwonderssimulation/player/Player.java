package dpapukchiev.sevenwonderssimulation.player;

import dpapukchiev.sevenwonderssimulation.cards.Card;
import dpapukchiev.sevenwonderssimulation.cards.CardName;
import dpapukchiev.sevenwonderssimulation.cards.HandOfCards;
import dpapukchiev.sevenwonderssimulation.cost.CostReport;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectExecutionContext;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectReward;
import dpapukchiev.sevenwonderssimulation.game.TurnContext;
import dpapukchiev.sevenwonderssimulation.reporting.CityStatistics;
import dpapukchiev.sevenwonderssimulation.resources.ResourceContext;
import dpapukchiev.sevenwonderssimulation.wonder.WonderContext;
import dpapukchiev.sevenwonderssimulation.wonder.WonderStage;
import jsl.modeling.elements.variable.RandomVariable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;
import java.util.List;

import static dpapukchiev.sevenwonderssimulation.player.WarPoint.MINUS_ONE;
import static dpapukchiev.sevenwonderssimulation.resources.ScienceSymbol.COGWHEEL;
import static dpapukchiev.sevenwonderssimulation.resources.ScienceSymbol.COMPASS;
import static dpapukchiev.sevenwonderssimulation.resources.ScienceSymbol.TABLET;
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
    private Vault                  vault;
    private CityStatistics         cityStatistics;

    public ResourceContext resourceContext() {
        return new ResourceContext(this);
    }

    public Player initVault() {
        vault = Vault.builder()
                .wonderContext(wonderContext)
                .build();
        return this;
    }

    public void executeTurn(TurnContext turnContext) {
        // get the hand of cards
        var handOfCards = turnContext.getHandOfCards();

        var nextAffordableWonderStage = getWonderContext().getNextAffordableWonderStage(turnContext);
        if (nextAffordableWonderStage.isPresent()) {
            buildWonderStage(nextAffordableWonderStage.get(), turnContext);
            return;
        }

        // get cards to build as extensions of already built cards

        var noCostCards = handOfCards.getCardsWithNoCost(turnContext);
        if (!noCostCards.isEmpty()) {
            // pick a card
            var card = selectRandomCard(noCostCards);// build the card
            playCard(turnContext, handOfCards, card);
            return;
        }

        // TODO: play with ordering
        var someCostCards = handOfCards.getCardsWithCost(turnContext)
                .stream()
                .sorted(Comparator.comparingDouble(c ->
                        c.getCost().generateCostReport(turnContext).getToPayTotal())
                )
                .toList();
        if (!someCostCards.isEmpty()) {
            // pick a card
            var card = selectRandomCard(someCostCards);
            // build the card
            playCard(turnContext, handOfCards, card);
            return;
        }

        discardCard(handOfCards);

        // get the cards that can be built for free (effect)
        // get the cards that can be built for free (previous card)

        // if no cards can be built, discard a card
        // if a card can be built, build it
        // pay the cost
        // apply the effect
    }

    private void buildWonderStage(
            Pair<WonderStage, CostReport> nextAffordableWonderStage,
            TurnContext turnContext
    ) {
        var wonderStage = nextAffordableWonderStage.getLeft();
        var costReport = nextAffordableWonderStage.getRight();
        var handOfCards = turnContext.getHandOfCards();
        var cardToConsume = selectRandomCard(handOfCards.getCards());

        wonderStage.build(cardToConsume, turnContext, costReport);
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
            collectMetric("war-wins-from-left", warPoint.getValue());
        } else if (myShields < leftShields) {
            getVault().addWarPoint(MINUS_ONE);
            collectMetric("war-loss-from-left", MINUS_ONE.getValue());
        }

        if (myShields > rightShields) {
            getVault().addWarPoint(warPoint);
            collectMetric("war-wins-from-left", warPoint.getValue());
        } else if (myShields < rightShields) {
            getVault().addWarPoint(MINUS_ONE);
            collectMetric("war-loss-from-right", MINUS_ONE.getValue());
        }
    }

    public ScoreCard score() {
        return ScoreCard.builder()
                .coins(vault.getCoins())
                .warPointsScore(vault.getWarPointsScore())
                .victoryPoints(vault.getVictoryPoints())
                .scienceTablets(resourceContext().getScienceSymbolCount(TABLET))
                .scienceCompasses(resourceContext().getScienceSymbolCount(COMPASS))
                .scienceCogwheels(resourceContext().getScienceSymbolCount(COGWHEEL))
                .build();
    }

    public String report() {
        return String.format("\n%s: %s %s %s",
                name,
                wonderContext.report(),
                effectExecutionContext.report(),
                vault.report()
        );
    }

    private void discardCard(HandOfCards handOfCards) {
        var coinsBefore = getVault().getCoins();
        var cardToDiscard = selectRandomCard(handOfCards.getCards());
        getVault().addCoins(3);

        handOfCards.discard(cardToDiscard);
        getVault().discardCard(cardToDiscard);

        log.info("\nPlayer {} discards card {} and gets 3 coins. {} => {}",
                name,
                cardToDiscard.report(),
                coinsBefore,
                getVault().getCoins()
        );
    }

    private Card selectRandomCard(List<Card> cards) {
        return randomlySelect(cards, pickACard.getStreamNumber());
    }

    private void playCard(TurnContext turnContext, HandOfCards handOfCards, Card card) {
        log.info("\nPlayer {} plays from hand {} card {}", getName(), handOfCards.getUuid(), card.report());
        collectMetric("cards-played", 1);
        collectMetric("%s-cards-played".formatted(card.getType()), 1);
        collectMetric("%s-effect-played".formatted(card.getEffect().getClass().getSimpleName()), 1);
        collectMetric("%s-costs-played".formatted(card.getCost().getClass().getSimpleName()), 1);

        removeFromHandAndAddToVault(handOfCards, card);

        card.getEffect().scheduleRewardEvaluationAndCollection(this);

        var cost = card.getCost().generateCostReport(turnContext);

        if (!cost.isAffordable()) {
            throw new IllegalStateException("Player %s cannot afford card %s".formatted(getName(), card.getCost().report()));
        }

        if (cost.getToPayTotal() == 0) {
            collectMetric("cards-played-for-free", 1);
            return;
        }

//        var availableFreeCostActions = getVault().getSpecialAction(PLAY_CARD_WITHOUT_COST);
//        if (availableFreeCostActions.isPresent()) {
//            collectMetric("special-action-play-card-without-cost", 1);
//            log.info("Player {} uses special action to play card {} with cost {} for free",
//                    getName(),
//                    card.getName(),
//                    cost.getToPayTotal()
//            );
//            getVault().useSpecialAction(PLAY_CARD_WITHOUT_COST);
//            return;
//        }

        payCost(cost, card.getName());
    }

    private void payCost(CostReport cost, CardName cardName) {
        getLeftPlayer().getVault().addCoins(cost.getToPayLeft());
        collectMetric("to-pay-left", cost.getToPayLeft());

        getRightPlayer().getVault().addCoins(cost.getToPayRight());
        collectMetric("to-pay-right", cost.getToPayLeft());

        collectMetric("to-pay-bank", cost.getToPayLeft());

        getVault().removeCoins(cost.getToPayTotal());

        if (cost.getToPayBank() > 1) {
            throw new IllegalStateException("Player %s has %s coins to pay to bank".formatted(getName(), cost.getToPayBank()));
        }

        log.info(
                "Player {} pays for card {} \n${} to bank \n${} to L({}) \n${} to R({}) \ntotal {}",
                getName(),
                cardName,
                cost.getToPayBank(),
                cost.getToPayLeft(),
                getLeftPlayer().getName(),
                cost.getToPayRight(),
                getRightPlayer().getName(),
                cost.getToPayTotal()
        );
    }

    private void removeFromHandAndAddToVault(HandOfCards handOfCards, Card card) {
        handOfCards.remove(card);
        getVault().getBuiltCards().add(card);
    }

    public void applyEffectReward(EffectReward effectReward) {
        log.info("Applying effect reward to player {} ({})", name, effectReward.report());
        vault.addCoins(effectReward.getCoinReward());
        vault.addVictoryPoints(effectReward.getVictoryPointsReward());
        vault.addShields(effectReward.getShields());
    }

    public void collectMetric(String metricName, double value) {
        cityStatistics.collectMetric(metricName, value, this);
    }
}
