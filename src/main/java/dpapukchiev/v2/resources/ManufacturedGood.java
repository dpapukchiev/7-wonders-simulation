package dpapukchiev.v2.resources;

import java.util.List;

public enum ManufacturedGood {
    GLASS,
    TEXTILE,
    SCRIPTS;

    public static List<ManufacturedGood> all() {
        return List.of(GLASS, TEXTILE, SCRIPTS);
    }

    public static List<ManufacturedGood> twoOf(ManufacturedGood good) {
        return List.of(good, good);
    }
}
