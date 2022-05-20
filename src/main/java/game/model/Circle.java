package game.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Represents the circle
 */
public class Circle {

    private CircleType type;
    private final ObjectProperty<Position> position = new SimpleObjectProperty<>();

    public Circle(CircleType type, Position position){
        this.type = type;
        this.position.set(position);
    }

    /**
     *
     * @return the type of the circle
     */
    public CircleType getType() { return type; }

    /**
     *
     * @return the position of the circle
     */
    public Position getPosition() { return position.get(); }

    /**
     *
     * @return the positionProperty of the circle
     */
    public ObjectProperty<Position> positionProperty() { return position; }

    /**
     * Moves the circle to the specified direction
     * @param direction a direction to which the circle is want to be moved
     */
    public void moveTo(Direction direction){
        Position newPosition = position.get().getPositionAt(direction);
        position.set(newPosition);
    }
}
