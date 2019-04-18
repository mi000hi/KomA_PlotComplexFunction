import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

public class Leinwand extends JPanel {

	private final Dimension PANELSIZE; // size of the painting area / JPanel
	private final int MARGIN; // margin around coordinate system
	private final int DRAWWIDTH; // width of drawing area
	private final int DRAWHEIGHT; // height of drawing area
	private String functionLabel; // name of the function

	// Graphing options
	private final double[] INPUT_NUMBERAREA; // take input z = x+iy with x = [0...1] and y = [2...3]
	private final double DENSITY; // how many values are calculated between x && y = [n...n+1]
	private final int[] OUTPUT_NUMBERAREA; // draw [0...1] x [2...3]
	private final double COORDINATELINE_DENSITY;
	private final Point ZERO; // (0, 0) on the coordinate system
	private final Point WIDTH_HEIGHT; // (WIDTH, HEIGHT) of one step in coordinate system
	private final int DOTWIDTH; // width of a calculated point
	private final char FUNCTIONINDEX; // define which function to draw
	private final int LINEOPACITY; // opacity of lines
	private final int LINEOPACITY2; // opacity of lines2

	private final int SHIFTCOLORRANGE2; // offset of colorrange2
	private final Font FONT = new Font("Ubuntu", Font.PLAIN, 30);
	
	// Functions
	private Complex z;
	private Map<Character, Thread> functions = new HashMap<>();
	
	// set what to draw
	private final boolean PAINTFUNCTIONPOINTS;
	private final boolean PAINTHORIZONTALLINES;
	private final boolean PAINTVERTICALLINES;

	/**
	 * Constructor, builds the JPanel and defines values used to draw the function
	 * 
	 * @param size	size of the jpanel
	 * @param functionIndex index of function to draw
	 * @param input_numberarea input definition area
	 * @param output_numberarea output definition area
	 */
	public Leinwand(Dimension size, char functionIndex, double[] input_numberarea, int[] output_numberarea, boolean showFunctionPoints, boolean showHorizontalLines, boolean showVerticalLines, double density, double coordinatelineDensity, int dotwidth) {

		FUNCTIONINDEX = functionIndex;
		PANELSIZE = size;
		MARGIN = 20;
		DRAWWIDTH = (int) size.getWidth() - 2 * MARGIN;
		DRAWHEIGHT = (int) size.getHeight() - 2 * MARGIN;
		ZERO = new Point((int) (1.03 * size.getWidth() / 2.0), (int) (size.getHeight() / 2.0));
		INPUT_NUMBERAREA = input_numberarea;
		OUTPUT_NUMBERAREA = output_numberarea;
		WIDTH_HEIGHT = new Point(DRAWWIDTH / (Math.abs(OUTPUT_NUMBERAREA[0]) + OUTPUT_NUMBERAREA[1]),
				DRAWHEIGHT / (Math.abs(OUTPUT_NUMBERAREA[2]) + OUTPUT_NUMBERAREA[3]));
		
		DENSITY = density;
		COORDINATELINE_DENSITY = coordinatelineDensity;
		DOTWIDTH = dotwidth;

		LINEOPACITY = 255;
		LINEOPACITY2 = 255;
		SHIFTCOLORRANGE2 = 0;
		
		PAINTFUNCTIONPOINTS = showFunctionPoints;
		PAINTHORIZONTALLINES = showHorizontalLines;
		PAINTVERTICALLINES = showVerticalLines;
		
		setFunctionLabel();

		// add functions to the HashMap
		functions.put('0', new Thread(() -> function0(z)));
		functions.put('1', new Thread(() -> function1(z)));
		functions.put('2', new Thread(() -> function2(z)));
		functions.put('3', new Thread(() -> function3(z)));
		functions.put('4', new Thread(() -> function4(z)));
		functions.put('5', new Thread(() -> function5(z)));

		this.setOpaque(true);
		this.setBackground(Color.BLACK);
		this.setSize(PANELSIZE);

	}

