package de.netsat.orekit.matlab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.orekit.errors.OrekitException;
import org.orekit.forces.gravity.potential.GravityFieldFactory;
import org.orekit.forces.gravity.potential.NormalizedSphericalHarmonicsProvider;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.propagation.SpacecraftState;
import org.orekit.utils.Constants;

import de.netsat.orekit.NetSatConfiguration;
import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;

public class AlexTest {
	static int sat_nr = 1;
	
/*	
public static SpacecraftState runNumericalPropagatorlocal(MatlabInterface mi, KeplerianOrbit keplerOrbit) throws MatlabInvocationException, OrekitException  {
		
		Object[] returningObject = mi.returningEval("setNumericalPropagatorSettings()", 5);
        double positionTolerance = ((double[])returningObject[0])[0];
        double minStep  = ((double[])returningObject[1])[0];
        double maxstep =  ((double[])returningObject[2])[0];
        double duration = ((double[])returningObject[3])[0];
        double outputStepSize = ((double[])returningObject[4])[0];
        
		
		final OrbitType propagationType = OrbitType.KEPLERIAN;
		final double[][] tolerances =
                NumericalPropagator.tolerances(positionTolerance, keplerOrbit, propagationType);
        AdaptiveStepsizeIntegrator integrator =
                new DormandPrince853Integrator(minStep, maxstep, tolerances[0], tolerances[1]);

		 
		 
		// Force Model (reduced to perturbing gravity field)
        
        final NormalizedSphericalHarmonicsProvider provider =
                GravityFieldFactory.getNormalizedProvider(10, 10);
        ForceModel holmesFeatherstone =
                new HolmesFeatherstoneAttractionModel(FramesFactory.getITRF(IERSConventions.IERS_2010,
                                                                            true),
                                                      provider);
        
        
        ForceModel atmosphericDrag = new DragForce(new HarrisPriester(CelestialBodyFactory.getSun(),
                new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
                        Constants.WGS84_EARTH_FLATTENING,
                        FramesFactory.getITRF(IERSConventions.IERS_2010, true))),
                        new BoxAndSolarArraySpacecraft(0.1, 0.1, 0.1, CelestialBodyFactory.getSun(), 0, Vector3D.PLUS_J, 2.2, 0.0, 0.0));
        
        
        NumericalPropagator numericPropagator = new NumericalPropagator(integrator);
		numericPropagator.setOrbitType(propagationType);
		
		
		
		SpacecraftState initialState = new SpacecraftState(keplerOrbit, 
				new Attitude(FramesFactory.getEME2000(),
						new TimeStampedAngularCoordinates(keplerOrbit.getDate(), 
								new PVCoordinates(new Vector3D(10,10), new Vector3D(1, 2)), 
								new PVCoordinates(new Vector3D(15,3),  new Vector3D(1, 2)))));
		
		SpacecraftState thrustedSat = initialState;
		
		thrustedSat = initialState.addAdditionalState("Thrust", 0,0,0);
		AdditionalEquations ThrustEq =  new AdditionalEquations() {
			
			@Override
			public String getName() {
				return "Thrust";
			}
			
			@Override
			public double[] computeDerivatives(SpacecraftState s, double[] pDot)
					throws OrekitException {
//					double[] thrust = s.getPVCoordinates().getVelocity().normalize().toArray();
				Vector3D thrust_vector = s.getPVCoordinates().getVelocity().normalize();
				thrust_vector = thrust_vector.scalarMultiply(0.);
//					System.out.println("Thrust: " + thrust_vector.getX() + thrust_vector.getY() + thrust_vector.getZ());
				
				double[] thrust = {0,0,0};
				
				//return thrust;
				return thrust_vector.toArray();
			}
		};
		numericPropagator.addAdditionalEquations(ThrustEq);
		
		
		*/
		/*
		PVCoordinatesProvider earth = CelestialBodyFactory.getEarth();
		PVCoordinatesProvider sun = CelestialBodyFactory.getSun();
		final EclipseDetector eclipseState = new EclipseDetector(sun, 696000000., earth, Constants.WGS84_EARTH_EQUATORIAL_RADIUS).
                withHandler(new EventHandler<EclipseDetector>() {
	                public Action eventOccurred(final SpacecraftState s, final EclipseDetector detector, final boolean increasing) {
	
	                    return Action.CONTINUE;
	                }
                    public SpacecraftState resetState(EclipseDetector detector, SpacecraftState oldState) {
                        return oldState;
                    }
                });
        */
		
