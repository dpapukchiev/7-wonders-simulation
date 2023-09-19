package dpapukchiev.v1.cards;

import java.util.List;

public enum RawMaterial {
    METAL_ORE,
    CLAY,
    WOOD,
    STONE;

    public static List<RawMaterial> all() {
        return List.of(METAL_ORE, CLAY, WOOD, STONE);
    }

    public static List<RawMaterial> twoOf(RawMaterial material) {
        return List.of(material, material);
    }
}
