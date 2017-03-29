/*****************************************************************************
 * Copyright (c) 2010 CEA LIST.
 *
 *    
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Florian Noyrit (CEA LIST) florian.noyrit@cea.fr - Initial API and implementation
 *
 *****************************************************************************/

package org.eclipse.papyrus.example.utils;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * This class implements a simple simulator for the MixingMachine case.
 * The initial state for the MixingMachine is:
 * - left and right shutters are open
 * - needle position is 4 (Drain)
 * - needle is empty
 * - filter applied is NONE
 * 
 */
public class MixingMachineUtils {

	/**
	 * Duration to wait between each action
	 */
	private static final long STEP_TIME = 2000;

	/**
	 * Marker to know if the simulator has been initialized
	 */
	private static boolean init = false;

	/**
	 * Marker to decide if the simulator must be stopped.
	 */
	private static boolean stopIt = false;

	/**
	 * Indicates the needle's current position
	 */
	private static int position = 4;

	/**
	 * Marker to indicate if the left shutter is closed or not
	 */
	private static boolean leftShutterClosed = false;

	/**
	 * Marker to indicate if the right shutter is closed or not
	 */
	private static boolean rightShutterClosed = false;

	/**
	 * The mixingMachine simulatorPart
	 */
	private static MixingMachineComponent mixingMachineComponent;

	/**
	 * Marker to decide if simulator must be refreshed
	 */
	private static boolean needsRefresh = false;

	/**
	 * Marker to decide the filter applied on the needle
	 */
	private static FilterKind filterApplied = FilterKind.NONE;

	/**
	 * Size of the window
	 */
	private static int windowWidth = 500;

	private static int windowHeight = 500;

	/**
	 * Marker to decide which action is to be done
	 */
	private static ActionKind action = ActionKind.None;

	/**
	 * The amount sucked or blowed by the needle
	 */
	private static Double amountToDisplay = new Double("0");

	/**
	 * Internal enum to define the current action
	 */
	private static enum ActionKind {
		Suck, Blow, None
	}

	/**
	 * Implementation of the simulator window
	 */
	private static class MixingMachineWindow extends JFrame {

		private static final long serialVersionUID = 1L;

		private JPanel simulatorPanel;

		private void addButton(Container c, String title, ActionListener a) {
			JButton b = new JButton(title);
			c.add(b);
			b.addActionListener(a);
		}

		public MixingMachineWindow() {
			super();
			setTitle("MixingMachine Simulator");
			setSize(windowWidth, windowHeight);
			setLocationRelativeTo(null);
			setResizable(false);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			Container contentPane = getContentPane();
			simulatorPanel = new JPanel();
			contentPane.add(simulatorPanel, "Center");
			JPanel buttonsPanel = new JPanel();
			mixingMachineComponent = new MixingMachineComponent(simulatorPanel);

			addButton(buttonsPanel, "Stop", new ActionListener() {

				public void actionPerformed(ActionEvent evt) {
					stopIt = true;
				}
			});

			addButton(buttonsPanel, "Close", new ActionListener() {

				public void actionPerformed(ActionEvent evt) {
					simulatorPanel.setVisible(false);
					System.exit(0);
				}
			});
			contentPane.add(buttonsPanel, "South");
		}
	}

	private static class MixingMachineComponent extends Thread {

		private int nbCups = 11;

		private int widthCups = windowWidth / (nbCups + 2);

		private int heightCups = 20;

		private int heightNeedle = 200;

		private int heightShutter = 400;

		private int YPositionNeedle = 150;

		private JPanel simulatorPanel;

		public MixingMachineComponent(JPanel simulatorPanel) {
			this.simulatorPanel = simulatorPanel;
		}

		/**
		 * Draw the cups
		 */
		private void drawPlatform() {
			Graphics g = simulatorPanel.getGraphics();
			for (int i = 0; i < nbCups; i++) {
				g.drawRect(widthCups + (i * widthCups),
						simulatorPanel.getHeight() - heightCups, widthCups,
						heightCups);
				String name = "";
				if (i == 3) {
					name += "C";
				} else if (i == 4) {
					name += "D";
				} else if (i == 10) {
					name += "O";
				} else if (i >= 5 && i <= 9) {
					name += "V" + (i + 1);
				} else {
					name += i + 1;
				}

				g.drawString(name, widthCups + (i * widthCups) + widthCups / 2,
						simulatorPanel.getHeight() - (heightCups / 2));
			}
		}

		/**
		 * Draw the needle
		 */
		private void drawNeedle() {
			Graphics g = simulatorPanel.getGraphics();
			if (action == ActionKind.Blow) {
				g.setColor(Color.GREEN);
			} else if (action == ActionKind.Suck) {
				g.setColor(Color.RED);
			} else {
				g.setColor(Color.BLACK);
			}
			g.fillRect(widthCups + (position * widthCups), YPositionNeedle,
					widthCups, heightNeedle);
			g.setColor(Color.BLACK);
			g.drawString("" + amountToDisplay, widthCups
					+ (position * widthCups) + (widthCups / 2), YPositionNeedle
					+ (heightNeedle / 2));
		}

