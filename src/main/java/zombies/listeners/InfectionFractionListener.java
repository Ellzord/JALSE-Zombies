package zombies.listeners;

import java.awt.Color;
import zombies.entities.Healthy;
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

		float r = (float) ((Healthy.COLOR.getRed() * (1. - infectionFraction) + Infected.COLOR
				.getRed() * infectionFraction) / 255.);
		float g = (float) ((Healthy.COLOR.getGreen() * (1. - infectionFraction) + Infected.COLOR
				.getGreen() * infectionFraction) / 255.);
		float b = (float) ((Healthy.COLOR.getBlue() * (1. - infectionFraction) + Infected.COLOR
				.getBlue() * infectionFraction) / 255.);

		person.setColor(new Color(r, g, b));
	}
}