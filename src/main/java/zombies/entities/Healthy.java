package zombies.entities;

import java.awt.Color;

public interface Healthy extends Person {
	static final Color COLOR = Color.WHITE;
	static final int SIGHT_RANGE = 50;
	static final double SPEED = 3.0;
}