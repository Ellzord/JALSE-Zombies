package zombies.actions;

import jalse.actions.Action;
import jalse.actions.ActionContext;
import jalse.entities.Entity;
import zombies.ZombiesProperties;
import zombies.entities.Infected;

public class Starve implements Action<Entity> {

    @Override
    public void perform(final ActionContext<Entity> context) throws InterruptedException {
	final Infected infected = context.getActor().asType(Infected.class);

	// Infected slowly starve to death after biting
	final double hunger = infected.getHungerPercentage() + 1. / 30 / ZombiesProperties.getStarveTime();
	infected.setHungerPercentage(hunger);
    }
}
