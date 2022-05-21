package game.model;

import javafx.beans.property.ObjectProperty;

import java.util.*;

/**
 * Represents the model of the game
 */
public class GameModel {

    /**
     * Height of a board
     */
    public static final int MAX_ROW_SIZE = 5;

    /**
     * Width of a board
     */
    public static final int MAX_COL_SIZE = 4;

    /**
     * Storing the positions of a winning circle
     */
    public List<Position> winningPositions = new ArrayList<>();

    /**
     * Storing all circle
     */
    private Circle[] circles;

    Random random = new Random();
    private int playerTurn = random.nextInt(2);

    /**
     * Increasing the number of turn
     */
    public void increasePlayerTurn(){
        playerTurn++;
    }

    /**
     * @return number that determines which player's turn
     */
    public int getPlayerTurn() {
        return playerTurn;
    }

    /**
     * Creates a {@code GameModel} object that corresponds to the original
     * starter state of the game.
     */
    public GameModel() {
        this(new Circle(CircleType.BLUE, new Position(0, 0)),
                new Circle(CircleType.RED, new Position(0, 1)),
                new Circle(CircleType.BLUE, new Position(0, 2)),
                new Circle(CircleType.RED, new Position(0, 3)),
                new Circle(CircleType.BLUE, new Position(MAX_ROW_SIZE - 1, 1)),
                new Circle(CircleType.RED, new Position(MAX_ROW_SIZE - 1, 0)),
                new Circle(CircleType.BLUE, new Position(MAX_ROW_SIZE - 1, 3)),
                new Circle(CircleType.RED, new Position(MAX_ROW_SIZE - 1, 2)));
    }

    /**
     * Creates a {@code GameModel} object initializing the positions of the
     * circles with the positions specified.
     *
     * @param circles all circle
     */
    public GameModel(Circle... circles) {
        checkCircles(circles);
        this.circles = circles.clone();
    }

    private void checkCircles(Circle[] circles) {
        var seen = new HashSet<Position>();
        for (var circle : circles) {
            if (!isOnBoard(circle.getPosition()) || seen.contains(circle.getPosition())) {
                throw new IllegalArgumentException();
            }
            seen.add(circle.getPosition());
        }
    }

    /**
     * @return Number of all circles
     */
    public int getCircleCount() {
        return circles.length;
    }

    /**
     * @param circleID the number of a circle
     * @return the type of the circle specified
     */
    public CircleType getCircleType(int circleID) {
        return circles[circleID].getType();
    }

    /**
     *
     * @param circleID the number of a circle
     * @return the position of the circle specified
     */
    public Position getCirclePosition(int circleID) {
        return circles[circleID].getPosition();
    }

    /**
     *
     * @param circleID the number of a circle
     * @return the positionProperty of the circle specified
     */
    public ObjectProperty<Position> positionProperty(int circleID) {
        return circles[circleID].positionProperty();
    }

