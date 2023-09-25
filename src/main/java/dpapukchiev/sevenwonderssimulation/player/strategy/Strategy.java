package dpapukchiev.sevenwonderssimulation.player.strategy;

import dpapukchiev.sevenwonderssimulation.cards.Card;
import dpapukchiev.sevenwonderssimulation.cost.CostReport;
import dpapukchiev.sevenwonderssimulation.effects.core.Effect;
import dpapukchiev.sevenwonderssimulation.game.Turn;
import dpapukchiev.sevenwonderssimulation.game.TurnContext;
import dpapukchiev.sevenwonderssimulation.player.Player;
import dpapukchiev.sevenwonderssimulation.wonder.WonderStage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;

import static dpapukchiev.sevenwonderssimulation.effects.core.SpecialAction.PLAY_BOTH_CARDS_AT_LAST_TURN_IN_AGE;

@Getter
@Log4j2
@RequiredArgsConstructor
public class Strategy {
    public enum StrategyName {
        DEFAULT,
        V2,
        V4,
        V5,
        V3
    }

    private final StrategyName       name;
    private final List<StrategyStep> steps;

    public static Strategy defaultStrategy() {
        return new Strategy(StrategyName.DEFAULT, List.of(
                new BuildRandomFreeUpgrade(),
                new BuildRandomWithCostCardIfAffordable(),
                new BuildMostExpensiveUsingPlayWithoutCost(),
                new BuildWonderIfAvailableDiscardRandom(),
                new BuildRandomWithNoCostCard(),
                new DiscardRandom()
        ));
    }

    public static Strategy v2() {
        return new Strategy(StrategyName.V2, List.of(
                new BuildMostExpensiveUsingPlayWithoutCost(),
                new BuildRandomWithNoCostCard(),
                new BuildRandomFreeUpgrade(),
                new BuildRandomWithCostCardIfAffordable(),
                new BuildWonderIfAvailableDiscardRandom(),
                new DiscardRandom()
        ));
    }

    public static Strategy v3() {
        return new Strategy(StrategyName.V3, List.of(
                new BuildWonderIfAvailableDiscardRandom(),
                new BuildMostExpensiveUsingPlayWithoutCost(),
                new BuildRandomWithNoCostCard(),
                new BuildRandomFreeUpgrade(),
                new BuildRandomWithCostCardIfAffordable(),
                new DiscardRandom()
        ));
    }

    public static Strategy v4() {
        return new Strategy(StrategyName.V4, List.of(
                new BuildRandomFreeUpgrade(),
                new BuildRandomWithNoCostCard(),
                new BuildWonderIfAvailableDiscardRandom(),
                new BuildMostExpensiveUsingPlayWithoutCost(),
                new BuildRandomWithCostCardIfAffordable(),
                new DiscardRandom()
        ));
    }

    public static Strategy v5() {
        return new Strategy(StrategyName.V5, List.of(
                new BuildWonderIfAvailableDiscardRandom(),
                new BuildRandomFreeUpgrade(),
                new BuildMostExpensiveUsingPlayWithoutCost(),
                new BuildRandomWithNoCostCard(),
                new BuildRandomWithCostCardIfAffordable(),
                new DiscardRandom()
        ));
    }

    public void execute(TurnContext turnContext) {
        var player = turnContext.getPlayer();

        boolean cardPlayed = play1Card(turnContext, player);
        if (turnContext.getTurnCountAge() == 6) {
            if (player.getVault().getSpecialAction(PLAY_BOTH_CARDS_AT_LAST_TURN_IN_AGE).isPresent()) {
                playRemainingCard(turnContext, player);
            }
        }
        if (cardPlayed) return;

        throw new RuntimeException("StrategyStep no action to execute");
    }

    private boolean play1Card(TurnContext turnContext, Player player) {
        for (StrategyStep step : steps) {
            var result = step.execute(turnContext);
            player.log("Player %s executing strategy step %s => %s"
                    .formatted(player.getName(), step.getClass().getSimpleName(), result.action()));
            switch (result.action()) {
                case BUILD_FOR_FREE -> {
                    removeFromHandAndAddToVault(turnContext, result.card());
                    scheduleEffectReward(player, result.card().getEffect(), turnContext.getTurn());

                    player.collectMetric("free-builds", 1);
                    return true;
                }
                case BUILD_WITH_SPECIAL_EFFECT -> {
                    removeFromHandAndAddToVault(turnContext, result.card());
                    scheduleEffectReward(player, result.card().getEffect(), turnContext.getTurn());

                    player.collectMetric("build-card-with-special-effect", 1);
                    return true;
                }
                case BUILD_WITH_COST -> {
                    payCost(turnContext, result.card());
                    removeFromHandAndAddToVault(turnContext, result.card());
                    scheduleEffectReward(player, result.card().getEffect(), turnContext.getTurn());

                    player.collectMetric("build-with-cost", 1);
                    return true;
                }
                case DISCARD -> {
                    discardCard(turnContext, result.card());

                    player.collectMetric("discarded", 1);
                    return true;
                }
                case WONDER -> {
                    var wonderStage = player
                            .getWonderContext()
                            .getNextWonderStage(turnContext);
                    if (wonderStage.isEmpty()) {
                        throw new RuntimeException("StrategyStep selected wonder but no wonder stage to build");
                    }
                    buildWonderStage(turnContext, wonderStage.get(), result.card());

                    player.collectMetric("wonder-stages-" + wonderStage.get().getStageNumber(), 1);
                    return true;
                }
                case SKIP -> {
                }
            }
        }
        return false;
    }

