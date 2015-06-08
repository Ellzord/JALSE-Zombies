package zombies.actions;

import jalse.actions.Action;
import jalse.actions.ActionContext;
import jalse.entities.Entity;

import java.awt.Point;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import zombies.PersonProperties;
import zombies.ZombiesPanel;
import zombies.entities.Corpse;
import zombies.entities.Field;
import zombies.entities.Healthy;
import zombies.entities.Infected;
import zombies.entities.Person;

public class MovePeople implements Action<Entity> {

    static int bounded(final int value, final int min, final int max) {
	return value < min ? min : value > max ? max : value;
    }

    public static Double directionCarrier(final Person person, final Stream<Person> people) {
	// A carrier moves randomly
	final Random rand = ThreadLocalRandom.current();
	final double moveAngle = person.getAngle() + 2. * (rand.nextDouble() - 0.5);

	return moveAngle;
    }

    public static Double directionHealthy(final Person person, final Stream<Person> people) {
	// A healthy person moves away from the nearest infected person they see
	double moveAngle = person.getAngle();
	final Point personPos = person.getPosition();
	final Optional<Person> closestInfected = getClosestPersonOfType(person, people, Infected.class);

	boolean infectedVisible = false;
	if (closestInfected.isPresent()) {
	    final int dx = personPos.x - closestInfected.get().getPosition().x;
	    final int dy = personPos.y - closestInfected.get().getPosition().y;

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

    public static Double directionInfected(final Person person, final Stream<Person> people) {
	// An infected person moves toward the nearest healthy person they see
	double moveAngle = person.getAngle();
	final Point personPos = person.getPosition();
	final Optional<Person> closestHealthy = getClosestPersonOfType(person, people, Healthy.class);

	final boolean healthyVisible = false;
	if (closestHealthy.isPresent()) {
	    final int dx = closestHealthy.get().getPosition().x - personPos.x;
	    final int dy = closestHealthy.get().getPosition().y - personPos.y;

	    if (dx * dx + dy * dy < person.getSightRange() * person.getSightRange()) {
		moveAngle = Math.atan2(dy, dx);
		if (dx * dx + dy * dy < PersonProperties.SIZE * PersonProperties.SIZE) {
		    person.asType(Infected.class).bite(closestHealthy.get());
		}
	    }
	}
	if (!healthyVisible) {
	    final Random rand = ThreadLocalRandom.current();
	    moveAngle += 2. * (rand.nextDouble() - 0.5);
	}
	return moveAngle;
    }

    private static Optional<Person> getClosestPersonOfType(final Person person, final Stream<Person> people,
	    final Class<? extends Entity> type) {
	final Point personPos = person.getPosition();
	final Integer sightRange = person.getSightRange();
	return people
		.filter(p -> !p.equals(person))
		.filter(p -> p.isMarkedAsType(type))
		.filter(p -> Math.abs(p.getPosition().x - personPos.x) <= sightRange)
		.filter(p -> Math.abs(p.getPosition().y - personPos.y) <= sightRange)
		.collect(
			Collectors.minBy((a, b) -> {
			    final Point aPos = a.getPosition();
			    final Point bPos = b.getPosition();
			    final int d1 = (aPos.x - personPos.x) * (aPos.x - personPos.x) + (aPos.y - personPos.y)
				    * (aPos.y - personPos.y);
			    final int d2 = (bPos.x - personPos.x) * (bPos.x - personPos.x) + (bPos.y - personPos.y)
				    * (bPos.y - personPos.y);
			    return d1 - d2;
			}));
    }

    @Override
    public void perform(final ActionContext<Entity> context) throws InterruptedException {
	final Field field = context.getActor().asType(Field.class);

	field.streamPeople()
		.filter(p -> !p.isMarkedAsType(Corpse.class))
		.forEach(person -> {
		    // Original
			final Point pos = person.getPosition();
			final int size = PersonProperties.SIZE;

			// Move r = speed
			final double moveDist = person.getSpeed();
			double moveAngle = person.getAngle();
			try {
			    // Move theta = apply appropriate method above
			    moveAngle = (Double) person.getDirectionMethod().invoke(person,
				    new Object[] { person, field.streamPeople() });
			} catch (final Exception e) {
			    e.printStackTrace();
			}
			person.setAngle(moveAngle);

			final Point moveDelta = new Point((int) (moveDist * Math.cos(moveAngle)),
				(int) (moveDist * Math.sin(moveAngle)));

			// Calculate bounded x & y
			final int x = bounded(pos.x + moveDelta.x, 0, ZombiesPanel.WIDTH - size);
			final int y = bounded(pos.y + moveDelta.y, 0, ZombiesPanel.HEIGHT - size);

			if (pos.x != x || pos.y != y) {
			    // Update if changed
			    person.setPosition(new Point(x, y));
			}
		    });
    }
}
