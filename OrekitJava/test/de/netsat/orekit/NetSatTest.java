package de.netsat.orekit;

import java.lang.reflect.Method;

import fr.cs.examples.attitude.EarthObservation;
import fr.cs.examples.attitude.EarthObservation_day_night_switch_with_fixed_transitions;
import fr.cs.examples.attitude.EarthObservation_day_night_switch_with_spinned_transitions;
import fr.cs.examples.bodies.Phasing;
import fr.cs.examples.conversion.PropagatorConversion;
import fr.cs.examples.frames.Frames1;
import fr.cs.examples.frames.Frames2;
import fr.cs.examples.frames.Frames3;
import fr.cs.examples.propagation.DSSTPropagation;
import fr.cs.examples.propagation.EphemerisMode;
import fr.cs.examples.propagation.MasterMode;
import fr.cs.examples.propagation.SlaveMode;
import fr.cs.examples.propagation.TrackCorridor;
import fr.cs.examples.propagation.VisibilityCheck;
import fr.cs.examples.propagation.VisibilityCircle;
import fr.cs.examples.time.Time1;

/**
 * This class should be used to run tests.
 * 
 * @author Slavi Dombrovski
 * @version 1.0
 */
public class NetSatTest {
	
	/**
	 *  Loads the remote configuration from servers into local path.
	 */
	@org.junit.Test
	public void testConfigLoading() {
		NetSatConfigurationCreator.getRemoteConfiguration(90);
	}
	
	/**
	 * Runs defined and selected tests.
	 */
	@org.junit.Test
	public void runDefaultTests() {
		runAllExamples();
	}
	
	/**
	 * Runs all defined tests. Additional tests can be called here!
	 */
	public static void runAllExamples() {
		runTest(EarthObservation_day_night_switch_with_fixed_transitions.class);
		runTest(EarthObservation_day_night_switch_with_spinned_transitions.class);
		runTest(EarthObservation.class);
		runTest(Phasing.class);
		runTest(PropagatorConversion.class);
		runTest(Frames1.class);
		runTest(Frames2.class);
		runTest(Frames3.class);
		runTest(DSSTPropagation.class);
		runTest(EphemerisMode.class);
		runTest(MasterMode.class);
		runTest(SlaveMode.class);
		runTest(TrackCorridor.class);
		runTest(DSSTPropagation.class);
		runTest(VisibilityCheck.class);
		runTest(VisibilityCircle.class);
		runTest(Time1.class);
	}
	
	/**
	 * Runs the main method of a provided class and prints class name 
	 * and exception messages.
	 * 
	 * @param clazz provided class
	 */
	private static void runTest(Class<?> clazz) {
		if(clazz == null)
			return;
		try {
			Method main = clazz.getMethod("main", String[].class);
			try {
				System.out.println("\r\n###########################");
				System.out.println(clazz.getSimpleName());
				System.out.println("###########################");
				main.invoke(null, (Object)null);
			} catch(Exception ex) {
				System.err.println(clazz.getSimpleName() + ": " + ex.getMessage());
			}
		} catch (Exception e) {
			System.err.println(clazz.getSimpleName() + " does not contain a main method");
		}
	}
}
