package org.comtel.javafx;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Locale;

import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.util.Duration;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import org.comtel.javafx.control.KeyBoardPopup;
import org.comtel.javafx.control.KeyBoardPopupBuilder;
import org.comtel.javafx.robot.RobotFactory;

public class SwingMainDemo extends JApplet {

	private static final long serialVersionUID = 1L;

	private KeyBoardPopup fxKeyboardPopup;
	private Transition transition;

	public SwingMainDemo() {
	}

	public void init() {
		// create swing table

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());

		JTextField tf = new JTextField(50);
		tf.addFocusListener(new FocusListener() {

			public void focusLost(FocusEvent e) {
				setKeyboardVisible(false, null);

			}

			public void focusGained(FocusEvent e) {
				setKeyboardVisible(true, e.getComponent().getLocationOnScreen());

			}
		});

		JTextField tf2 = new JTextField(50);
		tf2.addFocusListener(new FocusListener() {

			public void focusLost(FocusEvent e) {
				setKeyboardVisible(false, null);

			}

			public void focusGained(FocusEvent e) {
				setKeyboardVisible(true, e.getComponent().getLocationOnScreen());

			}
		});

		panel.add(tf);
		panel.add(tf2);
		panel.add(new JButton("Ok"));
		panel.add(new JButton("Cancel"));
		panel.setPreferredSize(new Dimension(800, 400));
		setLayout(new BorderLayout());
		add(panel, BorderLayout.SOUTH);

		// create javafx panel
		final JFXPanel javafxPanel = new JFXPanel();
		javafxPanel.setFocusable(false);
		javafxPanel.setOpaque(false);

		JWindow fxKeyboard = new JWindow();
		fxKeyboard.setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
		fxKeyboard.getContentPane().add(javafxPanel);
		fxKeyboard.setFocusable(false);
		fxKeyboard.setBackground(null);
		// fxKeyboard.pack();
		// fxKeyboard.setLocationByPlatform(true);

		// fxKeyboard.setVisible(false);

		// create JavaFX scene
		Platform.runLater(new Runnable() {
			public void run() {
				createScene(javafxPanel);
				System.err.println("JavaFX: " + System.getProperty("javafx.runtime.version"));
			}
		});

	}

	public void createScene(JFXPanel javafxPanel) {

		String css = this.getClass().getResource("/css/KeyboardButtonStyle.css").toExternalForm();

		// create empty scene
		Scene scene = new Scene(new Group(), 0, 0);
		javafxPanel.setScene(scene);
		scene.getStylesheets().add(css);
		fxKeyboardPopup = KeyBoardPopupBuilder.create().initLocale(Locale.ENGLISH)
				.addIRobot(RobotFactory.createAWTRobot()).build();
		fxKeyboardPopup.getKeyBoard().setOnKeyboardCloseButton(new EventHandler<Event>() {
			public void handle(Event event) {
				setKeyboardVisible(false, null);
			}
		});
		fxKeyboardPopup.show(scene.getWindow());
	}

	public void setKeyboardVisible(boolean flag, Point point) {
		final boolean visible = flag;
		final Point location = point;
		Platform.runLater(new Runnable() {
			public void run() {
				if (fxKeyboardPopup == null) {
					return;
				}
				if (location != null) {
					fxKeyboardPopup.setX(location.getX());
					fxKeyboardPopup.setY(location.getY() + 20);
				}

				if (transition == null) {
					// transition = new FadeTransition(Duration.millis(200),
					// fxKeyboard);
					transition = new ScaleTransition(Duration.millis(200), fxKeyboardPopup.getKeyBoard());
					transition.setCycleCount(1);
					transition.setAutoReverse(false);
				}
				if (visible) {
					// fxKeyboardPopup.show(fxKeyboardPopup.getOwnerWindow());
					System.err.println("fade in");
					transition.stop();

					((ScaleTransition) transition).setFromX(0.0d);
					((ScaleTransition) transition).setFromY(0.0d);
					((ScaleTransition) transition).setToX(fxKeyboardPopup.getKeyBoard().getScale());
					((ScaleTransition) transition).setToY(fxKeyboardPopup.getKeyBoard().getScale());

					// ((FadeTransition) transition).setFromValue(0.0f);
					// ((FadeTransition) transition).setToValue(1.0f);
					transition.play();

				} else {
					System.err.println("fade out");
					transition.stop();
					((ScaleTransition) transition).setFromX(fxKeyboardPopup.getKeyBoard().getScale());
					((ScaleTransition) transition).setFromY(fxKeyboardPopup.getKeyBoard().getScale());
					((ScaleTransition) transition).setToX(0.0d);
					((ScaleTransition) transition).setToY(0.0d);

					// ((FadeTransition) transition).setFromValue(1.0f);
					// ((FadeTransition) transition).setToValue(0.0f);
					transition.play();

					// fxKeyboardPopup.hide();
				}
			}
		});
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				JFrame frame = new JFrame("Swing FX Keyboard");
				frame.setResizable(false);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				JApplet applet = new SwingMainDemo();
				applet.init();

				frame.setContentPane(applet.getContentPane());

				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);

				applet.start();
			}
		});
	}

}
