package zombies;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import zombies.entities.Carrier;
import zombies.entities.Corpse;
import zombies.entities.Healthy;
import zombies.entities.Infected;
import zombies.entities.Person;

public class ZombiesProperties {

    private static class PersonProperties {

	private final AtomicReference<Color> colour;
	private final AtomicInteger sightRange;
	private final AtomicLong speed;

	PersonProperties(final Color colour, final int sightRange, final double speed) {
	    this.colour = new AtomicReference<>(colour);
	    this.sightRange = new AtomicInteger(sightRange);
	    this.speed = new AtomicLong(Double.doubleToLongBits(speed));
	}
    }

    private static final int SIZE = 16;

    private static AtomicLong infectionTime = new AtomicLong(Double.doubleToLongBits(5));

    private static AtomicLong starveTime = new AtomicLong(Double.doubleToLongBits(10));

    private static AtomicInteger population = new AtomicInteger(100);

    private static Map<Class<?>, PersonProperties> props = new HashMap<>();

    static {
	props.put(Healthy.class, new PersonProperties(Color.WHITE, 75, 3.0));
	props.put(Carrier.class, new PersonProperties(Color.WHITE, 75, 3.0));
	props.put(Infected.class, new PersonProperties(Color.RED, 50, 4.5));
	props.put(Corpse.class, new PersonProperties(Color.DARK_GRAY, 0, 0.0));
    }

    public static Color getColour(final Class<? extends Person> type) {
	return props.get(type).colour.get();
    }

    public static double getInfectionTime() {
	return Double.longBitsToDouble(infectionTime.get());
    }

    public static int getPopulation() {
	return population.get();
    }

    public static int getSightRange(final Class<? extends Person> type) {
	return props.get(type).sightRange.get();
    }

    public static int getSize() {
	return SIZE;
    }

    public static double getSpeed(final Class<? extends Person> type) {
	return Double.longBitsToDouble(props.get(type).speed.get());
    }

    public static double getStarveTime() {
	return Double.longBitsToDouble(starveTime.get());
    }

    public static void setHealthySightRange(final int sightRange) {
	setSightRange(Healthy.class, sightRange);
    }

    public static void setInfectedRelativeSpeed(final double percentage) {
	final double fraction = percentage / 100;
	props.get(Infected.class).speed.set(Double.doubleToLongBits(fraction * getSpeed(Healthy.class)));
    }

    public static void setInfectedSightRange(final int sightRange) {
	setSightRange(Infected.class, sightRange);
    }

    public static void setInfectionTime(final double infectionTime) {
	ZombiesProperties.infectionTime.set(Double.doubleToLongBits(infectionTime));
    }

    public static void setPopulation(final int population) {
	ZombiesProperties.population.set(population);
    }

    private static void setSightRange(final Class<? extends Person> type, final int sightRange) {
	props.get(type).sightRange.set(Math.max(sightRange, SIZE));
    }

    public static void setSpeed(final Class<? extends Person> type, final double speed) {
	props.get(type).speed.set(Double.doubleToLongBits(speed));
    }

    public static void setStarveTime(final double starveTime) {
	ZombiesProperties.starveTime.set(Double.doubleToLongBits(starveTime));
    }
}