		/**
		 * Draw the two shutters
		 */
		private void drawShutters() {
			Graphics g = simulatorPanel.getGraphics();
			if (leftShutterClosed == true) {
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, widthCups, heightShutter);
			} else {
				g.drawRect(0, 0, widthCups, heightShutter);
			}

			if (rightShutterClosed == true) {
				g.setColor(Color.BLACK);
				g.fillRect(windowWidth - widthCups, 0, widthCups, heightShutter);
			} else {
				g.drawRect(windowWidth - widthCups, 0, widthCups, heightShutter);
			}
		}

		/**
		 * Draw the filter
		 */
		private void drawFilter() {
			Graphics g = simulatorPanel.getGraphics();
			g.drawRect(widthCups + (position * widthCups), YPositionNeedle
					+ heightNeedle, widthCups, heightCups);
			String filterText = "";
			if (filterApplied == FilterKind.A) {
				filterText += "A";
			} else if (filterApplied == FilterKind.B) {
				filterText += "B";
			} else {
				filterText += "None";
			}
			g.drawString(filterText, widthCups + (position * widthCups),
					YPositionNeedle + heightNeedle + (heightCups / 2));
		}

		/**
		 * Animate
		 */
		private void moveIt() {
			Graphics g = simulatorPanel.getGraphics();
			//If needed: clear
			if (needsRefresh) {
				g.clearRect(simulatorPanel.getX(), simulatorPanel.getY(),
						simulatorPanel.getWidth(), simulatorPanel.getHeight()
								- heightCups);
				needsRefresh = false;
			}

			drawPlatform();
			drawNeedle();
			drawShutters();
			drawFilter();
		}

		/**
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			try {
				drawPlatform();
				while (!stopIt) {
					//Animate
					moveIt();
					//Otherwise the CPU would be overloaded
					sleep(10);
				}
			} catch (InterruptedException e) {
			}

		}
	}

	/**
	 * Initialize and show the simultor
	 */
	private static void initilization() {
		MixingMachineWindow window = new MixingMachineWindow();
		window.setVisible(true);

		try {
			Thread.sleep(STEP_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		mixingMachineComponent.start();
		init = true;
	}

	/**
	 * Sleep for a while
	 */
	private static void sleep() {
		try {
			Thread.sleep(STEP_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Enumeration for the two shutter : Left and Right shutters
	 */
	public static enum ShutterKind {
		Left, Right
	};

	/**
	 * Enumeration for the three filters available : A, B or None
	 */
	public static enum FilterKind {
		A, B, NONE
	};

	/**
	 * Opens the shutter specified
	 * 
	 * @param shutter
	 *        The shutter to open
	 */
	public static void open(ShutterKind shutter) {
		if (init == false) {
			initilization();
		}

		if (shutter == ShutterKind.Left) {
			leftShutterClosed = false;
		} else {
			rightShutterClosed = false;
		}

		action = ActionKind.None;
		needsRefresh = true;

		sleep();
	}

	/**
	 * Shuts the shutter specified
	 * 
	 * @param shutter
	 *        The shutter to shut
	 */
	public static void shut(ShutterKind shutter) {
		if (init == false) {
			initilization();
		}

		if (shutter == ShutterKind.Left) {
			leftShutterClosed = true;
		} else {
			rightShutterClosed = true;
		}

		action = ActionKind.None;
		needsRefresh = true;

		sleep();
	}

	/**
	 * Suck a certain amount
	 * 
	 * @param amount
	 *        The amount to suck
	 */
	public static void suck(Double amount) {
		if (init == false) {
			initilization();
		}

		action = ActionKind.Suck;
		amountToDisplay = amount;
		needsRefresh = true;

		sleep();
	}

	/**
	 * Blow a certain amount
	 * 
	 * @param amount
	 *        The amount to blow
	 */
	public static void blow(Double amount) {
		if (init == false) {
			initilization();
		}

		action = ActionKind.Blow;
		amountToDisplay = amount;
		needsRefresh = true;

		sleep();
	}

	/**
	 * Specify the filter to apply on the needle
	 * 
	 * @param filter
	 *        The filter to apply
	 */
	public static void filter(FilterKind filter) {
		if (init == false) {
			initilization();
		}

		action = ActionKind.None;

		filterApplied = filter;

		needsRefresh = true;

		sleep();
	}

	/**
	 * Move the needle to another position
	 * 
	 * @param offset
	 *        The offset to apply for the move
	 */
	public static void move(Integer offset) {
		if (init == false) {
			initilization();
		}

		action = ActionKind.None;
		position += offset;

		needsRefresh = true;

		sleep();
	}
}
