package zombies.entities;

import static jalse.attributes.Attributes.newNamedDoubleType;
import static zombies.ZombiesPanel.TICK_INTERVAL;

import java.util.concurrent.TimeUnit;

import jalse.attributes.NamedAttributeType;
import jalse.entities.annotations.GetAttribute;
import jalse.entities.annotations.SetAttribute;
import zombies.actions.GetSick;

public interface Infected extends Person {

    final NamedAttributeType<Double> HUNGER_PERCENTAGE_TYPE = newNamedDoubleType("hungerPercentage");

    public default void bite(final Person person) {
	person.unmarkAsType(Healthy.class);
	person.markAsType(Carrier.class);
	person.scheduleForActor(new GetSick(), TICK_INTERVAL, TICK_INTERVAL, TimeUnit.MILLISECONDS);

	// Restart the Starve() counter
	setHungerPercentage(0.0);
    }

    @GetAttribute
    double getHungerPercentage();

    @SetAttribute
    void setHungerPercentage(double hungerPercentage);
}