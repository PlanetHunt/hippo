package de.netsat.orekit.matlab;

import org.apache.commons.math3.ode.nonstiff.AdaptiveStepsizeIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.orekit.errors.OrekitException;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.OrbitType;

import de.netsat.orekit.matlab.loadScripts;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.numerical.NumericalPropagator;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;

public class MagenticFieldTest {
	public static SpacecraftState runNumericalPropagatorlocal(MatlabInterface mi)
			throws MatlabInvocationException, OrekitException

	{
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
        NumericalPropagator numericPropagator = new NumericalPropagator(integrator);
		numericPropagator.setOrbitType(propagationType);
        SpacecraftState finalState = numericPropagator.propagate(keplerOrbit.getDate().shiftedBy(duration));
        
        return finalState;
		

	}

	public static void main(String[] args)
			throws OrekitException, MatlabConnectionException, MatlabInvocationException {
		//Object[] obj = null;
		MatlabInterface mi;
		mi = new MatlabInterface(MatlabInterface.MATLAB_PATH, null);
		//obj = runNumericalPropagatorlocal(mi);

		//System.out.println(((double[]) obj[1])[0]);
	}

}
