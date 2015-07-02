package zombies.attributes;

import jalse.attributes.AttributeEvent;
import jalse.attributes.AttributeListener;
import jalse.entities.Entity;
import zombies.entities.Corpse;
import zombies.entities.Infected;
import zombies.entities.Person;

public class StarvationListener implements AttributeListener<Double> {

    @Override
    public void attributeAdded(final AttributeEvent<Double> event) {
	final Person person = ((Entity) event.getContainer()).asType(Person.class);
	final double starvation = event.getValue();

	// Check starvation
	if (starvation >= 1.0) {
	    person.cancelAllScheduledForActor();
	    person.unmarkAsType(Infected.class);
	    person.markAsType(Corpse.class);
	}
    }
}
