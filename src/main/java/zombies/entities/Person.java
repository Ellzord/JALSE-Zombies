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