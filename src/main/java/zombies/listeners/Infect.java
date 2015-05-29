package zombies.listeners;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import zombies.PersonProperties;
import zombies.actions.GetSick;
import zombies.actions.MovePeople;
import zombies.actions.Starve;
import zombies.entities.Carrier;
import zombies.entities.Corpse;
import zombies.entities.Healthy;
import zombies.entities.Infected;
import zombies.entities.Person;
import jalse.entities.Entity;
import jalse.entities.EntityTypeEvent;
import jalse.entities.EntityTypeListener;

public class Infect implements EntityTypeListener {
	private static List<Class<? extends Entity>> types = Arrays.asList(
			Healthy.class, Carrier.class, Infected.class, Corpse.class);

	@Override
	public void entityMarkedAsType(EntityTypeEvent event) {
		Person person = event.getEntity().asType(Person.class);
		final String directionMethod;
		final Class<? extends Entity> type = event.getTypeChange();

		// Clear out any other type and cancel any scheduled actions
		for (Class<? extends Entity> t : types) {
			if (person.isMarkedAsType(t) && t != type) {
				person.unmarkAsType(t);
			}
		}
		person.cancelAllScheduledForActor();

		// Set the person's constants
		try {
			Class<? extends PersonProperties.Properties> properties = PersonProperties
					.getPropertiesForType(type);
			person.setColor((Color) properties.getDeclaredField("COLOR").get(
					null));
			person.setSpeed((double) properties.getDeclaredField("SPEED").get(
					null));
			person.setSightRange((int) properties.getDeclaredField(
					"SIGHT_RANGE").get(null));
		} catch (IllegalArgumentException | IllegalAccessException
				| NoSuchFieldException | SecurityException e1) {
			System.err.println("Problem getting fields for type "
					+ String.valueOf(type));
		}

		// Set the direction method to use, and schedule actions if necessary
		if (type == Healthy.class) {
			directionMethod = "directionHealthy";
		} else if (type == Carrier.class) {
			person.scheduleForActor(new GetSick(), 1000 / 30, 1000 / 30,
					TimeUnit.MILLISECONDS);
			directionMethod = "directionCarrier";
		} else if (type == Infected.class) {
			person.scheduleForActor(new Starve(), 1000 / 30, 1000 / 30,
					TimeUnit.MILLISECONDS);
			directionMethod = "directionInfected";
		} else if (type == Corpse.class) {
			directionMethod = "directionCarrier";
		} else {
			directionMethod = "directionCarrier";
		}

		try {
			person.setDirectionMethod(MovePeople.class.getMethod(
					directionMethod, Person.class, Stream.class));
		} catch (NoSuchMethodException e) {
			System.err.println("Method " + directionMethod
					+ " is undefined for Action MovePeople.");
		} catch (SecurityException e) {
			System.err.println("Method " + directionMethod
					+ " is not public for Action MovePeople.");
		}
	}
}