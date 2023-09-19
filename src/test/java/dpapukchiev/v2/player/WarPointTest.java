package dpapukchiev.v2.player;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class WarPointTest {

    @Test
    void from() {
        assertEquals(WarPoint.MINUS_ONE, WarPoint.from(-1));
        assertEquals(WarPoint.ONE, WarPoint.from(1));
        assertEquals(WarPoint.THREE, WarPoint.from(3));
        assertEquals(WarPoint.FIVE, WarPoint.from(5));
        assertThrows(IllegalArgumentException.class, () -> WarPoint.from(0));
    }
}