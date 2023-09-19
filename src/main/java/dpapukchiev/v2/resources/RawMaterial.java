package dpapukchiev.v2.resources;

import java.util.ArrayList;
import java.util.List;

public enum RawMaterial {
    METAL_ORE,
    CLAY,
    WOOD,
    STONE;

    public static List<RawMaterial> all() {
        return new ArrayList<>(List.of(METAL_ORE, CLAY, WOOD, STONE));
    }
}
