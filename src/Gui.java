import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Gui extends JFrame implements ActionListener, KeyListener {

//	private SettingsGui settingsFrame;
	private SettingsGui2 settingsFrame;
//	private static Leinwand canvas;
	private static Leinwand2D canvas;
	private static JButton applySettingsButton = new JButton("apply");
	private static JTextField functionInputField = new JTextField();

	private double[] inputArea;
	private int[] outputArea;
	private int calculationDensity, paintDensity;
	private String function;
	private ArrayList<Complex> functionInputPoints, functionOutputPoints;

	private Leinwand2D locationCanvas; // 2d plot of new location of f(z)
	private Leinwand3D realPartCanvas; // 3d plot of real part
	private Leinwand3D imaginaryPartCanvas; // 3d plot of imaginary part
	private Leinwand3D argumentCanvas; // 3d plot of argument
	private Leinwand3D radiusCanvas; // 3d plot of abs(z)
	
	private JLabel titleLabel;
	private double secretNumber = -0.96875; // 0.11111 in binary

	private final Font BUTTON_FONT = new Font("Ubuntu", Font.PLAIN, 40);

	private int coordinateLineDensity, dotWidth;
	private boolean paintLocationDots, paintHorizontalLines, paintVerticalLines;

	public Gui(String function) {

		this.function = function;
		outputArea = new int[] { -4, 4, -4, 4 }; // TODO: remove?

		this.setTitle("KomA_Uebungsserie07 || plot complex functions || by Michael Roth || 3.4.2019");
		this.setSize(new Dimension(2000, 1100));

		functionInputPoints = new ArrayList<Complex>();
		functionOutputPoints = new ArrayList<Complex>();

		locationCanvas = new Leinwand2D(this);
		realPartCanvas = new Leinwand3D(this);
		imaginaryPartCanvas = new Leinwand3D(this);
		radiusCanvas = new Leinwand3D(this);
		argumentCanvas = new Leinwand3D(this);

		setPlotSettings();
		calculateFunctionPoints(calculationDensity);

		settingsFrame = new SettingsGui2(this, new Dimension(1000, 1000), function, functionInputField,
				applySettingsButton);

		setupCanvas();
		
		// add a button for settings
		addGuiElements(this);
		applySettingsButton.addActionListener(this);
		functionInputField.addKeyListener(this);

		this.setBackground(Color.BLACK);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);

	}

	private void setupCanvas() {
		
		long time = System.currentTimeMillis();

		if(outputArea == null) {
			outputArea = getOutputAreaSquare();
			settingsFrame.setOutputArea(getOutputAreaSquare());
		}
		
		ArrayList<Point3D> functionValues = new ArrayList<Point3D>();

		for (int i = 0; i < functionInputPoints.size(); i++) {
			functionValues.add(new Point3D(functionInputPoints.get(i).getRe(), functionInputPoints.get(i).getIm(),
					functionOutputPoints.get(i).getRe()));
		}
		realPartCanvas.setFunctionValues(functionValues);

		functionValues = new ArrayList<Point3D>();
		for (int i = 0; i < functionInputPoints.size(); i++) {
			functionValues.add(new Point3D(functionInputPoints.get(i).getRe(), functionInputPoints.get(i).getIm(),
					functionOutputPoints.get(i).getIm()));
		}
		imaginaryPartCanvas.setFunctionValues(functionValues);

		functionValues = new ArrayList<Point3D>();
		for (int i = 0; i < functionInputPoints.size(); i++) {
			functionValues.add(new Point3D(functionInputPoints.get(i).getRe(), functionInputPoints.get(i).getIm(),
					functionOutputPoints.get(i).getRadius()));
		}
		radiusCanvas.setFunctionValues(functionValues);

		functionValues = new ArrayList<Point3D>();
		for (int i = 0; i < functionInputPoints.size(); i++) {
			functionValues.add(new Point3D(functionInputPoints.get(i).getRe(), functionInputPoints.get(i).getIm(),
					functionOutputPoints.get(i).getPhi()));
		}
		argumentCanvas.setFunctionValues(functionValues);

		// this will also set inputArea and outputArea of locationCanvas
		calculateFunctionPoints(paintDensity);
		locationCanvas.setFunctionValues(functionInputPoints, functionOutputPoints);

		// set sizes
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

		locationCanvas.setPlotSettings(paintDensity, calculationDensity, coordinateLineDensity, dotWidth,
				paintLocationDots, paintHorizontalLines, paintVerticalLines);
		
//		realPartCanvas.setPlotSettings(dotWidth, false, true);
//		imaginaryPartCanvas.setPlotSettings(dotWidth, false, true);
//		radiusCanvas.setPlotSettings(dotWidth, false, true);
//		argumentCanvas.setPlotSettings(dotWidth, false, true);
		
		realPartCanvas.setPlotSettings(dotWidth, true, false);
		imaginaryPartCanvas.setPlotSettings(dotWidth, true, false);
		radiusCanvas.setPlotSettings(dotWidth, true, false);
		argumentCanvas.setPlotSettings(dotWidth, true, false);
		
		System.out.println("took " + (System.currentTimeMillis() - time) + "ms");

	}

	private void repaintCanvas() {

		locationCanvas.repaint();
		realPartCanvas.repaint();
		imaginaryPartCanvas.repaint();
		radiusCanvas.repaint();
		argumentCanvas.repaint();

	}

	private void addGuiElements(JFrame parentFrame) {

		JPanel titlePanel = new JPanel();
		titlePanel.setOpaque(true);
		titlePanel.setBackground(Color.BLACK);
		titlePanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));

		titleLabel = new JLabel("f(z) = " + function);
		titleLabel.setFont(new Font("Ubuntu", Font.PLAIN, 50));
		titleLabel.setForeground(Color.WHITE);

		titlePanel.add(titleLabel);

		JPanel canvasPanel = new JPanel();
		canvasPanel.setLayout(new GridLayout(1, 2));
		canvasPanel.setBorder(BorderFactory.createLineBorder(Color.RED));

		JPanel plot3DPanel = new JPanel();
		plot3DPanel.setLayout(new GridLayout(2, 2));
		plot3DPanel.setBorder(BorderFactory.createLineBorder(Color.GREEN));

		plot3DPanel.add(realPartCanvas);
		plot3DPanel.add(imaginaryPartCanvas);
		plot3DPanel.add(radiusCanvas);
		plot3DPanel.add(argumentCanvas);

		canvasPanel.add(plot3DPanel);
		canvasPanel.add(locationCanvas);

		JButton settings = new JButton("settings");
		settings.setFont(BUTTON_FONT);
		settings.addActionListener(this);

		parentFrame.add(titlePanel, BorderLayout.PAGE_START);
		parentFrame.add(canvasPanel, BorderLayout.CENTER);
		parentFrame.add(settings, BorderLayout.PAGE_END);

	}

	/**
	 * returns the first number that finds place on a gridline or a gridline +- a
	 * multiple of 1 / calculationDensity
	 * 
	 * @return smallest x value that is on gridline +- factor / calculationDensity
	 */
	private double getFirstXValue() {

		double result;

		// go above smallest gridline
		if (inputArea[0] < 0) {
			result = (int) inputArea[0];
		} else {
			result = 1 + (int) inputArea[0];
		}

		// subtract 1 / DENSITY as long as we are in the INPUT_NUMBERAREA
		while (result - 1.0 / calculationDensity > inputArea[0]) {
			result -= 1.0 / calculationDensity;
		}

		return result;

	}

	private boolean setPlotSettings() {

		boolean needToRecalculateFunction = false;

		if (settingsFrame == null) {

			inputArea = new double[] { -2, 2, -2, 2 };

			calculationDensity = 30;
			paintDensity = 10;
			coordinateLineDensity = 1;
			dotWidth = 3;
			paintLocationDots = false;
			paintHorizontalLines = true;
			paintVerticalLines = true;

			needToRecalculateFunction = true;

		} else {

			String newFunction = settingsFrame.getFunction();

			double[] newInputArea = settingsFrame.getInputArea();
			
			outputArea = settingsFrame.getOutputArea();

			int newCalculationDensity = settingsFrame.getCalculationDensity();
			paintDensity = settingsFrame.getDensity();
			coordinateLineDensity = settingsFrame.getCoordinatelineDensity();
			dotWidth = settingsFrame.getDotWidth();
			paintLocationDots = settingsFrame.paintFunctionPoints();
			paintHorizontalLines = settingsFrame.paintHorizontalLines();
			paintVerticalLines = settingsFrame.paintVerticalLines();

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
	 * for each point in INPUT_NUMBERAREA on the DENSITY, get a corresponding
	 * functionValue and draw that on the JPanel. parallel it will draw a grid that
	 * links the functionValue's that were in a rectangular grid in INPUT_NUMBERAREA
	 * 
	 * @param functionIndex defines which function is to be drawn
	 * @param g             using Graphics to directly paint on the JPanel
	 */
	public void calculateFunctionPoints(int density) {

		Complex functionInput, functionOutput;
		functionInputPoints = new ArrayList<Complex>();
		functionOutputPoints = new ArrayList<Complex>();

//		System.out.println("calculateFunctionPoints()");

//		int xCounter = 0;
		for (double x = getFirstXValue(); x <= inputArea[1] + 1.0 / density / 2; x += 1.0 / density) {

//			xCounter++;
//			System.out.println("xCounter = " + xCounter);

			for (double y = inputArea[2]; y <= inputArea[3] + 1.0 / density / 2; y += 1.0 / density) {

//				System.out.println("x = " + x + " y = " + y);

				// calculate function value f(z)
				functionInput = new Complex(x, y, true);
				functionOutput = calculate(functionInput, function);

				if (functionOutput != null) {
					functionInputPoints.add(functionInput);
					functionOutputPoints.add(functionOutput);
				} else {
					functionInputPoints.add(new Complex(secretNumber, secretNumber, true));
					functionOutputPoints.add(new Complex(secretNumber, secretNumber, true));
				}

			}

		}

	}

	/**
	 * calculates f(z) with the given z
	 */
	private Complex calculate(Complex z, String input) {

//	System.out.println("to calculate: " + input);

		if (z == null) {
			return null;
		}

		Complex result = new Complex(0, 0, true);
		int openBrackets = 0;
		boolean bracketsRemoved;

		do {

			bracketsRemoved = false;

			for (int i = 0; i < input.length(); i++) {
				// System.out.println(input.charAt(i) + " found");
				switch (input.charAt(i)) {

				case '(':
					openBrackets++;
					break;

				case ')':
					openBrackets--;
					break;

				case '+':
					if (openBrackets == 0) {

						Complex number = calculate(z, input.substring(0, i));

						if (number == null) {
							return null;
						}

						result = number.add(calculate(z, input.substring(i + 1, input.length())));
						return result;
					}
					break;

				case '-':
					if (openBrackets == 0) {

						Complex number = calculate(z, input.substring(0, i));

						if (number == null) {
							return null;
						}

						result = number.subtract(calculate(z, input.substring(i + 1, input.length())));
						return result;
					}
					break;

				}

			}

			for (int i = input.length() - 1; i >= 0; i--) {
				switch (input.charAt(i)) {

				case '(':
					openBrackets++;
					break;

				case ')':
					openBrackets--;
					break;

				case '*':
					if (openBrackets == 0) {
						Complex number = calculate(z, input.substring(0, i));

						if (number == null) {
							return null;
						}

						result = calculate(z, input.substring(0, i))
								.multiply(calculate(z, input.substring(i + 1, input.length())));
						return result;
					}
					break;

				case '/':
					if (openBrackets == 0) {
						Complex numerator = calculate(z, input.substring(0, i));
						Complex denumerator = calculate(z, input.substring(i + 1, input.length()));

						if (numerator == null) {
							return null;
						}

						result = numerator.divide(denumerator);

						return result;
					}
					break;

				}
			}

			for (int i = 0; i < input.length(); i++) {
				switch (input.charAt(i)) {

				case '(':
					openBrackets++;
					break;

				case ')':
					openBrackets--;
					break;

				case 's':
					if (openBrackets == 0) {
						Complex argument = calculate(z, input.substring(i + 4, input.length() - 1));
						if (argument == null) {
							return null;
						}
						result = argument.sin();
						return result;
					}
					break;

				case 'c':
					if (openBrackets == 0) {
						Complex argument = calculate(z, input.substring(i + 4, input.length() - 1));
						if (argument == null) {
							return null;
						}
						result = argument.cos();
						return result;
					}
					break;

				case 'e':
					if (openBrackets == 0) {
						Complex argument = calculate(z, input.substring(i + 4, input.length() - 1));
						if (argument == null) {
							return null;
						}
						result = argument.exp();
						return result;
					}
					break;

				}
			}

			if (input.charAt(0) == '(' && input.charAt(input.length() - 1) == ')') {
//			System.out.println("removing brackets around " + input);
				input = input.substring(1, input.length() - 1);
				bracketsRemoved = true;
//			System.out.println("new input: " + input);
			}

		} while (bracketsRemoved);

//	System.out.println("make a new Complex number from: " + input);
		switch (input) {

		case "z":
			return z;

		case "i":
			return new Complex(0, 1, true);

		default:
			return new Complex(Double.parseDouble(input), 0, true);
		}

	}

	public static void main(String[] args) {

		// first shown function is function 2
		new Gui("sin(z)");

	}

	public double[] getInputAreaSquare() {

		double[] result = new double[4];

		// return the square
		double side = Math.max(
				Math.max(Math.abs(functionInputPoints.get(0).getRe()), Math.abs(functionInputPoints.get(0).getIm())),
				Math.max(Math.abs(functionInputPoints.get(functionInputPoints.size() - 1).getRe()),
						Math.abs(functionInputPoints.get(functionInputPoints.size() - 1).getIm())));

		result[0] = -side;
		result[2] = -side;

		result[1] = side;
		result[3] = side;

		return result;

	}

	public int[] getOutputAreaSquare() {

		int[] result = new int[4];

		double minRe = Collections.min(functionOutputPoints.stream().map(e -> e.getRe()).collect(Collectors.toList()));
		double maxRe = Collections.max(functionOutputPoints.stream().map(e -> e.getRe()).collect(Collectors.toList()));
		double minIm = Collections.min(functionOutputPoints.stream().map(e -> e.getIm()).collect(Collectors.toList()));
		double maxIm = Collections.max(functionOutputPoints.stream().map(e -> e.getIm()).collect(Collectors.toList()));

		// return the square
		double side = Math.max(Math.max(Math.abs(minRe), Math.abs(minIm)), Math.max(Math.abs(maxRe), Math.abs(maxIm)));

		result[0] = (int) (-side - 0.9999);
		result[2] = (int) (-side - 0.9999);

		result[1] = (int) (side + 0.9999);
		result[3] = (int) (side + 0.9999);

		return result;

	}
	
	public int[] getOutputArea() {
		
		return settingsFrame.getOutputArea();
		
	}
	
	public int getCalculationDensity() {
		return calculationDensity;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		System.out.println(e.getActionCommand());

		switch (e.getActionCommand()) {

		case "settings":
			settingsFrame.setVisible(true);
			break;

		case "apply":
			if (setPlotSettings()) {
				calculateFunctionPoints(calculationDensity);
			}
			setupCanvas();
			repaintCanvas();
			break;
		}

	}
	
	public double getSecretNumber() {
		return secretNumber;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {

		System.out.println(e.getKeyCode() + " pressed");
		
		if(e.getKeyCode() == 10) {// enter pressed
			
			if (setPlotSettings()) {
				calculateFunctionPoints(calculationDensity);
			}
			setupCanvas();
			repaintCanvas();
			
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
