package de.netsat.orekit;

import matlabcontrol.MatlabInvocationException;

import org.apache.commons.math3.ode.AbstractIntegrator;
import org.apache.commons.math3.ode.nonstiff.AdaptiveStepsizeIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.errors.OrekitException;
import org.orekit.errors.PropagationException;
import org.orekit.forces.ForceModel;
import org.orekit.forces.SphericalSpacecraft;
import org.orekit.forces.drag.DragForce;
import org.orekit.forces.drag.HarrisPriester;
import org.orekit.forces.gravity.HolmesFeatherstoneAttractionModel;
import org.orekit.forces.gravity.potential.GravityFieldFactory;
import org.orekit.forces.gravity.potential.NormalizedSphericalHarmonicsProvider;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.OrbitType;
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;

import de.netsat.orekit.matlab.MatlabInterface;
/**
 * This class contains functions that can easily setup different simulation initializations, such as propagators, spacecraft initial states, etc. It also contains 
 * get functions for the different parameters.
 * 
 * @author Alexander Kramer
 *
 */
public class setupSimulation {
	static double outputStepSize;
	static double positionTolerance;
	static double duration;
	static double minstep;
	static double maxstep;
	
	/**
	 * This function initializes a numeric propagator for a single satellite by calling the Matlab function "setNumericalPropagatorSettings()". 
	 * Therefore it needs a {@link MatlabInterface}, a {@link KeplerianOrbit} and a choice of the wanted integrator (null: DormandPrince853, 1: ClassicalRungeKutta).   
	 * 	
	 * @param mi
	 * @param Kepler
	 * @param choiceOfProp
	 * @return NumericalPropagator
	 * @throws MatlabInvocationException
	 * @throws OrekitException 
	 */
	public static NumericalPropagator setupNumPropSingleSat(MatlabInterface mi, KeplerianOrbit Kepler, double choiceOfProp) throws MatlabInvocationException, OrekitException{
				// Get simulation parameters from Matlab:
				Object[] returningObject;
				AbstractIntegrator integrator;
				returningObject = mi.returningEval("getVariables(position_tolerance, min_step, max_step, duration, step_size)", 1);
				
				positionTolerance = ((double[])returningObject[0])[0];
			    minstep  = ((double[])returningObject[0])[1];
			    maxstep =  ((double[])returningObject[0])[2];
			    duration =  ((double[])returningObject[0])[3];
			    outputStepSize = ((double[])returningObject[0])[4];
			    // Build the propagator model:
				final OrbitType propagationType = OrbitType.CARTESIAN;

				final double[][] tolerances =
			            NumericalPropagator.tolerances(positionTolerance, Kepler, propagationType);
				
				if(choiceOfProp == 1){
					integrator = new ClassicalRungeKuttaIntegrator(outputStepSize); 
				}
				else { integrator =
			            new DormandPrince853Integrator(minstep, maxstep, tolerances[0], tolerances[1]);}
			    
				NumericalPropagator numericPropagator = new NumericalPropagator (integrator);
				numericPropagator.setOrbitType(propagationType);
				
				//Setup Force models:
				
				final NormalizedSphericalHarmonicsProvider provider = GravityFieldFactory.getNormalizedProvider(10, 10);
			    ForceModel holmesFeatherstone =
			            new HolmesFeatherstoneAttractionModel(FramesFactory.getITRF(IERSConventions.IERS_2010, true), provider);
			    
			    ForceModel atmosphericDrag = new DragForce(new HarrisPriester(CelestialBodyFactory.getSun(),
			            new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
			                    Constants.WGS84_EARTH_FLATTENING,
			                    FramesFactory.getITRF(IERSConventions.IERS_2010, true))),
			                    new SphericalSpacecraft(0.01, 2.2, 0, 0));
			    
			    numericPropagator.addForceModel(holmesFeatherstone);
			    numericPropagator.addForceModel(atmosphericDrag);
				
				return numericPropagator;
	}
	
	/** This function initializes a numeric propagator for a single satellite by calling the Matlab function "setNumericalPropagatorSettings()" using a
	 * DormandPrince853 integrator. 
	 * @param mi
	 * @param Kepler
	 * @param choiceOfProp
	 * @return
	 * @throws PropagationException
	 * @throws MatlabInvocationException
	 */
	public static NumericalPropagator setupNumPropSingleSatAdap(MatlabInterface mi, KeplerianOrbit Kepler) throws PropagationException, MatlabInvocationException{
		// Get simulation parameters from Matlab:
		Object[] returningObject;

		returningObject = mi.returningEval("setNumericalPropagatorSettings()", 5);
	
	    positionTolerance = ((double[])returningObject[0])[0];
	    minstep  = ((double[])returningObject[1])[0];
	    maxstep =  ((double[])returningObject[2])[0];
	    duration =  ((double[])returningObject[3])[0];
	    outputStepSize = ((double[])returningObject[4])[0];
	    // Build the propagator model:
		final OrbitType propagationType = OrbitType.CARTESIAN;
		
		final double[][] tolerances =
	            NumericalPropagator.tolerances(positionTolerance, Kepler, propagationType);
    	//System.out.println("Tolerances" + Arrays.toString(tolerances[1]));

	    AdaptiveStepsizeIntegrator integrator =
	            new DormandPrince853Integrator(minstep, maxstep, tolerances[0], tolerances[1]); 

		
	    NumericalPropagator numericPropagator = new NumericalPropagator(integrator);
		numericPropagator.setOrbitType(propagationType);				
		
		return numericPropagator;
}
	
	/**
	 * This function initializes a numeric propagator for a single satellite by calling the Matlab function "setNumericalPropagatorSettings()" using
	 * a ClassicalRungeKuttaIntegrator.
	 * 	
	 * @param mi
	 * @param Kepler
	 * @return NumericalPropagator
	 * @throws PropagationException
	 * @throws MatlabInvocationException
	 */
	public static NumericalPropagator setupNumPropSingleSatFixedStep(MatlabInterface mi, KeplerianOrbit Kepler) throws PropagationException, MatlabInvocationException{
				// Get simulation parameters from Matlab:
				Object[] returningObject;

				returningObject = mi.returningEval("setNumericalPropagatorSettings()", 5);
			
			    positionTolerance = ((double[])returningObject[0])[0];
			    minstep  = ((double[])returningObject[1])[0];
			    maxstep =  ((double[])returningObject[2])[0];
			    duration =  ((double[])returningObject[3])[0];
			    outputStepSize = ((double[])returningObject[4])[0];
			    // Build the propagator model:
				final OrbitType propagationType = OrbitType.CARTESIAN;
				
				//final double[][] tolerances =
			    //        NumericalPropagator.tolerances(positionTolerance, Kepler, propagationType);

		        AbstractIntegrator integrator = new ClassicalRungeKuttaIntegrator(outputStepSize);   

				
			    NumericalPropagator numericPropagator = new NumericalPropagator(integrator);
				numericPropagator.setOrbitType(propagationType);				
				
				return numericPropagator;
	}
	public static double getOutputStepSize()
	{
		return outputStepSize;
	}
	
	public static double getPositionTolerance()
	{
		return positionTolerance;
	}
	
	public static double getminstep()
	{
		return minstep;
	}
	
	public static double getmaxstep()
	{
		return maxstep;
	}
	
	public static double getduration()
	{
		return duration;
	}
}
