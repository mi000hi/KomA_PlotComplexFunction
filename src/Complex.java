
public class Complex {

	private double real; // x from z = x + iy
	private double imaginary; // y from z = x + iy

	// CONSTRUCTORS
	public Complex(double radius, double argument) {

		real = radius * Math.cos(argument);
		imaginary = radius * Math.sin(argument);

	}

	public Complex(double x, double y, boolean useless) {

		real = x;
		imaginary = y;

	}

	public void print() {
		System.out.println(real + " + i(" + imaginary + ")");
	}

	// GETTERS
	public double getRe() {
		return real;
	}

	public double getIm() {
		return imaginary;
	}
	
	public double getPhi() {
		return Math.atan(imaginary / real);
	}
	
	public double getRadius() {
		return Math.sqrt(Math.pow(real, 2) + Math.pow(imaginary, 2));
	}

	// FUNCTIONS TO CALCULATE
	public Complex add(Complex number) {
		double x, y;
		x = real + number.getRe();
		y = imaginary + number.getIm();

		return new Complex(x, y, true);
	}

	public Complex subtract(Complex number) {
		double x, y;
		x = real - number.getRe();
		y = imaginary - number.getIm();

		return new Complex(x, y, true);
	}

	public Complex multiply(Complex factor) {

		double x = real * factor.getRe() - imaginary * factor.getIm();
		double y = imaginary * factor.getRe() + real * factor.getIm();

		return new Complex(x, y, true);

	}

	public Complex divide(Complex denumerator) {

		Complex newDenumerator = new Complex(Math.pow(denumerator.getRe(), 2) + Math.pow(denumerator.getIm(), 2), 0,
				true);
		Complex newNumerator = this.multiply(new Complex(denumerator.getRe(), -denumerator.getIm(), true));
		Complex result;

		if (Math.abs(newDenumerator.getRe()) < 0.1 && Math.abs(newDenumerator.getIm()) < 0.1) {
//			System.out.print("newDenumerator = ");
//			newDenumerator.print();
			return null;
		}
		result = new Complex(newNumerator.getRe() / newDenumerator.getRe(),
				newNumerator.getIm() / newDenumerator.getRe(), true);

		return result;

	}

	public Complex exp() {

		// exp(z) = exp(x + iy) = exp(x) * exp(iy)
		double e_pow_x;
		Complex result;

		// exp(z) = exp(x + iy) = e_pow_x * exp(iy)
		e_pow_x = Math.exp(real);

		// exp(z)
		result = new Complex(e_pow_x, imaginary);

		return result;

	}

	public Complex sin() {

		// sin(z) = 1/(2i) * (exp(xi-y) - exp(-xi+y))

		Complex summand1, summand2, numerator, result;

		// sin(z) = 1/(2i) * (summand1 + summand2)
		summand1 = new Complex(-imaginary, real, true).exp();
		summand2 = new Complex(imaginary, -real + Math.PI, true).exp();

		// sin(z)
		numerator = summand1.add(summand2);

		result = numerator.divide(new Complex(0, 2, true));

		return result;

	}

	public Complex cos() {

		// cos(z) = 1/2 * (exp(xi-y) + exp(-xi+y))

		Complex summand1, summand2, result;

		// sin(z) = 1/(2i) * (summand1 + summand2)
		summand1 = new Complex(-imaginary, real, true).exp();
		summand2 = new Complex(imaginary, -real, true).exp();

		// sin(z)
		result = summand1.add(summand2);
		result = result.divide(new Complex(2, 0, true));

		return result;

	}

}
