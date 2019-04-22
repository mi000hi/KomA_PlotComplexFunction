import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

public class Leinwand2D extends JPanel {

	private final int MARGIN; // margin around coordinate system

	// Graphing options
	private double[] inputArea; // take input z = x+iy with x = [0...1] and y = [2...3]
	private int density; // how many values are drawn between x && y = [n...n+1]
	private int[] outputArea; // draw [0...1] x [2...3]
	private int coordinatelineDensity;
	private Point ZERO; // (0, 0) on the coordinate system
	private int dotwidth; // width of a calculated point

	private Font FONT = new Font("Ubuntu", Font.PLAIN, 30); // font used for the title

	// set what to draw
	private boolean paintFunctionPoints; // painting function points
	private boolean paintHorizontalLines; // painting horizontal lines from input grid
	private boolean paintVerticalLines; // painting verticalLines from input grid
	private boolean paintBackgroundImage; // painting backgroundimage

	private Dimension paintableDimension; // square dimension where we can paint on
	private Point ONE; // location of the point (1, 1)

	private Gui parent; // this parent gui, where we can get informations from

	private ArrayList<Complex> inputPoints, outputPoints; // input and output points from f(z)
	private ArrayList<ArrayList<Complex>> transformedFunctionOutputPoints; // outputPoints from f(g(x))
	private ArrayList<Color> transformedFunctionColors; // colors of the input functions and transformed functions

	private BufferedImage backgroundImage; // background image

	/* CONSTRUCTOR */

	/**
	 * Constructor, builds the JPanel and defines values used to draw the function
	 * 
	 * @param parent parent class, this is where we get informations from
	 */
	public Leinwand2D(Gui parent) {

		// save given variables
		this.parent = parent;

		// initialize final variables
		MARGIN = 50;

		// set up this jpanel
		this.setOpaque(true);
		this.setBackground(Color.BLACK);

	}

	/* PAINTCOMPONENT */

	/**
	 * this function paints everything, the function, the coordinate system, the
	 * legends and the connecting lines
	 * 
	 * @param g needed to paint
	 */
	public void paintComponent(Graphics g) {

		// reset image, clearing the Graphics g variable
		super.paintComponent(g);

		// return if we have nothing to paint
		if (inputArea == null) {
			return;
		}

		g.setFont(FONT);

		// calculate square that we can paint on
		int smallerLength = Math.min(this.getSize().width - MARGIN, this.getSize().height - MARGIN);
		paintableDimension = new Dimension(smallerLength, smallerLength);

		// set the zero point accordingly
		ZERO = new Point(this.getSize().width / 2, this.getSize().height / 2);

		// draw the coordinate System with its labels
		drawCoordinateSystem(g);

//		if(backgroundImage != null) {
//			
//			System.out.println("LEINWAND2D: \t painting background image");
//			Point drawLocation = getPointAt(outputArea[0], outputArea[3]);
//			g.drawImage(backgroundImage, drawLocation.x, drawLocation.y, paintableDimension.width, paintableDimension.height, null);
//			
//		}

		// plot the function values f(z)
		drawFunction(g); // Select which function to plot here

		// plot the transformed function values f(g(x))
		drawTransformedFunctions(g);

		// draw the color legends
		drawColorLegend(g);

	}

