package zombies.actions;

import zombies.entities.Person;
import jalse.actions.Action;
import jalse.actions.ActionContext;
import jalse.attributes.Attributes;
import jalse.entities.Entity;

public class GetSick implements Action<Entity> {
	private double infectionFraction = 0.0;

	@Override
	public void perform(final ActionContext<Entity> context)
			throws InterruptedException {
		Person person = context.getActor().asType(Person.class);

		// Increase infection fraction a bit
		infectionFraction += 1. / 30 / Person.INFECTION_TIME_SECONDS;
		person.setAttribute("infectionFraction", Attributes.DOUBLE_TYPE,
				infectionFraction);
	}
}
