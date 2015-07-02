package zombies.attributes;

import static zombies.ZombiesPanel.TICK_INTERVAL;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

import jalse.attributes.AttributeEvent;
import jalse.attributes.AttributeListener;
import jalse.entities.Entity;
import zombies.ZombiesProperties;
import zombies.actions.Starve;
import zombies.entities.Carrier;
import zombies.entities.Healthy;
import zombies.entities.Infected;
import zombies.entities.Person;

public class InfectionListener implements AttributeListener<Double> {

    @Override
    public void attributeAdded(final AttributeEvent<Double> event) {
	final Person person = ((Entity) event.getContainer()).asType(Person.class);
	final double infection = event.getValue();

	// Check infected
	if (infection >= 1.0) {
	    person.cancelAllScheduledForActor();
	    person.unmarkAsType(Carrier.class);
	    person.markAsType(Infected.class);
	    person.scheduleForActor(new Starve(), TICK_INTERVAL, TICK_INTERVAL, TimeUnit.MILLISECONDS);
	    return;
	}

	// Base colours
	final Color healthyColour = ZombiesProperties.getColour(Healthy.class);
	final Color infectedColour = ZombiesProperties.getColour(Infected.class);

	// Gradually transition from "healthy" to "infected" color
	final float r = (float) ((healthyColour.getRed() * (1. - infection) + infectedColour.getRed() * infection)
		/ 255.);
	final float g = (float) ((healthyColour.getGreen() * (1. - infection) + infectedColour.getGreen() * infection)
		/ 255.);
	final float b = (float) ((healthyColour.getBlue() * (1. - infection) + infectedColour.getBlue() * infection)
		/ 255.);

	person.setColour(new Color(r, g, b));
    }
}