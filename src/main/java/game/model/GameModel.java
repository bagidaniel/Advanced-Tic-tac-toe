package game.model;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Label;

import java.util.*;

public class GameModel {

    public static final int MAX_ROW_SIZE = 5;
    public static final int MAX_COL_SIZE = 4;

    public List<Position> winningPositions = new ArrayList<>();

    private Circle[] circles;

    Random random = new Random();
    private int playerTurn = random.nextInt(2);

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

    public int getCircleCount() {
        return circles.length;
    }

    public CircleType getCircleType(int circleID) {
        return circles[circleID].getType();
    }

    public Position getCirclePosition(int circleID) {
        return circles[circleID].getPosition();
    }

    public ObjectProperty<Position> positionProperty(int circleID) {
        return circles[circleID].positionProperty();
    }

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

    public Set<Direction> getValidMoves(int circleID) {
        EnumSet<Direction> validMoves = EnumSet.noneOf(Direction.class);
        for (var direction : Direction.values()) {
            if (isValidMove(circleID, direction)) {
                validMoves.add(direction);
            }
        }
        return validMoves;
    }

    public void move(int circleID, Direction direction) {
        circles[circleID].moveTo(direction);
    }

    private boolean isOnBoard(Position position) {
        return position.row() >= 0 && position.row() < MAX_ROW_SIZE &&
                position.col() >= 0 && position.col() < MAX_COL_SIZE;
    }

    public CircleType switchPlayer(){
        if (playerTurn % 2 == 0) {
            return CircleType.BLUE;
        }
        return CircleType.RED;
    }

    private void increasePlayerTurn(){
        playerTurn++;
    }

    public void setTurnLabel(Label label){
        increasePlayerTurn();
        if (playerTurn % 2 == 0) {
            label.setText("Blue player turns");
            label.setStyle("-fx-background-color: black;" + "-fx-text-fill: blue");
        }
        else{
            label.setText("Red player turns");
            label.setStyle("-fx-background-color: black;" + "-fx-text-fill: red");
        }
    }

    public void setWinLabel(Label label){
        if (playerTurn % 2 == 0){
            label.setText("Blue player wins");
        }
        else{
            label.setText("Red player wins");
        }
        label.setStyle("-fx-background-color: black;" + "-fx-text-fill: green");
    }

    public List<Position> getCirclePositions() {
        List<Position> positions = new ArrayList<>(circles.length);
        for (var circle : circles) {
            if (circle.getType().equals(switchPlayer())) {
                positions.add(circle.getPosition());
            }
        }
        System.out.println(positions);
        return positions;
    }

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

    private boolean checkRowWin(Position position1, Position position2, Position position3){
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

    private boolean checkColWin(Position position1, Position position2, Position position3){
        if (position1.col() == position2.col() && position1.col() == position3.col() &&
                (position1.row() + position2.row() + position3.row()) % 3 == 0){
            addToWinningCells(position1, position2, position3);
            return true;
        }
        return false;
    }

    private boolean diagWins(List<Position> positions){
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

    public OptionalInt getCircleID(Position position) {
        for (int i = 0; i < circles.length; i++) {
            if (circles[i].getPosition().equals(position)) {
                return OptionalInt.of(i);
            }
        }
        return OptionalInt.empty();
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(circles);
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
