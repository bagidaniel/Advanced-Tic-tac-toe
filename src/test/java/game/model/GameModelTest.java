package game.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameModelTest {

    GameModel model1 = new GameModel();

    @Test
    void switchPlayer() {
        assertEquals(model1.switchPlayer(0), CircleType.BLUE);
        assertEquals(model1.switchPlayer(1), CircleType.RED);
        assertEquals(model1.switchPlayer(2), CircleType.BLUE);
        assertEquals(model1.switchPlayer(3), CircleType.RED);
    }

    @Test
    void isOnBoard() {
        assertFalse(model1.isOnBoard(new Position(5,2)));
        assertFalse(model1.isOnBoard(new Position(3,4)));
        assertTrue(model1.isOnBoard(new Position(1,3)));
        assertTrue(model1.isOnBoard(new Position(4,0)));
    }

    @Test
    void isWin() {
        List<Position> positions = new ArrayList<>(Arrays.asList(new Position(0, 0), new Position(1, 1), new Position(2, 2), new Position(0, 1)));
        assertTrue(model1.isWin(positions));
        List<Position> positions1 = new ArrayList<>(Arrays.asList(new Position(1, 1), new Position(5, 4), new Position(1, 2), new Position(1, 3)));
        assertTrue(model1.isWin(positions1));
        List<Position> positions2 = new ArrayList<>(Arrays.asList(new Position(4, 0), new Position(3, 1), new Position(3, 0), new Position(2, 0)));
        assertTrue(model1.isWin(positions2));
        List<Position> positions3 = new ArrayList<>(Arrays.asList(new Position(0, 1), new Position(5, 2), new Position(0, 3), new Position(0, 4)));
        assertFalse(model1.isWin(positions3));
        List<Position> positions4 = new ArrayList<>(Arrays.asList(new Position(0, 0), new Position(3, 0), new Position(0, 4), new Position(2, 2)));
        assertFalse(model1.isWin(positions4));
    }

    @Test
    void checkRowWin() {
        assertFalse(model1.checkRowWin(new Position(0,0), new Position(1,1), new Position(2,2)));
        assertFalse(model1.checkRowWin(new Position(0,0), new Position(0,1), new Position(0,3)));
        assertTrue(model1.checkRowWin(new Position(1,0), new Position(1,1), new Position(1,2)));
        assertTrue(model1.checkRowWin(new Position(3,1), new Position(3,2), new Position(3,3)));
    }

    @Test
    void checkColWin() {
        assertFalse(model1.checkColWin(new Position(0,0), new Position(1,1), new Position(2,2)));
        assertFalse(model1.checkColWin(new Position(0,0), new Position(1,0), new Position(3,0)));
        assertTrue(model1.checkColWin(new Position(0,1), new Position(1,1), new Position(2,1)));
        assertTrue(model1.checkColWin(new Position(1,4), new Position(2,4), new Position(3,4)));
    }

    @Test
    void diagWin() {
        List<Position> positions = new ArrayList<>(Arrays.asList(new Position(0, 0), new Position(1, 1), new Position(2, 2), new Position(0, 1)));
        assertTrue(model1.diagWins(positions));
        List<Position> positions1 = new ArrayList<>(Arrays.asList(new Position(0, 1), new Position(3, 2), new Position(2, 4), new Position(0, 0)));
        assertFalse(model1.diagWins(positions1));
        List<Position> positions2 = new ArrayList<>(Arrays.asList(new Position(4, 0), new Position(3, 1), new Position(5, 0), new Position(2, 2)));
        assertTrue(model1.diagWins(positions2));
        List<Position> positions3 = new ArrayList<>(Arrays.asList(new Position(0, 1), new Position(5, 2), new Position(0, 3), new Position(0, 4)));
        assertFalse(model1.diagWins(positions3));
    }
}