	/**
	 * for each point in inputArea on the density, get a corresponding functionValue
	 * and draw that on the JPanel. parallel it will draw a grid that links the
	 * functionValue's that were in a rectangular grid in inputArea
	 * 
	 * @param g using Graphics to directly paint on the JPanel
	 */
	private void drawFunction(Graphics g) {

		int backgroundColor;
		Complex functionOutputValue = new Complex(0, 0, true); // result of the function
		Complex functionInputValue = new Complex(0, 0, true); // input of the function
		Point currentOutputPoint = new Point(0, 0), lastPoint = currentOutputPoint; // on-screen-location of the current
																					// drawn
		// functionValue
		Point currentInputPoint;

		boolean startNewLine = false; // is false while we draw all y coordinates at a fixed x coordinate

		Point numberOfPoints = parent.getNumberOfPoints();

//		System.out.println(pointsInXDirection + " x " + pointsInYDirection + " = " + outputPoints.size() + " points");

		for (int x = 0; x < numberOfPoints.x; x += density) {

			startNewLine = true;

			for (int y = 0; y < numberOfPoints.y; y += density) {

//				if (x * numberOfPoints.y + y < outputPoints.size()) {	// TODO: remove?
				functionOutputValue = outputPoints.get(x * numberOfPoints.y + y);
				functionInputValue = inputPoints.get(x * numberOfPoints.y + y);

				if (functionOutputValue != null && functionOutputValue.getRe() != parent.getSecretNumber()) {

					if (startNewLine) {
						currentOutputPoint = getPointAt(functionOutputValue.getRe(), functionOutputValue.getIm());
						lastPoint = currentOutputPoint;
						startNewLine = false;
					} else {
						lastPoint = currentOutputPoint;
						currentOutputPoint = getPointAt(functionOutputValue.getRe(), functionOutputValue.getIm());
					}

					// TODO: use resolution / density of background image
					// TODO: usable for rectangular input areas
					// TODO: this method would be useful if we like to transform a complete image
//					if (backgroundImage != null && paintBackgroundImage) {
//
//						currentInputPoint = getPointAt(functionInputValue.getRe(), functionInputValue.getIm());
//						currentInputPoint.setLocation(x * backgroundImage.getWidth() / numberOfPoints.x,
//								y * backgroundImage.getHeight() / numberOfPoints.y);
//						backgroundColor = backgroundImage.getRGB(currentInputPoint.x, currentInputPoint.y);
//
////						System.out.println("LEINWAND2D: \t painting at " + functionOutputValue.getRe() + ", " + functionOutputValue.getIm());
////						System.out.println("LEINWAND2D: \t backgroundColor: " + backgroundColor);
//
//						// if point is not transparent
//						if (backgroundColor != 0) {
//							drawPointAt(functionOutputValue.getRe(), -functionOutputValue.getIm(), g,
//									new Color(backgroundColor));
//						}
//					}

					if (paintVerticalLines) {

						g.setColor(getColor2(x, 0, numberOfPoints.x));
						g.drawLine(lastPoint.x, lastPoint.y, currentOutputPoint.x, currentOutputPoint.y);
					}
					if (paintFunctionPoints) {
						drawPointAt(functionOutputValue.getRe(), functionOutputValue.getIm(), g, Color.YELLOW);
					}

				} else {
					startNewLine = true;
				}

//				} // TODO: remove?

			}

		}

		if (paintHorizontalLines) {
			for (int y = 0; y < numberOfPoints.y; y += density) {

				startNewLine = true;
				g.setColor(getColor(y, 0, numberOfPoints.y - 1));

				for (int x = 0; x < numberOfPoints.x; x += density) {

					if (x * numberOfPoints.y + y < outputPoints.size()) {
						functionOutputValue = outputPoints.get(x * numberOfPoints.y + y);

						if (functionOutputValue != null && functionOutputValue.getRe() != parent.getSecretNumber()) {

							if (startNewLine) {
								currentOutputPoint = getPointAt(functionOutputValue.getRe(),
										functionOutputValue.getIm());
								lastPoint = currentOutputPoint;
								startNewLine = false;
							} else {
								lastPoint = currentOutputPoint;
								currentOutputPoint = getPointAt(functionOutputValue.getRe(),
										functionOutputValue.getIm());
							}

							g.drawLine(lastPoint.x, lastPoint.y, currentOutputPoint.x, currentOutputPoint.y);
							// drawPointAt(functionValue.getRe(), functionValue.getIm(), g);
						} else {
							startNewLine = true;
						}

					}

				}

			}
		}

	}

