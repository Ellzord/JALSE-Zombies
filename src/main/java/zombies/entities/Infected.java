package zombies.entities;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

import zombies.actions.Starve;

public interface Infected extends Person {
	static final Color COLOR = Color.RED;
	static final int SIGHT_RANGE = 50;
	static final double SPEED = 3.5;

	public default void bite(Person person) {
		this.cancelAllScheduledForActor();
		person.markAsType(Carrier.class);
		
		// Restart the Starve() counter
		this.scheduleForActor(new Starve(), 1000 / 30, 1000 / 30,
				TimeUnit.MILLISECONDS);
	}
}