package dpapukchiev.v2.resources;

import java.util.ArrayList;
import java.util.List;

public enum ManufacturedGood {
    GLASS,
    TEXTILE,
    SCRIPTS;

    public static List<ManufacturedGood> all() {
        return new ArrayList<>(List.of(GLASS, TEXTILE, SCRIPTS));
    }
}
