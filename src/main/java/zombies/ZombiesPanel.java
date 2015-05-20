package zombies;

import static jalse.JALSEBuilder.buildManualJALSE;
import jalse.JALSE;
import jalse.attributes.Attributes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import javax.swing.JPanel;
import javax.swing.Timer;

import zombies.actions.MovePeople;
import zombies.entities.Field;
import zombies.entities.Healthy;
import zombies.entities.Infected;
import zombies.entities.Person;
import zombies.listeners.Infect;
import zombies.listeners.InfectionFractionListener;

@SuppressWarnings("serial")
public class ZombiesPanel extends JPanel implements ActionListener,
		MouseListener {
	private static final Color BACKGROUND_COLOR = Color.BLACK;

	public static final int WIDTH = 700;
	public static final int HEIGHT = 500;

	private static void drawElement(final Graphics g, final Person person) {
		final Point position = person.getPosition();
		final int size = Person.SIZE;
		g.setColor(person.getColor());
		g.fillOval(position.x, position.y, size, size);
		g.setColor(BACKGROUND_COLOR);
		// g.fillRect(position.x, position.y, size.width, size.height);
	}

	private final JALSE jalse;

	public ZombiesPanel() {
		// Manually ticked JALSE
		jalse = buildManualJALSE();
		// Create data model
		createEntities();
		// Size to field size
		setPreferredSize(getField().getSize());
		// Set black background
		setBackground(BACKGROUND_COLOR);
		// Listener for key events
		setFocusable(true);
		;
		addMouseListener(this);
		// Start ticking and rendering (30 FPS)
		new Timer(1000 / 30, this).start();
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		jalse.resume();

		// Request repaint
		repaint();
	}

	private void createEntities() {
		// Create field
		final Field field = jalse.newEntity(Field.ID, Field.class);
		field.setSize(new Dimension(WIDTH, HEIGHT));
		field.scheduleForActor(new MovePeople(), 0, 1000 / 30,
				TimeUnit.MILLISECONDS);
		field.addEntityListener(new Infect());

		// Create healthy people
		for (int i = 0; i < 100; i++) {
			final Person person = field.newEntity(UUID.randomUUID(),
					Person.class);
			person.setPosition(randomPosition());
			person.setAngle(randomAngle());
			person.markAsType(Healthy.class);
			person.addAttributeListener("infectionFraction",
					Attributes.DOUBLE_TYPE, new InfectionFractionListener());
		}
	}

	private Field getField() {
		return jalse.getEntityAsType(Field.ID, Field.class);
	}

	@Override
	protected void paintComponent(final Graphics g) {
		// Draw component as before
		super.paintComponent(g);

		// Field data
		final Field field = getField();

		// Set background color
		g.setColor(BACKGROUND_COLOR);

		// Draw people
		field.streamPeople().forEach(p -> drawElement(g, p));

		// Clean up
		g.dispose();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// Infect clicked person(s)
		Point point = e.getPoint();
		getField()
				.streamPeople()
				.filter(p -> {
					Point pos = p.getPosition();
					return ((pos.x - point.x) * (pos.x - point.x) + (pos.y - point.y)
							* (pos.y - point.y)) < Person.SIZE * Person.SIZE;
				}).forEach(p -> p.markAsType(Infected.class));
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	private Point randomPosition() {
		final Random rand = ThreadLocalRandom.current();
		return new Point(Person.SIZE + rand.nextInt(WIDTH - 20), Person.SIZE
				+ rand.nextInt(HEIGHT - 20));
	}

	private Double randomAngle() {
		final Random rand = ThreadLocalRandom.current();
		return rand.nextDouble() * Math.PI * 2;
	}
}
