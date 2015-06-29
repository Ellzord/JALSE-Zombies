package zombies;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;

import zombies.entities.Healthy;
import zombies.entities.Infected;

@SuppressWarnings("serial")
public class ControlPanel extends JPanel {

    private static JLabel newLabel(final String text) {
	final JLabel label = new JLabel(text, SwingConstants.CENTER);
	label.setAlignmentX(Component.CENTER_ALIGNMENT);
	return label;
    }

    private static JSlider newSlider(final int min, final int max, final int ticks, final int initial,
	    final ChangeListener listener) {
	final JSlider slider = new JSlider(min, max, initial);
	slider.setMajorTickSpacing(ticks);
	slider.setPaintTicks(true);
	slider.setAlignmentX(Component.CENTER_ALIGNMENT);
	slider.addChangeListener(listener);
	return slider;
    }

    public ControlPanel(final ZombiesPanel zombiesPanel) {
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

	// Population
	add(newLabel("Population"));
	add(newSlider(0, 200, 50, ZombiesProperties.getPopulation(), e -> {
	    ZombiesProperties.setPopulation(((JSlider) e.getSource()).getValue());
	    zombiesPanel.adjustPopulation();
	}));
	add(Box.createVerticalGlue());

	// Infected speed
	add(newLabel("Infected Relative Speed (%)"));
	add(newSlider(50, 200, 50,
		(int) (100 * ZombiesProperties.getSpeed(Infected.class) / ZombiesProperties.getSpeed(Healthy.class)),
		e -> {
		    ZombiesProperties.setInfectedRelativeSpeed(((JSlider) e.getSource()).getValue());
		    zombiesPanel.adjustInfectedSpeed();
		}));
	add(Box.createVerticalGlue());

	// Healthy sight
	add(newLabel("Healthy Sight Range"));
	add(newSlider(0, 200, 50, ZombiesProperties.getSightRange(Healthy.class), e -> {
	    ZombiesProperties.setHealthySightRange(((JSlider) e.getSource()).getValue());
	    zombiesPanel.adjustSightRange(Healthy.class);
	}));
	add(Box.createVerticalGlue());

	// Infected sight
	add(newLabel("Infected Sight Range"));
	add(newSlider(0, 200, 50, ZombiesProperties.getSightRange(Infected.class), e -> {
	    ZombiesProperties.setInfectedSightRange(((JSlider) e.getSource()).getValue());
	    zombiesPanel.adjustSightRange(Infected.class);
	}));
	add(Box.createVerticalGlue());

	// Infection time
	add(newLabel("Infection Time (s.)"));
	add(newSlider(0, 30, 5, (int) ZombiesProperties.getInfectionTime(), e -> {
	    ZombiesProperties.setInfectionTime(((JSlider) e.getSource()).getValue());
	}));
	add(Box.createVerticalGlue());

	// Starvation time
	add(newLabel("Starvation Time (s.)"));
	add(newSlider(0, 30, 5, (int) ZombiesProperties.getStarveTime(), e -> {
	    ZombiesProperties.setStarveTime(((JSlider) e.getSource()).getValue());
	}));
	add(Box.createVerticalGlue());

	// Reset
	final JButton resetButton = new JButton("Reset Simulation");
	resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	resetButton.addActionListener(e -> zombiesPanel.reset());
	add(resetButton);
    }
}