	/**
	 * Draws the transformed functions f(g(x))
	 */
	private void drawTransformedFunctions(Graphics g) {

		// paint the background image
		if (paintBackgroundImage && transformedFunctionOutputPoints != null) {

			ArrayList<Complex> currentTransformedFunction;
			Complex currentPoint;
			Color currentColor;

			// draw each inputfunction
			for (int i = 0; i < transformedFunctionOutputPoints.size(); i++) {

				currentTransformedFunction = transformedFunctionOutputPoints.get(i);
				currentColor = transformedFunctionColors.get(i);

				// draw each outputPoint
				for (int j = 0; j < currentTransformedFunction.size(); j++) {
					
					currentPoint = currentTransformedFunction.get(j);
					
//					System.out.println("LEINWAND2D: \t drawing point at " + currentPoint.getRe() + " + i(" + currentPoint.getIm() + ")");
					drawPointAt(currentPoint.getRe(), currentPoint.getIm(), g, currentColor);
					
				}

			}

		}

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
		numberOfLines = (int) ((Math.abs(outputArea[0]) + Math.abs(outputArea[1])));
		lineSpace = paintableDimension.width / numberOfLines;
		ONE = new Point(lineSpace, lineSpace);
		for (int x = outputArea[0]; x <= outputArea[1]; x += coordinatelineDensity) {

//			System.out.println("x = " + x);
			// define line location
			lineStart = getPointAt(x, outputArea[2]);
			lineEnd = getPointAt(x, outputArea[3]);

			// draw line
			g.setColor(Color.GRAY);
			g.drawLine(lineStart.x, lineStart.y, lineEnd.x, lineEnd.y);

			// draw linelabel
			g.setColor(Color.WHITE);
			g.drawString(Integer.toString(x), ZERO.x + x * lineSpace + 5, ZERO.y - 5);
		}

		// draw horizontal lines y = constant

		lineSpace = paintableDimension.height / numberOfLines;
		numberOfLines = (int) ((Math.abs(outputArea[2]) + Math.abs(outputArea[3])) / coordinatelineDensity);
		for (int y = outputArea[2]; y <= outputArea[3]; y += coordinatelineDensity) {

			// define line location
			lineStart = getPointAt(outputArea[0], y);
			lineEnd = getPointAt(outputArea[1], y);

			// draw line
			g.setColor(Color.GRAY);
			g.drawLine(lineStart.x, lineStart.y, lineEnd.x, lineEnd.y);

			// draw line label
			g.setColor(Color.WHITE);
			g.drawString(Integer.toString(y) + "i", ZERO.x + 5, lineStart.y - 5);
		}

		// draw x = 0 and y = 0 lines again but thicker
		g.setColor(Color.white);
		g.fillRect(ZERO.x - 1, ZERO.y - paintableDimension.height / 2, 3, paintableDimension.height);
		g.fillRect(ZERO.x - paintableDimension.width / 2, ZERO.y - 1, paintableDimension.width, 3);

	}

	/**
	 * draws two legends for the colors, one for the colors in y direction and one
	 * for the colors in x direction with bounds of inputArea
	 * 
	 * @param g using Graphics to directly draw the legends on the JPanel
	 */
	private void drawColorLegend(Graphics g) {

		int size = paintableDimension.width / 10; // legends is a square
		Point location = new Point(paintableDimension.width - size,
				ZERO.y + paintableDimension.height / 2 - size - MARGIN);
		Point location2 = new Point(2 * MARGIN, ZERO.y + paintableDimension.height / 2 - size - MARGIN);

		// draw colorlegends
		for (int x = 0; x <= size; x++) {
			g.setColor(getColor2(x, 0, size));
			g.drawLine(location.x + x, location.y, location.x + x, location.y + size);

			g.setColor(getColor(x, 0, size));
			g.drawLine(location2.x, location2.y + size - x, location2.x + size, location2.y + size - x);
		}

		// draw strings of inputArea bounds
		g.setColor(Color.WHITE);
		g.drawString("x=", location.x - 2 * FONT.getSize(), location.y - 5);
		g.drawString(Integer.toString((int) Math.round(inputArea[0])), location.x, location.y - 5);
		g.drawString(Integer.toString((int) Math.round(inputArea[1])), location.x + size - 5, location.y - 5);

		g.drawString("y=", location2.x - 2 * FONT.getSize(), location2.y - 5);
		g.drawString(Integer.toString((int) Math.round(inputArea[3])), location2.x - FONT.getSize(), location2.y + 10);
		g.drawString(Integer.toString((int) Math.round(inputArea[2])), location2.x - FONT.getSize(),
				location2.y + size);

	}

	/**
	 * draw a point at location x, y in the Coordinate system. x and y are NOT
	 * screen coordinates
	 * 
	 * @param x location x value according to coordinate system
	 * @param y location y value according to coordinate system
	 * @param g using Graphics to directly draw on the JPanel
	 */
	private void drawPointAt(double x, double y, Graphics g, Color color) {

		g.setColor(color);

		// invert y axis and draw point on screen
		g.fillRect((int) (x * ONE.x + ZERO.x - dotwidth / 2), (int) ((-1) * y * ONE.y + ZERO.y - dotwidth / 2),
				dotwidth, dotwidth);

	}

	/* GETTERS */

