package de.netsat.orekit.matlab;

import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.orekit.attitudes.Attitude;
import org.orekit.attitudes.LofOffset;
import org.orekit.errors.OrekitException;
import org.orekit.frames.LOFType;
import org.orekit.orbits.KeplerianOrbit;

import de.netsat.orekit.NetSatConfiguration;
import de.netsat.orekit.matlab.MatlabFixedPushHandler;
import de.netsat.orekit.matlab.EventCalculator;

import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.time.AbsoluteDate;
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
		/* Generates the constant numbers */
		final ConstantValues constants = new ConstantValues();

		/* Ask for the initial settings in Matlab */
		mi.getProxy().setVariable("muValue", constants.getMu());
		Object[] initialVars = mi.getProxy().returningEval("initialiseSimulationVariables(muValue)", 15);
		/* Initial Orbit Settings */
		final double[] initialDate = ((double[]) initialVars[0]);
		final double[] initialOrbit = ((double[]) initialVars[1]);
		final double startingMass = ((double[]) initialVars[2])[0];
		final double equivalentIsp = ((double[]) initialVars[8])[0];
		final double equivalentThrust = ((double[]) initialVars[9])[0];

		/* Initial Propagation Settings */
		final double positionTolerance = ((double[]) initialVars[3])[0];
		final double minStep = ((double[]) initialVars[4])[0];
		final double maxStep = ((double[]) initialVars[5])[0];
		final double duration = ((double[]) initialVars[6])[0];
		final double stepSize = ((double[]) initialVars[7])[0];

		final double maxCheck = ((double[]) initialVars[10])[0];
		final double propagtiontype = ((double[]) initialVars[11])[0];
		final double isForceModelsActive = ((double[]) initialVars[12])[0];
		final double isValidationFlag = ((double[]) initialVars[13])[0];
		final double emptyMass = ((double[]) initialVars[14])[0];

		/* Generate the force models */
		final NetSatForceModelFactory fmf;
		if (isForceModelsActive == 1.0) {
			fmf = new NetSatForceModelFactory(constants);
		} else {
			fmf = null;
		}

		/* Set the data types to be set to Matlab */
		final SensorDataType[] options = { SensorDataType.ORBITAL_ELEMENTS, SensorDataType.TIMESTAMP,
				SensorDataType.CURRENT_MASS, SensorDataType.MEAN_ORBITAL_ELEMENTS , SensorDataType.MEAN_ORBITAL_ELEMENTS_ECKSTEIN};

		/* Set the Matlab functions that should run */
		final MatlabFunctionType[] matlabFunctions = { MatlabFunctionType.MATLAB_STEP_HANDLER };

		/* Create the initial Orbit */
		final KeplerianOrbit keplerOrbit = (new InitialOrbit(initialOrbit, initialDate, constants)).getKeplerianOrbit();

		/* Create initial State */
		SpacecraftState initialState = new SpacecraftState(keplerOrbit,
				new Attitude(constants.getEci(),
						new TimeStampedAngularCoordinates(keplerOrbit.getDate(),
								new PVCoordinates(new Vector3D(10, 10), new Vector3D(1, 2)),
								new PVCoordinates(new Vector3D(15, 3), new Vector3D(1, 2)))),
				startingMass);

		/* Set the Numerical Propagtor Type */
		final PropagatorDataType np;
		if (propagtiontype == 1.0) {
			np = PropagatorDataType.NUMERICAL_KEPLERIAN_ADAPTIVE;
		} else {
			np = PropagatorDataType.NUMERICAL_KEPLERIAN_RUNGEKUTTA;
		}
		NumericalPropagator numericPropagator = (new NetSatPropagatorFactory(np, maxStep, minStep, duration, stepSize,
				positionTolerance, keplerOrbit)).getNumericalPropagator();

		/* Event Calculator for validation of event detection in Matlab */
		final EventCalculator eventCal;
		if (isValidationFlag == 1.0) {
			eventCal = new EventCalculator(initialState, keplerOrbit.getDate(), keplerOrbit, constants);
		} else {
			eventCal = null;
		}

		/* Propulsion System Initiate */
		PropulsionSystem propulsionSystem = new PropulsionSystem(
				new AbsoluteDate(1, 1, 1, 0, 0, 0, constants.getTimeScale()), 100, equivalentThrust, equivalentIsp,
				new Vector3D(1, 0, 0), maxCheck, emptyMass, mi);

		/* Create the Matlab push Handler */
		final MatlabFixedPushHandler mph;
		final MatlabVariablePushHandler maph;
		if (propagtiontype == 1.0) {
			maph = new MatlabVariablePushHandler(mi, options, matlabFunctions, false, propulsionSystem, eventCal,
					constants);
			numericPropagator.setMasterMode(maph);
			mph = null;
		} else {
			mph = new MatlabFixedPushHandler(mi, options, matlabFunctions, false, propulsionSystem, eventCal,
					constants);
			numericPropagator.setMasterMode(stepSize, mph);
			maph = null;
		}

		/* Register the force models */
		if (isForceModelsActive == 1.0) {
			numericPropagator.addForceModel(fmf.getDrag());
			numericPropagator.addForceModel(fmf.getHolmesFeatherstone());
		}
		/* Register the events */
		if (isValidationFlag == 1.0) {
			numericPropagator.addEventDetector(eventCal.getApogeeEventDetector());
			numericPropagator.addEventDetector(eventCal.getLatArg(0));
			numericPropagator.addEventDetector(eventCal.getLatArg(90));
		}

		/* Deactivate the thrusters by validation */
		if (isValidationFlag == 0.0) {
			numericPropagator.addForceModel(propulsionSystem);
		}

		/*
		 * Set the Attitude Provider when thrust direction need local orbital
		 * frame
		 */
		numericPropagator
				.setAttitudeProvider(new LofOffset(initialState.getFrame(), LOFType.LVLH, RotationOrder.XYZ, 0, 0, 0));
		numericPropagator.setInitialState(initialState);
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
