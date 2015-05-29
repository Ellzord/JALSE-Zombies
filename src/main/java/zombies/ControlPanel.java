package zombies;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class ControlPanel extends JPanel {
	private ZombiesPanel zombiesPanel;

	private class ControlSlider extends JSlider {
		ControlSlider(int min, int max, int ticks, int initial,
				String methodName) {
			super(min, max, initial);
			setMajorTickSpacing(ticks);
			setPaintLabels(true);
			setAlignmentX(Component.CENTER_ALIGNMENT);
			Method method;
			try {
				method = ZombiesPanel.class
						.getMethod(methodName, Integer.class);
				addChangeListener(new ControlChangeListener(method));
			} catch (NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}
	}

	private class ControlLabel extends JLabel {
		ControlLabel(String label) {
			super(label, SwingConstants.CENTER);
			setAlignmentX(Component.CENTER_ALIGNMENT);
		}
	}

	private class ControlButton extends JButton {
		ControlButton(String label) {
			super(label);
			setAlignmentX(Component.CENTER_ALIGNMENT);
			addActionListener(new ResetListener());
		}
	}

	private class ControlChangeListener implements ChangeListener {
		private Method changeMethod;

		ControlChangeListener(Method method) {
			this.changeMethod = method;
		}

		@Override
		public void stateChanged(ChangeEvent event) {
			JSlider source = (JSlider) event.getSource();

			if (!source.getValueIsAdjusting()) {
				try {
					changeMethod.invoke(zombiesPanel,
							new Object[] { (int) source.getValue() });
				} catch (IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class ResetListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			zombiesPanel.reset();
		}
	}

	public ControlPanel(ZombiesPanel zombiesPanel) {
		this.zombiesPanel = zombiesPanel;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(new ControlLabel("Population"));
		add(new ControlSlider(0, 200, 50, 100, "setPopulation"));
		add(Box.createVerticalGlue());

		add(new ControlLabel("Infected Relative Speed (%)"));
		add(new ControlSlider(
				50,
				200,
				50,
				(int) (100 * PersonProperties.Infected.SPEED / PersonProperties.Healthy.SPEED),
				"setInfectedRelativeSpeed"));
		add(Box.createVerticalGlue());

		add(new ControlLabel("Healthy Sight Range"));
		add(new ControlSlider(0, 200, 50, PersonProperties.Healthy.SIGHT_RANGE,
				"setHealthySightRange"));
		add(Box.createVerticalGlue());

		add(new ControlLabel("Infected Sight Range"));
		add(new ControlSlider(0, 200, 50,
				PersonProperties.Infected.SIGHT_RANGE, "setInfectedSightRange"));
		add(Box.createVerticalGlue());

		add(new ControlLabel("Infection Time (s.)"));
		add(new ControlSlider(0, 30, 5,
				(int) PersonProperties.INFECTION_TIME_SECONDS,
				"setInfectionTime"));
		add(Box.createVerticalGlue());

		add(new ControlLabel("Starvation Time (s.)"));
		add(new ControlSlider(0, 30, 5,
				(int) PersonProperties.STARVE_TIME_SECONDS, "setStarveTime"));
		add(Box.createVerticalGlue());

		add(new ControlButton("Reset Simulation"));
	}
}
