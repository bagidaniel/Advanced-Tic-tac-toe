package game.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PositionTest {

    Position position = new Position(0, 0);

    void assertPosition(int expectedRow, int expectedCol, Position position) {
        assertAll("position",
                () -> assertEquals(expectedRow, position.row()),
                () -> assertEquals(expectedCol, position.col())
        );
    }

    @Test
    void getPositionAt() {
        assertPosition(-1, 0, position.getPositionAt(Direction.UP));
        assertPosition(0, 1, position.getPositionAt(Direction.RIGHT));
        assertPosition(1, 0, position.getPositionAt(Direction.DOWN));
        assertPosition(0, -1, position.getPositionAt(Direction.LEFT));
    }

    @Test
    void testToString() {
        assertEquals("(0,0)", position.toString());
        Position position2 = new Position(4, 3);
        assertEquals("(4,3)", position2.toString());
        Position position3 = new Position(2, 1);
        assertEquals("(2,1)", position3.toString());
    }
}
