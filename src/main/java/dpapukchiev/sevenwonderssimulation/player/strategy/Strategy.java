package dpapukchiev.sevenwonderssimulation.player.strategy;

import dpapukchiev.sevenwonderssimulation.cards.Card;
import dpapukchiev.sevenwonderssimulation.cost.CostReport;
import dpapukchiev.sevenwonderssimulation.effects.core.Effect;
import dpapukchiev.sevenwonderssimulation.game.Turn;
import dpapukchiev.sevenwonderssimulation.game.TurnContext;
import dpapukchiev.sevenwonderssimulation.player.Player;
import dpapukchiev.sevenwonderssimulation.wonder.WonderStage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;

import static dpapukchiev.sevenwonderssimulation.effects.core.SpecialAction.PLAY_BOTH_CARDS_AT_LAST_TURN_IN_AGE;

@Log4j2
@RequiredArgsConstructor
public class Strategy {
    private final List<StrategyStep> steps;

    public static Strategy defaultStrategy() {
        return new Strategy(List.of(
                new BuildRandomFreeUpgrade(),
                new BuildMostExpensiveUsingPlayWithoutCost(),
                new BuildWonderIfAvailableDiscardRandom(),
                new BuildRandomWithNoCostCard(),
                new BuildRandomWithCostCardIfAffordable(),
                new DiscardRandom()
        ));
    }

    public void execute(TurnContext turnContext) {
        var player = turnContext.getPlayer();
        for (StrategyStep step : steps) {
            var result = step.execute(turnContext);
            switch (result.action()) {
                case BUILD_FOR_FREE -> {
                    removeFromHandAndAddToVault(turnContext, result.card());
                    scheduleEffectReward(player, result.card().getEffect(), turnContext.getTurn());
                    player.collectMetric("free-builds", 1);
                    playLastCardIfHasSpecialAction(turnContext, player);
                    return;
                }
                case BUILD_WITH_SPECIAL_EFFECT -> {
                    removeFromHandAndAddToVault(turnContext, result.card());
                    scheduleEffectReward(player, result.card().getEffect(), turnContext.getTurn());
                    playLastCardIfHasSpecialAction(turnContext, player);

                    player.collectMetric("build-card-with-special-effect", 1);
                    return;
                }
                case BUILD_WITH_COST -> {
                    payCost(turnContext, result.card());
                    removeFromHandAndAddToVault(turnContext, result.card());
                    scheduleEffectReward(player, result.card().getEffect(), turnContext.getTurn());
                    playLastCardIfHasSpecialAction(turnContext, player);

                    player.collectMetric("build-with-cost", 1);
                    return;
                }
                case DISCARD -> {
                    discardCard(turnContext, result.card());
                    playLastCardIfHasSpecialAction(turnContext, player);

                    player.collectMetric("discarded", 1);
                    return;
                }
                case WONDER -> {
                    var wonderStage = player
                            .getWonderContext()
                            .getNextWonderStage(turnContext);
                    if (wonderStage.isEmpty()) {
                        throw new RuntimeException("StrategyStep no wonder stage to build");
                    }
                    buildWonderStage(turnContext, wonderStage.get(), result.card());
                    playLastCardIfHasSpecialAction(turnContext, player);

                    player.collectMetric("wonder-stages-" + wonderStage.get().getStageNumber(), 1);
                    return;
                }
                case SKIP -> {
                }
            }
        }

        throw new RuntimeException("StrategyStep no action to execute");
    }

    private static void playLastCardIfHasSpecialAction(TurnContext turnContext, Player player) {
        if (turnContext.getTurnCountAge() == 6) {
            if (player.getVault().getSpecialAction(PLAY_BOTH_CARDS_AT_LAST_TURN_IN_AGE).isPresent()) {
                player.getVault().useSpecialAction(PLAY_BOTH_CARDS_AT_LAST_TURN_IN_AGE);
                List<Card> cardList = turnContext
                        .getHandOfCards()
                        .getCardsWithoutAlreadyBuilt(turnContext);

                if (cardList.size() > 1) {
                    throw new RuntimeException("More than one card to play: %s".formatted(cardList));
                }
                if (cardList.isEmpty()) {
                    log.info("Player {} can play 0/{} cards at last turn in age {}",
                            player.getName(),
                            turnContext.getHandOfCards().getCards().size(),
                            turnContext.getAge()
                    );
                    return;
                }

                var cartToPlay = cardList.get(0);
                if (cartToPlay == null) {
                    throw new RuntimeException("No leftover card to play");
                }
                player.playExtraCard(cartToPlay);
                turnContext.getHandOfCards().remove(cartToPlay);
                if (!turnContext.getHandOfCards().getCards().isEmpty()) {
                    throw new RuntimeException(("%s cards left in hand").formatted(turnContext.getHandOfCards().getCards().size()));
                }
            }
        }
    }