	/**
	 * this function paints everything, the function, the coordinate system, the legends and the connecting lines
	 */
	public void paintComponent(Graphics g) {

		super.paintComponent(g);

		g.setFont(FONT);

		// draw the coordinate System with its labels
		drawCoordinateSystem(g);

		// plot the function values f(z)
		drawFunction(FUNCTIONINDEX, g); // Select which function to plot here

		// draw the color legends
		drawColorLegend(g);

		// draw the label of the function f(z) = ...
		drawFunctionLabel(g, FUNCTIONINDEX);

	}

	/**
	 * draws the label of the function corresponding to functionIndex on top of the
	 * screen example: f(z) = sin(z)
	 * 
	 * @param g             using Graphics to draw directly on the JPanel
	 * @param functionIndex index of which function is drawn
	 */
	private void drawFunctionLabel(Graphics g, int functionIndex) {

		Font font = new Font("Ubuntu", Font.PLAIN, 50); // labelfont
		Point location = new Point(5 * MARGIN, 5 * MARGIN); // location of the label

		// paint the label
		g.setFont(font);
		g.setColor(Color.white);
		g.drawString(functionLabel, location.x, location.y);

	}

	/**
	 * Draws the coordinate system of the complex number area with coordiante lines
	 * and labels
	 * 
	 * @param g using Graphics to draw on the JPanel
	 */
	private void drawCoordinateSystem(Graphics g) {

		int lineSpace; // space between two lines
		Point lineStart, lineEnd; // temporary start and end on-screen-position of the lines

		// draw vertical lines x = constant
		lineSpace = WIDTH_HEIGHT.x;
		for (int x = OUTPUT_NUMBERAREA[0]; x <= OUTPUT_NUMBERAREA[1]; x += COORDINATELINE_DENSITY) {
			// define line location
			lineStart = getPointAt(x, OUTPUT_NUMBERAREA[2]);
			lineEnd = getPointAt(x, OUTPUT_NUMBERAREA[3]);

			// draw line
			g.setColor(Color.GRAY);
			g.drawLine(lineStart.x, lineStart.y, lineEnd.x, lineEnd.y);

			// draw linelabel
			g.setColor(Color.WHITE);
			g.drawString(Integer.toString(x), ZERO.x + x * lineSpace + 5, ZERO.y - 5);
		}

		// draw horizontal lines y = constant
		lineSpace = WIDTH_HEIGHT.y;
		for (int y = OUTPUT_NUMBERAREA[2]; y <= OUTPUT_NUMBERAREA[3]; y += COORDINATELINE_DENSITY) {

			// define line location
			lineStart = getPointAt(OUTPUT_NUMBERAREA[0], y);
			lineEnd = getPointAt(OUTPUT_NUMBERAREA[1], y);

			// draw line
			g.setColor(Color.GRAY);
			g.drawLine(lineStart.x, lineStart.y, lineEnd.x, lineEnd.y);

			// draw line label
			g.setColor(Color.WHITE);
			g.drawString(Integer.toString(y) + "i", ZERO.x + 5, lineStart.y - 5);
		}

		// draw x = 0 and y = 0 lines again but thicker
		g.setColor(Color.white);
		g.fillRect(ZERO.x - 1, ZERO.y - DRAWHEIGHT / 2, 3, DRAWHEIGHT);
		g.fillRect(ZERO.x - DRAWWIDTH / 2, ZERO.y - 1, DRAWWIDTH, 3);

	}

