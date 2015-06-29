package zombies;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;
import javax.swing.Timer;

import jalse.DefaultJALSE;
import jalse.JALSE;
import jalse.attributes.Attributes;
import zombies.actions.MovePeople;
import zombies.entities.Corpse;
import zombies.entities.Field;
import zombies.entities.Healthy;
import zombies.entities.Infected;
import zombies.entities.Person;
import zombies.listeners.Infect;
import zombies.listeners.InfectionFractionListener;

@SuppressWarnings("serial")
public class ZombiesPanel extends JPanel implements ActionListener, MouseListener {

    public static final int WIDTH = 700;
    public static final int HEIGHT = 500;

    private static void drawElement(final Graphics g, final Person person) {
	final Point position = person.getPosition();
	final int size = ZombiesProperties.getSize();
	g.setColor(Color.GRAY);
	g.fillOval(position.x - 2, position.y - 2, size + 4, size + 4);
	g.setColor(person.getColor());
	g.fillOval(position.x, position.y, size, size);
    }

    private final JALSE jalse;

    public ZombiesPanel() {
	// Manually ticked JALSE
	jalse = new DefaultJALSE.Builder().setManualEngine().build();
	// Create data model
	createEntities();
	// Size to field size
	setPreferredSize(getField().getSize());
	// Set black background
	setBackground(Color.BLACK);
	// Listener for key events
	setFocusable(true);
	addMouseListener(this);
	// Start ticking and rendering (30 FPS)
	new Timer(1000 / 30, this).start();
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
	// Tick model
	jalse.resume();
	// Request repaint
	repaint();
    }

    private void addPersonAtRandomPosition() {
	final Person person = getField().newEntity(Person.class);
	person.addEntityTypeListener(new Infect());
	person.setPosition(randomPosition());
	person.setAngle(randomAngle());
	person.addAttributeListener("infectionFraction", Attributes.DOUBLE_TYPE, new InfectionFractionListener());
	person.markAsType(Healthy.class);
    }

    public void adjustInfectedSpeed() {
	final double speed = ZombiesProperties.getSpeed(Infected.class);
	getField().streamEntitiesOfType(Infected.class).forEach(p -> p.setSpeed(speed));
    }

    public void adjustPopulation() {
	final int population = ZombiesProperties.getPopulation();
	int count = getField().getEntityCount();
	// Increase population
	while (count < population) {
	    addPersonAtRandomPosition();
	    count++;
	}
	// Decrease population
	while (count > population) {
	    removeRandomPerson();
	    count--;
	}
    }

    public void adjustSightRange(final Class<? extends Person> type) {
	final int sightRange = ZombiesProperties.getSightRange(type);
	getField().streamEntitiesOfType(type).forEach(p -> p.setSightRange(sightRange));
    }

    private void createEntities() {
	// Create field
	final Field field = jalse.newEntity(Field.ID, Field.class);
	field.setSize(new Dimension(WIDTH, HEIGHT));
	field.scheduleForActor(new MovePeople(), 0, 1000 / 30, TimeUnit.MILLISECONDS);

	// Create randomly-placed healthy people
	reset();
    }

    private Field getField() {
	return jalse.getEntityAsType(Field.ID, Field.class);
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
	// Infect clicked person(s)
	final Point point = e.getPoint();
	final int size = ZombiesProperties.getSize();
	getField().streamPeople().filter(p -> {
	    final Point pos = p.getPosition();
	    return (pos.x - point.x) * (pos.x - point.x) + (pos.y - point.y) * (pos.y - point.y) < size * size;
	}).forEach(p -> p.markAsType(Infected.class));
    }

    @Override
    public void mouseEntered(final MouseEvent e) {}

    @Override
    public void mouseExited(final MouseEvent e) {}

    @Override
    public void mousePressed(final MouseEvent e) {}

    @Override
    public void mouseReleased(final MouseEvent e) {}

    @Override
    protected void paintComponent(final Graphics g) {
	// Draw component as before
	super.paintComponent(g);

	// Draw people
	getField().streamPeople().forEach(p -> drawElement(g, p));

	// Sync (Linux fix)
	Toolkit.getDefaultToolkit().sync();
    }

    private Double randomAngle() {
	final Random rand = ThreadLocalRandom.current();
	return rand.nextDouble() * Math.PI * 2;
    }

    private Point randomPosition() {
	final int size = ZombiesProperties.getSize();
	final Random rand = ThreadLocalRandom.current();
	return new Point(size + rand.nextInt(WIDTH - 20), size + rand.nextInt(HEIGHT - 20));
    }

    private void removeRandomPerson() {
	final Field field = getField(); // TODO
	final Random rand = ThreadLocalRandom.current();

	// Try to remove a non-corpse first
	int currentCount = (int) field.streamEntities().filter(p -> !p.isMarkedAsType(Corpse.class)).count();
	if (currentCount > 0) {
	    final int randIndex = rand.nextInt(currentCount);
	    field.streamEntities().filter(p -> !p.isMarkedAsType(Corpse.class)).skip(randIndex).findFirst().get()
		    .kill();
	} else {
	    currentCount = (int) field.streamEntities().count();
	    final int randIndex = rand.nextInt(currentCount);
	    field.streamEntities().skip(randIndex).findFirst().get().kill();
	}
    }

    public void reset() {
	// Kill them all
	getField().killEntities();
	// Create randomly-placed healthy people
	final int population = ZombiesProperties.getPopulation();
	for (int i = 0; i < population; i++) {
	    addPersonAtRandomPosition();
	}
    }
}
