package dpapukchiev.sevenwonderssimulation.player;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WarPoint {
    MINUS_ONE(-1),
    ONE(1),
    THREE(3),
    FIVE(5);

    private final int value;

    public static WarPoint from(int warPoints) {
        return switch (warPoints) {
            case -1 -> MINUS_ONE;
            case 1 -> ONE;
            case 3 -> THREE;
            case 5 -> FIVE;
            default -> throw new IllegalArgumentException("Invalid war points: " + warPoints);
        };
    }
}
