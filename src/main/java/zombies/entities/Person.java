package zombies.entities;

import java.awt.Color;
import java.awt.Point;

import jalse.entities.Entity;
import jalse.entities.annotations.GetAttribute;
import jalse.entities.annotations.SetAttribute;

public interface Person extends Entity {

    @GetAttribute
    double getAngle();

    @GetAttribute
    Color getColour();

    @GetAttribute
    Point getPosition();

    @GetAttribute
    int getSightRange();

    @GetAttribute
    double getSpeed();

    @SetAttribute
    void setAngle(double angle);

    @SetAttribute
    void setColour(Color colour);

    @SetAttribute
    void setPosition(Point position);

    @SetAttribute
    void setSightRange(int range);

    @SetAttribute
    void setSpeed(double speed);
}