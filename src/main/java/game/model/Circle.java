package game.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Circle {

    private CircleType type;
    private final ObjectProperty<Position> position = new SimpleObjectProperty<>();

    public Circle(CircleType type, Position position){
        this.type = type;
        this.position.set(position);
    }

    public CircleType getType() { return type; }

    public Position getPosition() { return position.get(); }

    public ObjectProperty<Position> positionProperty() { return position; }

    public void moveTo(Direction direction){
        Position newPosition = position.get().getPositionAt(direction);
        position.set(newPosition);
    }
}
