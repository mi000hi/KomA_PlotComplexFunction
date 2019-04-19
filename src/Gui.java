import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Gui extends JFrame implements ActionListener, KeyListener {

	// Gui elements that need to be a public variable
	private SettingsGui2 settingsFrame; // jframe to manage plot settings
	private JButton applySettingsButton = new JButton("apply"); // apply button on settingsFrame
	private final Font BUTTON_FONT = new Font("Ubuntu", Font.PLAIN, 40); // font on jbutton
	private JTextField functionInputField = new JTextField(); // input field for function on settingsFrame

	private Leinwand2D locationCanvas; // 2d plot of new location of f(z)
	private Leinwand3D realPartCanvas; // 3d plot of real part
	private Leinwand3D imaginaryPartCanvas; // 3d plot of imaginary part
	private Leinwand3D argumentCanvas; // 3d plot of argument
	private Leinwand3D radiusCanvas; // 3d plot of abs(z)

	private JLabel titleLabel; // top label of the jframe, showing the function name

	// variables that are needed for plotting
	private String function; // the function to calculate as a string
	private double[] inputArea; // input definition area for the function
	private int[] outputArea; // output area for the 2D canvas
	private ArrayList<Complex> functionInputPoints, functionOutputPoints; // resulting input and output locations

	private double secretNumber = -0.96875; // 0.11111 in binary TODO: remove?
	private Point numberOfPoints; // number of points in x and y axis that have been calculated

	private int calculationDensity, paintDensity; // will calculate/paint *Density^2 points in a 1x1 unit square
	private int coordinateLineDensity; // draws one coordinateLine every coordinateLineDensity units
	private int dotWidth, circleWidth; // width of dots in 2D plot and width of circles in 3D plot
	private boolean paintLocationDots, paintHorizontalLines, paintVerticalLines; // whether to paint these things or not
																					// on 2D canvas
	/* MAIN FUNCTION */

	/**
	 * will creaate a jframe of this class with the panels to plot the complex
	 * function functionOutputPoints.add(new Complex(pointsInX, pointsInY, true));
	 * 
	 * @param args arguments from command line at startup, will be ignored
	 */
	public static void main(String[] args) {

		// first shown function is identity f(z) = z
		new Gui("z");

	}

	/* CONSTRUCTOR */
	/**
	 * builds this jframe with all panels and plots a first function
	 * 
	 * @param function the first function that will be plotted
	 */
	public Gui(String function) {

		// save given variables
		this.function = function;

		// setup this jframe
		this.setTitle("KomA_Uebungsserie07 || plot complex functions || by Michael Roth || 3.4.2019");
		this.setSize(new Dimension(2000, 1100));
		this.setBackground(Color.BLACK);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		outputArea = new int[] { -4, 4, -4, 4 }; // TODO: remove?

		functionInputPoints = new ArrayList<Complex>();
		functionOutputPoints = new ArrayList<Complex>();

		// creating the different jpanels to draw on
		locationCanvas = new Leinwand2D(this);
		implement3DCanvas();

		// prepare for plotting
		setPlotSettings();
		calculateFunctionPoints(calculationDensity);

		settingsFrame = new SettingsGui2(this, new Dimension(1000, 1000), function, functionInputField,
				applySettingsButton);

		// plot calculated first function
		setupCanvas();

		// add gui to this jframe
		addGuiElements(this);

		// add listeners
		applySettingsButton.addActionListener(this);
		functionInputField.addKeyListener(this);

		useNewSettings();
		
		// show jframe
		this.setVisible(true);

	}

	/**
	 * implements the 4 3D canvas and overrides their getFunctionValue function so
	 * that they plot what they should plot
	 */
	private void implement3DCanvas() {

		realPartCanvas = new Leinwand3D(this) {
			protected double getFunctionValue(int index) {

				Complex complex = functionOutputPoints.get(index);

				if (complex != null) {
					return complex.getRe();
				}
				
				System.out.println("returning secret number");
				return secretNumber;

			}
		};
		imaginaryPartCanvas = new Leinwand3D(this) {
			protected double getFunctionValue(int index) {

			Complex complex = functionOutputPoints.get(index);

			if (complex != null) {
				return complex.getIm();
			}
			
			System.out.println("returning secret number");
			return secretNumber;

		}
		};
		radiusCanvas = new Leinwand3D(this) {
			protected double getFunctionValue(int index) {

			Complex complex = functionOutputPoints.get(index);

			if (complex != null) {
				return complex.getRadius();
			}
			
			System.out.println("returning secret number");
			return secretNumber;

		}
		};
		argumentCanvas = new Leinwand3D(this) {
			protected double getFunctionValue(int index) {

			Complex complex = functionOutputPoints.get(index);

			if (complex != null) {
				return complex.getPhi();
			}
			
			System.out.println("returning secret number");
			return secretNumber;

		}
		};

	}

	/**
	 * gives the needed variables to the different canvas so they can plot when
	 * *.repaint(); is called
	 */
	private void setupCanvas() {

		// TODO: optimize
		long time = System.currentTimeMillis(); // measure time to see if this is very inefficient

		if (outputArea == null) {
			outputArea = getOutputAreaSquare();
			settingsFrame.setOutputArea(outputArea);
		}

		realPartCanvas.setFunctionValues(functionInputPoints, functionOutputPoints);

		imaginaryPartCanvas.setFunctionValues(functionInputPoints, functionOutputPoints);

		radiusCanvas.setFunctionValues(functionInputPoints, functionOutputPoints);

		argumentCanvas.setFunctionValues(functionInputPoints, functionOutputPoints);

		// calculate functionPoints for 2D canvas
		calculateFunctionPoints(paintDensity);

		// this will also set inputArea and outputArea of locationCanvas
		locationCanvas.setFunctionValues(functionInputPoints, functionOutputPoints);

		// set jpanel sizes
		Dimension smallCanvas = new Dimension(this.getSize().width / 4, (this.getSize().height - 100) / 2);
//		locationCanvas.setSize(this.getSize().width / 2, this.getSize().height - 100);
		realPartCanvas.setSize(smallCanvas);
		imaginaryPartCanvas.setSize(smallCanvas);
		radiusCanvas.setSize(smallCanvas);
		argumentCanvas.setSize(smallCanvas);

		// set titles
		realPartCanvas.setTitle("Re( " + function + " )");
		imaginaryPartCanvas.setTitle("Im( " + function + " )");
		radiusCanvas.setTitle("| " + function + " |");
		argumentCanvas.setTitle("Arg( " + function + " )");

		locationCanvas.setPlotSettings(paintDensity, coordinateLineDensity, dotWidth, paintLocationDots,
				paintHorizontalLines, paintVerticalLines);

		// plotting 3D plots as a grid
//		realPartCanvas.setPlotSettings(dotWidth, false, true);
//		imaginaryPartCanvas.setPlotSettings(dotWidth, false, true);
//		radiusCanvas.setPlotSettings(dotWidth, false, true);
//		argumentCanvas.setPlotSettings(dotWidth, false, true);

		// plotting 3D plots with points
		realPartCanvas.setPlotSettings(circleWidth, true, false);
		imaginaryPartCanvas.setPlotSettings(circleWidth, true, false);
		radiusCanvas.setPlotSettings(circleWidth, true, false);
		argumentCanvas.setPlotSettings(circleWidth, true, false);

		System.out.println("SETUPCANVAS: \t distributing values took " + (System.currentTimeMillis() - time) + "ms");

	}

	/**
	 * repaints the 5 canvas
	 */
	private void repaintCanvas() {

		locationCanvas.repaint();
		realPartCanvas.repaint();
//		imaginaryPartCanvas.repaint();
//		radiusCanvas.repaint();
//		argumentCanvas.repaint();

	}

	/**
	 * adds the gui to the given jframe, here parentFrame == this
	 * 
	 * @param parentFrame jframe to containing new gui elements
	 */
	private void addGuiElements(JFrame parentFrame) {

		// top panel for the title
		JPanel titlePanel = new JPanel();
		titlePanel.setOpaque(true);
		titlePanel.setBackground(Color.BLACK);
//		titlePanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));

		titleLabel = new JLabel("f(z) = " + function);
		titleLabel.setFont(new Font("Ubuntu", Font.PLAIN, 50));
		titleLabel.setForeground(Color.WHITE);

		titlePanel.add(titleLabel);

		// big jpanel for the different canvas
		JPanel canvasPanel = new JPanel();
		canvasPanel.setLayout(new GridLayout(1, 2));
//		canvasPanel.setBorder(BorderFactory.createLineBorder(Color.RED));

		// jpanel for the 4 3D plots
		JPanel plot3DPanel = new JPanel();
		plot3DPanel.setLayout(new GridLayout(2, 2));
//		plot3DPanel.setBorder(BorderFactory.createLineBorder(Color.GREEN));

		plot3DPanel.add(realPartCanvas);
		plot3DPanel.add(imaginaryPartCanvas);
		plot3DPanel.add(radiusCanvas);
		plot3DPanel.add(argumentCanvas);

		canvasPanel.add(plot3DPanel);
		canvasPanel.add(locationCanvas);

		// bottom jbutton to access settingsFrame
		JButton settings = new JButton("settings");
		settings.setFont(BUTTON_FONT);
		settings.addActionListener(this);

		parentFrame.add(titlePanel, BorderLayout.PAGE_START);
		parentFrame.add(canvasPanel, BorderLayout.CENTER);
		parentFrame.add(settings, BorderLayout.PAGE_END);

	}

	/**
	 * sets or initializes settings to be able to plot the functions later on
	 * 
	 * @return true if we need to recalculate the function, false if we just can
	 *         adjust the visual part without recalculating the function
	 */
	private boolean setPlotSettings() {

		boolean needToRecalculateFunction = false; // will be returned

		// if we need to initialize because settingsFrame doesnt exist yet
		if (settingsFrame == null) {

			inputArea = new double[] { -2, 2, -2, 2 };

			calculationDensity = 50;
			paintDensity = 10;
			coordinateLineDensity = 1;
			dotWidth = 3;
			circleWidth = 5;
			paintLocationDots = false;
			paintHorizontalLines = true;
			paintVerticalLines = true;

			needToRecalculateFunction = true;

		} else { // set new variables as given in settingsFrame

			String newFunction = settingsFrame.getFunction();

			// adjust definition area
			double[] newInputArea = settingsFrame.getInputArea();
			outputArea = settingsFrame.getOutputArea();

			// adjust plot and visual settings
			int newCalculationDensity = settingsFrame.getCalculationDensity();
			paintDensity = settingsFrame.getDensity();
			coordinateLineDensity = settingsFrame.getCoordinatelineDensity();
			dotWidth = settingsFrame.getDotWidth();
			paintLocationDots = settingsFrame.paintFunctionPoints();
			paintHorizontalLines = settingsFrame.paintHorizontalLines();
			paintVerticalLines = settingsFrame.paintVerticalLines();

			// if something of these values changed, we need to recalculate the function
			if (newFunction != function || newInputArea != inputArea || newCalculationDensity != calculationDensity) {

				needToRecalculateFunction = true;

				function = newFunction;
				titleLabel.setText("f(z) = " + function);
				inputArea = newInputArea;
				calculationDensity = newCalculationDensity;

			}

		}

		return needToRecalculateFunction;

	}

	/**
	 * load new calculation and plot settings and recalculate function if needed
	 */
	private void useNewSettings() {

		if (setPlotSettings()) {
			calculateFunctionPoints(calculationDensity);
		}

		// give parameters to the canvas and repaint them
		setupCanvas();
		repaintCanvas();

	}

	/**
	 * for each point in inputArea on the density, get a corresponding functionvalue
	 * 
	 * @param calculationDensity the density to calulate the points
	 */
	public void calculateFunctionPoints(int calculationDensity) {

		Complex currentFunctionInput, currentFunctionOutput;
		functionInputPoints = new ArrayList<Complex>();
		functionOutputPoints = new ArrayList<Complex>();

		int pointsInX = 0, pointsInY = 0; // counts how many points are in x and y direction

//		System.out.println("calculateFunctionPoints()");

//		int xCounter = 0;
		for (double x = getFirstXValue(); x <= inputArea[1] + 1.0 / calculationDensity / 2; x += 1.0
				/ calculationDensity) {

			pointsInX++;
			pointsInY = 0;

//			xCounter++;
//			System.out.println("xCounter = " + xCounter);

			for (double y = inputArea[2]; y <= inputArea[3] + 1.0 / calculationDensity / 2; y += 1.0
					/ calculationDensity) {

				pointsInY++;

//				System.out.println("x = " + x + " y = " + y);

				// calculate function value f(z)
				currentFunctionInput = new Complex(x, y, true);
				currentFunctionOutput = calculate(currentFunctionInput, function);

				// if we couldnt calculate the current functionvalue, add a secret number so the
				// grid stays squared
				if (currentFunctionOutput != null) {
					functionInputPoints.add(currentFunctionInput);
					functionOutputPoints.add(currentFunctionOutput);
				} else { // TODO: remove if it works
					functionInputPoints.add(null);
					functionOutputPoints.add(null);
				}

			}

		}

		numberOfPoints = new Point(pointsInX, pointsInY);

	}

	/**
	 * calculates f(z) with the given z, this function is recursive!
	 * 
	 * @param z        value of z in the given function, null if we divided by
	 *                 something too small
	 * @param function contains the function
	 * @param return   returns the resulting value = function(z)
	 */
	private Complex calculate(Complex z, String function) {

//	System.out.println("to calculate: " + input);

		// test if a previous calculation returned null
		if (z == null) {
			return null;
		}

		Complex result = new Complex(0, 0, true); // will be returned
		int openBrackets = 0; // counts the open brackets while we read through the string
		boolean bracketsRemoved; // true if theres was nothing to do but removing brackets at start and end

		do {

			bracketsRemoved = false;

			// for each character in function, look for + and -
			for (int i = 0; i < function.length(); i++) {
				// System.out.println(input.charAt(i) + " found");
				switch (function.charAt(i)) {

				case '(':
					openBrackets++;
					break;

				case ')':
					openBrackets--;
					break;

				case '+':
					if (openBrackets == 0) {

						Complex number = calculate(z, function.substring(0, i));

						if (number == null) {
							return null;
						}

						result = number.add(calculate(z, function.substring(i + 1, function.length())));
						return result;
					}
					break;

				case '-':
					if (openBrackets == 0) {

						Complex number = calculate(z, function.substring(0, i));

						if (number == null) {
							return null;
						}

						result = number.subtract(calculate(z, function.substring(i + 1, function.length())));
						return result;
					}
					break;

				}

			}

			// if there wasnt a + or -, for each character in function, look for * and /
			for (int i = function.length() - 1; i >= 0; i--) {
				switch (function.charAt(i)) {

				case '(':
					openBrackets++;
					break;

				case ')':
					openBrackets--;
					break;

				case '*':
					if (openBrackets == 0) {
						Complex number = calculate(z, function.substring(0, i));

						if (number == null) {
							return null;
						}

						result = calculate(z, function.substring(0, i))
								.multiply(calculate(z, function.substring(i + 1, function.length())));
						return result;
					}
					break;

				case '/':
					if (openBrackets == 0) {
						Complex numerator = calculate(z, function.substring(0, i));
						Complex denumerator = calculate(z, function.substring(i + 1, function.length()));

						if (numerator == null) {
							return null;
						}

						result = numerator.divide(denumerator);

						return result;
					}
					break;

				}
			}

			// if there wasnt a * or /, for each character in function, look for sin, cos
			// and exp
			for (int i = 0; i < function.length(); i++) {
				switch (function.charAt(i)) {

				case '(':
					openBrackets++;
					break;

				case ')':
					openBrackets--;
					break;

				case 's':
					if (openBrackets == 0) {
						Complex argument = calculate(z, function.substring(i + 4, function.length() - 1));
						if (argument == null) {
							return null;
						}
						result = argument.sin();
						return result;
					}
					break;

				case 'c':
					if (openBrackets == 0) {
						Complex argument = calculate(z, function.substring(i + 4, function.length() - 1));
						if (argument == null) {
							return null;
						}
						result = argument.cos();
						return result;
					}
					break;

				case 'e':
					if (openBrackets == 0) {
						Complex argument = calculate(z, function.substring(i + 4, function.length() - 1));
						if (argument == null) {
							return null;
						}
						result = argument.exp();
						return result;
					}
					break;

				}
			}

			// if there was nothing to do, we need to remove brackets at start and end
			if (function.charAt(0) == '(' && function.charAt(function.length() - 1) == ')') {
//			System.out.println("removing brackets around " + input);
				function = function.substring(1, function.length() - 1);
				bracketsRemoved = true;
//			System.out.println("new input: " + input);
			}

		} while (bracketsRemoved);

		// if there also were no brackets to remove, we need to create a new complex
		// number
//	System.out.println("make a new Complex number from: " + input);
		switch (function) {

		case "z":
			return z;

		case "i":
			return new Complex(0, 1, true);

		default:
			System.out.println("CALCULATE: \t default switch case: should not be here");
			return new Complex(Double.parseDouble(function), 0, true);
		}

	}

	/**
	 * returns the first number that finds place on a gridline or a gridline +-
	 * factor / calculationDensity
	 * 
	 * @return smallest x value that is on gridline +- factor / calculationDensity
	 */
	private double getFirstXValue() {

		double result; // to be retuned

		// go above smallest gridline
		if (inputArea[0] < 0) {
			result = (int) inputArea[0];
		} else {
			result = 1 + (int) inputArea[0];
		}

		// subtract 1 / DENSITY as long as we are in the inputArea
		while (result > inputArea[0]) {
			result -= 1.0 / calculationDensity;
		}

		return result;

	}

	/* GETTERS */

	/**
	 * @return a square area that contains all inputPoints
	 */
	public double[] getInputAreaSquare() {

		double[] result = new double[4]; // will be returned

		// get the side of the square
		double side = Math.max(
				Math.max(Math.abs(functionInputPoints.get(0).getRe()), Math.abs(functionInputPoints.get(0).getIm())),
				Math.max(Math.abs(functionInputPoints.get(functionInputPoints.size() - 1).getRe()),
						Math.abs(functionInputPoints.get(functionInputPoints.size() - 1).getIm())));

		// create the square
		result[0] = -side;
		result[2] = -side;

		result[1] = side;
		result[3] = side;

		return result;

	}

	/**
	 * @return outputArea set in the settingsFrame
	 */
	public int[] getOutputArea() {

		return settingsFrame.getOutputArea();

	}

	/**
	 * @return smallest possible square outputArea for the 2D plot that contains all
	 *         outputPoints
	 */
	public int[] getOutputAreaSquare() {

		int[] result = new int[4]; // will be returned

		// get the minimums and maximums
		double minRe = Collections.min(functionOutputPoints.stream().map(e -> e.getRe()).collect(Collectors.toList()));
		double maxRe = Collections.max(functionOutputPoints.stream().map(e -> e.getRe()).collect(Collectors.toList()));
		double minIm = Collections.min(functionOutputPoints.stream().map(e -> e.getIm()).collect(Collectors.toList()));
		double maxIm = Collections.max(functionOutputPoints.stream().map(e -> e.getIm()).collect(Collectors.toList()));

		// get the square side
		double side = Math.max(Math.max(Math.abs(minRe), Math.abs(minIm)), Math.max(Math.abs(maxRe), Math.abs(maxIm)));

		// create the square
		result[0] = (int) (-side - 0.9999);
		result[2] = (int) (-side - 0.9999);

		result[1] = (int) (side + 0.9999);
		result[3] = (int) (side + 0.9999);

		return result;

	}

	/**
	 * @return calculationDensity set in the settingsPanel
	 */
	public int getCalculationDensity() {
		return calculationDensity;
	}

	/**
	 * @return return the secretNumber
	 */
	public double getSecretNumber() {
		return secretNumber;
	}

	/**
	 * @return return number of points
	 */
	public Point getNumberOfPoints() {
		return numberOfPoints;
	}

	/* IMPLEMENTED FUNCTIONS */

	/**
	 * actionListener for this class
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

//		System.out.println(e.getActionCommand());

		switch (e.getActionCommand()) {

		case "settings": // show the settingsFrame
			settingsFrame.setVisible(true);
			break;

		case "apply": // use the new settings
			useNewSettings();
			break;
		}

	}

	/**
	 * keyListener for this class, nothing done here so far
	 */
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	/**
	 * keyListener for this class
	 */
	@Override
	public void keyPressed(KeyEvent e) {

//		System.out.println(e.getKeyCode() + " pressed");

		if (e.getKeyCode() == 10) {// enter pressed, interpret as if "apply" was clicked, use the new settings

			useNewSettings();

		}

	}

	/**
	 * keyListener for this class, nothing done here so far
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}