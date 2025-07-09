/* Steve Buer
   Olympic College 
   CS& 141 - Winter 2025 
   Final Project */

/**
* Class that represents a tuning network consisting of a series inductor
* and parallel capacitor.
* <p>
* The implementation assumes the capacitor is variable and the
* inductor is tapped with selectable positions.
*
* @author Steve Buer, N7MKO
* @version 0.5
*/

public class LNetwork {

	/**
	* maximum number of inductor tap positions
        */

	public final int INDUCTOR_MAX_TAPS = 16;

	/* fields */

	double min_capacitance; /* minimum capacitance in picofarads */
	double max_capacitance; /* maximum capacitance in picofarads */
	private double capacitance; /* current capacitance setting in picofarads */
	private double inductance; /* inductance in microhenries */
	private double[] inductance_taps; /* array of tapped inductor values in microhenries */
	private int inductor_tap_switch; /* position of inductor tap switch */
	private int inductor_tap_count; /* total number of inductor taps */
	private double frequency; /* frequency in Mhz */

	boolean highpass = false; /* determines network configuraton: lowpass (default) or highpass */
	boolean debug = false; // set in env?

	private Complex antenna_impedance; /* antenna impedance (frequency dependent) */
	Complex tuned_impedance; /* after tuning */

	/**
	* Default constructor for 50 ohm load
	*/

	public LNetwork() {

		this.antenna_impedance = new Complex(50, 0);
		this.inductance_taps = new double[INDUCTOR_MAX_TAPS];
	}

	/**
	* Construct an LNetwork connected to antenna of specified impedance
	*
	* @param antenna Complex impedance of antenna. 
	*/

	public LNetwork(Complex antenna) {

		this.antenna_impedance = antenna;
		this.inductance_taps = new double[INDUCTOR_MAX_TAPS];
	}

	/**
	* Method to add a variable capacitor to the network
	*
	* @param  min minimum capacitance value
	* @param  max maximum capacttance value
	*/

	public void addCapacitor(double min, double max) {

		/* todo: error check */

		/* can I set a message on invalid arg exception? */

		this.min_capacitance = min;
		this.max_capacitance = max;

		capacitance = max / 2.0; /* initial setting to midrange */
	}

	/**
	* Add an inductor (tap position) to the list of inductances
	*
	* @param t Inductance value in microhenries
	*/

	public void addInductor(double t) {

		if (t < 0.0)
			throw new IllegalArgumentException("invalid negative inductance");

		inductance_taps[inductor_tap_count] = t;
		inductor_tap_count++;
	}

	/**
	* Set the tap by letter, simulate turning the rotary switch
	*
	* @param tapchar Upper case letter A-P
	*/

	public void setInductanceTap(char tapchar) {

		byte b = (byte) tapchar; /* cast to byte */

		int tapnum = b - 65; /* convert tap letter to array index */

		if (tapnum > inductor_tap_count)		
			throw new IllegalArgumentException("max tap count exceeded");

		inductor_tap_switch = tapnum;
	}

	/**
	* Set an arbitrary inductance value
	*
	* @param h Inductance value in microhenries
	*/

	public void setInductance(double h) {

		if (h < 0.0)
			throw new IllegalArgumentException("invalid negative inductance");

		this.inductance = h;
	}

	/**
	 * Get inductance from tap selection or arbitrary value setting
         *
	 * @return Inductance value in microhenries
	 */ 

	public double getInductance() {

		if (this.inductance > 0.0)
			return this.inductance;
		else
			return getTappedInductorValue();
	}
	
	/**
	 * Return the currently selected inductance
         *
	 * @return Inductance value in microhenries
	 */ 

	double getTappedInductorValue() {

		return inductance_taps[inductor_tap_switch];
	}

	/**
	 * Return a formatted version of the inductor tap list
         *
	 * @return String describing the list
	 */ 

	String formatInductor() {

		String fmt = "";

		for (int i = 0; i < inductor_tap_count; i++) {

			char c = (char) (65 + i);

			fmt += c + ":" + inductance_taps[i] + " \u00b5H"; // https://en.wikipedia.org/wiki/List_of_Unicode_characters

			if (i != inductor_tap_count-1) /* skip comma on last fencepost */
				fmt += ", ";
		}

		return fmt;
	}

	/**
	 * Set the capacitor value
         *
	 * @param c capacitance in picofarads
	 */ 

	public void setCapacitance(double c) {

		if (c < this.min_capacitance || c > this.max_capacitance)
			throw new IllegalArgumentException("capacitance outside valid range");

		this.capacitance = c;
	}

	/**
	 * Get capacitor setting
         *
	 * @return capacitance value in picofarads
	 */ 

	public double getCapacitance() {

		return this.capacitance;
	}

	/**
	 * Set circuit frequency
         *
         * @param f frequency in megahertz
	 */ 

	public void setFrequency(double f) {

		if (f < 0.0)
			throw new IllegalArgumentException("invalid negative frequency");

		this.frequency = f;
	}

	/**
	 * Get circuit frequency
         *
	 * @return frequency in megahertz
	 */ 

