import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SpringLayout.Constraints;
import javax.swing.SwingConstants;

public class SettingsGui2 extends JFrame {

	private Font FONT = new Font("Ubuntu", Font.PLAIN, 40); // font used on the panel
	private Font INFOFONT = new Font("Ubuntu", Font.ITALIC, 20); // font used on the panel
	private String function; // name of function
	private SpringLayout layout = new SpringLayout();
	private JTextField inputArea, outputArea; // to set input and output area
	private JTextField density, calculationDensity, coordinatelineDensity, dotwidth;
	private JTextField functionField;
	private JCheckBox functionPoints, horizontalLines, verticalLines; // true if it should be drawn
	private JButton apply; // apply settings and close window
	private JLabel title; // window title
	private Gui parent;

	/* CONSTRUCTOR */

	/**
	 * builds this jframe
	 * 
	 * @param parent              parent gui
	 * @param size                size of this jframe
	 * @param function            the function that is plotted first
	 * @param functionInputField  input field for the function
	 * @param applySettingsButton apply button
	 */
	public SettingsGui2(Gui parent, Dimension size, String function, JTextField functionInputField,
			JButton applySettingsButton) {

		super("settings for complex function plotting || by Michael Roth || 5.4.2019");

		// save give variables
		this.function = function;
		this.functionField = functionInputField;
		this.apply = applySettingsButton;
		this.parent = parent;

		// set up this jframe
		this.setLayout(layout);
		this.setSize(size);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		// add gui elements
		addSettingsElements();

		this.pack();

	}

