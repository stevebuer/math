/* Steve Buer
   Olympic College 
   CS& 141 - Winter 2025
   Final Project */

import java.util.*;
import java.io.*;

/**
* Command Line Interface for L-Network Tuner
*
* @author Steve Buer, N7MKO
* @version 0.5
*
*/

class Tuner {

	/* our tuning network instance */

	static LNetwork network;

	/* default config file */

	static String configFile = "tuner.cfg";

	/**
	* Pretty-print complex number
	*
	* @param Complex a complex number
	*/

	static void printComplex(Complex c) {

		if (c.getImaginary() < 0)
			System.out.printf("%.01f - j%.01f \u03A9\n", c.getReal(), Math.abs(c.getImaginary()));
		else
			System.out.printf("%.01f + j%.01f \u03A9\n", c.getReal(), c.getImaginary());
	}

	/**
	* Parse a line of the config file
	*
	* @param String config file line
	*/
	
	static void parseConfigLine(String line) {

		Scanner s = new Scanner(line);

		while (s.hasNext()) {

			String t = s.next();

			switch (t.toUpperCase()) {

				case "CAPACITOR":

					double min = s.nextDouble();
					double max = s.nextDouble();
					network.addCapacitor(min, max);
					break;

				case "INDUCTOR":

					while (s.hasNextDouble())
						network.addInductor(s.nextDouble());
					break;

				case "ANTENNA":

					double re = s.nextDouble();
					double im = s.nextDouble();
					network.setAntenna(re, im);
					break;

				case "FREQUENCY":

					network.setFrequency(s.nextDouble());
					break;

				case "HIGHPASS":
			
					if (s.nextInt() != 0)
						network.setHighPass();
					break;

				default:
					System.out.println("Unknown config paramter: " + t);
			}
		}
	}

	/**
	* Load a configuration file and read line by line
	*
	* @param String file path name
	*/

	static void loadConfig(String filename) throws FileNotFoundException {

		System.out.println("Read config file: " + filename);

		Scanner s = new Scanner(new File(filename));

		while (s.hasNextLine()) {

			String l = s.nextLine();

			if (l.isBlank() || l.charAt(0) == '#') /* skip blank or comment lines */
				continue;

			parseConfigLine(l);
		}
	}

	/**
	* parse and execute a command passed as a line of text
	*
	* @param String line of text to parse
	*/

	static void runCommand(String input) {

		Scanner cs = new Scanner(input);

		String cmd = cs.next();

		switch (cmd.toLowerCase()) {

			case "c":
				network.setCapacitance(cs.nextDouble());
				break;

			case "f":

				network.setFrequency(cs.nextDouble());
				break;

			case "i":
				if (cs.hasNextDouble())
					network.setInductance(cs.nextDouble());
				else if (cs.hasNext())
					network.setInductanceTap(cs.next().toUpperCase().charAt(0));
				else
					System.out.println("usage: i <value>");
				break;

			case "o":
				System.out.println("\nNetwork Parameters:\n");	
				System.out.print("Frequency: " + network.getFrequency() + " Mhz\nAntenna Impedance: ");	
				printComplex(network.getAntenna());
				System.out.println("Capacitor Range: " + network.min_capacitance + " pF --> " + network.max_capacitance + " pF");
				System.out.println("Inductor Taps: " + network.formatInductor());
				System.out.println("High Pass: " + network.isHighPass());
				System.out.println("\nCurrent Settings:\n");	
				System.out.println("Inductor: " + network.getInductance() + " \u00b5H" );
				System.out.println("Capacitor: " + network.getCapacitance() + " pF\n");
				break;

			case "q":
				System.exit(0);

			case "s":
				System.out.print("\nUntuned:\n\nAntenna: ");
				printComplex(network.getAntenna());
				System.out.printf("SWR: %.01f\n\n", network.calculateSwr(network.calculateGamma(network.getAntenna())));
				if (network.isHighPass())
					network.transformHighPass();
				else
					network.transformLowPass();
				System.out.printf("\nTuned:\n\nInput: ");
				printComplex(network.tuned_impedance);
				System.out.printf("SWR: %.01f\n\n", network.calculateSwr(network.calculateGamma(network.tuned_impedance)));
				break;

			default:
				System.out.println("\nsimulator commands:\n");
				System.out.println("c <x.x>    set capacitor");
				System.out.println("i <X|x.x>  set inductor");
				System.out.println("f <x.xxx>  set frequency");
				System.out.println("o          show config");
				System.out.println("q          quit");
				System.out.println("s          calculate swr");
		}
	}

	/**
	 * Program entry main method
	 *
	 * @param String argument list
	 */

	public static void main(String args[]) {
		
		System.out.println("L-Network Simulator: Steve Buer, N7MKO");

		network = new LNetwork();

		/* read config file from command line if specified */

		if (args.length == 1)
			configFile = args[0];

		try {
			loadConfig(configFile);

		} catch (Exception e) {

			System.err.println("Error loading config: " + configFile);
			return;
		}

		/* read commands from stdin */

		Scanner repl = new Scanner(System.in);

		System.out.print("cmd> "); /* fencepost */

		while (repl.hasNextLine()) {

				String cmd = repl.nextLine();

				if (!cmd.isEmpty()) {

					try {
						runCommand(cmd);

					} catch (Exception e) {

						System.out.println("cmd error: " + e.getMessage());
					}
				}

				System.out.print("cmd> ");
		} // while

	}
}
