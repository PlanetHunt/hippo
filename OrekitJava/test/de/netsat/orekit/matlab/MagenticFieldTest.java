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
		String[] options = { "velocity" };
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
		// new matlabPushStepHandler(mi);
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

	/**
	 * Converts the ECI Coordinates (R;V) to Latitude, Longitude, Altitude
	 * (L;L;A). The point it uses the ITRF (Inertial Terrestrial reference frame
	 * for the frame.
	 * 
	 * @param ECICoordinates
	 * @param oae
	 * @param date
	 * @return
	 * @throws OrekitException
	 */
	public static GeodeticPoint getLLA(Vector3D ECICoordinates, OneAxisEllipsoid oae, AbsoluteDate date)
			throws OrekitException {
		return oae.transform(ECICoordinates, FramesFactory.getITRF(IERSConventions.IERS_2010, true), date);
	}

	/**
	 * Calculates the magnetic field in a given ECI points.
	 * 
	 * @param ECICoordinates
	 * @param oae
	 * @param date
	 * @param model
	 * @return
	 * @throws OrekitException
	 */
	public static Vector3D calculateMagenticField(Vector3D ECICoordinates, OneAxisEllipsoid oae, AbsoluteDate date,
			GeoMagneticField model) throws OrekitException {
		GeodeticPoint geop = getLLA(ECICoordinates, oae, date);
		// The altitude which is delivered by the getLLA function is in m it
		// should be converted to KM.
		double altitude = geop.getAltitude() / 1000;
		double latitude = geop.getLatitude();
		double longtitude = geop.getLongitude();
		GeoMagneticElements geome = model.calculateField(Math.toDegrees(latitude), Math.toDegrees(longtitude),
				altitude);
		return geome.getFieldVector();
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