	/**
	 * adds all gui elements to this jframe
	 */
	private void addSettingsElements() {

		// function name as title
		title = new JLabel(" --------------    f(z) = " + function + "    -------------- ");
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setFont(FONT);
		SpringLayout.Constraints titleCons = new Constraints(title);
		titleCons.setWidth(Spring.constant(this.getSize().width));
		titleCons.setHeight(Spring.constant(50));
		layout.addLayoutComponent(title, titleCons);

		JLabel functionFieldLabel = new JLabel("f(z) = ");
		functionFieldLabel.setFont(FONT);
		SpringLayout.Constraints functionFieldLabelCons = new Constraints(functionFieldLabel);
		functionFieldLabelCons.setWidth(Spring.constant((int) (0.15 * this.getSize().width)));
		functionFieldLabelCons.setHeight(Spring.constant(50));
		functionFieldLabelCons.setY(titleCons.getConstraint(SpringLayout.SOUTH));
		layout.addLayoutComponent(functionFieldLabel, functionFieldLabelCons);

		functionField.setFont(FONT);
		functionField.setText(function);
		SpringLayout.Constraints functionFieldCons = new Constraints(functionField);
		functionFieldCons.setWidth(Spring.constant((int) (0.85 * this.getSize().width)));
		functionFieldCons.setHeight(Spring.constant(50));
		functionFieldCons.setX(functionFieldLabelCons.getConstraint(SpringLayout.EAST));
		functionFieldCons.setY(functionFieldLabelCons.getConstraint(SpringLayout.NORTH));
		layout.addLayoutComponent(functionField, functionFieldCons);

		JLabel functionFieldInfoLabel = new JLabel(
				"example function: f(z) = (sin(z)*cos(z) + 1) * exp(z / (0 - i) + 2 * i) - 2");
		functionFieldInfoLabel.setFont(INFOFONT);
		SpringLayout.Constraints functionFieldInfoLabelCons = new Constraints(functionFieldInfoLabel);
		functionFieldInfoLabelCons.setWidth(Spring.constant((int) (0.85 * this.getSize().width)));
		functionFieldInfoLabelCons.setHeight(Spring.constant(25));
		functionFieldInfoLabelCons.setX(functionFieldCons.getConstraint(SpringLayout.WEST));
		functionFieldInfoLabelCons.setY(functionFieldCons.getConstraint(SpringLayout.SOUTH));
		layout.addLayoutComponent(functionFieldInfoLabel, functionFieldInfoLabelCons);

		// show Textfields for definition area and output area
		JLabel inputAreaLabel = new JLabel("f: ");
		inputAreaLabel.setFont(FONT);
		SpringLayout.Constraints inputAreaLabelCons = new Constraints(inputAreaLabel);
		inputAreaLabelCons.setWidth(Spring.constant(this.getSize().width / 10));
		inputAreaLabelCons.setHeight(Spring.constant(50));
		inputAreaLabelCons
				.setY(Spring.sum(functionFieldInfoLabelCons.getConstraint(SpringLayout.SOUTH), Spring.constant(25)));
		layout.addLayoutComponent(inputAreaLabel, inputAreaLabelCons);

		JLabel inputInfoLabel2 = new JLabel("[  x_min,    x_max,    y_min,    y_max  ]");
		inputInfoLabel2.setFont(INFOFONT);
		SpringLayout.Constraints inputInfoLabel2Cons = new Constraints(inputInfoLabel2);
		inputInfoLabel2Cons.setWidth(Spring.constant((int) (0.35 * this.getSize().width)));
		inputInfoLabel2Cons.setHeight(Spring.constant(25));
		inputInfoLabel2Cons.setX(inputAreaLabelCons.getConstraint(SpringLayout.EAST));
		inputInfoLabel2Cons
				.setY(Spring.sum(inputAreaLabelCons.getConstraint(SpringLayout.NORTH), Spring.constant(-25)));
		layout.addLayoutComponent(inputInfoLabel2, inputInfoLabel2Cons);

		double[] input = parent.getInputAreaSquare();
		inputArea = new JTextField(
				(int) input[0] + ", " + (int) input[1] + ", " + (int) input[2] + ", " + (int) input[3]);
		inputArea.setFont(FONT);
		SpringLayout.Constraints inputAreaCons = new Constraints(inputArea);
		inputAreaCons.setWidth(Spring.constant((int) (0.35 * this.getSize().width)));
		inputAreaCons.setHeight(Spring.constant(50));
		inputAreaCons.setX(inputAreaLabelCons.getConstraint(SpringLayout.EAST));
		inputAreaCons.setY(inputAreaLabelCons.getConstraint(SpringLayout.NORTH));
		layout.addLayoutComponent(inputArea, inputAreaCons);

		JLabel outputAreaLabel = new JLabel(" ---> ");
		outputAreaLabel.setFont(FONT);
		outputAreaLabel.setHorizontalAlignment(JLabel.CENTER);
		SpringLayout.Constraints outputAreaLabelCons = new Constraints(outputAreaLabel);
		outputAreaLabelCons.setWidth(Spring.constant(this.getSize().width / 5));
		outputAreaLabelCons.setHeight(Spring.constant(50));
		outputAreaLabelCons.setX(inputAreaCons.getConstraint(SpringLayout.EAST));
		outputAreaLabelCons.setY(inputAreaLabelCons.getConstraint(SpringLayout.NORTH));
		layout.addLayoutComponent(outputAreaLabel, outputAreaLabelCons);

		JLabel outputInfoLabel2 = new JLabel("[  x_min,    x_max,    y_min,    y_max  ]");
		outputInfoLabel2.setFont(INFOFONT);
		SpringLayout.Constraints outputInfoLabel2Cons = new Constraints(outputInfoLabel2);
		outputInfoLabel2Cons.setWidth(Spring.constant((int) (0.35 * this.getSize().width)));
		outputInfoLabel2Cons.setHeight(Spring.constant(25));
		outputInfoLabel2Cons.setX(outputAreaLabelCons.getConstraint(SpringLayout.EAST));
		outputInfoLabel2Cons
				.setY(Spring.sum(outputAreaLabelCons.getConstraint(SpringLayout.NORTH), Spring.constant(-25)));
		layout.addLayoutComponent(outputInfoLabel2, outputInfoLabel2Cons);

		int[] output = parent.getOutputAreaSquare();
		outputArea = new JTextField(output[0] + ", " + output[1] + ", " + output[2] + ", " + output[3]);
		outputArea.setFont(FONT);
		SpringLayout.Constraints outputAreaCons = new Constraints(outputArea);
		outputAreaCons.setWidth(Spring.constant((int) (0.35 * this.getSize().width)));
		outputAreaCons.setHeight(Spring.constant(50));
		outputAreaCons.setX(outputAreaLabelCons.getConstraint(SpringLayout.EAST));
		outputAreaCons.setY(inputAreaLabelCons.getConstraint(SpringLayout.NORTH));
		layout.addLayoutComponent(outputArea, outputAreaCons);

		JLabel inputInfoLabel = new JLabel("only Integers");
		inputInfoLabel.setFont(INFOFONT);
		SpringLayout.Constraints inputInfoLabelCons = new Constraints(inputInfoLabel);
		inputInfoLabelCons.setWidth(Spring.constant((int) (0.3 * this.getSize().width)));
		inputInfoLabelCons.setHeight(Spring.constant(25));
		inputInfoLabelCons.setX(inputAreaCons.getConstraint(SpringLayout.WEST));
		inputInfoLabelCons.setY(inputAreaCons.getConstraint(SpringLayout.SOUTH));
		layout.addLayoutComponent(inputInfoLabel, inputInfoLabelCons);

		JLabel outputInfoLabel = new JLabel("only Integers");
		outputInfoLabel.setFont(INFOFONT);
		SpringLayout.Constraints outputInfoLabelCons = new Constraints(outputInfoLabel);
		outputInfoLabelCons.setWidth(Spring.constant((int) (0.3 * this.getSize().width)));
		outputInfoLabelCons.setHeight(Spring.constant(25));
		outputInfoLabelCons.setX(outputAreaCons.getConstraint(SpringLayout.WEST));
		outputInfoLabelCons.setY(outputAreaCons.getConstraint(SpringLayout.SOUTH));
		layout.addLayoutComponent(outputInfoLabel, outputInfoLabelCons);

		// add fields for density
		JLabel calculationDensityLabel = new JLabel("dots to calculate in n..n+1: ");
		calculationDensityLabel.setFont(FONT);
		SpringLayout.Constraints calculationDensityLabelCons = new Constraints(calculationDensityLabel);
		calculationDensityLabelCons.setWidth(Spring.constant((int) (0.8 * this.getSize().width)));
		calculationDensityLabelCons.setHeight(Spring.constant(50));
		calculationDensityLabelCons.setY(outputInfoLabelCons.getConstraint(SpringLayout.SOUTH));
		layout.addLayoutComponent(calculationDensityLabel, calculationDensityLabelCons);

		calculationDensity = new JTextField("20");
		calculationDensity.setFont(FONT);
		SpringLayout.Constraints calculationDensityCons = new Constraints(calculationDensity);
		calculationDensityCons.setWidth(Spring.constant((int) (0.2 * this.getSize().width)));
		calculationDensityCons.setHeight(Spring.constant(50));
		calculationDensityCons.setX(calculationDensityLabelCons.getConstraint(SpringLayout.EAST));
		calculationDensityCons.setY(calculationDensityLabelCons.getConstraint(SpringLayout.NORTH));
		layout.addLayoutComponent(calculationDensity, calculationDensityCons);

		JLabel densityLabel = new JLabel("dots to paint in n..n+1: ");
		densityLabel.setFont(FONT);
		SpringLayout.Constraints densityLabelCons = new Constraints(densityLabel);
		densityLabelCons.setWidth(Spring.constant((int) (0.8 * this.getSize().width)));
		densityLabelCons.setHeight(Spring.constant(50));
		densityLabelCons.setY(calculationDensityCons.getConstraint(SpringLayout.SOUTH));
		layout.addLayoutComponent(densityLabel, densityLabelCons);

		density = new JTextField("20");
		density.setFont(FONT);
		SpringLayout.Constraints densityCons = new Constraints(density);
		densityCons.setWidth(Spring.constant((int) (0.2 * this.getSize().width)));
		densityCons.setHeight(Spring.constant(50));
		densityCons.setX(densityLabelCons.getConstraint(SpringLayout.EAST));
		densityCons.setY(densityLabelCons.getConstraint(SpringLayout.NORTH));
		layout.addLayoutComponent(density, densityCons);

		JLabel densityInfoLabel = new JLabel("density >= 4");
		densityInfoLabel.setFont(INFOFONT);
		SpringLayout.Constraints densityInfoLabelCons = new Constraints(densityInfoLabel);
		densityInfoLabelCons.setWidth(Spring.constant((int) (0.2 * this.getSize().width)));
		densityInfoLabelCons.setHeight(Spring.constant(25));
		densityInfoLabelCons.setX(densityCons.getConstraint(SpringLayout.WEST));
		densityInfoLabelCons.setY(densityCons.getConstraint(SpringLayout.SOUTH));
		layout.addLayoutComponent(densityInfoLabel, densityInfoLabelCons);

		JLabel coordinatelineDensityLabel = new JLabel("coordinate lines to draw between n..n+1: ");
		coordinatelineDensityLabel.setFont(FONT);
		SpringLayout.Constraints coordinatelineDensityLabelCons = new Constraints(coordinatelineDensityLabel);
		coordinatelineDensityLabelCons.setWidth(Spring.constant((int) (0.8 * this.getSize().width)));
		coordinatelineDensityLabelCons.setHeight(Spring.constant(50));
		coordinatelineDensityLabelCons.setY(densityInfoLabelCons.getConstraint(SpringLayout.SOUTH));
		layout.addLayoutComponent(coordinatelineDensityLabel, coordinatelineDensityLabelCons);

		coordinatelineDensity = new JTextField("1");
		coordinatelineDensity.setFont(FONT);
		SpringLayout.Constraints coordinatelineDensityCons = new Constraints(coordinatelineDensity);
		coordinatelineDensityCons.setWidth(Spring.constant((int) (0.2 * this.getSize().width)));
		coordinatelineDensityCons.setHeight(Spring.constant(50));
		coordinatelineDensityCons.setX(coordinatelineDensityLabelCons.getConstraint(SpringLayout.EAST));
		coordinatelineDensityCons.setY(coordinatelineDensityLabelCons.getConstraint(SpringLayout.NORTH));
		layout.addLayoutComponent(coordinatelineDensity, coordinatelineDensityCons);

		JLabel coordinatelineDensityInfoLabel = new JLabel("coord.lineDensity >= 1");
		coordinatelineDensityInfoLabel.setFont(INFOFONT);
		SpringLayout.Constraints coordinatelineDensityInfoLabelCons = new Constraints(coordinatelineDensityInfoLabel);
		coordinatelineDensityInfoLabelCons.setWidth(Spring.constant((int) (0.2 * this.getSize().width)));
		coordinatelineDensityInfoLabelCons.setHeight(Spring.constant(25));
		coordinatelineDensityInfoLabelCons.setX(coordinatelineDensityCons.getConstraint(SpringLayout.WEST));
		coordinatelineDensityInfoLabelCons.setY(coordinatelineDensityCons.getConstraint(SpringLayout.SOUTH));
		layout.addLayoutComponent(coordinatelineDensityInfoLabel, coordinatelineDensityInfoLabelCons);

		JLabel dotwidthLabel = new JLabel("width of a dot at location f(z): ");
		dotwidthLabel.setFont(FONT);
		SpringLayout.Constraints dotwidthLabelCons = new Constraints(dotwidthLabel);
		dotwidthLabelCons.setWidth(Spring.constant((int) (0.8 * this.getSize().width)));
		dotwidthLabelCons.setHeight(Spring.constant(50));
		dotwidthLabelCons.setY(coordinatelineDensityInfoLabelCons.getConstraint(SpringLayout.SOUTH));
		layout.addLayoutComponent(dotwidthLabel, dotwidthLabelCons);

		dotwidth = new JTextField("3");
		dotwidth.setFont(FONT);
		SpringLayout.Constraints dotwidthCons = new Constraints(dotwidth);
		dotwidthCons.setWidth(Spring.constant((int) (0.2 * this.getSize().width)));
		dotwidthCons.setHeight(Spring.constant(50));
		dotwidthCons.setX(dotwidthLabelCons.getConstraint(SpringLayout.EAST));
		dotwidthCons.setY(dotwidthLabelCons.getConstraint(SpringLayout.NORTH));
		layout.addLayoutComponent(dotwidth, dotwidthCons);

		// add checkboxes
		JLabel functionPointsLabel = new JLabel("painting points where f(z) is located:");
		functionPointsLabel.setFont(FONT);
		SpringLayout.Constraints functionPointsLabelCons = new Constraints(functionPointsLabel);
		functionPointsLabelCons.setWidth(Spring.constant((int) (0.8 * this.getSize().width)));
		functionPointsLabelCons.setHeight(Spring.constant(50));
		functionPointsLabelCons.setY(dotwidthLabelCons.getConstraint(SpringLayout.SOUTH));
		layout.addLayoutComponent(functionPointsLabel, functionPointsLabelCons);

		functionPoints = new JCheckBox();
		functionPoints.setSelected(false);
		SpringLayout.Constraints functionPointsCons = new Constraints(functionPoints);
		functionPointsCons.setX(Spring.constant((int) (0.8 * this.getSize().width)));
		functionPointsCons.setHeight(Spring.constant(50));
		functionPointsCons.setY(functionPointsLabelCons.getConstraint(SpringLayout.NORTH));
		layout.addLayoutComponent(functionPoints, functionPointsCons);

		JLabel horizontalLinesLabel = new JLabel("painting horizontal lines from input grid:");
		horizontalLinesLabel.setFont(FONT);
		SpringLayout.Constraints horizontalLinesLabelCons = new Constraints(horizontalLinesLabel);
		horizontalLinesLabelCons.setWidth(Spring.constant((int) (0.8 * this.getSize().width)));
		horizontalLinesLabelCons.setHeight(Spring.constant(50));
		horizontalLinesLabelCons.setY(functionPointsLabelCons.getConstraint(SpringLayout.SOUTH));
		layout.addLayoutComponent(horizontalLinesLabel, horizontalLinesLabelCons);

		horizontalLines = new JCheckBox();
		horizontalLines.setSelected(true);
		SpringLayout.Constraints horizontalLinesCons = new Constraints(horizontalLines);
		horizontalLinesCons.setX(Spring.constant((int) (0.8 * this.getSize().width)));
		horizontalLinesCons.setHeight(Spring.constant(50));
		horizontalLinesCons.setY(functionPointsLabelCons.getConstraint(SpringLayout.SOUTH));
		layout.addLayoutComponent(horizontalLines, horizontalLinesCons);

		JLabel verticalLinesLabel = new JLabel("painting vertical lines from input grid:");
		verticalLinesLabel.setFont(FONT);
		SpringLayout.Constraints verticalLinesLabelCons = new Constraints(verticalLinesLabel);
		verticalLinesLabelCons.setWidth(Spring.constant((int) (0.8 * this.getSize().width)));
		verticalLinesLabelCons.setHeight(Spring.constant(50));
		verticalLinesLabelCons.setY(horizontalLinesLabelCons.getConstraint(SpringLayout.SOUTH));
		layout.addLayoutComponent(verticalLinesLabel, verticalLinesLabelCons);

		verticalLines = new JCheckBox();
		verticalLines.setSelected(true);
		SpringLayout.Constraints verticalLinesCons = new Constraints(verticalLines);
		verticalLinesCons.setX(Spring.constant((int) (0.8 * this.getSize().width)));
		verticalLinesCons.setHeight(Spring.constant(50));
		verticalLinesCons.setY(horizontalLinesLabelCons.getConstraint(SpringLayout.SOUTH));
		layout.addLayoutComponent(verticalLines, verticalLinesCons);

		// apply Button
		apply.setFont(FONT);
		SpringLayout.Constraints applyCons = new Constraints(apply);
		applyCons.setWidth(Spring.constant(this.getSize().width));
		applyCons.setHeight(Spring.constant(50));
		applyCons.setY(Spring.constant(this.getSize().height - 50));
		layout.addLayoutComponent(apply, applyCons);

		// add components
		this.add(title);
		this.add(functionFieldLabel);
		this.add(functionField);
		this.add(functionFieldInfoLabel);
//		this.add(comboBox);
		this.add(inputInfoLabel2);
		this.add(inputAreaLabel);
		this.add(inputArea);
		this.add(outputInfoLabel2);
		this.add(outputAreaLabel);
		this.add(outputArea);
		this.add(inputInfoLabel);
		this.add(outputInfoLabel);
		this.add(functionPoints);
		this.add(horizontalLines);
		this.add(verticalLines);
		this.add(functionPointsLabel);
		this.add(horizontalLinesLabel);
		this.add(verticalLinesLabel);
		this.add(densityLabel);
		this.add(density);
		this.add(calculationDensityLabel);
		this.add(calculationDensity);
		this.add(densityInfoLabel);
		this.add(coordinatelineDensityLabel);
		this.add(coordinatelineDensity);
		this.add(coordinatelineDensityInfoLabel);
		this.add(dotwidthLabel);
		this.add(dotwidth);
		this.add(apply);

	}

