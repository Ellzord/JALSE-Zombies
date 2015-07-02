package zombies.actions;

import jalse.actions.Action;
import jalse.actions.ActionContext;
import jalse.entities.Entity;
import zombies.ZombiesProperties;
import zombies.entities.Carrier;

public class GetSick implements Action<Entity> {

    @Override
    public void perform(final ActionContext<Entity> context) throws InterruptedException {
	final Carrier carrier = context.getActor().asType(Carrier.class);

	// Increase infection fraction a bit
	final double infection = carrier.getInfectionPercentage() + 1. / 30 / ZombiesProperties.getInfectionTime();
	carrier.setInfectionPercentage(infection);
    }
}
