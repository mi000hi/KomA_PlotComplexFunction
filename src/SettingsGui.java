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

public class SettingsGui extends JFrame {

	private final Font FONT = new Font("Ubuntu", Font.PLAIN, 40); // font used on the panel
	private final Font INFOFONT = new Font("Ubuntu", Font.ITALIC, 20); // font used on the panel
	private final int FUNCTIONINDEX; // index of function
	private Leinwand canvas;
	private SpringLayout layout = new SpringLayout();
	private final Dimension SIZE; // size of the jframe
	private JComboBox comboBox; // to select which function to paint
	private JTextField inputArea, outputArea; // to set input and output area
	private JTextField density, coordinatelineDensity, dotwidth;
	private JCheckBox functionPoints, horizontalLines, verticalLines; // true if it should be drawn
	private JButton apply; // apply settings and close window
	private JLabel title; // window title

	public SettingsGui(int functionIndex, Leinwand leinwand, Dimension size, String[] functionLabels,
			JButton applySettingsButton, JComboBox comboBox) {

		super("settings for complex function plotting || by Michael Roth || 5.4.2019");
		FUNCTIONINDEX = functionIndex;
		SIZE = size;
		canvas = leinwand;
		this.apply = applySettingsButton;

		this.comboBox = comboBox;

		this.setLayout(layout);
		this.setSize(SIZE);
		addSettingsElements();

		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

	}

