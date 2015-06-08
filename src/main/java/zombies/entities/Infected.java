package zombies.entities;

import java.util.concurrent.TimeUnit;

import zombies.actions.Starve;

public interface Infected extends Person {

    public default void bite(final Person person) {
	this.cancelAllScheduledForActor();
	person.markAsType(Carrier.class);

	// Restart the Starve() counter
	this.scheduleForActor(new Starve(), 1000 / 30, 1000 / 30, TimeUnit.MILLISECONDS);
    }
}