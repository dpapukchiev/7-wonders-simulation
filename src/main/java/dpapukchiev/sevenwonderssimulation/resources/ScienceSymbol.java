package dpapukchiev.sevenwonderssimulation.resources;

import java.util.List;

public enum ScienceSymbol {
    COGWHEEL,
    COMPASS,
    TABLET;

    public static List<ScienceSymbol> all() {
        return List.of(ScienceSymbol.values());
    }
}