    private void scheduleEffectReward(Player player, Effect cardEffect, Turn turn) {
        cardEffect.scheduleRewardEvaluationAndCollection(player, turn);

        log.info("Effect {} scheduled for player {}", cardEffect.report(), player.getName());
    }

    private void removeFromHandAndAddToVault(TurnContext turnContext, Card card) {
        turnContext.getHandOfCards().remove(card);
        turnContext.getPlayer().getVault().addBuiltCard(card);

        log.info("\nPlayer {} builds card {}", turnContext.getPlayer().getName(), card.report());
    }

    private void payCost(TurnContext turnContext, Card card) {
        var costReport = card.getCost().generateCostReport(turnContext);
        payCost(turnContext, "card" + card.getName().name(), costReport);
    }

    private void payCost(TurnContext turnContext, String name, CostReport costReport) {
        var player = turnContext.getPlayer();
        if (!costReport.isAffordable()) {
            throw new IllegalStateException("Player %s cannot afford to pay for %s"
                    .formatted(player.getName(), name));
        }
        if (costReport.getToPayTotal() > player.getVault().getCoins()) {
            throw new IllegalStateException("Player %s has %s coins but needs %s to pay for %s"
                    .formatted(player.getName(), player.getVault().getCoins(), costReport.getToPayTotal(), name));
        }
        if (costReport.getToPayTotal() == 0) {
            log.info("Player {} plays card {} for FREE", player.getName(), name);
            return;
        }

        player.getLeftPlayer().getVault().addCoins(costReport.getToPayLeft());
        player.collectMetric("to-pay-left", costReport.getToPayLeft());

        player.getRightPlayer().getVault().addCoins(costReport.getToPayRight());
        player.collectMetric("to-pay-right", costReport.getToPayLeft());

        player.collectMetric("to-pay-bank", costReport.getToPayLeft());

        player.getVault().removeCoins(costReport.getToPayTotal());

        if (costReport.getToPayBank() > 1) {
            throw new IllegalStateException("Player %s has %s coins to pay to bank".formatted(player.getName(), costReport.getToPayBank()));
        }

        log.info(
                "Player {} pays for {} \n${} to bank \n${} to L({}) \n${} to R({}) \ntotal {}",
                player.getName(),
                name,
                costReport.getToPayBank(),
                costReport.getToPayLeft(),
                player.getLeftPlayer().getName(),
                costReport.getToPayRight(),
                player.getRightPlayer().getName(),
                costReport.getToPayTotal()
        );
    }

    private void discardCard(TurnContext turnContext, Card cardToDiscard) {
        var player = turnContext.getPlayer();
        var coinsBefore = player.getVault().getCoins();
        player.getVault().addCoins(3);

        turnContext.getHandOfCards().discard(cardToDiscard);
        player.getVault().discardCard(cardToDiscard);

        log.info("\nPlayer {} discards card {} and gets 3 coins. {} => {}",
                player.getName(),
                cardToDiscard.report(),
                coinsBefore,
                player.getVault().getCoins()
        );
    }

    private void buildWonderStage(
            TurnContext turnContext,
            WonderStage wonderStage,
            Card cardToConsume
    ) {
        var costReport = wonderStage.getCost().generateCostReport(turnContext);
        wonderStage.build(cardToConsume, turnContext);
        turnContext.getHandOfCards().remove(cardToConsume);
        scheduleEffectReward(turnContext.getPlayer(), wonderStage.getEffect(), turnContext.getTurn());
        payCost(
                turnContext,
                "wonder stage %s-%s".formatted(
                        turnContext.getPlayer().getWonderContext().getName(),
                        wonderStage.getStageNumber()
                ),
                costReport
        );
    }

}
