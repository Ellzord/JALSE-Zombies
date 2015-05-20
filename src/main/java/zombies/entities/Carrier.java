package zombies.entities;

import java.awt.Color;

public interface Carrier extends Person {
	static final Color COLOR = Healthy.COLOR;
	static final int SIGHT_RANGE = Healthy.SIGHT_RANGE;
	static final double SPEED = Healthy.SPEED;
}