	private void addSettingsElements() {

		// function name as title
		title = new JLabel(" ---------- " + comboBox.getSelectedItem() + " ---------- ");
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setFont(FONT);
		SpringLayout.Constraints titleCons = new Constraints(title);
		titleCons.setWidth(Spring.constant(SIZE.width));
		titleCons.setHeight(Spring.constant(50));
		layout.addLayoutComponent(title, titleCons);

		// add function combo box
		comboBox.setFont(FONT);
		SpringLayout.Constraints comboBoxCons = new Constraints(comboBox);
		comboBoxCons.setWidth(Spring.constant(SIZE.width));
		comboBoxCons.setHeight(Spring.constant(50));
		comboBoxCons.setY(titleCons.getConstraint(SpringLayout.SOUTH));
		layout.addLayoutComponent(comboBox, comboBoxCons);
		
		// show Textfields for definition area and output area
		JLabel inputAreaLabel = new JLabel("f: ");
		inputAreaLabel.setFont(FONT);
		SpringLayout.Constraints inputAreaLabelCons = new Constraints(inputAreaLabel);
		inputAreaLabelCons.setWidth(Spring.constant(SIZE.width / 10));
		inputAreaLabelCons.setHeight(Spring.constant(50));
		inputAreaLabelCons.setY(Spring.sum(comboBoxCons.getConstraint(SpringLayout.SOUTH), Spring.constant(25)));
		layout.addLayoutComponent(inputAreaLabel, inputAreaLabelCons);
		
		JLabel inputInfoLabel2 = new JLabel("[  x_min,    x_max,    y_min,    y_max  ]");
		inputInfoLabel2.setFont(INFOFONT);
		SpringLayout.Constraints inputInfoLabel2Cons = new Constraints(inputInfoLabel2);
		inputInfoLabel2Cons.setWidth(Spring.constant((int) (0.35 * SIZE.width)));
		inputInfoLabel2Cons.setHeight(Spring.constant(25));
		inputInfoLabel2Cons.setX(inputAreaLabelCons.getConstraint(SpringLayout.EAST));
		inputInfoLabel2Cons.setY(Spring.sum(inputAreaLabelCons.getConstraint(SpringLayout.NORTH), Spring.constant(-25)));
		layout.addLayoutComponent(inputInfoLabel2, inputInfoLabel2Cons);

		String[] input = canvas.getInputArea();
		inputArea = new JTextField(input[0] + ", " + input[1] + ", " + input[2] + ", " + input[3]);
		inputArea.setFont(FONT);
		SpringLayout.Constraints inputAreaCons = new Constraints(inputArea);
		inputAreaCons.setWidth(Spring.constant((int) (0.35 * SIZE.width)));
		inputAreaCons.setHeight(Spring.constant(50));
		inputAreaCons.setX(inputAreaLabelCons.getConstraint(SpringLayout.EAST));
		inputAreaCons.setY(inputAreaLabelCons.getConstraint(SpringLayout.NORTH));
		layout.addLayoutComponent(inputArea, inputAreaCons);

		JLabel outputAreaLabel = new JLabel(" ---> ");
		outputAreaLabel.setFont(FONT);
		outputAreaLabel.setHorizontalAlignment(JLabel.CENTER);
		SpringLayout.Constraints outputAreaLabelCons = new Constraints(outputAreaLabel);
		outputAreaLabelCons.setWidth(Spring.constant(SIZE.width / 5));
		outputAreaLabelCons.setHeight(Spring.constant(50));
		outputAreaLabelCons.setX(inputAreaCons.getConstraint(SpringLayout.EAST));
		outputAreaLabelCons.setY(inputAreaLabelCons.getConstraint(SpringLayout.NORTH));
		layout.addLayoutComponent(outputAreaLabel, outputAreaLabelCons);

		JLabel outputInfoLabel2 = new JLabel("[  x_min,    x_max,    y_min,    y_max  ]");
		outputInfoLabel2.setFont(INFOFONT);
		SpringLayout.Constraints outputInfoLabel2Cons = new Constraints(outputInfoLabel2);
		outputInfoLabel2Cons.setWidth(Spring.constant((int) (0.35 * SIZE.width)));
		outputInfoLabel2Cons.setHeight(Spring.constant(25));
		outputInfoLabel2Cons.setX(outputAreaLabelCons.getConstraint(SpringLayout.EAST));
		outputInfoLabel2Cons.setY(Spring.sum(outputAreaLabelCons.getConstraint(SpringLayout.NORTH), Spring.constant(-25)));
		layout.addLayoutComponent(outputInfoLabel2, outputInfoLabel2Cons);
		
		int[] output = canvas.getOutputArea();
		outputArea = new JTextField(output[0] + ", " + output[1] + ", " + output[2] + ", " + output[3]);
		outputArea.setFont(FONT);
		SpringLayout.Constraints outputAreaCons = new Constraints(outputArea);
		outputAreaCons.setWidth(Spring.constant((int) (0.35 * SIZE.width)));
		outputAreaCons.setHeight(Spring.constant(50));
		outputAreaCons.setX(outputAreaLabelCons.getConstraint(SpringLayout.EAST));
		outputAreaCons.setY(inputAreaLabelCons.getConstraint(SpringLayout.NORTH));
		layout.addLayoutComponent(outputArea, outputAreaCons);

		JLabel inputInfoLabel = new JLabel("only Doubles, -2Pi, -Pi, Pi, 2Pi");
		inputInfoLabel.setFont(INFOFONT);
		SpringLayout.Constraints inputInfoLabelCons = new Constraints(inputInfoLabel);
		inputInfoLabelCons.setWidth(Spring.constant((int) (0.3 * SIZE.width)));
		inputInfoLabelCons.setHeight(Spring.constant(25));
		inputInfoLabelCons.setX(inputAreaCons.getConstraint(SpringLayout.WEST));
		inputInfoLabelCons.setY(inputAreaCons.getConstraint(SpringLayout.SOUTH));
		layout.addLayoutComponent(inputInfoLabel, inputInfoLabelCons);

		JLabel outputInfoLabel = new JLabel("only Integers");
		outputInfoLabel.setFont(INFOFONT);
		SpringLayout.Constraints outputInfoLabelCons = new Constraints(outputInfoLabel);
		outputInfoLabelCons.setWidth(Spring.constant((int) (0.3 * SIZE.width)));
		outputInfoLabelCons.setHeight(Spring.constant(25));
		outputInfoLabelCons.setX(outputAreaCons.getConstraint(SpringLayout.WEST));
		outputInfoLabelCons.setY(outputAreaCons.getConstraint(SpringLayout.SOUTH));
		layout.addLayoutComponent(outputInfoLabel, outputInfoLabelCons);
		
		// add fields for density
		JLabel densityLabel = new JLabel("dots to calculate in n..n+1: ");
		densityLabel.setFont(FONT);
		SpringLayout.Constraints densityLabelCons = new Constraints(densityLabel);
		densityLabelCons.setWidth(Spring.constant((int) (0.8 * SIZE.width)));
		densityLabelCons.setHeight(Spring.constant(50));
		densityLabelCons.setY(inputInfoLabelCons.getConstraint(SpringLayout.SOUTH));
		layout.addLayoutComponent(densityLabel, densityLabelCons);

		density = new JTextField("10.0");
		density.setFont(FONT);
		SpringLayout.Constraints densityCons = new Constraints(density);
		densityCons.setWidth(Spring.constant((int) (0.2 * SIZE.width)));
		densityCons.setHeight(Spring.constant(50));
		densityCons.setX(densityLabelCons.getConstraint(SpringLayout.EAST));
		densityCons.setY(densityLabelCons.getConstraint(SpringLayout.NORTH));
		layout.addLayoutComponent(density, densityCons);
		
		JLabel densityInfoLabel = new JLabel("density >= 4");
		densityInfoLabel.setFont(INFOFONT);
		SpringLayout.Constraints densityInfoLabelCons = new Constraints(densityInfoLabel);
		densityInfoLabelCons.setWidth(Spring.constant((int) (0.2 * SIZE.width)));
		densityInfoLabelCons.setHeight(Spring.constant(25));
		densityInfoLabelCons.setX(densityCons.getConstraint(SpringLayout.WEST));
		densityInfoLabelCons.setY(densityCons.getConstraint(SpringLayout.SOUTH));
		layout.addLayoutComponent(densityInfoLabel, densityInfoLabelCons);
		
		JLabel coordinatelineDensityLabel = new JLabel("coordinate lines to draw between n..n+1: ");
		coordinatelineDensityLabel.setFont(FONT);
		SpringLayout.Constraints coordinatelineDensityLabelCons = new Constraints(coordinatelineDensityLabel);
		coordinatelineDensityLabelCons.setWidth(Spring.constant((int) (0.8 * SIZE.width)));
		coordinatelineDensityLabelCons.setHeight(Spring.constant(50));
		coordinatelineDensityLabelCons.setY(densityInfoLabelCons.getConstraint(SpringLayout.SOUTH));
		layout.addLayoutComponent(coordinatelineDensityLabel, coordinatelineDensityLabelCons);

		coordinatelineDensity = new JTextField("1.0");
		coordinatelineDensity.setFont(FONT);
		SpringLayout.Constraints coordinatelineDensityCons = new Constraints(coordinatelineDensity);
		coordinatelineDensityCons.setWidth(Spring.constant((int) (0.2 * SIZE.width)));
		coordinatelineDensityCons.setHeight(Spring.constant(50));
		coordinatelineDensityCons.setX(coordinatelineDensityLabelCons.getConstraint(SpringLayout.EAST));
		coordinatelineDensityCons.setY(coordinatelineDensityLabelCons.getConstraint(SpringLayout.NORTH));
		layout.addLayoutComponent(coordinatelineDensity, coordinatelineDensityCons);
		
		JLabel coordinatelineDensityInfoLabel = new JLabel("coord.lineDensity >= 1");
		coordinatelineDensityInfoLabel.setFont(INFOFONT);
		SpringLayout.Constraints coordinatelineDensityInfoLabelCons = new Constraints(coordinatelineDensityInfoLabel);
		coordinatelineDensityInfoLabelCons.setWidth(Spring.constant((int) (0.2 * SIZE.width)));
		coordinatelineDensityInfoLabelCons.setHeight(Spring.constant(25));
		coordinatelineDensityInfoLabelCons.setX(coordinatelineDensityCons.getConstraint(SpringLayout.WEST));
		coordinatelineDensityInfoLabelCons.setY(coordinatelineDensityCons.getConstraint(SpringLayout.SOUTH));
		layout.addLayoutComponent(coordinatelineDensityInfoLabel, coordinatelineDensityInfoLabelCons);
		
		JLabel dotwidthLabel = new JLabel("width of a dot at location f(z): ");
		dotwidthLabel.setFont(FONT);
		SpringLayout.Constraints dotwidthLabelCons = new Constraints(dotwidthLabel);
		dotwidthLabelCons.setWidth(Spring.constant((int) (0.8 * SIZE.width)));
		dotwidthLabelCons.setHeight(Spring.constant(50));
		dotwidthLabelCons.setY(coordinatelineDensityInfoLabelCons.getConstraint(SpringLayout.SOUTH));
		layout.addLayoutComponent(dotwidthLabel, dotwidthLabelCons);

		dotwidth = new JTextField("3");
		dotwidth.setFont(FONT);
		SpringLayout.Constraints dotwidthCons = new Constraints(dotwidth);
		dotwidthCons.setWidth(Spring.constant((int) (0.2 * SIZE.width)));
		dotwidthCons.setHeight(Spring.constant(50));
		dotwidthCons.setX(dotwidthLabelCons.getConstraint(SpringLayout.EAST));
		dotwidthCons.setY(dotwidthLabelCons.getConstraint(SpringLayout.NORTH));
		layout.addLayoutComponent(dotwidth, dotwidthCons);

		// add checkboxes
		JLabel functionPointsLabel = new JLabel("painting points where f(z) is located:");
		functionPointsLabel.setFont(FONT);
		SpringLayout.Constraints functionPointsLabelCons = new Constraints(functionPointsLabel);
		functionPointsLabelCons.setWidth(Spring.constant((int) (0.8 * SIZE.width)));
		functionPointsLabelCons.setHeight(Spring.constant(50));
		functionPointsLabelCons.setY(dotwidthLabelCons.getConstraint(SpringLayout.SOUTH));
		layout.addLayoutComponent(functionPointsLabel, functionPointsLabelCons);

		functionPoints = new JCheckBox();
		functionPoints.setSelected(true);
		SpringLayout.Constraints functionPointsCons = new Constraints(functionPoints);
		functionPointsCons.setX(Spring.constant((int) (0.8 * SIZE.width)));
		functionPointsCons.setHeight(Spring.constant(50));
		functionPointsCons.setY(functionPointsLabelCons.getConstraint(SpringLayout.NORTH));
		layout.addLayoutComponent(functionPoints, functionPointsCons);

		JLabel horizontalLinesLabel = new JLabel("painting horizontal lines from input grid:");
		horizontalLinesLabel.setFont(FONT);
		SpringLayout.Constraints horizontalLinesLabelCons = new Constraints(horizontalLinesLabel);
		horizontalLinesLabelCons.setWidth(Spring.constant((int) (0.8 * SIZE.width)));
		horizontalLinesLabelCons.setHeight(Spring.constant(50));
		horizontalLinesLabelCons.setY(functionPointsLabelCons.getConstraint(SpringLayout.SOUTH));
		layout.addLayoutComponent(horizontalLinesLabel, horizontalLinesLabelCons);

		horizontalLines = new JCheckBox();
		horizontalLines.setSelected(true);
		SpringLayout.Constraints horizontalLinesCons = new Constraints(horizontalLines);
		horizontalLinesCons.setX(Spring.constant((int) (0.8 * SIZE.width)));
		horizontalLinesCons.setHeight(Spring.constant(50));
		horizontalLinesCons.setY(functionPointsLabelCons.getConstraint(SpringLayout.SOUTH));
		layout.addLayoutComponent(horizontalLines, horizontalLinesCons);

		JLabel verticalLinesLabel = new JLabel("painting vertical lines from input grid:");
		verticalLinesLabel.setFont(FONT);
		SpringLayout.Constraints verticalLinesLabelCons = new Constraints(verticalLinesLabel);
		verticalLinesLabelCons.setWidth(Spring.constant((int) (0.8 * SIZE.width)));
		verticalLinesLabelCons.setHeight(Spring.constant(50));
		verticalLinesLabelCons.setY(horizontalLinesLabelCons.getConstraint(SpringLayout.SOUTH));
		layout.addLayoutComponent(verticalLinesLabel, verticalLinesLabelCons);

		verticalLines = new JCheckBox();
		verticalLines.setSelected(true);
		SpringLayout.Constraints verticalLinesCons = new Constraints(verticalLines);
		verticalLinesCons.setX(Spring.constant((int) (0.8 * SIZE.width)));
		verticalLinesCons.setHeight(Spring.constant(50));
		verticalLinesCons.setY(horizontalLinesLabelCons.getConstraint(SpringLayout.SOUTH));
		layout.addLayoutComponent(verticalLines, verticalLinesCons);

		// apply Button
		apply.setFont(FONT);
		SpringLayout.Constraints applyCons = new Constraints(apply);
		applyCons.setWidth(Spring.constant(SIZE.width));
		applyCons.setHeight(Spring.constant(50));
		applyCons.setY(Spring.constant(SIZE.height - 50));
		layout.addLayoutComponent(apply, applyCons);

		// add components
		this.add(title);
		this.add(comboBox);
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
		this.add(densityInfoLabel);
		this.add(coordinatelineDensityLabel);
		this.add(coordinatelineDensity);
		this.add(coordinatelineDensityInfoLabel);
		this.add(dotwidthLabel);
		this.add(dotwidth);
		this.add(apply);

	}

