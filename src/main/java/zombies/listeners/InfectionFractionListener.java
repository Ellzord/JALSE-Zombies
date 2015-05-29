package zombies.listeners;

import java.awt.Color;

import zombies.PersonProperties;
import zombies.entities.Infected;
import zombies.entities.Person;
import jalse.entities.Entity;
import jalse.attributes.AttributeEvent;
import jalse.attributes.AttributeListener;

public class InfectionFractionListener implements AttributeListener<Double> {
	@Override
	public void attributeAdded(final AttributeEvent<Double> event) {
		Person person = ((Entity) event.getContainer()).asType(Person.class);
		Double infectionFraction = event.getValue();

		if (infectionFraction >= 1.0) {
			person.markAsType(Infected.class);
			return;
		}

		// Gradually transition from "healthy" to "infected" color
		float r = (float) ((PersonProperties.Healthy.COLOR.getRed()
				* (1. - infectionFraction) + PersonProperties.Infected.COLOR
				.getRed() * infectionFraction) / 255.);
		float g = (float) ((PersonProperties.Healthy.COLOR.getGreen()
				* (1. - infectionFraction) + PersonProperties.Infected.COLOR
				.getGreen() * infectionFraction) / 255.);
		float b = (float) ((PersonProperties.Healthy.COLOR.getBlue()
				* (1. - infectionFraction) + PersonProperties.Infected.COLOR
				.getBlue() * infectionFraction) / 255.);

		person.setColor(new Color(r, g, b));
	}
}