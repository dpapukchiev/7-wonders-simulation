package dpapukchiev.sevenwonderssimulation.effects;

import dpapukchiev.sevenwonderssimulation.cards.Card;
import dpapukchiev.sevenwonderssimulation.effects.core.BaseEffect;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectReward;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectTiming;
import dpapukchiev.sevenwonderssimulation.game.Turn;
import dpapukchiev.sevenwonderssimulation.player.Player;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import static dpapukchiev.sevenwonderssimulation.effects.core.SpecialAction.PLAY_CARD_FROM_DISCARD;

@Log4j2
public class PlayFromDiscardedWithVictoryPoints extends BaseEffect {
    private final double victoryPoints;

    public PlayFromDiscardedWithVictoryPoints(double victoryPoints) {
        this.victoryPoints = victoryPoints;
    }

    public static PlayFromDiscardedWithVictoryPoints of(double victoryPoints) {
        return new PlayFromDiscardedWithVictoryPoints(victoryPoints);
    }

    @Override
    public void scheduleRewardEvaluationAndCollection(Player player, Turn turn) {
        player.getVault().addSpecialActions(PLAY_CARD_FROM_DISCARD);
        player.getEffectExecutionContext()
                .scheduleRewardEvaluationAndCollection(
                        this,
                        EffectTiming.END_OF_TURN
                );

    }

    @Override
    public Optional<EffectReward> collectReward(Player player) {
        var playerVault = player.getVault();
        playerVault.useSpecialAction(PLAY_CARD_FROM_DISCARD);

        var allDiscardedCards = playerVault.getDeck().getDiscardedCards();
        var discardedCardsToPickFrom = allDiscardedCards
                .stream()
                .filter(c -> !playerVault.getBuiltCardNames().contains(c.getName().name()))
                .toList();

        if (!discardedCardsToPickFrom.isEmpty()) {
            var cardToPlay = player.selectRandomCard(discardedCardsToPickFrom);
            player.log("Player %s used special action %s to build card %s and got %s\nother options\n%s".formatted(
                    player.getName(), PLAY_CARD_FROM_DISCARD,
                    cardToPlay.getName(),
                    cardToPlay.getEffect().report(),
                    discardedCardsToPickFrom.stream()
                            .filter(c -> !c.getName().equals(cardToPlay.getName()))
                            .map(Card::report)
                            .collect(Collectors.joining("\n"))
            ));

            player.playExtraCard(cardToPlay);
            player.collectMetric("play-from-discard", 1);
        } else {
            player.log("Player %s used special action %s but 0/%s can be built.".formatted(
                    player.getName(),
                    PLAY_CARD_FROM_DISCARD,
                    allDiscardedCards.size()
            ));
        }

        return Optional.of(EffectReward.builder()
                .victoryPointsReward(victoryPoints)
                .build());
    }

    @Override
    public String report() {
        var report = new ArrayList<String>();
        if (victoryPoints > 0) {
            report.add("VP:%s ".formatted(victoryPoints));
        }
        return "HMR(%s %s)".formatted(String.join(",", report), PLAY_CARD_FROM_DISCARD);
    }
}
