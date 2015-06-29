package zombies.listeners;

import java.util.concurrent.TimeUnit;

import jalse.entities.EntityTypeEvent;
import jalse.entities.EntityTypeListener;
import zombies.ZombiesProperties;
import zombies.actions.GetSick;
import zombies.actions.Starve;
import zombies.entities.Carrier;
import zombies.entities.Infected;
import zombies.entities.Person;

public class Infect implements EntityTypeListener {

    @Override
    public void entityMarkedAsType(final EntityTypeEvent event) {
	@SuppressWarnings("unchecked")
	final Class<? extends Person> type = (Class<? extends Person>) event.getTypeChange();
	final Person person = event.getEntity().asType(Person.class);

	// Clear out any other type and cancel any scheduled actions
	person.streamMarkedAsTypes().filter(t -> !t.equals(Person.class) && !t.equals(type))
		.forEach(t -> person.unmarkAsType(t));
	person.cancelAllScheduledForActor();

	// Set the person's constants
	person.setColor(ZombiesProperties.getColour(type));
	person.setSpeed(ZombiesProperties.getSpeed(type));
	person.setSightRange(ZombiesProperties.getSightRange(type));

	// Schedule actions if necessary
	if (type == Carrier.class) {
	    person.scheduleForActor(new GetSick(), 1000 / 30, 1000 / 30, TimeUnit.MILLISECONDS);
	} else if (type == Infected.class) {
	    person.scheduleForActor(new Starve(), 1000 / 30, 1000 / 30, TimeUnit.MILLISECONDS);
	}
    }
}