    /**
     *
     * @param circleID the number of a circle
     * @param direction a direction to which the circle is want to be moved
     * @return return true if the move is possible, return false otherwise
     */
    public boolean isValidMove(int circleID, Direction direction) {
        if (circleID < 0 || circleID >= circles.length) {
            throw new IllegalArgumentException();
        }
        Position newPosition = circles[circleID].getPosition().getPositionAt(direction);
        if (!isOnBoard(newPosition)) {
            return false;
        }
        for (var piece : circles) {
            if (piece.getPosition().equals(newPosition)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param circleID the number of a circle
     * @return direction, whither the circle is can be moved
     */
    public Set<Direction> getValidMoves(int circleID) {
        EnumSet<Direction> validMoves = EnumSet.noneOf(Direction.class);
        for (var direction : Direction.values()) {
            if (isValidMove(circleID, direction)) {
                validMoves.add(direction);
            }
        }
        return validMoves;
    }

    /**
     * Call the moveTo method of the Circle class
     * Moves the specified circle to the specified direction
     * @param circleID the number of a circle
     * @param direction a direction to which the circle is want to be moved
     */
    public void move(int circleID, Direction direction) {
        circles[circleID].moveTo(direction);
    }

    /**
     *
     * @param position the position of the circle
     * @return return true if the circle is on the board
     */
    protected boolean isOnBoard(Position position) {
        return position.row() >= 0 && position.row() < MAX_ROW_SIZE &&
                position.col() >= 0 && position.col() < MAX_COL_SIZE;
    }

    /**
     *
     * @param playerTurn number that determines which player's turn
     * @return a circle type, depending on the playerTurn
     */
    protected CircleType switchPlayer(int playerTurn){
        if (playerTurn % 2 == 0) {
            return CircleType.BLUE;
        }
        return CircleType.RED;
    }

    /**
     *
     * @return position of circles of the same type
     */
    public List<Position> getCirclePositions() {
        List<Position> positions = new ArrayList<>(circles.length);
        for (var circle : circles) {
            if (circle.getType().equals(switchPlayer(playerTurn))) {
                positions.add(circle.getPosition());
            }
        }
        return positions;
    }

    /**
     *
     * @param positions the positions of the circles
     * @return true, if someone wins the game
     */
    public boolean isWin(List<Position> positions){
        return rowWins(positions) || colWins(positions) || diagWins(positions);
    }

    private void addToWinningCells(Position position1, Position position2, Position position3){
        winningPositions.clear();
        winningPositions.add(position1);
        winningPositions.add(position2);
        winningPositions.add(position3);
    }

    private boolean rowWins(List<Position> positions){
        return checkRowWin(positions.get(0), positions.get(1), positions.get(2)) ||
                checkRowWin(positions.get(0), positions.get(1), positions.get(3)) ||
                checkRowWin(positions.get(0), positions.get(2), positions.get(3)) ||
                checkRowWin(positions.get(1), positions.get(2), positions.get(3));
    }

    /**
     *
     * @param position1 position of a circle
     * @param position2 position of a circle
     * @param position3 position of a circle
     * @return true, if there are three circles in a row next to each other
     */
    protected boolean checkRowWin(Position position1, Position position2, Position position3){
        if (position1.row() == position2.row() && position1.row() == position3.row() &&
                (position1.col() + position2.col() + position3.col()) % 3 == 0){
            addToWinningCells(position1, position2, position3);
            return true;
        }
        return false;
    }

    private boolean colWins(List<Position> positions){
        return checkColWin(positions.get(0), positions.get(1), positions.get(2)) ||
                checkColWin(positions.get(0), positions.get(1), positions.get(3)) ||
                checkColWin(positions.get(0), positions.get(2), positions.get(3)) ||
                checkColWin(positions.get(1), positions.get(2), positions.get(3));
    }

    /**
     *
     * @param position1 position of a circle
     * @param position2 position of a circle
     * @param position3 position of a circle
     * @return true, if there are three circles in a column next to each other
     */
    protected boolean checkColWin(Position position1, Position position2, Position position3){
        if (position1.col() == position2.col() && position1.col() == position3.col() &&
                (position1.row() + position2.row() + position3.row()) % 3 == 0){
            addToWinningCells(position1, position2, position3);
            return true;
        }
        return false;
    }

    /**
     *
     * @param positions the positions of the circles
     * @return true, if there are three circles in a diagonal next to each other
     */
    protected boolean diagWins(List<Position> positions){
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (positions.get(i).row()-1 == positions.get(j).row() && positions.get(i).col()-1 == positions.get(j).col()) {
                    for (int k = 0; k < 4; k++) {
                        if (positions.get(i).row() + 1 == positions.get(k).row() && positions.get(i).col() + 1 == positions.get(k).col()) {
                            addToWinningCells(positions.get(i), positions.get(j), positions.get(k));
                            return true;
                        }
                    }
                }
                if (positions.get(i).row()-1 == positions.get(j).row() && positions.get(i).col()+1 == positions.get(j).col()){
                    for (int k = 0; k < 4; k++) {
                        if (positions.get(i).row() + 1 == positions.get(k).row() && positions.get(i).col() - 1 == positions.get(k).col()) {
                            addToWinningCells(positions.get(i), positions.get(j), positions.get(k));
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     *
     * @param position the position of the circle
     * @return number, that assign to a circle
     */
    public OptionalInt getCircleID(Position position) {
        for (int i = 0; i < circles.length; i++) {
            if (circles[i].getPosition().equals(position)) {
                return OptionalInt.of(i);
            }
        }
        return OptionalInt.empty();
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(",", "[", "]");
        for (var circle : circles) {
            joiner.add(circle.toString());
        }
        return joiner.toString();
    }
}
