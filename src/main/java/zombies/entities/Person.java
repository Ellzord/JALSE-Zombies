package zombies.entities;

import jalse.entities.Entity;
import jalse.entities.annotations.GetAttribute;
import jalse.entities.annotations.SetAttribute;

import java.awt.Color;
import java.awt.Point;
import java.lang.reflect.Method;

public interface Person extends Entity {

    @GetAttribute
    Double getAngle();

    @GetAttribute
    Color getColor();

    @GetAttribute
    Method getDirectionMethod();

    @GetAttribute
    Point getPosition();

    @GetAttribute
    Integer getSightRange();

    @GetAttribute
    Double getSpeed();

    @SetAttribute
    void setAngle(Double angle);

    @SetAttribute
    void setColor(Color color);

    @SetAttribute
    void setDirectionMethod(Method method);

    @SetAttribute
    void setPosition(Point position);

    @SetAttribute
    void setSightRange(Integer range);

    @SetAttribute
    void setSpeed(Double speed);
}