		/*
		thrustedSat = thrustedSat.addAdditionalState("BatteryState", 20);
		AdditionalEquations batteryEquations = new AdditionalEquations() {

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "BatteryState";
			}
			
					
			@Override
			public double[] computeDerivatives(SpacecraftState s, double[] pDot)
					throws OrekitException {
				// TODO Auto-generated method stub
				double[] charging = {-.001};
				
//				System.out.println("pDot: " + pDot[0] + "");
//				System.out.println("Battery: " + s.getAdditionalState("BatteryState")[0] + "");
//				TimeStampedPVCoordinates sun = CelestialBodyFactory.getSun().getPVCoordinates(s.getDate(), s.getFrame());
//	            TimeStampedPVCoordinates earth = CelestialBodyFactory.getEarth().getPVCoordinates(s.getDate(), s.getFrame());
//	            TimeStampedPVCoordinates sat = s.getPVCoordinates();
	            
//	            if (eclipseState.getTotalEclipse()){
//	            	charging = -0.01;
//	            }else{
//	            	charging = 0.01;
//	            }
				
				return null;
			}
		};
		numericPropagator.addAdditionalEquations(batteryEquations);
		
        // Add force model to the propagator
		numericPropagator.addForceModel(holmesFeatherstone);
        numericPropagator.addForceModel(atmosphericDrag);
        
        // Set up operating mode for the propagator as master mode
        // with fixed step and specialized step handler
		new matlabPushStepHandler(mi);
        numericPropagator.setMasterMode(outputStepSize, new matlabPushStepHandler(mi));
        // Set up initial state in the propagator
        numericPropagator.setInitialState(thrustedSat);
        // Extrapolate from the initial to the final date
        SpacecraftState finalState = numericPropagator.propagate(keplerOrbit.getDate().shiftedBy(duration));
        
        return finalState;
		
	}*/
	
	
	public static void main(String[] args) throws MatlabConnectionException, MatlabInvocationException, IOException
	{		
		NetSatConfiguration.init();
		
		MatlabProxy proxy = null;
			MatlabInterface mi = new MatlabInterface(MatlabInterface.MATLAB_PATH, null);
			proxy = mi.getProxy();
			
		 try {

	            // gravity field
			 	
			 	mi.eval("clear");
			 	mi.eval("tic;");
	            NormalizedSphericalHarmonicsProvider provider = GravityFieldFactory.getNormalizedProvider(2, 0);
	            double mu =  provider.getMu();
	            proxy.setVariable("mu", mu);	    	    									// set earth gravitational constant in matlab
	            		
	            // get keplerian elements for both satellites from matlab and create satellite object 
				KeplerianOrbit uwe_3 = loadScripts.getKeplerOrbit(mi, 3);
	            System.out.println("a = "+ uwe_3.getA() + " e = " + uwe_3.getE());
	            //KeplerianOrbit canx_5 = getKeplerOrbit(mi, 2);
	    		//System.out.println("CanX 4: a = " + String.valueOf(canx_4.getA()) + " e = " + String.valueOf(canx_4.getE()) + " i = " + String.valueOf(canx_4.getI()) + " Omega = " + String.valueOf(canx_4.getPerigeeArgument()) + " RAAN = " + String.valueOf(canx_4.getRightAscensionOfAscendingNode()) + " M = " + String.valueOf(canx_4.getMeanAnomaly()) + "\r\n");
 				//System.out.println("CanX 5: a = " + String.valueOf(canx_5.getA()) + " e = " + String.valueOf(canx_5.getE()) + " i = " + String.valueOf(canx_5.getI()) + " Omega = " + String.valueOf(canx_5.getPerigeeArgument()) + " RAAN = " + String.valueOf(canx_5.getRightAscensionOfAscendingNode()) + " M = " + String.valueOf(canx_5.getMeanAnomaly()) + "\r\n");
 				//SpacecraftState finished = Maneuvers.runImpulseManeuver(mi,canx_4);
	            
 				//SpacecraftState finished = Maneuvers.runThrustManeuver(mi,canx_4);
	            //SpacecraftState finished = Maneuvers.runConstantDeOrbitManeuver(mi,canx_4);
	            //SpacecraftState finished = Maneuvers.runApogeeBoostDeOrbitManeuver(mi,uwe_3);
	            SpacecraftState finished = Maneuvers.runAnalyticalPropagator(mi,uwe_3);
	            
	            proxy.setVariable("earth_radius", Constants.EGM96_EARTH_EQUATORIAL_RADIUS);
	            mi.eval("toc");

 				proxy.disconnect();
 				//System.out.println("a = "+ finished.getA() + " e = " + finished.getE());
 				
 				//System.out.println("Satellite final mass: " + finished.getMass());
 				
				//System.out.println("semi-major axis: " + finished.getA() + "; eccentricity: " + finished.getE() + "; inclination: " + finished.getI());
 				System.out.println("done");
 				
	        } catch (OrekitException oe) {
	            System.err.println(oe.getLocalizedMessage());
	            System.exit(1);
	            
	        } 
		 		/*
	        	catch (FileNotFoundException fnfe) {
	            System.err.println(fnfe.getLocalizedMessage());
	            System.exit(1);
	        }
	        */
			
			/*
			final Double[] ArrayTest = testDouble();
			proxy.setVariable("Test", ArrayTest);
			System.out.println("done");
			*/
			proxy.disconnect();
	}
	
	
	public static Double[] testDouble() {
		// Create List of numbers
		List<Double> numbers = new ArrayList<Double>();
		
		// Generate 100 random numbers
		for(int i = 0; i < 100; i++) {
			double rnd = Math.random();
			
			// Add it to list
			numbers.add(rnd);
		}
		
		// Convert back to array
		return numbers.toArray(new Double[numbers.size()]);
	}

}
