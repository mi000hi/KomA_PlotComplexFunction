import java.util.ArrayList;

import javax.swing.JPanel;

/**
 * this class is used as parent fro Leinwand3D.java, so that we can override the function method
 * @author michael
 *
 */

public class ParentLeinwand3D extends JPanel {
	
	protected ArrayList<Complex> functionInputPoints, functionOutputPoints;
	
	protected double getFunctionValue(int index) {
		
		/**     OVERRIDE THIS FUNCTION     */		
		return 0;
		
	}

}
