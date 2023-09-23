package dpapukchiev.sevenwonderssimulation.wonder;

import java.util.List;

public enum CityName {
    BABYLON,
    RHODOS,
    OLIMPIA,
    ALEXANDRIA,
    HALIKARNASSOS,
    GIZAH,
    EPHESOS;

    public static List<CityName> allCities() {
        return List.of(
                BABYLON,
                RHODOS,
                OLIMPIA,
                ALEXANDRIA,
                HALIKARNASSOS,
                GIZAH,
                EPHESOS
        );
    }

    public static List<CityName> fixedCities() {
        return List.of(
                BABYLON,
                OLIMPIA,
                HALIKARNASSOS
        );
    }
}
