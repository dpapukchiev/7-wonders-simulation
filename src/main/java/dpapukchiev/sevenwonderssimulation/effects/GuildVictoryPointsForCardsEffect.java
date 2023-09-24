package dpapukchiev.sevenwonderssimulation.effects;

import dpapukchiev.sevenwonderssimulation.effects.core.BaseEffect;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectTiming;
import dpapukchiev.sevenwonderssimulation.game.Turn;
import dpapukchiev.sevenwonderssimulation.player.Player;

import static dpapukchiev.sevenwonderssimulation.effects.core.EffectDirectionConstraint.SELF;
import static dpapukchiev.sevenwonderssimulation.effects.core.EffectMultiplierType.GUILD_CARD;
import static dpapukchiev.sevenwonderssimulation.effects.core.EffectMultiplierType.MANUFACTURED_GOOD_CARD;
import static dpapukchiev.sevenwonderssimulation.effects.core.EffectMultiplierType.RAW_MATERIAL_CARD;

public class GuildVictoryPointsForCardsEffect extends BaseEffect {

    public static GuildVictoryPointsForCardsEffect newInstance() {
        return new GuildVictoryPointsForCardsEffect();
    }

    @Override
    public void scheduleRewardEvaluationAndCollection(Player player, Turn turn) {
        player.getEffectExecutionContext()
                .scheduleRewardEvaluationAndCollection(VictoryPointWithModifiersEffect.of(
                        SELF,
                        RAW_MATERIAL_CARD,
                        1
                ), EffectTiming.END_OF_GAME);

        player.getEffectExecutionContext()
                .scheduleRewardEvaluationAndCollection(VictoryPointWithModifiersEffect.of(
                        SELF,
                        MANUFACTURED_GOOD_CARD,
                        1
                ), EffectTiming.END_OF_GAME);

        player.getEffectExecutionContext()
                .scheduleRewardEvaluationAndCollection(VictoryPointWithModifiersEffect.of(
                        SELF,
                        GUILD_CARD,
                        1
                ), EffectTiming.END_OF_GAME);
    }

    @Override
    public String report() {
        return "VP(1x#%s + 1x#%s + 1x#%s)".formatted(
                RAW_MATERIAL_CARD,
                MANUFACTURED_GOOD_CARD,
                GUILD_CARD
        );
    }
}