	public void setInputArea(double[] values) {
		String result = "";
		for (int i = 0; i < 4; i++) {
			if (values[i] == Math.PI) {
				result += "Pi";
			} else if (values[i] == -Math.PI) {
				result += "-Pi";
			} else if (values[i] == 2 * Math.PI){
				result += "2Pi";
			} else if (values[i] == -2 * Math.PI){
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

	public void setOutputArea(int[] values) {
		String result = "";
		for (int i = 0; i < 4; i++) {
			result += values[i] + ", ";
		}
		// remove last ','
		result = result.substring(0, result.length() - 1);
		outputArea.setText(result);
	}
	
	public void setDensity(double value) {
		density.setText(Double.toString(value));
	}
	
	public void setTitleLabel(String name) {
		title.setText(" ---------- " + name + " ---------- ");
	}

	public char getSelectedIndex() {
		return (char) (comboBox.getSelectedIndex() + '0');
	}

	public boolean paintFunctionPoints() {
		return functionPoints.isSelected();
	}

	public boolean paintHorizontalLines() {
		return horizontalLines.isSelected();
	}

	public boolean paintVerticalLines() {
		return verticalLines.isSelected();
	}
	
	public int getDotWidth() {
		return Integer.parseInt(dotwidth.getText());
	}
	
	public double getDensity() {
		return Double.parseDouble(density.getText());
	}
	
	public double getCoordinatelineDensity() {
		return Double.parseDouble(coordinatelineDensity.getText());
	}

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

	public int[] getOutputArea() {
		int[] result = new int[4];
		String[] split = outputArea.getText().split(",");

		for (int i = 0; i < 4; i++) {
			split[i] = split[i].replaceAll("\\s", "");
			result[i] = Integer.parseInt(split[i]);
		}

		return result;
	}

}
