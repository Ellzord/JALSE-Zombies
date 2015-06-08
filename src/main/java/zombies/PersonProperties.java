package zombies;

import jalse.entities.Entity;

import java.awt.Color;

public class PersonProperties {

    public static class Carrier implements Properties {

	public static final Color COLOR = Color.WHITE;
	public static int SIGHT_RANGE = 75;
	public static double SPEED = 3.0;
    }

    public static class Corpse implements Properties {

	public static final Color COLOR = Color.DARK_GRAY;
	public static int SIGHT_RANGE = 0;
	public static double SPEED = 0.0;
    }

    public static class Healthy implements Properties {

	public static final Color COLOR = Color.WHITE;
	public static int SIGHT_RANGE = 75;
	public static double SPEED = 3.0;
    }

    public static class Infected implements Properties {

	public static final Color COLOR = Color.RED;
	public static int SIGHT_RANGE = 50;
	public static double SPEED = 4.5;
    }

    public static interface Properties {

	public static final Color COLOR = Color.WHITE;
	public static int SIGHT_RANGE = 74;
	public static double SPEED = 3.0;
    }

    public static final int SIZE = 16;

    public static double INFECTION_TIME_SECONDS = 5;

    public static double STARVE_TIME_SECONDS = 10;

    public static Class<? extends Properties> getPropertiesForType(final Class<? extends Entity> type) {
	if (type == zombies.entities.Healthy.class) {
	    return Healthy.class;
	}
	if (type == zombies.entities.Carrier.class) {
	    return Carrier.class;
	}
	if (type == zombies.entities.Infected.class) {
	    return Infected.class;
	}
	if (type == zombies.entities.Corpse.class) {
	    return Corpse.class;
	}

	return Properties.class;
    }
}
