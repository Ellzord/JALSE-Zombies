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

	private static Optional<Person> getClosestPersonOfType(Person person,
			Stream<Person> people, Class<? extends Entity> type) {
		Point personPos = person.getPosition();
		Integer sightRange = person.getSightRange();
		return people
				.filter(p -> !p.equals(person))
				.filter(p -> p.isMarkedAsType(type))
				.filter(p -> Math.abs(p.getPosition().x - personPos.x) <= sightRange)
				.filter(p -> Math.abs(p.getPosition().y - personPos.y) <= sightRange)
				.collect(
						Collectors.minBy((a, b) -> {
							Point aPos = a.getPosition();
							Point bPos = b.getPosition();
							int d1 = (aPos.x - personPos.x)
									* (aPos.x - personPos.x)
									+ (aPos.y - personPos.y)
									* (aPos.y - personPos.y);
							int d2 = (bPos.x - personPos.x)
									* (bPos.x - personPos.x)
									+ (bPos.y - personPos.y)
									* (bPos.y - personPos.y);
							return d1 - d2;
						}));
	}

	public static Double directionHealthy(Person person, Stream<Person> people) {
		double moveAngle = person.getAngle();
		Point personPos = person.getPosition();
		Optional<Person> closestInfected = getClosestPersonOfType(person,
				people, Infected.class);

		boolean infectedVisible = false;
		if (closestInfected.isPresent()) {
			int dx = personPos.x - closestInfected.get().getPosition().x;
			int dy = personPos.y - closestInfected.get().getPosition().y;

			if (dx * dx + dy * dy < person.getSightRange()
					* person.getSightRange()) {
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

	public static Double directionInfected(Person person, Stream<Person> people) {
		double moveAngle = person.getAngle();
		Point personPos = person.getPosition();
		Optional<Person> closestHealthy = getClosestPersonOfType(person,
				people, Healthy.class);

		boolean healthyVisible = false;
		if (closestHealthy.isPresent()) {
			int dx = closestHealthy.get().getPosition().x - personPos.x;
			int dy = closestHealthy.get().getPosition().y - personPos.y;

			if (dx * dx + dy * dy < person.getSightRange()
					* person.getSightRange()) {
				moveAngle = Math.atan2(dy, dx);
				if (dx * dx + dy * dy < Person.SIZE * Person.SIZE) {
					person.asType(Infected.class).bite(closestHealthy.get());
					;
				}
			}
		}
		if (!healthyVisible) {
			final Random rand = ThreadLocalRandom.current();
			moveAngle += 2. * (rand.nextDouble() - 0.5);
		}
		return moveAngle;
	}

	public static Double directionCarrier(Person person, Stream<Person> people) {
		final Random rand = ThreadLocalRandom.current();
		final double moveAngle = person.getAngle() + 2.
				* (rand.nextDouble() - 0.5);

		return moveAngle;
	}

	@Override
	public void perform(final ActionContext<Entity> context)
			throws InterruptedException {
		final Field field = context.getActor().asType(Field.class);

		field.streamPeople()
				.filter(p -> !p.isMarkedAsType(Corpse.class))
				.forEach(person -> {
					// Original
						final Point pos = person.getPosition();
						final int size = Person.SIZE;

						double moveDist = person.getSpeed();
						double moveAngle = person.getAngle();
						try {
							moveAngle = (Double) person.getDirectionMethod()
									.invoke(person,
											new Object[] { person,
													field.streamPeople() });
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						person.setAngle(moveAngle);

						final Point moveDelta = new Point(
								(int) (moveDist * Math.cos(moveAngle)),
								(int) (moveDist * Math.sin(moveAngle)));

						// Calculate bounded x & y
						final int x = bounded(pos.x + moveDelta.x, 0,
								ZombiesPanel.WIDTH - size);
						final int y = bounded(pos.y + moveDelta.y, 0,
								ZombiesPanel.HEIGHT - size);

						if (pos.x != x || pos.y != y) {
							// Update if changed
							person.setPosition(new Point(x, y));
						}
					});
	}
}
