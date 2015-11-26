/**
 * 
 */
package de.netsat.orekit.actuator;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.orekit.utils.Constants;

/**
 * This class implements the properties of the NanoFEEP thrusters of TU Dresden
 * according to their paper 'Highly miniaturized FEEP thrusters for CubeSat
 * applications'.
 * 
 * @author Alexander Kramer
 *
 */
public class NanoFEEP extends Thruster {
	public static final String ISP_FILE = "." + File.separatorChar + "test"
			+ File.separatorChar + "de" + File.separatorChar + "netsat"
			+ File.separatorChar + "orekit" + File.separatorChar
			+ "NanoFEEP_ISp.txt";

	/**
	 * Contains Thrust vs ISP entries.
	 */
	public static TreeMap<Double, Double> ISPLookUp;

	static {
		try {
			ISPLookUp = ISpLookUpInit();
		} catch (IOException e) {
			System.err.println("Error occured during the init of ISP file");
			e.printStackTrace();
		}
	}

	/**
	 * Read ISP file and create a map (thrust[uN] vs ISP[s]).
	 * 
	 * @return
	 * @throws IOException
	 */
	private static TreeMap<Double, Double> ISpLookUpInit() throws IOException {
		TreeMap<Double, Double> toRet = new TreeMap<Double, Double>();
		File ISpFile = new File(ISP_FILE);
		try (Scanner scanner = new Scanner(ISpFile)) {
			while (scanner.hasNextLine()) {
				processLine(toRet, scanner.nextLine());
			}
		}
		//System.out.println(toRet.toString());
		return toRet;
	}

	private static void processLine(Map<Double, Double> map, String aLine) {
		// use a second Scanner to parse the content of each line

		try (Scanner scanner = new Scanner(aLine)) {
			scanner.useDelimiter(",");
			if (scanner.hasNext()) {
				// assumes the line has a certain structure
				double key = Double.parseDouble(scanner.next());
				double value = Double.parseDouble(scanner.next());
				map.put(key, value);
			}
		}
	}

	/** Power consumption of one thruster in W. */
	//private double power;

	/** Specific impulse of one thruster in s. */
	//private double ISp;

	/** Flow rate of one thruster in kg/s. */
	//private double flowrate;
	
	public NanoFEEP(Vector3D position, Vector3D direction) {
		super(position, direction);
	}

	@Override
	public double getFlowRate(double thrust) {
		return -thrust / (Constants.G0_STANDARD_GRAVITY * getISP(thrust));
	}

	@Override
	public double getISP(double thrust) {
		thrust *=1e6;
		return Tools.linearInterpolation(ISPLookUp, thrust);
	}

	/**
	 * Get the power consumption of one thruster.
	 * 
	 * @param thrust (N)
	 * @return power (mW)
	 */
	@Override
	public double getPowerConsumption_mW(double thrust) {
		double power;
		if (thrust <= 0.0000015 && thrust >= 0.00000005) {
			power = 0.09 + 0.3733 * thrust * 1e6;
		} else if (thrust <= 0.000005 && thrust > 0.0000015) {
			power = 0.5 + 0.1 * thrust * 1e6;
		} else if (thrust <= 0.000022 && thrust > 0.000005) {
			power = 0.625 + 0.075 * thrust * 1e6;
		} else {
			System.out
					.println("Thrust not in operational range of NanoFEEP thruster");
			power = -100;
		}
		return power*1e3;
	}
}
