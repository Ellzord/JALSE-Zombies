package zombies.entities;

import java.awt.Color;
import java.awt.Point;
import java.lang.reflect.Method;

import jalse.entities.Entity;
import jalse.entities.annotations.GetAttribute;
import jalse.entities.annotations.SetAttribute;

public interface Person extends Entity {
	static final Color COLOR = Color.WHITE;
	static final int SIGHT_RANGE = 50;
	static final double SPEED = 3.0;

	static final double INFECTION_TIME_SECONDS = 5;
	static final double STARVE_TIME_SECONDS = 10;
	static final int SIZE = 16;

	@GetAttribute(name = "angle")
	Double getAngle();

	@GetAttribute(name = "color")
	Color getColor();

	@GetAttribute(name = "directionMethod")
	Method getDirectionMethod();

	@GetAttribute(name = "position")
	Point getPosition();

	@GetAttribute(name = "sightRange")
	Integer getSightRange();

	@GetAttribute
	Double getSpeed();

	@SetAttribute(name = "angle")
	void setAngle(Double angle);

	@SetAttribute(name = "color")
	void setColor(Color color);

	@SetAttribute(name = "directionMethod")
	void setDirectionMethod(Method method);

	@SetAttribute(name = "position")
	void setPosition(Point position);

	@SetAttribute(name = "sightRange")
	void setSightRange(Integer range);

	@SetAttribute(name = "speed")
	void setSpeed(Double speed);
}