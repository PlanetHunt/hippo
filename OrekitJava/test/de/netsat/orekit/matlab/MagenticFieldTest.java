package de.netsat.orekit.matlab;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.orekit.attitudes.Attitude;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.errors.OrekitException;
import org.orekit.forces.ForceModel;
import org.orekit.forces.SphericalSpacecraft;
import org.orekit.forces.drag.DragForce;
import org.orekit.forces.drag.HarrisPriester;
import org.orekit.forces.gravity.HolmesFeatherstoneAttractionModel;
import org.orekit.forces.gravity.potential.GravityFieldFactory;
import org.orekit.forces.gravity.potential.NormalizedSphericalHarmonicsProvider;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;

import de.netsat.orekit.NetSatConfiguration;
import de.netsat.orekit.matlab.MatlabPushHandler;
import de.netsat.orekit.matlab.EventCalculator;

import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;
import org.orekit.utils.PVCoordinates;
import org.orekit.utils.TimeStampedAngularCoordinates;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;

public class MagenticFieldTest {

	/**
	 * 
	 * @param mi
	 * @return
	 * @throws MatlabInvocationException
	 * @throws OrekitException
	 */

	public static SpacecraftState runNumericalPropagatorlocal(MatlabInterface mi, double mu)
			throws MatlabInvocationException, OrekitException

	{
		boolean fire = false;
		double[] thrustDirection = { 1, 0, 0 };
		double massLoss = -0.0001;
		SensorDataType[] options = { SensorDataType.ORBITAL_ELEMENTS, SensorDataType.TIMESTAMP,
				SensorDataType.CURRENT_MASS, SensorDataType.VELOCITY, SensorDataType.POSITION, SensorDataType.ACC };
		MatlabFunctionType[] matlabFunctions = { MatlabFunctionType.MATLAB_STEP_HANDLER };
		MatlabPushHandler mph = new MatlabPushHandler(mi, options, matlabFunctions);
		mph.setVariableInMatlab("muValue", mu);
		Object[] initialVars = mph.runMatlabFunction("initialiseSimulationVariables(muValue)", 12);
		PropagatorDataType np = PropagatorDataType.NUMERICAL_KEPLERIAN_RUNGEKUTTA;

		/* Initial Orbit Settings */
		double[] initialDate = ((double[]) initialVars[0]);
		double[] initialOrbit = ((double[]) initialVars[1]);
		double thrusterNumber = ((double[]) initialVars[2])[0];
		double thrust = ((double[]) initialVars[3])[0];
		double startingMass = ((double[]) initialVars[4])[0];
		/* Initial Propagation Settings */
		double positionTolerance = ((double[]) initialVars[5])[0];
		double minStep = ((double[]) initialVars[6])[0];
		double maxStep = ((double[]) initialVars[7])[0];
		double duration = ((double[]) initialVars[8])[0];
		double stepSize = ((double[]) initialVars[9])[0];
		double equivalentIsp = ((double[]) initialVars[10])[0];
		double equivalentThrust = ((double[]) initialVars[11])[0];
		final NormalizedSphericalHarmonicsProvider provider = GravityFieldFactory.getNormalizedProvider(10, 10);
		ForceModel holmesFeatherstone = new HolmesFeatherstoneAttractionModel(
				FramesFactory.getITRF(IERSConventions.IERS_2010, true), provider);
		ForceModel atmosphericDrag = new DragForce(
				new HarrisPriester(CelestialBodyFactory.getSun(),
						new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS, Constants.WGS84_EARTH_FLATTENING,
								FramesFactory.getITRF(IERSConventions.IERS_2010, true))),
				new SphericalSpacecraft(0.01, 2.2, 0, 0));

		InitialOrbit initOrbit = new InitialOrbit(initialOrbit, initialDate, mi);
		KeplerianOrbit keplerOrbit = initOrbit.getKeplerianOrbit();

		NetsatPropagatorFactory NumericalPropagatorFactory = new NetsatPropagatorFactory(np, maxStep, minStep, duration,
				stepSize, positionTolerance, keplerOrbit);
		NumericalPropagator numericPropagator = NumericalPropagatorFactory.getNumericalPropagator();
		SpacecraftState initialState = new SpacecraftState(keplerOrbit,
				new Attitude(FramesFactory.getEME2000(),
						new TimeStampedAngularCoordinates(keplerOrbit.getDate(),
								new PVCoordinates(new Vector3D(10, 10), new Vector3D(1, 2)),
								new PVCoordinates(new Vector3D(15, 3), new Vector3D(1, 2)))),
				startingMass);
		EventCalculator eventCal = new EventCalculator(initialState, keplerOrbit.getDate(), keplerOrbit);
		// NetSatThrustEquations thrustEq = new NetSatThrustEquations("Thrust",
		// "experimental", fire, (int) thrusterNumber,
		// thrust, thrustDirection, massLoss, stepSize);
		AbsoluteDate dummyStartDate = new AbsoluteDate(1, 1, 1, 0, 0, 0, TimeScalesFactory.getUTC());
		PropulsionSystem prop = new PropulsionSystem(dummyStartDate, duration, equivalentThrust, equivalentIsp,
				new Vector3D(thrustDirection));
		mph = new MatlabPushHandler(mi, options, matlabFunctions, false, prop);
		// initialState = initialState.addAdditionalState("Thrust", 0, 0, 0);

		// numericPropagator.addAdditionalEquations(thrustEq);
		numericPropagator.addForceModel(holmesFeatherstone);
		numericPropagator.addForceModel(atmosphericDrag);

		numericPropagator.setInitialState(initialState);
		numericPropagator.setMasterMode(stepSize, mph);
		SpacecraftState finalState = numericPropagator.propagate(keplerOrbit.getDate().shiftedBy(duration));
		return finalState;

	}
	/**
	 * Set the variable in Matlab (the variable should be the type double or
	 * could be casted to double.
	 * 
	 * @param mi
	 * @param name
	 * @param value
	 * @throws MatlabInvocationException
	 */
	public static void setVariableInMatlab(MatlabInterface mi, String name, double value)
			throws MatlabInvocationException {

		mi.getProxy().setVariable(name, value);
	}

	public static void main(String[] args)
			throws OrekitException, MatlabConnectionException, MatlabInvocationException {
		// Object[] obj = null;
		NetSatConfiguration.init();
		MatlabInterface mi;
		ConstantValues constants = new ConstantValues();
		mi = new MatlabInterface(MatlabInterface.MATLAB_PATH, null);
		runNumericalPropagatorlocal(mi, constants.getMu());

		// System.out.println(((double[]) obj[1])[0]);
	}

}
