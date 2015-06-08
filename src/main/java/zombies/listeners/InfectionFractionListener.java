package zombies.listeners;

import jalse.attributes.AttributeEvent;
import jalse.attributes.AttributeListener;
import jalse.entities.Entity;

import java.awt.Color;

import zombies.PersonProperties;
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

	// Gradually transition from "healthy" to "infected" color
	final float r = (float) ((PersonProperties.Healthy.COLOR.getRed() * (1. - infectionFraction) + PersonProperties.Infected.COLOR
		.getRed() * infectionFraction) / 255.);
	final float g = (float) ((PersonProperties.Healthy.COLOR.getGreen() * (1. - infectionFraction) + PersonProperties.Infected.COLOR
		.getGreen() * infectionFraction) / 255.);
	final float b = (float) ((PersonProperties.Healthy.COLOR.getBlue() * (1. - infectionFraction) + PersonProperties.Infected.COLOR
		.getBlue() * infectionFraction) / 255.);

	person.setColor(new Color(r, g, b));
    }
}