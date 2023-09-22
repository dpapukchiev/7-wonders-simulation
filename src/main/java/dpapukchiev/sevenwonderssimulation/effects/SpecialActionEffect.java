package dpapukchiev.sevenwonderssimulation.effects;

import dpapukchiev.sevenwonderssimulation.effects.core.BaseEffect;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectTiming;
import dpapukchiev.sevenwonderssimulation.effects.core.SpecialAction;
import dpapukchiev.sevenwonderssimulation.player.Player;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class SpecialActionEffect extends BaseEffect {
    private final SpecialAction specialAction;
    private final EffectTiming  eventTiming;

    public static SpecialActionEffect of(SpecialAction specialAction, EffectTiming eventTiming) {
        return new SpecialActionEffect(specialAction, eventTiming);
    }

    @Override
    public void scheduleEffect(Player player) {
        // TODO:
//        switch (specialAction){
//
//            case PLAY_CARD_WITHOUT_COST -> {
//            }
//            case PLAY_CARD_FROM_DISCARD -> {
//            }
//            case PLAY_BOTH_CARDS_AT_LAST_TURN_IN_AGE -> {
//            }
//            case COPY_GUILD_CARD -> {
//            }
//        }
        player.getEffectExecutionContext()
                .addEffect(this, eventTiming);
    }

    @Override
    public Optional<SpecialAction> getSpecialAction() {
        return Optional.of(specialAction);
    }

    @Override
    public String report() {
        return "SA(%s)".formatted(specialAction.name());
    }
}
