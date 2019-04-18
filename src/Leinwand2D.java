import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Leinwand2D extends JPanel {

	private Dimension PANELSIZE; // size of the painting area / JPanel
	private int MARGIN; // margin around coordinate system
	private int DRAWWIDTH; // width of drawing area
	private int DRAWHEIGHT; // height of drawing area
	private String FUNCTION; // name of the function

	// Graphing options
	private double[] inputArea; // take input z = x+iy with x = [0...1] and y = [2...3]
	private int density, calculatedDensity; // how many values are calculated between x && y = [n...n+1]
	private int[] outputArea; // draw [0...1] x [2...3]
	private int coordinatelineDensity;
	private Point ZERO; // (0, 0) on the coordinate system
	private Point WIDTH_HEIGHT; // (WIDTH, HEIGHT) of one step in coordinate system
	private int DOTWIDTH; // width of a calculated point
	private int LINEOPACITY; // opacity of lines
	private int LINEOPACITY2; // opacity of lines2

	private int SHIFTCOLORRANGE2; // offset of colorrange2
	private Font FONT = new Font("Ubuntu", Font.PLAIN, 30);

	// Functions
	private Complex z;

	// set what to draw
	private boolean PAINTFUNCTIONPOINTS;
	private boolean PAINTHORIZONTALLINES;
	private boolean PAINTVERTICALLINES;
	private boolean SHOW3DPLOTS;

	private Dimension paintableDimension;
	private Point ONE;

	private Gui parent;

	private ArrayList<Complex> inputPoints, outputPoints;

	/**
	 * Constructor, builds the JPanel and defines values used to draw the function
	 * 
	 * @param size              size of the jpanel
	 * @param functionIndex     index of function to draw
	 * @param input_numberarea  input definition area
	 * @param output_numberarea output definition area
	 */
	public Leinwand2D(Gui parent) {

		MARGIN = 50;
		this.parent = parent;

//		DRAWWIDTH = (int) size.getWidth() - 2 * MARGIN;
//		DRAWHEIGHT = (int) size.getHeight() - 2 * MARGIN;
//		ZERO = new Point((int) (1.03 * size.getWidth() / 2.0), (int) (size.getHeight() / 2.0));
//		INPUT_NUMBERAREA = input_numberarea;
//		OUTPUT_NUMBERAREA = output_numberarea;
//		WIDTH_HEIGHT = new Point(DRAWWIDTH / (Math.abs(OUTPUT_NUMBERAREA[0]) + OUTPUT_NUMBERAREA[1]),
//				DRAWHEIGHT / (Math.abs(OUTPUT_NUMBERAREA[2]) + OUTPUT_NUMBERAREA[3]));
//
//		DENSITY = density;
//		COORDINATELINE_DENSITY = coordinatelineDensity;
//		DOTWIDTH = dotwidth;
//
//		LINEOPACITY = 255;
//		LINEOPACITY2 = 255;
//		SHIFTCOLORRANGE2 = 0;
//
//		PAINTFUNCTIONPOINTS = showFunctionPoints;
//		PAINTHORIZONTALLINES = showHorizontalLines;
//		PAINTVERTICALLINES = showVerticalLines;

		this.setOpaque(true);
		this.setBackground(Color.BLACK);

	}

	public void setPlotSettings(int density, int calculatedDensity, int coordinatelineDensity, int dotWidth,
			boolean paintDots, boolean paintHorizontalLines, boolean paintVerticalLines) {

		this.density = density;
		this.calculatedDensity = calculatedDensity;
		this.coordinatelineDensity = coordinatelineDensity;
		this.DOTWIDTH = dotWidth;
		this.PAINTFUNCTIONPOINTS = paintDots;
		this.PAINTHORIZONTALLINES = paintHorizontalLines;
		this.PAINTVERTICALLINES = paintVerticalLines;

	}

	/**
	 * this function paints everything, the function, the coordinate system, the
	 * legends and the connecting lines
	 */
	public void paintComponent(Graphics g) {

		super.paintComponent(g);

		if (inputArea == null) {
			return;
		}

		g.setFont(FONT);

		int smallerLength = Math.min(this.getSize().width - MARGIN, this.getSize().height - MARGIN);
		paintableDimension = new Dimension(smallerLength, smallerLength);

		ZERO = new Point(this.getSize().width / 2, this.getSize().height / 2);
		// draw the coordinate System with its labels
		drawCoordinateSystem(g);

		// plot the function values f(z)
		drawFunction(g); // Select which function to plot here

		// draw the color legends
		drawColorLegend(g);

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
	 * for each point in INPUT_NUMBERAREA on the DENSITY, get a corresponding
	 * functionValue and draw that on the JPanel. parallel it will draw a grid that
	 * links the functionValue's that were in a rectangular grid in INPUT_NUMBERAREA
	 * 
	 * @param functionIndex defines which function is to be drawn
	 * @param g             using Graphics to directly paint on the JPanel
	 */
	private void drawFunction(Graphics g) {

		Complex functionValue = new Complex(0, 0, true); // result of the function
		Point currentPoint = new Point(0, 0), lastPoint = currentPoint; // on-screen-location of the current drawn
																		// functionValue

		boolean startNewLine = false; // is false while we draw all y coordinates at a fixed x coordinate

		int pointsInXDirection = 1 + (int) (Math.abs(inputArea[0]) + Math.abs(inputArea[1])) * density;
		int pointsInYDirection = 1 + (int) ((Math.abs(inputArea[2]) + Math.abs(inputArea[3])) * density);

//		System.out.println(pointsInXDirection + " x " + pointsInYDirection + " = " + outputPoints.size() + " points");

		for (int x = 0; x < pointsInXDirection; x++) {

			startNewLine = true;

			for (int y = 0; y < pointsInYDirection; y++) {

				if (x * pointsInYDirection + y < outputPoints.size()) {
					functionValue = outputPoints.get(x * pointsInYDirection + y);

					if (functionValue.getRe() != parent.getSecretNumber()) {

						if (startNewLine) {
							currentPoint = getPointAt(functionValue.getRe(), functionValue.getIm());
							lastPoint = currentPoint;
							startNewLine = false;
						} else {
							lastPoint = currentPoint;
							currentPoint = getPointAt(functionValue.getRe(), functionValue.getIm());
						}

						if (PAINTVERTICALLINES) {

							g.setColor(getColor2(x, 0, pointsInXDirection - 1));
							g.drawLine(lastPoint.x, lastPoint.y, currentPoint.x, currentPoint.y);
						}
						if (PAINTFUNCTIONPOINTS) {
							drawPointAt(functionValue.getRe(), functionValue.getIm(), g);
						}

					} else {
						startNewLine = true;
					}

				}

			}

		}

		if (PAINTHORIZONTALLINES) {
			for (int y = 0; y < pointsInYDirection; y++) {

				startNewLine = true;
				g.setColor(getColor(y, 0, pointsInYDirection - 1));

				for (int x = 0; x < pointsInXDirection; x++) {

					if (x * pointsInYDirection + y < outputPoints.size()) {
						functionValue = outputPoints.get(x * pointsInYDirection + y);

						if (functionValue.getRe() != parent.getSecretNumber()) {

							if (startNewLine) {
								currentPoint = getPointAt(functionValue.getRe(), functionValue.getIm());
								lastPoint = currentPoint;
								startNewLine = false;
							} else {
								lastPoint = currentPoint;
								currentPoint = getPointAt(functionValue.getRe(), functionValue.getIm());
							}

							g.drawLine(lastPoint.x, lastPoint.y, currentPoint.x, currentPoint.y);
							// drawPointAt(functionValue.getRe(), functionValue.getIm(), g);
						} else {
							startNewLine = true;
						}

					}

				}

			}
		}

	}

	private Color getColor(double value, double min, double max) {

		Color result;
		double change1 = (max + Math.abs(min)) / 2;
		// System.out.println("change at x = " + change1 + ", " + change2);

		double shiftedValue = value - min;

		// minimum
		if (shiftedValue <= change1) {

//			System.out.println(((int) (shiftedValue * 255 / change1)) + " " + (255 - (int) (shiftedValue * 255 / change1)));
			result = new Color((int) (shiftedValue * 255 / change1), 255 - (int) (shiftedValue * 255 / change1), 0,
					255);

			// maximum
		} else {

			result = new Color(255, (int) ((shiftedValue - change1) * 255 / change1), 0, 255);

		}

		return result;
	}

	private Color getColor2(double value, double min, double max) {

		Color result;
		double change1 = (max + Math.abs(min)) / 2;
		// System.out.println("change at x = " + change1 + ", " + change2);

		double shiftedValue = value - min;

		// minimum
		if (shiftedValue <= change1) {

//			System.out.println(((int) (shiftedValue * 255 / change1)) + " " + (255 - (int) (shiftedValue * 255 / change1)));
			result = new Color(100, 255 - (int) (shiftedValue * 255 / change1),
					255 - (int) (shiftedValue * 100 / change1), 255);

			// maximum
		} else {

			result = new Color(100 + (int) ((shiftedValue - change1) * 155 / change1), 0, 155, 255);

		}

		return result;
	}

	/**
	 * draws two legends for the colors, one for the colors in y direction and one
	 * for the colors in x direction with bounds of INPUT_NUMBERAREA
	 * 
	 * @param g using Graphics to directly draw the legends on the JPanel
	 */
	private void drawColorLegend(Graphics g) {

		int size = paintableDimension.width / 10; // legends is a square
		Point location = new Point(paintableDimension.width - size,
				ZERO.y + paintableDimension.height / 2 - size - MARGIN);
		Point location2 = new Point(2 * MARGIN, ZERO.y + paintableDimension.height / 2 - size - MARGIN);

		// draw colors
		/** TODO sync colors with colors used to plot the function */
		for (int x = 0; x <= size; x++) {
			g.setColor(getColor2(x, 0, size));
			g.drawLine(location.x + x, location.y, location.x + x, location.y + size);

			g.setColor(getColor(x, 0, size));
			g.drawLine(location2.x, location2.y + size - x, location2.x + size, location2.y + size - x);
		}

		// draw strings of INPUT_NUMBERAREA bounds
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
	 * @param x location x value
	 * @param y location y value
	 * @param g using Graphics to directly draw on the JPanel
	 */
	private void drawPointAt(double x, double y, Graphics g) {

		g.setColor(Color.YELLOW);

		// invert y axis and draw point on screen
		g.fillRect((int) (x * ONE.x + ZERO.x - DOTWIDTH / 2), (int) ((-1) * y * ONE.y + ZERO.y - DOTWIDTH / 2),
				DOTWIDTH, DOTWIDTH);

	}

	/**
	 * returns the screen coordinates of a given point (x, y)
	 * 
	 * @param x location x value
	 * @param y location y value
	 * @return Point with on-screen-coordinates
	 */
	private Point getPointAt(double x, double y) {

		// invert y axis and return screenPosition of (x, y)
		return new Point((int) (x * ONE.x + ZERO.x), (int) ((-1) * y * ONE.y + ZERO.y));

	}

//	public String[] getInputArea() {
//		String[] result = new String[4];
//
//		for (int i = 0; i < 4; i++) {
//			if (INPUT_NUMBERAREA[i] == Math.PI) {
//				result[i] = "Pi";
//			} else if (INPUT_NUMBERAREA[i] == -Math.PI) {
//				result[i] = "-Pi";
//			} else if (INPUT_NUMBERAREA[i] == 2 * Math.PI) {
//				result[i] = "2Pi";
//			} else if (INPUT_NUMBERAREA[i] == -2 * Math.PI) {
//				result[i] = "2Pi";
//			} else {
//				result[i] = Double.toString(INPUT_NUMBERAREA[i]);
//			}
//		}
//
//		return result;
//
//	}

	public void setFunctionValues(ArrayList<Complex> inputPoints, ArrayList<Complex> outputPoints) {

		this.inputPoints = inputPoints;
		this.outputPoints = outputPoints;

		inputArea = parent.getInputAreaSquare();
		this.outputArea = parent.getOutputArea();
		if (outputArea == null) {
			this.outputArea = parent.getOutputAreaSquare();
		}

	}

	public void setOutputArea(int[] outputArea) {
		if (outputArea == null) {
			this.outputArea = parent.getOutputAreaSquare();
		} else {
			this.outputArea = outputArea;
		}

	}

//	public void addDot(Point3D location) {
//
//		functionPoints.add(location);
//	}

}