	public double getFrequency() {

		return this.frequency;
	}

	/**
	 * Set antenna impedance
         *
         * @param real resistive component of impedance
	 * @param imaginary reactive component of impedance
	 */ 

	public void setAntenna(double real, double imaginary) {

		this.antenna_impedance = new Complex(real, imaginary);
	}

	/**
	 * Get antenna impedance
         *
	 * @return Complex value of impedance
	 */ 

	public Complex getAntenna() {

		return this.antenna_impedance;
	}

	/**
	 * Calculate an SWR for the current network settings 
	 *
	 * @param gamma gamma value for the impedance 
         * @return Standing Wave Ratio (SWR)
         */

	public double calculateSwr(Complex gamma) {

		/* swr = (1 + abs(gamma)) / (1 - abs(gamma)) */	

		return (1 + gamma.abs()) / (1 - gamma.abs());		
	}

 	/**
	 * Calculate gamma for an impedance 
	 *
	 * @param z_load load impedance
         * @return Complex value of gamma
         */
	
	public Complex calculateGamma(Complex z_load) {

		/* gamma = (z_load - z_ref) / (z_load + z_ref) */

		Complex numerator, denominator;

		Complex z_ref = new Complex(50.0, 0.0); // move to class constant Z_REF

		numerator = z_load.subtract(z_ref);

		denominator = z_load.add(z_ref);

		return numerator.divide(denominator);
	}

	/**
	 * Calculate the reactance of the capacitor
	 *
         * @return imaginary component of impedance (negative)
         */

	public double capacitiveReactance() {

		/* reactance of capacitor: -1/(2*pi*f*C) with f to hertz and capacitance in farads */

		double xc = -1.0 / (2.0 * Math.PI * (this.frequency * 1_000_000.0) * (this.capacitance / 1_000_000_000_000.0));  // convert: f to Hertz, pF to Farads

		if (debug)
			System.out.printf("debug: Xc: %.03f\n", xc);

		return xc;
	}

	/**
	 * Calculate the reactance of the inductor
	 *
         * @return imaginary component of impedance (positive)
         */

	public double inductiveReactance() {

		/* reactance of inductor: 2*pi*f*L with f in hertz and inductance in farads */
		
		double xl = 2.0 * Math.PI * (this.frequency * 1_000_000.0) * (this.getInductance() / 1_000_000.0); // convert: f to Hertz,  microH to Henries

		if (debug)
			System.out.printf("debug: Xl: %.06f\n", xl);

		return xl;
	}

	/**
	 * Calculate impedance z_load after network transformation for low pass configuration
	 *
         * @return Network input impedance after transformation
         */

	public Complex transformLowPass() {

		/* this method is too long and needs to be re-factored into smaller sub-routines */

		Complex z_c, z_l, z_1, z_2, numerator, denominator;

		double x_c = capacitiveReactance();

		/* apply first transformation step for parallel capacitance : z_1 = (X_c * X_ant) / (X_c + X_ant) */

		z_c = new Complex(0.0, x_c);

		numerator = z_c.multiply(this.antenna_impedance);

		denominator = z_c.add(this.antenna_impedance);

		z_1 = numerator.divide(denominator); 
		
		if (debug)
			System.out.println("debug: z_1: " + z_1);

		/* reactance of inductor:  */
		
		double x_l = inductiveReactance();

		if (debug)
			System.out.printf("debug: Xl: %.06f\n", x_l);
		
		/* apply second transformation step for series inductance : z_2 = z_l + z_1 */

		z_l = new Complex(0.0, x_l);

		z_2 = z_l.add(z_1);

		if (debug)
			System.out.println("debug: z_2: " + z_2);

		// set this as tuned Impedance: z_in = z_2

		this.tuned_impedance = z_2;

		return this.tuned_impedance;
	}

	/**
	 * Calculate impedance z_load after network transformation for high pass configuration
	 *
         * @return Network input impedance after transformation
         */

	public Complex transformHighPass() {

		/* this is reverse sequence from low pass, and re-factored for shorter method vs. above */

		Complex z_1, z_2, numerator, denominator;
		
		/* apply first transformation step for series inductance : z_1 = z_antenna + z_l */

		z_1 = this.antenna_impedance.add(new Complex(0.0, inductiveReactance()));

		/* apply second transformation step for parallel capacitance : z_2 = (z_1 * z_c) / (z_1 + z_c) */

		double x_c = capacitiveReactance();
		
		numerator = z_1.multiply(new Complex(0.0, x_c));

		denominator = z_1.add(new Complex(0.0, x_c));

		z_2 = numerator.divide(denominator);

		this.tuned_impedance = z_2;

		return z_2;
	}

	/**
	 * Rsource > Rload (standard configuration)
         */

	public void setLowPass() {

		this.highpass = false;
	}
	
	/**
	 * Rload > Rsource (reverse coax connections)
         */

	public void setHighPass() {
		
		this.highpass = true;
	}

	/**
	 * Check if high pass set
         * 
         * @return true or false
         */

	public boolean isHighPass() {

		return this.highpass;
	}
}