	/* GETTERS */
	
	/**
	 * @return true if we paint function points
	 */
	public boolean paintFunctionPoints() {
		return functionPoints.isSelected();
	}

	/**
	 * @return true if we paint horizontal lines
	 */
	public boolean paintHorizontalLines() {
		return horizontalLines.isSelected();
	}

	/**
	 * @return true if we paint vertical lines
	 */
	public boolean paintVerticalLines() {
		return verticalLines.isSelected();
	}

	/**
	 * @return the dotWidth
	 */
	public int getDotWidth() {
		return Integer.parseInt(dotwidth.getText());
	}

	/**
	 * @return the density
	 */
	public int getDensity() {
		return Integer.parseInt(density.getText());
	}

	/**
	 * @return the calculation density
	 */
	public int getCalculationDensity() {
		return Integer.parseInt(calculationDensity.getText());
	}

	/**
	 * @return the coordinateline desnity
	 */
	public int getCoordinatelineDensity() {
		return Integer.parseInt(coordinatelineDensity.getText());
	}

	/**
	 * @return the inputArea
	 */
	public double[] getInputArea() {
		double[] result = new double[4];
		String[] split = inputArea.getText().split(",");

		for (int i = 0; i < 4; i++) {
			split[i] = split[i].replaceAll("\\s", "");

			switch (split[i]) {

			case "Pi":
				result[i] = Math.PI;
				break;

			case "-Pi":
				result[i] = -Math.PI;
				break;

			case "2Pi":
				result[i] = 2 * Math.PI;
				break;

			case "-2Pi":
				result[i] = -2 * Math.PI;
				break;

			default:
				result[i] = Double.parseDouble(split[i]);

			}
		}

		return result;

	}

