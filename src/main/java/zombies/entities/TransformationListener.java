package zombies.entities;

import jalse.entities.Entity;
import jalse.entities.EntityTypeEvent;
import jalse.entities.EntityTypeListener;
import zombies.ZombiesProperties;

public class TransformationListener implements EntityTypeListener {

    @Override
    public void entityMarkedAsType(final EntityTypeEvent event) {
	final Person person = event.getEntity().asType(Person.class);
	final Class<? extends Entity> type = event.getTypeChange();

	person.setColour(ZombiesProperties.getColour(type));
	person.setSightRange(ZombiesProperties.getSightRange(type));
	person.setSpeed(ZombiesProperties.getSpeed(type));
    }
}
