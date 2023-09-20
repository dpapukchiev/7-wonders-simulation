package dpapukchiev.v2.effects;

import dpapukchiev.v2.effects.core.BaseEffect;
import dpapukchiev.v2.player.Player;
import dpapukchiev.v2.resources.ResourceBundle;
import dpapukchiev.v2.resources.ScienceSymbol;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class ScienceSymbolsEffect extends BaseEffect {

    private final List<ScienceSymbol> scienceSymbols;

    public static ScienceSymbolsEffect of(ScienceSymbol... scienceSymbols) {
        return new ScienceSymbolsEffect(List.of(scienceSymbols));
    }

    @Override
    public Optional<ResourceBundle> getResourceBundle(Player player) {
        return Optional.of(ResourceBundle.builder()
                .scienceSymbols(scienceSymbols)
                .build());
    }
}
