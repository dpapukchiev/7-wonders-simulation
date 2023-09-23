package dpapukchiev.sevenwonderssimulation.effects.core;

import java.util.List;

public enum EffectMultiplierType {
    ONE,
    RAW_MATERIAL_CARD,
    MANUFACTURED_GOOD_CARD,
    SCIENCE_CARD,
    CIVIL_CARD,
    COMMERCIAL_CARD,
    MILITARY_CARD,
    GUILD_CARD,
    WONDER_STAGE,
    WAR_LOSS,
    WAR_WIN,
    WAR_WIN_1,
    WAR_WIN_3,
    WAR_WIN_5;

    public static List<EffectMultiplierType> getCardTypes() {
        return List.of(
                RAW_MATERIAL_CARD,
                MANUFACTURED_GOOD_CARD,
                SCIENCE_CARD,
                CIVIL_CARD,
                COMMERCIAL_CARD,
                MILITARY_CARD
        );
    }
}
