package game.model;

/**
 * Represents a 2D position.
 */
public record Position(int row, int col) {

    /**
     *
     * @param direction a direction to which the circle is want to be moved
     * @return the position whose vertical and horizontal distances from this position are equal to the coordinate changes of the direction given
     */
    public Position getPositionAt(Direction direction) {
        return new Position(row + direction.getRowChange(), col + direction.getColChange());
    }

    @Override
    public String toString() {
        return String.format("(%d,%d)", row, col);
    }

}
