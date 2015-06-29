package zombies.listeners;

import java.awt.Color;

import jalse.attributes.AttributeEvent;
import jalse.attributes.AttributeListener;
import jalse.entities.Entity;
import zombies.ZombiesProperties;
import zombies.entities.Healthy;
import zombies.entities.Infected;
import zombies.entities.Person;

public class InfectionFractionListener implements AttributeListener<Double> {

    @Override
    public void attributeAdded(final AttributeEvent<Double> event) {
	final Person person = ((Entity) event.getContainer()).asType(Person.class);
	final Double infectionFraction = event.getValue();

	if (infectionFraction >= 1.0) {
	    person.markAsType(Infected.class);
	    return;
	}

	final Color healthyColour = ZombiesProperties.getColour(Healthy.class);
	final Color infectedColour = ZombiesProperties.getColour(Infected.class);

	// Gradually transition from "healthy" to "infected" color
	final float r = (float) ((healthyColour.getRed() * (1. - infectionFraction)
		+ infectedColour.getRed() * infectionFraction) / 255.);
	final float g = (float) ((healthyColour.getGreen() * (1. - infectionFraction)
		+ infectedColour.getGreen() * infectionFraction) / 255.);
	final float b = (float) ((healthyColour.getBlue() * (1. - infectionFraction)
		+ infectedColour.getBlue() * infectionFraction) / 255.);

	person.setColor(new Color(r, g, b));
    }
}