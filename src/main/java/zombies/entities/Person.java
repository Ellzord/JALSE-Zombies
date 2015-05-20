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

	@GetAttribute("angle")
	Double getAngle();

	@GetAttribute("color")
	Color getColor();

	@GetAttribute("directionMethod")
	Method getDirectionMethod();

	@GetAttribute("position")
	Point getPosition();

	@GetAttribute("sightRange")
	Integer getSightRange();

	@GetAttribute("speed")
	Double getSpeed();

	@SetAttribute("angle")
	void setAngle(Double angle);

	@SetAttribute("color")
	void setColor(Color color);

	@SetAttribute("directionMethod")
	void setDirectionMethod(Method method);

	@SetAttribute("position")
	void setPosition(Point position);

	@SetAttribute("sightRange")
	void setSightRange(Integer range);

	@SetAttribute("speed")
	void setSpeed(Double speed);
}