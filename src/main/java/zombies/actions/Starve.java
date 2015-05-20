package zombies.actions;

import zombies.entities.Corpse;
import zombies.entities.Person;
import jalse.actions.Action;
import jalse.actions.ActionContext;
import jalse.entities.Entity;

public class Starve implements Action<Entity> {
	private double hungerFraction = 0.0;

	@Override
	public void perform(final ActionContext<Entity> context)
			throws InterruptedException {
		Person person = context.getActor().asType(Person.class);

		hungerFraction += 1. / 30 / Person.STARVE_TIME_SECONDS;

		if (hungerFraction >= 1.0) {
			person.markAsType(Corpse.class);
		}
	}
}
