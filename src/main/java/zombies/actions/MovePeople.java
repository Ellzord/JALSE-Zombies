package zombies.actions;

import static jalse.entities.Entities.isMarkedAsType;
import static jalse.entities.Entities.notMarkedAsType;
import static jalse.misc.Identifiable.not;

import java.awt.Point;
import java.util.Optional;
import java.util.Random;
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
	// A healthy person moves away from the nearest infected person they see
	double moveAngle = person.getAngle();
	final Optional<Person> closestInfected = getClosestPersonOfType(person, people, Infected.class);

	boolean infectedVisible = false;
	if (closestInfected.isPresent()) {
	    final Point personPos = person.getPosition();
	    final Point closestPos = closestInfected.get().getPosition();

	    // Distance
	    final int dx = personPos.x - closestPos.x;
	    final int dy = personPos.y - closestPos.y;

	    if (dx * dx + dy * dy < person.getSightRange() * person.getSightRange()) {
		moveAngle = Math.atan2(dy, dx);
		infectedVisible = true;
	    }
	}
	if (!infectedVisible) {
	    final Random rand = ThreadLocalRandom.current();
	    moveAngle += 2. * (rand.nextDouble() - 0.5);
	}
	return moveAngle;
    }

    private static Double directionToHealthyAndBite(final Person person, final Set<Person> people) {
	// An infected person moves toward the nearest healthy person they see
	double moveAngle = person.getAngle();
	final Optional<Person> closestHealthy = getClosestPersonOfType(person, people, Healthy.class);

	boolean healthyVisible = false;
	// Healthy found
	if (closestHealthy.isPresent()) {
	    final Person healthy = closestHealthy.get();

	    // Calculate distance
	    final Point personPos = person.getPosition();
	    final int dx = healthy.getPosition().x - personPos.x;
	    final int dy = healthy.getPosition().y - personPos.y;

	    // Check in sight
	    if (dx * dx + dy * dy < person.getSightRange() * person.getSightRange()) {
		moveAngle = Math.atan2(dy, dx);
		final int size = ZombiesProperties.getSize();
		if (dx * dx + dy * dy < size * size) {
		    // Bite
		    person.asType(Infected.class).bite(healthy);
		    healthyVisible = true;
		}
	    }
	}

	// Look for healthy
	if (!healthyVisible) {
	    final Random rand = ThreadLocalRandom.current();
	    moveAngle += 2. * (rand.nextDouble() - 0.5);
	}
	return moveAngle;
    }

    private static Optional<Person> getClosestPersonOfType(final Person person, final Set<Person> people,
	    final Class<? extends Entity> type) {
	final Point personPos = person.getPosition();
	final Integer sightRange = person.getSightRange();
	return people.stream().filter(not(person)).filter(isMarkedAsType(type)).filter(p -> {
	    final Point pPos = p.getPosition();
	    return Math.abs(pPos.x - personPos.x) <= sightRange && Math.abs(pPos.y - personPos.y) <= sightRange;
	}).collect(Collectors.minBy((a, b) -> {
	    final Point aPos = a.getPosition();
	    final Point bPos = b.getPosition();
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
