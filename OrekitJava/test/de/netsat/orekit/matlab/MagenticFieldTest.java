package de.netsat.orekit.matlab;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.ode.nonstiff.AdaptiveStepsizeIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.orekit.attitudes.Attitude;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.models.earth.GeoMagneticElements;
import org.orekit.models.earth.GeoMagneticField;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.OrbitType;

import de.netsat.orekit.NetSatConfiguration;
import de.netsat.orekit.matlab.loadScripts;
import de.netsat.orekit.matlab.MatlabPushHandler;

import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.IERSConventions;
import org.orekit.utils.PVCoordinates;
import org.orekit.utils.TimeStampedAngularCoordinates;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;

public class MagenticFieldTest {

	/**
	 * 
	 * @param mi
	 * @return
	 * @throws MatlabInvocationException
	 * @throws OrekitException
	 */
	public static SpacecraftState runNumericalPropagatorlocal(MatlabInterface mi)
			throws MatlabInvocationException, OrekitException

	{
		NetSatConfiguration.init();
		int sat_nr = 1;
		Object[] returningObject;
		returningObject = mi.returningEval("setNumericalPropagatorSettings()", 5);
		KeplerianOrbit keplerOrbit = loadScripts.getKeplerOrbit(mi, sat_nr);
		double positionTolerance = ((double[]) returningObject[0])[0];
		double minStep = ((double[]) returningObject[1])[0];
		double maxstep = ((double[]) returningObject[2])[0];
		double duration = ((double[]) returningObject[3])[0];
		double outputStepSize = ((double[]) returningObject[4])[0];

		final OrbitType propagationType = OrbitType.KEPLERIAN;
		final double[][] tolerances = NumericalPropagator.tolerances(positionTolerance, keplerOrbit, propagationType);
		AdaptiveStepsizeIntegrator integrator = new DormandPrince853Integrator(minStep, maxstep, tolerances[0],
				tolerances[1]);

		SpacecraftState initialState = new SpacecraftState(keplerOrbit,
				new Attitude(FramesFactory.getEME2000(),
						new TimeStampedAngularCoordinates(keplerOrbit.getDate(),
								new PVCoordinates(new Vector3D(10, 10), new Vector3D(1, 2)),
								new PVCoordinates(new Vector3D(15, 3), new Vector3D(1, 2)))),
				1.0);

		NumericalPropagator numericPropagator = new NumericalPropagator(integrator);
		numericPropagator.setInitialState(initialState);
		String[] options = { "mu", "velocity", "position"};
		numericPropagator.setMasterMode(outputStepSize, new MatlabPushHandler(mi, options));
		SpacecraftState finalState = numericPropagator.propagate(keplerOrbit.getDate().shiftedBy(duration));

		return finalState;

	}

	/**
	 * Set the variable in matltab (the variable should be the type double or
	 * could be casted to double.
	 * 
	 * @param mi
	 * @param name
	 * @param value
	 * @throws MatlabInvocationException
	 */
	public void setVariableInMatlab(MatlabInterface mi, String name, double value) throws MatlabInvocationException {

		mi.getProxy().setVariable(name, value);
	}

	public static void main(String[] args)
			throws OrekitException, MatlabConnectionException, MatlabInvocationException {
		// Object[] obj = null;
		MatlabInterface mi;
		mi = new MatlabInterface(MatlabInterface.MATLAB_PATH, null);
		SpacecraftState obj = runNumericalPropagatorlocal(mi);

		// System.out.println(((double[]) obj[1])[0]);
	}

}
