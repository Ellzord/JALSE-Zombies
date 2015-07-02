package zombies.actions;

import static jalse.entities.Entities.isMarkedAsType;
import static jalse.entities.Entities.notMarkedAsType;
import static jalse.misc.Identifiable.not;

import java.awt.Point;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import jalse.actions.Action;
import jalse.actions.ActionContext;
import jalse.entities.Entity;
import zombies.ZombiesPanel;
import zombies.ZombiesProperties;
import zombies.entities.Carrier;
import zombies.entities.Corpse;
import zombies.entities.Field;
import zombies.entities.Healthy;
import zombies.entities.Infected;
import zombies.entities.Person;

public class MovePeople implements Action<Entity> {

    private static int bounded(final int value, final int min, final int max) {
	return value < min ? min : value > max ? max : value;
    }

    public static Double directionAwayFromInfected(final Person person, final Set<Person> people) {
	// Find closest infected to hide from
	final Optional<Person> closestInfected = getClosestPersonOfType(person, people, Infected.class);

	// Cannot see any
	if (!closestInfected.isPresent()) {
	    return randomDirection(person);
	}

	final Point personPos = person.getPosition();
	final Point closestPos = closestInfected.get().getPosition();

	// Away
	final int dx = personPos.x - closestPos.x;
	final int dy = personPos.y - closestPos.y;

	// Convert
	return Math.atan2(dy, dx);
    }

    private static Double directionToHealthyAndBite(final Person person, final Set<Person> people) {
	// Find closest healthy person in sight
	final Optional<Person> closestHealthy = getClosestPersonOfType(person, people, Healthy.class);

	// Check can see any
	if (!closestHealthy.isPresent()) {
	    return randomDirection(person);
	}

	// Healthy person
	final Person healthy = closestHealthy.get();

	// Towards
	final Point personPos = person.getPosition();
	final int dx = healthy.getPosition().x - personPos.x;
	final int dy = healthy.getPosition().y - personPos.y;

	// Check in range of biting
	final int size = ZombiesProperties.getSize();
	if (dx * dx + dy * dy < size * size) {
	    person.asType(Infected.class).bite(healthy);
	}

	// Convert
	return Math.atan2(dy, dx);
    }

    private static Optional<Person> getClosestPersonOfType(final Person person, final Set<Person> people,
	    final Class<? extends Entity> type) {
	final Point personPos = person.getPosition();
	final Integer sightRange = person.getSightRange();
	// Stream other entities of type
	return people.stream().filter(not(person)).filter(isMarkedAsType(type)).filter(p -> {
	    final Point pPos = p.getPosition();
	    // Within range
	    return Math.abs(pPos.x - personPos.x) <= sightRange && Math.abs(pPos.y - personPos.y) <= sightRange;
	}).collect(Collectors.minBy((a, b) -> {
	    final Point aPos = a.getPosition();
	    final Point bPos = b.getPosition();
	    // Closest person
	    final int d1 = (aPos.x - personPos.x) * (aPos.x - personPos.x)
		    + (aPos.y - personPos.y) * (aPos.y - personPos.y);
	    final int d2 = (bPos.x - personPos.x) * (bPos.x - personPos.x)
		    + (bPos.y - personPos.y) * (bPos.y - personPos.y);
	    return d1 - d2;
	}));
    }

    private static Double randomDirection(final Person person) {
	return person.getAngle() + 2. * (ThreadLocalRandom.current().nextDouble() - 0.5);
    }

    @Override
    public void perform(final ActionContext<Entity> context) throws InterruptedException {
	final Field field = context.getActor().asType(Field.class);
	final Set<Person> people = field.getEntitiesOfType(Person.class);
	people.stream().filter(notMarkedAsType(Corpse.class)).forEach(person -> {
	    // Get correct move angle
	    double moveAngle;
	    if (person.isMarkedAsType(Infected.class)) {
		// Move towards healthy
		moveAngle = directionToHealthyAndBite(person, people);
	    } else if (person.isMarkedAsType(Carrier.class)) {
		// Move randomly
		moveAngle = randomDirection(person);
	    } else {
		// Run away
		moveAngle = directionAwayFromInfected(person, people);
	    }
	    person.setAngle(moveAngle);

	    // Calculate move delta
	    final double moveDist = person.getSpeed();
	    final Point moveDelta = new Point((int) (moveDist * Math.cos(moveAngle)),
		    (int) (moveDist * Math.sin(moveAngle)));

	    // Original values
	    final Point pos = person.getPosition();
	    final int size = ZombiesProperties.getSize();

	    // Apply bounded move delta
	    final int x = bounded(pos.x + moveDelta.x, 0, ZombiesPanel.WIDTH - size);
	    final int y = bounded(pos.y + moveDelta.y, 0, ZombiesPanel.HEIGHT - size);

	    if (pos.x != x || pos.y != y) {
		// Update if changed
		person.setPosition(new Point(x, y));
	    }
	});
    }
}
