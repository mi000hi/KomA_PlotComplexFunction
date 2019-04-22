import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class Editor extends JFrame implements ActionListener, KeyListener {

	private Point zero, one; // zero and (1, 1) point of the coordinateSystem
	private Dimension paintableDimension; // square are to paint on
	private final int MARGIN = 50; // margin around paintableDimension
	private int coordinatelineDensity; // density of coordinate lines
	private double[] inputArea; // square input area
	private Gui parent;
	private JTextField functionInputField, colorInputField; // jtextfield to add a new function

	private ArrayList<String> functions = new ArrayList<>(); // arraylist storing functions on this graph
	private ArrayList<Color> colors = new ArrayList<>(); // arraylist storing colors of the functions

	private final Font FONT = new Font("Ubuntu", Font.PLAIN, 30); // font used for the title

	private BufferedImage backgroundImage;

	/* CONSTRUCTOR */

	/**
	 * constructor of the editor class, you can draw a function here that will be
	 * drawn after applying f(z) on leinwand2d
	 * 
	 * @param frameName title of this jframe
	 * @param parent    parent gui so we can get important information
	 */
	public Editor(String frameName, Gui parent) {

		super(frameName);

		this.parent = parent;

		// jframe settings
		this.setSize(new Dimension(1500, 1500));
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		inputArea = parent.getInputAreaSquare();
		coordinatelineDensity = parent.getCoordinatelineDensity();

		// add graphing and settings panel
		addPanels();

	}

	/**
	 * adds a graphing and a settings panel to this jframe
	 */
	public void addPanels() {

		// graphing panel
		JPanel graph = new JPanel() {
			public void paintComponent(Graphics g) {

				super.paintComponent(g);

				g.setFont(FONT);
				inputArea = parent.getInputAreaSquare();

				// calculate square that we can paint on
				int smallerLength = Math.min(this.getSize().width - MARGIN, this.getSize().height - MARGIN);
				paintableDimension = new Dimension(smallerLength, smallerLength);

				// set the zero point accordingly
				zero = new Point(this.getSize().width / 2, this.getSize().height / 2);

				// draw the coordinate System with its labels
				drawCoordinateSystem(g);

				// draw functions
				drawFunctions(g);

			}
		};
		graph.setOpaque(true);
		graph.setBackground(Color.black);

		// settings panel
		JPanel settings = new JPanel();

		JLabel functionInputLabel = new JLabel("add function: y(x) = ");
		functionInputLabel.setFont(FONT);

		functionInputField = new JTextField();
		functionInputField.setFont(FONT);
		functionInputField.setSize(1 * this.getSize().width / 6, 50);
		functionInputField.setPreferredSize(functionInputField.getSize());
		functionInputField.addKeyListener(this);

		colorInputField = new JTextField("255, 255, 100");
		colorInputField.setFont(FONT);
		colorInputField.setSize(1 * this.getSize().width / 6, 50);
		colorInputField.setPreferredSize(functionInputField.getSize());
		colorInputField.addKeyListener(this);

		JButton add = new JButton("add");
		add.setFont(FONT);
		add.addActionListener(this);

		JButton remove = new JButton("remove last function");
		remove.setFont(FONT);
		remove.addActionListener(this);

		settings.add(functionInputLabel, BorderLayout.LINE_START);
		settings.add(functionInputField, BorderLayout.CENTER);
		settings.add(colorInputField, BorderLayout.CENTER);
		settings.add(add, BorderLayout.LINE_END);
		settings.add(remove, BorderLayout.LINE_END);

		// add panels to jframe
		this.add(graph, BorderLayout.CENTER);
		this.add(settings, BorderLayout.PAGE_END);

	}

	/**
	 * Draws the coordinate system of the complex number area with coordiante lines
	 * and labels
	 * 
	 * @param g using Graphics to draw on the JPanel
	 */
	private void drawCoordinateSystem(Graphics g) {

		int numberOfLines;
		int lineSpace; // space between two lines
		Point lineStart, lineEnd; // temporary start and end on-screen-position of the lines

		// draw vertical lines x = constant
		numberOfLines = (int) ((Math.abs(inputArea[0]) + Math.abs(inputArea[1])));
		lineSpace = paintableDimension.width / numberOfLines;
		one = new Point(lineSpace, lineSpace);
		for (int x = (int) inputArea[0]; x <= inputArea[1]; x += coordinatelineDensity) {

//			System.out.println("x = " + x);
			// define line location
			lineStart = getPointAt(x, inputArea[2]);
			lineEnd = getPointAt(x, inputArea[3]);

			// draw line
			g.setColor(Color.GRAY);
			g.drawLine(lineStart.x, lineStart.y, lineEnd.x, lineEnd.y);

			// draw linelabel
			g.setColor(Color.WHITE);
			g.drawString(Integer.toString(x), zero.x + x * lineSpace + 5, zero.y - 5);
		}

		// draw horizontal lines y = constant

		lineSpace = paintableDimension.height / numberOfLines;
		numberOfLines = (int) ((Math.abs(inputArea[2]) + Math.abs(inputArea[3])) / coordinatelineDensity);
		for (int y = (int) inputArea[2]; y <= inputArea[3]; y += coordinatelineDensity) {

			// define line location
			lineStart = getPointAt(inputArea[0], y);
			lineEnd = getPointAt(inputArea[1], y);

			// draw line
			g.setColor(Color.GRAY);
			g.drawLine(lineStart.x, lineStart.y, lineEnd.x, lineEnd.y);

			// draw line label
			g.setColor(Color.WHITE);
			g.drawString(Integer.toString(y), zero.x + 5, lineStart.y - 5);
		}

		// draw x = 0 and y = 0 lines again but thicker
		g.setColor(Color.white);
		g.fillRect(zero.x - 1, zero.y - paintableDimension.height / 2, 3, paintableDimension.height);
		g.fillRect(zero.x - paintableDimension.width / 2, zero.y - 1, paintableDimension.width, 3);

	}

	/**
	 * @param g using graphics to directly draw on the graphing jpanel
	 */
	private void drawFunctions(Graphics g) {

		Complex functionValue; // = y(x)
		Point currentFunctionPoint; // on screen location for y(x)

		for (int i = 0; i < functions.size(); i++) {

			g.setColor(colors.get(i));

			// draw function point for each x value
			for (double x = inputArea[0]; x <= inputArea[1]; x += 1.0 / paintableDimension.width) {

				functionValue = parent.calculate(new Complex(x, 0, true), functions.get(i));
				currentFunctionPoint = getPointAt(x, functionValue.getRe());
				g.fillRect(currentFunctionPoint.x - 1, currentFunctionPoint.y - 1, 3, 3);

			}

		}

	}

	/**
	 * gets the function from the input textfield and saves in a arraylist together
	 * with its color
	 */
	private void readAndAddFunction() {

		functions.add(functionInputField.getText().replaceAll("\\s", "").replaceAll("x", "z"));

		// read color
		// TODO: colors as they are in leinwand2d, so based on x input value
		String[] color = colorInputField.getText().replaceAll("\\s", "").split(",");
		colors.add(new Color(Integer.parseInt(color[0]), Integer.parseInt(color[1]), Integer.parseInt(color[2])));

		this.repaint();

	}

	/* GETTERS */

	/**
	 * returns the screen coordinates of a given point (x, y)
	 * 
	 * @param x location x value according to coordinate system
	 * @param y location y value according to coordinate system
	 * @return Point with on-screen-coordinates
	 */
	private Point getPointAt(double x, double y) {

		// invert y axis and return screenPosition of (x, y)
		return new Point((int) (x * one.x + zero.x), (int) ((-1) * y * one.y + zero.y));

	}

	/**
	 * @return the rendered bufferedimage to use as background image
	 */
	public BufferedImage getBackgroundImage() {

		if (paintableDimension == null) {
			return null;
		}
		backgroundImage = new BufferedImage(paintableDimension.width, paintableDimension.height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = backgroundImage.createGraphics();

		g.setColor(new Color(0, 0, 0, 0));
		g.fillRect(0, 0, paintableDimension.width, paintableDimension.height);

		zero = new Point(paintableDimension.width / 2, paintableDimension.height / 2);
		int numberOfLines = (int) ((Math.abs(inputArea[0]) + Math.abs(inputArea[1])));
		int lineSpace = paintableDimension.width / numberOfLines;
		one = new Point(lineSpace, lineSpace);

		drawFunctions(g);

		g.dispose();

		return backgroundImage;

	}
	
	@Override
	public void actionPerformed(ActionEvent e) {

//		System.out.println("EDITOR: \t \"" + e.getActionCommand() + "\" clicked");

		switch (e.getActionCommand()) {

		case "add":
			readAndAddFunction();
			break;

		case "remove last function":
			if (functions.size() > 0) {
				functions.remove(functions.size() - 1);
				colors.remove(colors.size() - 1);
				repaint();
			}
			break;

		}

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {

//		System.out.println("EDITOR: \t \"" + e.getKeyCode() + "\" pressed");

		switch (e.getKeyCode()) {

		case 10: // ENTER
			readAndAddFunction();
			break;

		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
