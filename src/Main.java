import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * this is only a function for testing purposes ! it is not needed by the actual
 * program !
 * 
 * @author michael
 *
 */
public class Main {

//	public static void main(String[] args) {
//        Map<Character, Thread> commands = new HashMap<>();
//
//        // Populate commands map
//        commands.put('1', new Thread(() -> function1("test2")));                              
//        commands.put('2', new Thread(() -> function2()));
//        int number = 2;
//
//
//        // Run selected command
//        commands.get('2').run();
// 
//    }
//	
//	private static void function1(String number) {
//		System.out.println("function 1 with number " + number);
//	}
//	
//	private static void function2() {
//		System.out.println("function 2");
//		
//	}
	
	private void printSomething() {
		
		System.out.println("printing something here");
		
	}

	public static void main(String[] args) {

//		String function = "1/z/z";
//		function = function.replaceAll("\\s", "");
//		Complex z = new Complex(1, 0, true);
//
//		Complex result = calculate(z, function);
//		
//		System.out.println("f(z = " + z.getRe() + " + i(" + z.getIm() + ") ) = " + result.getRe() + " +\n\t + i(" + result.getIm() + ")");

		Main main1 = new Main() {
			private void printSomething() {
				System.out.println("printing something else");
			}
		};
		
		Main main2 = new Main();
		
		main1.printSomething();
		
		main2.printSomething();
		
	}

	private static Complex calculate(Complex z, String input) {

//		System.out.println("to calculate: " + input);

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
						result = calculate(z, input.substring(0, i))
								.add(calculate(z, input.substring(i + 1, input.length())));
						return result;
					}
					break;

				case '-':
					if (openBrackets == 0) {
						result = calculate(z, input.substring(0, i))
								.subtract(calculate(z, input.substring(i + 1, input.length())));
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

				case '*':
					if (openBrackets == 0) {
						result = calculate(z, input.substring(0, i))
								.multiply(calculate(z, input.substring(i + 1, input.length())));
						return result;
					}
					break;

				case '/':
					if (openBrackets == 0) {
						Complex denumerator = calculate(z, input.substring(i + 1, input.length()));
						if(denumerator.getRe() == 0 && denumerator.getIm() == 0) {
							return new Complex(0, 0, true);
						}
						result = calculate(z, input.substring(0, i))
								.divide(denumerator);
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
						result = calculate(z, input.substring(i + 4, input.length() - 1)).sin();
						return result;
					}
					break;

				case 'c':
					if (openBrackets == 0) {
						result = calculate(z, input.substring(i + 4, input.length() - 1)).cos();
						return result;
					}
					break;

				case 'e':
					if (openBrackets == 0) {
						result = calculate(z, input.substring(i + 4, input.length() - 1)).exp();
						return result;
					}
					break;

				}
			}

			if (input.charAt(0) == '(' && input.charAt(input.length() - 1) == ')') {
//				System.out.println("removing brackets around " + input);
				input = input.substring(1, input.length() - 1);
				bracketsRemoved = true;
//				System.out.println("new input: " + input);
			}

		} while (bracketsRemoved);

		System.out.println("make a new Complex number from: " + input);
		switch (input) {

		case "z":
			return z;

		case "i":
			return new Complex(0, 1, true);

		default:
			return new Complex(Double.parseDouble(input), 0, true);
		}

	}

}