    private void playRemainingCard(TurnContext turnContext, Player player) {
        var cardList = turnContext
                .getHandOfCards()
                .getCardsWithNoCost(turnContext);

        if (cardList.isEmpty()) {
            log.info("Player {} can play 0/{} cards at last turn in age {}",
                    player.getName(),
                    turnContext.getHandOfCards().getCards().size(),
                    turnContext.getAge()
            );
            return;
        }
        if (cardList.size() > 1) {
            throw new RuntimeException("More than one card to play: %s".formatted(cardList));
        }

        var cardToPlay = cardList.get(0);

        removeFromHandAndAddToVault(turnContext, cardToPlay);
        scheduleEffectReward(player, cardToPlay.getEffect(), turnContext.getTurn());

        player.getVault().useSpecialAction(PLAY_BOTH_CARDS_AT_LAST_TURN_IN_AGE);

        player.log("Player %s plays card %s at last turn in age %s".formatted(
                player.getName(),
                cardToPlay.report(),
                turnContext.getAge()
        ));

        if (!turnContext.getHandOfCards().getCards().isEmpty()) {
            throw new RuntimeException(("%s cards left in hand").formatted(turnContext.getHandOfCards().getCards().size()));
        }
    }

    private void scheduleEffectReward(Player player, Effect cardEffect, Turn turn) {
        cardEffect.scheduleRewardEvaluationAndCollection(player, turn);

        player.getCityStatistics().log("Effect %s scheduled for player %s".formatted(cardEffect.report(), player.getName()));
    }

    private void removeFromHandAndAddToVault(TurnContext turnContext, Card card) {
        turnContext.getHandOfCards().remove(card);
        turnContext.getPlayer().getVault().addBuiltCard(card);

        turnContext.getPlayer().getCityStatistics().log("\nPlayer %s builds card %s".formatted(turnContext.getPlayer().getName(), card.report()));
    }

    private void payCost(TurnContext turnContext, Card card) {
        var costReport = card.getCost().generateCostReport(turnContext);
        payCost(turnContext, card.getName().name(), costReport);
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
            player.getCityStatistics().log("Player %s plays card %s for FREE".formatted(player.getName(), name));
            return;
        }

        player.getVault().removeCoins(costReport.getToPayTotal());

        player.getLeftPlayer().getVault().addCoins(costReport.getToPayLeft());
        player.collectMetric("to-pay-left", costReport.getToPayLeft());

        player.getRightPlayer().getVault().addCoins(costReport.getToPayRight());
        player.collectMetric("to-pay-right", costReport.getToPayLeft());

        player.collectMetric("to-pay-bank", costReport.getToPayBank());

        if (costReport.getToPayBank() > 1) {
            throw new IllegalStateException("Player %s has %s coins to pay to bank".formatted(player.getName(), costReport.getToPayBank()));
        }

        player.getCityStatistics().log(
                "Player %s pays for %s \n$%s to bank \n$%s to L(%s) \n$%s to R(%s) \ntotal %s".formatted(
                        player.getName(),
                        name,
                        costReport.getToPayBank(),
                        costReport.getToPayLeft(),
                        player.getLeftPlayer().getName(),
                        costReport.getToPayRight(),
                        player.getRightPlayer().getName(),
                        costReport.getToPayTotal()
                ));
    }

    private void discardCard(TurnContext turnContext, Card cardToDiscard) {
        var player = turnContext.getPlayer();
        var coinsBefore = player.getVault().getCoins();
        player.getVault().addCoins(3);

        turnContext.getHandOfCards().discard(cardToDiscard);
        player.getVault().discardCard(cardToDiscard);

        player.getCityStatistics().log("\nPlayer %s discards card %s and gets 3 coins. %s => %s".formatted(
                player.getName(),
                cardToDiscard.report(),
                coinsBefore,
                player.getVault().getCoins()
        ));
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