	/**
	 * for each point in INPUT_NUMBERAREA on the DENSITY, get a corresponding
	 * functionValue and draw that on the JPanel. parallel it will draw a grid that
	 * links the functionValue's that were in a rectangular grid in INPUT_NUMBERAREA
	 * 
	 * @param functionIndex defines which function is to be drawn
	 * @param g             using Graphics to directly paint on the JPanel
	 */
	private void drawFunction(int functionIndex, Graphics g) {

		Complex functionValue = new Complex(0, 0, true); // result of the function
		Point currentPoint = new Point(0, 0), lastPoint; // on-screen-location of the current drawn functionValue

		boolean startNewVerticalLine = false; // is false while we draw all y coordinates at a fixed x coordinate
		Color lineColor = new Color(0, 255, 0, LINEOPACITY); // color of a vertical line
		Color lineColor2 = new Color(255, 255, 0, LINEOPACITY2); // color of a horizontal line

		// draw the functionValues and the connect values where input reel number part
		// was the same
		// go over x from left to right while doing all y values per x
		for (double x = getFirstXValue(); x <= INPUT_NUMBERAREA[1] + 1 / DENSITY / 2; x += 1 / DENSITY) {

			// set this boolean so we know we need to start a new connecting line
			startNewVerticalLine = true;

			for (double y = INPUT_NUMBERAREA[2]; y <= INPUT_NUMBERAREA[3] + 1 / DENSITY / 2; y += 1 / DENSITY) {

				// start new connecting line if needed
				if (startNewVerticalLine) {

					// calculate function value f(z)
					z = new Complex(x, y, true);
					calculateFunction();
					functionValue = z;

					// functionValue = function1(new Complex(x, y, true));
					currentPoint = getPointAt(functionValue.getRe(), functionValue.getIm());
					lastPoint = currentPoint;
//					drawPointAt(functionValue.getRe(), functionValue.getIm(), g);

					// change line color
					lineColor = new Color(
							(int) ((double) ((x - INPUT_NUMBERAREA[0]) * 255
									/ (INPUT_NUMBERAREA[1] - INPUT_NUMBERAREA[0]))),
							255 - (int) ((double) ((x - INPUT_NUMBERAREA[0]) * 255
									/ (INPUT_NUMBERAREA[1] - INPUT_NUMBERAREA[0]))),
							0, LINEOPACITY);

					startNewVerticalLine = false;

				} else {

					lastPoint = currentPoint;

					// calculate function value f(z)
					z = new Complex(x, y, true);
					calculateFunction();
					functionValue = z;

//					System.out.println("functionValue: f(z) = " + functionValue.getRe() + " + i( "
//							+ functionValue.getIm() + " )");

					// draw rect where f(z) landed
					currentPoint = getPointAt(functionValue.getRe(), functionValue.getIm());
					if(PAINTFUNCTIONPOINTS) {
						drawPointAt(functionValue.getRe(), functionValue.getIm(), g);
					}

				}

				// draw line from old to new position
				g.setColor(lineColor);
				if(PAINTVERTICALLINES) {
					g.drawLine(lastPoint.x, lastPoint.y, currentPoint.x, currentPoint.y);
				}
				
			}
		}

		// calculate function value x, then y direction, were doing this other
		// directions so that we can draw the connecting lines that connect all
		// functionValues with the same imaginary part before put into the function
		// go over each row while doing all x-es per column
		if(PAINTHORIZONTALLINES) {
			for (double y = INPUT_NUMBERAREA[2]; y <= INPUT_NUMBERAREA[3] + 1 / DENSITY / 2; y += 1 / DENSITY) {
				
				startNewVerticalLine = true;
				
				for (double x = INPUT_NUMBERAREA[0]; x <= INPUT_NUMBERAREA[1] + 1 / DENSITY / 2; x += 1 / DENSITY) {
	
					// test if we need to start a new connecting line
					if (startNewVerticalLine) {
	
						// calculate function value f(z)
						z = new Complex(x, y, true);
						calculateFunction();
						functionValue = z;
	
						currentPoint = getPointAt(functionValue.getRe(), functionValue.getIm());
						lastPoint = currentPoint;
						// drawPointAt(functionValue.getRe(), functionValue.getIm(), g);
	
						// change the connecting line color
						lineColor2 = new Color(
								255 - (int) ((double) ((y - INPUT_NUMBERAREA[2]) * (255 - SHIFTCOLORRANGE2)
										/ (INPUT_NUMBERAREA[3] - INPUT_NUMBERAREA[2]))),
								(int) ((double) ((y - INPUT_NUMBERAREA[2]) * (255)
										/ (INPUT_NUMBERAREA[3] - INPUT_NUMBERAREA[2]))),
								255, LINEOPACITY2);
	
						startNewVerticalLine = false;
	
					} else {
	
						lastPoint = currentPoint;
	
						// calculate functionValue f(z)
						z = new Complex(x, y, true);
						calculateFunction();
						functionValue = z;
	
	//					System.out.println("functionValue: f(z) = " + functionValue.getRe() + " + i( "
	//							+ functionValue.getIm() + " )");
	
						// draw rect where f(z) landed
						currentPoint = getPointAt(functionValue.getRe(), functionValue.getIm());
						// drawPointAt(functionValue.getRe(), functionValue.getIm(), g);
	
					}
	
					// draw line from old to new position
					g.setColor(lineColor2);
					g.drawLine(lastPoint.x, lastPoint.y, currentPoint.x, currentPoint.y);
	
				}
			}
		}

	}

