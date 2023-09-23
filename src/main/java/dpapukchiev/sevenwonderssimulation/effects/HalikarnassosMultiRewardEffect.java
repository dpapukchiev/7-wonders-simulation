package dpapukchiev.sevenwonderssimulation.effects;

import dpapukchiev.sevenwonderssimulation.effects.core.BaseEffect;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectReward;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectTiming;
import dpapukchiev.sevenwonderssimulation.effects.core.SpecialAction;
import dpapukchiev.sevenwonderssimulation.game.Turn;
import dpapukchiev.sevenwonderssimulation.player.Player;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Optional;

@Log4j2
public class HalikarnassosMultiRewardEffect extends BaseEffect {
    private final double victoryPoints;

    public HalikarnassosMultiRewardEffect(double victoryPoints) {
        this.victoryPoints = victoryPoints;
    }

    public static HalikarnassosMultiRewardEffect of(double victoryPoints) {
        return new HalikarnassosMultiRewardEffect(victoryPoints);
    }

    @Override
    public void scheduleRewardEvaluationAndCollection(Player player, Turn turn) {
        player.getEffectExecutionContext()
                .scheduleRewardEvaluationAndCollection(
                        this,
                        EffectTiming.END_OF_TURN
                );

    }

    @Override
    public Optional<EffectReward> collectReward(Player player) {
        var discardedCards = player.getVault().getDeck().getDiscardedCards()
                .stream()
                .filter(c -> !player.getVault().getBuiltCardNames().contains(c.getName().name()))
                .toList();
        // TODO: make sure this works
        if (!discardedCards.isEmpty()) {
            var cardToPlay = player.selectRandomCard(discardedCards);
            player.getVault().useSpecialAction(SpecialAction.PLAY_CARD_FROM_DISCARD);
            player.playExtraCard(cardToPlay);
            log.info("Player {} used special action {} to build card {} and got {}",
                    player.getName(), SpecialAction.PLAY_CARD_FROM_DISCARD,
                    cardToPlay.getName(), cardToPlay.getEffect().report()
            );
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
        return "HMR(%s)".formatted(String.join(",", report));
    }
}
