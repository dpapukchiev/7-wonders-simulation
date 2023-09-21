package dpapukchiev.sevenwonderssimulation.effects;

import dpapukchiev.sevenwonderssimulation.effects.core.BaseEffect;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectTiming;
import dpapukchiev.sevenwonderssimulation.player.Player;
import dpapukchiev.sevenwonderssimulation.resources.ResourceBundle;
import dpapukchiev.sevenwonderssimulation.resources.ScienceSymbol;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
public class ScienceSymbolsEffect extends BaseEffect {

    private final List<ScienceSymbol> scienceSymbols;

    public static ScienceSymbolsEffect of(ScienceSymbol... scienceSymbols) {
        return new ScienceSymbolsEffect(List.of(scienceSymbols));
    }

    @Override
    public void scheduleEffect(Player player) {
        var effectTiming = scienceSymbols.stream().distinct().count() > 1 ? EffectTiming.END_OF_GAME : EffectTiming.ANYTIME;
        player.getEffectExecutionContext()
                .addEffect(this, effectTiming);
    }

    @Override
    public Optional<ResourceBundle> getResourceBundle(Player player) {
        return Optional.of(ResourceBundle.builder()
                .scienceSymbols(scienceSymbols)
                .build());
    }

    @Override
    public String report() {
        return "SC(%s)".formatted(scienceSymbols.stream()
                .map(ScienceSymbol::name)
                .collect(Collectors.joining("-"))
        );
    }
}