	/**
	 * returns the first number that finds place on a gridline or a gridline +- a multiple of 1 / DENSITY
	 * 
	 * @return smallest x value that is on gridline +- factor / DENSITY
	 */
	private double getFirstXValue() {

		double result;

		// go above smallest gridline
		if (INPUT_NUMBERAREA[0] < 0) {
			result = (int) INPUT_NUMBERAREA[0];
		} else {
			result = 1 + (int) INPUT_NUMBERAREA[0];
		}

		// subtract 1 / DENSITY as long as we are in the INPUT_NUMBERAREA
		while (result - 1 / DENSITY > INPUT_NUMBERAREA[0]) {
			result -= 1 / DENSITY;
		}

		return result;

	}

	/**
	 * creates a Thread containing the function with index FUNCTIONINDEX, runs it and waits for it to die
	 */
	private void calculateFunction() {

		Thread t = functions.get(FUNCTIONINDEX);
		t.run();
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * draws two legends for the colors, one for the colors in y direction and one for the colors in x direction with bounds of INPUT_NUMBERAREA
	 * 
	 * @param g using Graphics to directly draw the legends on the JPanel
	 */
	private void drawColorLegend(Graphics g) {

		int size = PANELSIZE.width / 10; // legends is a square
		Point location = new Point(PANELSIZE.width - 3 * MARGIN - size, PANELSIZE.height - 4 * MARGIN - size);
		Point location2 = new Point(5 * MARGIN, PANELSIZE.height - 4 * MARGIN - size);

		// draw colors
		/** TODO sync colors with colors used to plot the function */
		for (int x = 0; x <= size; x++) {
			g.setColor(new Color((int) ((double) (x * 255 / size)), 255 - (int) ((double) (x * 255 / size)), 0));
			g.drawLine(location.x + x, location.y, location.x + x, location.y + size);

			g.setColor(new Color(255 - (int) ((double) (x * (255 - SHIFTCOLORRANGE2) / size)),
					(int) ((double) (x * (255) / size)), 255));
			g.drawLine(location2.x, location2.y + size - x, location2.x + size, location2.y + size - x);
		}

		// draw strings of INPUT_NUMBERAREA bounds
		g.setColor(Color.WHITE);
		g.drawString("x=", location.x - 2 * FONT.getSize(), location.y - 5);
		g.drawString(Integer.toString((int) Math.round(INPUT_NUMBERAREA[0])), location.x, location.y - 5);
		g.drawString(Integer.toString((int) Math.round(INPUT_NUMBERAREA[1])), location.x + size - 5, location.y - 5);

		g.drawString("y=", location2.x - 2 * FONT.getSize(), location2.y - 5);
		g.drawString(Integer.toString((int) Math.round(INPUT_NUMBERAREA[3])), location2.x - FONT.getSize(),
				location2.y + 10);
		g.drawString(Integer.toString((int) Math.round(INPUT_NUMBERAREA[2])), location2.x - FONT.getSize(),
				location2.y + size);

	}
	
	/**
	 * calculating f(z) = z
	 * 
	 * @param z is the result of f(z), so z = f(z)
	 */
	private void function0(Complex z) {

		this.z = z;

	}

	/**
	 * calculating f(z) = z^3 - z
	 * 
	 * @param z is the result of f(z), so z = f(z)
	 */
	private void function1(Complex z) {

		this.z = z.multiply(z).multiply(z).add(z.multiply(new Complex(-1, 0, true)));

	}

	/**
	 * calculating f(z) = sin(z)
	 * 
	 * @param z is the result of f(z), so z = f(z)
	 */
	private void function2(Complex z) {

		this.z = z.sin();

	}
	
	/**
	 * calculating f(z) = z^2
	 * 
	 * @param z is the result of f(z), so z = f(z)
	 */
	private void function3(Complex z) {

		this.z = z.multiply(z);

	}
	
	/**
	 * calculating f(z) = 1 / (z + 1)
	 * 
	 * @param z is the result of f(z), so z = f(z)
	 */
	private void function4(Complex z) {

		if(!((z.getRe() + 1 > 0.05 || z.getRe() + 1 < 0.05)  && Math.abs(z.getIm()) > 0.05)) {
			z = new Complex(10, 10, true);
			return;
		}
		this.z = new Complex(1, 0, true).divide(z.add(new Complex(1, 0, true)));

	}
	
	/**
	 * calculating f(z) = exp(z)
	 * 
	 * @param z is the result of f(z), so z = f(z)
	 */
	private void function5(Complex z) {

		this.z = z.exp();

	}

	/**
	 * draw a point at location x, y in the Coordinate system. x and y are NOT screen coordinates
	 * 
	 * @param x location x value
	 * @param y	location y value
	 * @param g	using Graphics to directly draw on the JPanel
	 */
	private void drawPointAt(double x, double y, Graphics g) {

		g.setColor(Color.YELLOW);
		
		// invert y axis and draw point on screen
		g.fillRect((int) (x * WIDTH_HEIGHT.x + ZERO.x - DOTWIDTH / 2),
				(int) ((-1) * y * WIDTH_HEIGHT.y + ZERO.y - DOTWIDTH / 2), DOTWIDTH, DOTWIDTH);

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
		return new Point((int) (x * WIDTH_HEIGHT.x + ZERO.x), (int) ((-1) * y * WIDTH_HEIGHT.y + ZERO.y));

	}
	
	/**
	 * sets the label of the function
	 */
	private void setFunctionLabel() {
		
		functionLabel = "f(z) = "; // label
		
		// get the label for the right function
		switch (FUNCTIONINDEX) {

		case '0':
			functionLabel += "z";
			break;
			
		case '1':
			functionLabel += "z^3 - z";
			break;

		case '2':
			functionLabel += "sin(z)";
			break;
			
		case '3':
			functionLabel += "z^2";
			break;
			
		case '4':
			functionLabel += "1 / (z + 1)";
			break;
			
		case '5':
			functionLabel += "exp(z)";
			break;

		default:
			functionLabel += "no switch case";

		}
		
	}
	
	public char getFunctionIndex() {
		return FUNCTIONINDEX;
	}
	
	public String[] getInputArea() {
		String[] result = new String[4];

		for (int i = 0; i < 4; i++) {
			if (INPUT_NUMBERAREA[i] == Math.PI) {
				result[i] = "Pi";
			} else if (INPUT_NUMBERAREA[i] == -Math.PI) {
				result[i] = "-Pi";
			} else if (INPUT_NUMBERAREA[i] == 2 * Math.PI){
				result[i] = "2Pi";
			} else if (INPUT_NUMBERAREA[i] == -2 * Math.PI){
				result[i] = "2Pi";
			} else {
				result[i] = Double.toString(INPUT_NUMBERAREA[i]);
			}
		}

		return result;

	}
	
	public int[] getOutputArea() {
		return OUTPUT_NUMBERAREA;
	}

}
