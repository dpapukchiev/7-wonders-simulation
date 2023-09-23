package dpapukchiev.sevenwonderssimulation.player;

import dpapukchiev.sevenwonderssimulation.cards.Card;
import dpapukchiev.sevenwonderssimulation.cards.CardType;
import dpapukchiev.sevenwonderssimulation.cards.Deck;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectExecutionContext;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectReward;
import dpapukchiev.sevenwonderssimulation.game.TurnContext;
import dpapukchiev.sevenwonderssimulation.player.strategy.Strategy;
import dpapukchiev.sevenwonderssimulation.reporting.CityStatistics;
import dpapukchiev.sevenwonderssimulation.resources.ResourceContext;
import dpapukchiev.sevenwonderssimulation.wonder.WonderContext;
import jsl.modeling.elements.variable.RandomVariable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.List;

import static dpapukchiev.sevenwonderssimulation.effects.core.SpecialAction.COPY_GUILD_CARD;
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

    public Player initVault(Deck deck) {
        vault = Vault.builder()
                .wonderContext(wonderContext)
                .deck(deck)
                .build();
        return this;
    }

    public void applyEffectReward(EffectReward effectReward) {
        log.info("\nApplying effect reward to player {} ({})", name, effectReward.report());
        vault.addCoins(effectReward.getCoinReward());
        vault.addVictoryPoints(effectReward.getVictoryPointsReward());
        vault.addShields(effectReward.getShields());
    }

    public void executeTurn(TurnContext turnContext) {
        Strategy.defaultStrategy().execute(turnContext);
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

    public void playExtraCard(Card card) {
        getVault().addBuiltCard(card);
        var cardEffect = card.getEffect();
        cardEffect.collectReward(this)
                .ifPresent(this::applyEffectReward);
        cardEffect.markAsExhausted();
        getEffectExecutionContext()
                .getPermanentEffects()
                .add(cardEffect);
    }

    public void copyGuildCardEffectIfHasSpecialAction(List<Player> players){
        var vault = getVault();
        vault.getSpecialAction(COPY_GUILD_CARD)
                .ifPresent(specialAction -> {
                    var cardsToCopyFrom = players.stream()
                            .flatMap(p -> p.getVault().getBuiltCards().stream())
                            .filter(c -> c.getType().equals(CardType.GUILD))
                            .filter(c -> !vault.getBuiltCardNames().contains(c.getName().name()))
                            .toList();
                    if (cardsToCopyFrom.isEmpty()) {
                        log.info("No guild cards to copy from. Player {} will not copy a guild card", getName());
                        return;
                    }
                    var cardToCopy = selectRandomCard(cardsToCopyFrom);
                    var reward = cardToCopy.getEffect().collectReward(this);
                    if (reward.isPresent()) {
                        applyEffectReward(reward.get());
                        log.info("Player {} gets reward {} for copying guild card {}", getName(), reward.get().report(), cardToCopy.getName());
                    } else {
                        log.info("Player {} gets no reward for copying guild card {}", getName(), cardToCopy.getName());
                    }
                });
    }

    public Card selectRandomCard(List<Card> cards) {
        return randomlySelect(cards, pickACard.getStreamNumber());
    }

    public ScoreCard score() {
        return ScoreCard.builder()
                .coins(vault.getCoins())
                .warPointsScore(vault.getWarPointsScore())
                .victoryPoints(vault.getVictoryPoints())
                .scienceTablets(resourceContext().getScienceSymbolCount(TABLET))
                .scienceCompasses(resourceContext().getScienceSymbolCount(COMPASS))
                .scienceCogwheels(resourceContext().getScienceSymbolCount(COGWHEEL))
                .scienceWildcards(resourceContext().getScienceSymbolWildcardCount())
                .build();
    }

    public String report() {
        return String.format("\n%s: %s\n%s %s",
                name,
                wonderContext.report(),
                effectExecutionContext.report(),
                vault.report()
        );
    }

    public void collectMetric(String metricName, double value) {
        cityStatistics.collectMetric(metricName, value, this);
    }

    public void collectFinalMetrics() {
        var scoreCard = score();
        cityStatistics.collectMetric("score-total", scoreCard.getTotalScore(), this);
        cityStatistics.collectMetric("coins-score", scoreCard.getCoinsScore(), this);
        cityStatistics.collectMetric("science-score", scoreCard.getScienceScore(), this);
        cityStatistics.collectMetric("built-cards", getVault().getBuiltCards().size(), this);
        cityStatistics.collectMetric("discarded-cards", getVault().getDiscardedCards().size(), this);
    }
}
