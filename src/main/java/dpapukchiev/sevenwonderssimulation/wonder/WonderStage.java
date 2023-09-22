package dpapukchiev.sevenwonderssimulation.wonder;

import dpapukchiev.sevenwonderssimulation.cards.Card;
import dpapukchiev.sevenwonderssimulation.cost.Cost;
import dpapukchiev.sevenwonderssimulation.effects.core.Effect;
import dpapukchiev.sevenwonderssimulation.game.TurnContext;
import dpapukchiev.sevenwonderssimulation.player.Player;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
@Setter
@Builder
public class WonderStage {
    @Builder.Default
    private boolean built = false;
    private int     stageNumber;
    private Cost    cost;
    private Effect  effect;
    private Card consumedCard;

    public void build(Card consumedCard, TurnContext turnContext) {
        if (built) {
            return;
        }

        var player = turnContext.getPlayer();
        player.collectMetric("build-wonder-stage-" + stageNumber, 1);
        effect.scheduleEffect(player);
        cost.applyCost(turnContext, cost.generateCostReport(turnContext));
        turnContext.getHandOfCards().discard(consumedCard);

        built = true;
        this.consumedCard = consumedCard;

        log.info("\nPlayer %s builds wonder stage %s for %s and gets %s".formatted(
                player.getName(),
                stageNumber,
                cost.report(),
                effect.report()
        ));
    }
}
