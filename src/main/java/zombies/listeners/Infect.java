package zombies.listeners;

import java.awt.Color;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import zombies.actions.GetSick;
import zombies.actions.MovePeople;
import zombies.actions.Starve;
import zombies.entities.Carrier;
import zombies.entities.Corpse;
import zombies.entities.Healthy;
import zombies.entities.Infected;
import zombies.entities.Person;
import jalse.listeners.EntityEvent;
import jalse.listeners.EntityListener;

public class Infect implements EntityListener {
	@Override
	public void entityKilled(EntityEvent event) {
		Person person = event.getEntity().asType(Person.class);
		person.setColor(Color.LIGHT_GRAY);
	}

	@Override
	public void entityMarkedAsType(EntityEvent event) {
		Person person = event.getEntity().asType(Person.class);
		final String directionMethod;

		if (event.getTypeChange() == Healthy.class) {
			person.unmarkAsType(Carrier.class);
			person.unmarkAsType(Infected.class);
			person.unmarkAsType(Corpse.class);
			person.cancelAllScheduledForActor();
			person.setColor(Healthy.COLOR);
			person.setSpeed(Healthy.SPEED);
			person.setSightRange(Healthy.SIGHT_RANGE);
			directionMethod = "directionHealthy";
		} else if (event.getTypeChange() == Carrier.class) {
			person.unmarkAsType(Healthy.class);
			person.unmarkAsType(Infected.class);
			person.unmarkAsType(Corpse.class);
			person.cancelAllScheduledForActor();
			person.scheduleForActor(new GetSick(), 1000 / 30, 1000 / 30,
					TimeUnit.MILLISECONDS);
			person.setSpeed(Healthy.SPEED);
			directionMethod = "directionCarrier";
		} else if (event.getTypeChange() == Infected.class) {
			person.unmarkAsType(Healthy.class);
			person.unmarkAsType(Carrier.class);
			person.unmarkAsType(Corpse.class);
			person.cancelAllScheduledForActor();
			person.scheduleForActor(new Starve(), 1000 / 30, 1000 / 30,
					TimeUnit.MILLISECONDS);
			person.setColor(Infected.COLOR);
			person.setSpeed(Infected.SPEED);
			person.setSightRange(Infected.SIGHT_RANGE);
			directionMethod = "directionInfected";
		} else if (event.getTypeChange() == Corpse.class) {
			person.unmarkAsType(Healthy.class);
			person.unmarkAsType(Carrier.class);
			person.unmarkAsType(Infected.class);
			person.setColor(Corpse.COLOR);
			person.setSpeed(0.0);
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