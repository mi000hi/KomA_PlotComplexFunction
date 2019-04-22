import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class Leinwand3D extends ParentLeinwand3D {

	private JPanel panel; // this jpanel
	private Gui parent; // parent gui, this is where we get information from
	private String name; // name and title of this jpanel
	private JLabel titleLabel; // label that shows the name

	private Dimension panelSize, plotSize; // size of this panel and of the area that we will paint
	private final int MARGIN; // margin on the panel

	private Point zero = new Point(); // zero point of the coordinate system
	private double[] inputArea; // given input area, size of the x-y-area
	private int circleWidth = 5; // width of a circle
	private boolean drawLines, drawDots; // true if we draw these things

	/* CONSTRUCTOR */

	/**
	 * we will set up this panel here
	 * 
	 * @param parent parent gui, this is where we can get informations from
	 */
	public Leinwand3D(Gui parent) {

		// take variables
		this.parent = parent;

		// initialize final variables
		MARGIN = 5;

		// add a title label to this jpanel
		titleLabel = new JLabel();
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setFont(new Font("Ubuntu", Font.PLAIN, 30));
		this.add(titleLabel, BorderLayout.PAGE_START);

		// set up this jpanel
		this.setOpaque(true);
		this.setBackground(Color.black);
//		this.setBorder(BorderFactory.createLineBorder(Color.RED));

	}

	/* PAINTCOMPONENT */

	/**
	 * this function paints everything to paint, the 3d graph and the coordinate
	 * system
	 * 
	 * @param g graphics that is needed to paint
	 */
	public void paintComponent(Graphics g) {

		// clear the painting, resets g
		super.paintComponent(g);

		// adjust size
		panelSize = this.getSize();
		int squareSize = Math.min((int) (panelSize.getWidth() / 2), (int) (panelSize.getHeight() / 2));
		plotSize = new Dimension(squareSize, squareSize);
//		System.out.println("panel size: " + panelSize.width + "x" + panelSize.height);

		// set zero point accordingly to size
		double minZ = Math.max(2 * inputArea[0], getMinFunctionValue());
		if (minZ >= 0) {
			zero.setLocation(panelSize.getWidth() / 2, 3 * panelSize.getHeight() / 4);
		} else {
			zero.setLocation(panelSize.getWidth() / 2, panelSize.getHeight() / 2);
		}

		// draw the coordinate system
		drawCoordinateSystem(g);

		// draw the function, this will draw a coordinate system again
		drawFunction(g);

	}

	/**
	 * draws the 3d graph with points, or from a grid
	 * 
	 * @param g use graphics to draw on the jpanel
	 */
	private void drawFunction(Graphics g) {

		// return if theres nothing to draw
		if (functionInputPoints.size() == 0) {
			return;
		}

		// get minimum and maximum z value for the fancy color flow
		double minZ = Math.max(2 * inputArea[0], getMinFunctionValue());
		double maxZ;
		if (minZ >= 0) {
			maxZ = Math.min(4 * inputArea[1], getMaxFunctionValue());
//			System.out.println(2 * inputArea[1] + "  " + Collections.max(functionPoints.stream().map(e -> e.getZ()).collect(Collectors.toList())));
		} else {
			maxZ = Math.min(2 * inputArea[1], getMaxFunctionValue());
		}
//		System.out.println("function min, max: " + minZ + ", " + maxZ);

		Point currentPoint2D = new Point(0, 0); // the current point to draw on the screen
		Point lastPoint2D;
		Point3D currentPoint3D; // the current point to plot
		double currentFunctionValue;
		Complex currentInputValue;

		// if we want to draw dots
		if (drawDots) {

			int currentY = (int) inputArea[2]; // for coordinate system painting
			int currentX = (int) inputArea[0]; // for coordinate system painting
			Point3D lineStart, lineEnd;
			Point lineStart2, lineEnd2;

			boolean startNewLine = false; // is false while we draw all y coordinates at a fixed x coordinate

			Point numberOfPoints = parent.getNumberOfPoints();

			for (int x = 0; x < numberOfPoints.x; x++) {

				startNewLine = true;
				currentY = (int) inputArea[2];

				// draw horizontal coordinate system lines
				if (functionInputPoints.get(x * numberOfPoints.y).getRe() - currentX > 0) {
					lineStart = new Point3D(currentX, inputArea[2], 0);
					lineEnd = new Point3D(currentX, inputArea[3], 0);

					lineStart2 = get2DScreenCoordinates(lineStart);
					lineEnd2 = get2DScreenCoordinates(lineEnd);

					if (currentX == 0) {
						g.setColor(Color.WHITE);
						g.fillRect(lineStart2.x, lineStart2.y + 1, lineEnd2.x - lineStart2.x, 2);
					} else {
						g.setColor(Color.GRAY);
						g.fillRect(lineStart2.x, lineStart2.y, lineEnd2.x - lineStart2.x, 1);
					}

					currentX++;
				}

				for (int y = 0; y < numberOfPoints.y; y++) {

//					System.out.println("x, y: " + x + ", " + y);
					currentFunctionValue = getFunctionValue(x * numberOfPoints.y + y);
					currentInputValue = functionInputPoints.get(x * numberOfPoints.y + y);

					if (currentInputValue == null) {
						continue;
					}

					currentPoint3D = new Point3D(currentInputValue.getRe(), currentInputValue.getIm(),
							currentFunctionValue);

					if (currentPoint3D.getY() - currentY > 0) {

						Point point = get2DScreenCoordinates(
								new Point3D(currentPoint3D.getX(), currentPoint3D.getY(), 0));

						if (currentY == 0) {
							g.setColor(Color.WHITE);
							g.fillRect(point.x, point.y, 2, 2);
							// TODO: there are some dots in a circle around z axis resulting from the line
							// above, plot f(z) = 1/z for example
//							System.out.println("x, y = " + point.x + ", " + point.y);

							// draw z axis
							if (currentX == 0) {
								point = get2DScreenCoordinates(new Point3D(0, 0, 1.2 * maxZ));

								g.fillRect(point.x - 1, point.y, 3, zero.y - point.y);
							}
						} else {
							g.setColor(Color.GRAY);
							g.fillRect(point.x, point.y, 1, 1);
						}

						currentY++;
					}

					if (currentFunctionValue <= maxZ && currentFunctionValue >= minZ) {
						if (currentFunctionValue != parent.getSecretNumber()) {

							if (startNewLine) {
								currentPoint2D = get2DScreenCoordinates(currentPoint3D);
								lastPoint2D = currentPoint2D;
								startNewLine = false;
							} else {
								lastPoint2D = currentPoint2D;
								currentPoint2D = get2DScreenCoordinates(currentPoint3D);
							}

							g.setColor(getColor(currentFunctionValue, minZ, maxZ));

							g.fillOval(currentPoint2D.x - circleWidth / 2, currentPoint2D.y - circleWidth / 2,
									circleWidth, circleWidth);
//							g.drawLine(lastPoint2D.x, lastPoint2D.y, currentPoint2D.x, currentPoint2D.y);

						} else {
//							System.out.println("starting new line");
							startNewLine = true;
						}
					}

				}

			}

		}

	}

	/**
	 * draws the coordinate system TODO: z-axis
	 * 
	 * @param g using graphics to directly draw on the jpanel
	 */
	private void drawCoordinateSystem(Graphics g) {

		Point3D lineStart, lineEnd; // line in 3d coordinates
		Point lineStart2, lineEnd2; // line in 2d screen coordinates

		g.setColor(Color.GRAY);

		// draw x-grid
		for (int x = (int) inputArea[0]; x <= inputArea[1]; x++) {
			lineStart = new Point3D(x, inputArea[2], 0);
			lineEnd = new Point3D(x, inputArea[3], 0);

			lineStart2 = get2DScreenCoordinates(lineStart);
			lineEnd2 = get2DScreenCoordinates(lineEnd);

			g.drawLine(lineStart2.x, lineStart2.y, lineEnd2.x, lineEnd2.y);
		}

		// draw y-grid
		for (int y = (int) inputArea[2]; y <= inputArea[3]; y++) {
			lineStart = new Point3D(inputArea[0], y, 0);
			lineEnd = new Point3D(inputArea[1], y, 0);

			lineStart2 = get2DScreenCoordinates(lineStart);
			lineEnd2 = get2DScreenCoordinates(lineEnd);

			g.drawLine(lineStart2.x, lineStart2.y, lineEnd2.x, lineEnd2.y);
		}

		// draw x axis
		g.setColor(Color.WHITE);

		lineStart2 = get2DScreenCoordinates(new Point3D(inputArea[0], 0, 0));
		lineEnd2 = get2DScreenCoordinates(new Point3D(inputArea[1], 0, 0));

		g.drawLine(lineStart2.x - 1, lineStart2.y, lineEnd2.x - 1, lineEnd2.y);
		g.drawLine(lineStart2.x, lineStart2.y, lineEnd2.x, lineEnd2.y);
		g.drawLine(lineStart2.x + 1, lineStart2.y, lineEnd2.x + 1, lineEnd2.y);

		// draw y axis
		lineStart2 = get2DScreenCoordinates(new Point3D(0, inputArea[2], 0));
		lineEnd2 = get2DScreenCoordinates(new Point3D(0, inputArea[3], 0));

		g.drawLine(lineStart2.x, lineStart2.y - 1, lineEnd2.x, lineEnd2.y - 1);
		g.drawLine(lineStart2.x, lineStart2.y, lineEnd2.x, lineEnd2.y);
		g.drawLine(lineStart2.x, lineStart2.y + 1, lineEnd2.x, lineEnd2.y + 1);

	}

	/* GETTERS */

	/**
	 * returns 2d screen coordinates of a given 3d point
	 * 
	 * @param point given point in 3d
	 * @return 2d screen coordinates of the 3d point
	 */
	private Point get2DScreenCoordinates(Point3D point) {

		Point result = new Point();

		// y and z coordinates, x = 0
		result.x = (int) (point.getY() * (plotSize.getWidth() / 2 - MARGIN) / inputArea[1]);
		result.y = (int) (point.getZ() * (plotSize.getHeight() / 2 - MARGIN) / inputArea[3]);

		// x coordinate
		result.x -= (int) (point.getX() * (plotSize.getWidth() / 2 - MARGIN) / inputArea[1] / Math.sqrt(2));
		result.y -= (int) (point.getX() * (plotSize.getHeight() / 2 - MARGIN) / inputArea[3] / (2 * Math.sqrt(2)));

		// adjust zero to coordinate system zero
		result.x += zero.x;
		result.y *= (-1);
		result.y += zero.y;

		return result;

	}

	/**
	 * returns a color according to the value in [min...max] so that we get that
	 * fancy color change colors reach from min = yellow to red to blue to max =
	 * green
	 * 
	 * @param value where we are in [min...max]
	 * @param min   the minimum of the colorchange-area
	 * @param max   the maximum of the colorchange-area
	 * @return the color according to value in [min...max]
	 */
	private Color getColor(double value, double min, double max) {

//		System.out.println(min + " " + value + " " + max);
		Color result; // this will be returned

		// make 3 colorchanges
		double change1 = (max - min) / 3;
		double change2 = 2 * (max - min) / 3;
		// System.out.println("change at x = " + change1 + ", " + change2);

		double shiftedValue = value - min;

		if (shiftedValue <= change1) { // from yellow to red

//			System.out.println(
//					((int) (shiftedValue * 255 / change1)) + " " + (255 - (int) (shiftedValue * 255 / change1)));
			result = new Color(255, 255 - (int) (shiftedValue * 255 / change1), 0, 255);

		} else if (shiftedValue <= change2) { // from red to blue

			result = new Color(255 - (int) ((shiftedValue - change1) * 255 / change1), 0,
					(int) ((shiftedValue - change1) * 255 / change1), 255);

		} else { // from blue to green

			result = new Color(0, (int) ((shiftedValue - change2) * 255 / change1),
					255 - (int) ((shiftedValue - change2) * 255 / change1), 255);

		}

		return result;
	}

	/**
	 * @return the biggest functionvalue
	 */
	private double getMaxFunctionValue() {

		double maxValue = getFunctionValue(0);

		for (int i = 0; i < functionOutputPoints.size(); i++) {
			if (getFunctionValue(i) > maxValue) {
				maxValue = getFunctionValue(i);
			}
		}

		return maxValue;

	}

	/**
	 * @return the smallest functionvalue
	 */
	private double getMinFunctionValue() {

		double minValue = getFunctionValue(0);

		for (int i = 0; i < functionOutputPoints.size(); i++) {
			if (getFunctionValue(i) < minValue) {
				minValue = getFunctionValue(i);
			}
		}

		return minValue;

	}

	/* SETTERS */

	/**
	 * @param title part of the title to set
	 */
	public void setTitle(String title) {

		titleLabel.setText("f(z(x, y) = x + iy) = " + title);

	}

	/**
	 * @param inputArea the area and size of the x-y-plane
	 */
	public void setInputArea(double[] inputArea) {
		this.inputArea = inputArea;
	}

	/**
	 * sets the given variables to plot a new function
	 * 
	 * @param circleWidth with of the circles in the plot, at (x, y, g(f(z)))
	 * @param drawDots    true if we draw dots
	 * @param drawLines   true if we draw the lines
	 */
	public void setPlotSettings(int circleWidth, boolean drawDots, boolean drawLines) {

		this.circleWidth = circleWidth;
		this.drawDots = drawDots;
		this.drawLines = drawLines;

	}

	/**
	 * @param values the new function values to plot
	 */
	public void setFunctionValues(ArrayList<Complex> functionInputPoints, ArrayList<Complex> functionOutputPoints) {

		this.functionInputPoints = functionInputPoints;
		this.functionOutputPoints = functionOutputPoints;

		inputArea = parent.getInputAreaSquare();

	}

}