	/**
	 * @return the outputArea
	 */
	public int[] getOutputArea() {

		if (outputArea.getText().equals("auto")) {
			return null;
		}

		int[] result = new int[4];
		String[] split = outputArea.getText().split(",");

		for (int i = 0; i < 4; i++) {
			split[i] = split[i].replaceAll("\\s", "");
			result[i] = Integer.parseInt(split[i]);
		}

		return result;
	}

	/**
	 * @return the function to plot
	 */
	public String getFunction() {
		return functionField.getText().replaceAll("\\s", "");
	}
	
	/* SETTERS */
	/**
	 * @param values the new inputArea as double[] {..., ..., ..., ...}
	 */
	public void setInputArea(double[] values) {
		String result = "";
		for (int i = 0; i < 4; i++) {
			if (values[i] == Math.PI) {
				result += "Pi";
			} else if (values[i] == -Math.PI) {
				result += "-Pi";
			} else if (values[i] == 2 * Math.PI) {
				result += "2Pi";
			} else if (values[i] == -2 * Math.PI) {
				result += "-2Pi";
			} else {
				result += values[i];
			}
			result += ", ";
		}
		// remove last ', '
		result = result.substring(0, result.length() - 2);
		inputArea.setText(result);
	}

	/**
	 * @param values the new outputArea as int[] {..., ..., ..., ...}
	 */
	public void setOutputArea(int[] values) {
		String result = "";
		for (int i = 0; i < 4; i++) {
			result += values[i] + ", ";
		}
		// remove last ','
		result = result.substring(0, result.length() - 1);
		outputArea.setText(result);
	}

	/**
	 * @param value the new density
	 */
	public void setDensity(double value) {
		density.setText(Double.toString(value));
	}

	/**
	 * @param name sets the new title
	 */
	public void setTitleLabel(String name) {
		title.setText(" ---------- " + name + " ---------- ");
	}

}