	/**
	 * returns a color according to the value in [min...max] so that we get that
	 * fancy color change colors reach from min = green to red to max = yellow
	 * 
	 * @param value where we are in [min...max]
	 * @param min   the minimum of the colorchange-area
	 * @param max   the maximum of the colorchange-area
	 * @return the color according to value in [min...max]
	 */
	private Color getColor(double value, double min, double max) {

		Color result;
		double change1 = (max + Math.abs(min)) / 2;
		// System.out.println("change at x = " + change1 + ", " + change2);

		double shiftedValue = value - min;

		// minimum, green to mid: red
		if (shiftedValue <= change1) {

//			System.out.println(((int) (shiftedValue * 255 / change1)) + " " + (255 - (int) (shiftedValue * 255 / change1)));
			result = new Color((int) (shiftedValue * 255 / change1), 255 - (int) (shiftedValue * 255 / change1), 0,
					255);

			// maximum, yellow to mid: red
		} else {

			result = new Color(255, (int) ((shiftedValue - change1) * 255 / change1), 0, 255);

		}

		return result;
	}

	/**
	 * returns a color according to the value in [min...max] so that we get that
	 * fancy color change colors reach from min = light blue to violet to max = pink
	 * 
	 * @param value where we are in [min...max]
	 * @param min   the minimum of the colorchange-area
	 * @param max   the maximum of the colorchange-area
	 * @return the color according to value in [min...max]
	 */
	private Color getColor2(double value, double min, double max) {

		Color result;
		double change1 = (max + Math.abs(min)) / 2;
		// System.out.println("change at x = " + change1 + ", " + change2);

		double shiftedValue = value - min;

		// minimum, light blue to mid: violet
		if (shiftedValue <= change1) {

//			System.out.println(((int) (shiftedValue * 255 / change1)) + " " + (255 - (int) (shiftedValue * 255 / change1)));
			result = new Color(100, 255 - (int) (shiftedValue * 255 / change1),
					255 - (int) (shiftedValue * 100 / change1), 255);

			// maximum, pink to mid: violet
		} else {

			result = new Color(100 + (int) ((shiftedValue - change1) * 155 / change1), 0, 155, 255);

		}

		return result;
	}

	/**
	 * returns the screen coordinates of a given point (x, y)
	 * 
	 * @param x location x value according to coordinate system
	 * @param y location y value according to coordinate system
	 * @return Point with on-screen-coordinates
	 */
	private Point getPointAt(double x, double y) {

		// invert y axis and return screenPosition of (x, y)
		return new Point((int) (x * ONE.x + ZERO.x), (int) ((-1) * y * ONE.y + ZERO.y));

	}

	/**
	 * @return returns paintable dimension of this jpanel
	 */
	public Dimension getPaintableDimension() {
		return paintableDimension;
	}

	/* SETTERS */

	/**
	 * this function sets the given values with the new plot settings
	 * 
	 * @param density               density to draw the lines with TODO: now useless
	 *                              i think
	 * @param coordinatelineDensity density to draw the coordinateLines
	 * @param dotWidth              width of a dot at location f(z)
	 * @param paintDots             true if we paint the dots at location f(z)
	 * @param paintHorizontalLines  true if we paint the horizontal lines from the
	 *                              input grid
	 * @param paintVerticalLines    true if we paint the vertical lines from the
	 *                              input grid
	 */
	public void setPlotSettings(int density, int coordinatelineDensity, int dotWidth, boolean paintDots,
			boolean paintHorizontalLines, boolean paintVerticalLines, boolean paintBackgroundImage) {

		// saving given variables
		this.density = density;
		this.coordinatelineDensity = coordinatelineDensity;
		this.dotwidth = dotWidth;
		this.paintFunctionPoints = paintDots;
		this.paintHorizontalLines = paintHorizontalLines;
		this.paintVerticalLines = paintVerticalLines;
		this.paintBackgroundImage = paintBackgroundImage;

		backgroundImage = parent.getBackgroundImage();

	}

	/**
	 * @param inputPoints  points of the input definition function area
	 * @param outputPoints points of the output, f(z)
	 */
	public void setFunctionValues(ArrayList<Complex> inputPoints, ArrayList<Complex> outputPoints,
			ArrayList<ArrayList<Complex>> transformedFunctionOutputPoints, ArrayList<Color> colors) {

		this.inputPoints = inputPoints;
		this.outputPoints = outputPoints;
		this.transformedFunctionOutputPoints = transformedFunctionOutputPoints;
		this.transformedFunctionColors = colors;

		inputArea = parent.getInputAreaSquare();
		this.outputArea = parent.getOutputArea();
		if (outputArea == null) {
			this.outputArea = parent.getOutputAreaSquare();
		}

	}

	/**
	 * @param outputArea adjust the output area size
	 */
	public void setOutputArea(int[] outputArea) {
		if (outputArea == null) {
			this.outputArea = parent.getOutputAreaSquare();
		} else {
			this.outputArea = outputArea;
		}

	}

}
