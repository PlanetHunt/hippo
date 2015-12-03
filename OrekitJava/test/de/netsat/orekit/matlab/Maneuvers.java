package de.netsat.orekit.matlab;

import matlabcontrol.MatlabInvocationException;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.ode.nonstiff.AdaptiveStepsizeIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.orekit.attitudes.Attitude;
import org.orekit.attitudes.LofOffset;
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
import org.orekit.forces.maneuvers.ImpulseManeuver;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.LOFType;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.OrbitType;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.propagation.events.ApsideDetector;
import org.orekit.propagation.events.EventShifter;
import org.orekit.propagation.events.handlers.EventHandler;
import org.orekit.propagation.integration.AdditionalEquations;
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.propagation.sampling.OrekitFixedStepHandler;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;
import org.orekit.utils.PVCoordinates;
import org.orekit.utils.TimeStampedAngularCoordinates;
import org.orekit.utils.TimeStampedPVCoordinates;

import de.netsat.orekit.setupSimulation;
import de.netsat.orekit.actuator.NanoFEEP;

public class Maneuvers {
	
	static double thrust = 19e-6;
	static double num_thrusters =2;
	static NanoFEEP myNanoFEEP1 = new NanoFEEP(new Vector3D(0, 0), new Vector3D(0, 0));
	
	// dedicated apogee detector handler - sets thruster to 'fire' and stops firing afterwards (see eventshifter)
	public static class ApogeeHandler implements EventHandler<ApsideDetector> {

        public Action eventOccurred(final SpacecraftState s, final ApsideDetector detector,
                                    final boolean increasing) {
            if (increasing) {
            	//fire = false;
            	System.out.println("Apogee Time Stop" + s.getDate());
                return Action.CONTINUE;
            } else {
                System.out.println("Apogee Time Fire" + s.getDate());
                //fire = true;
                return Action.CONTINUE;
            }
        }

        public SpacecraftState resetState(final ApsideDetector detector, final SpacecraftState oldState) {
            return oldState;
        }

    }
	
	public static class matlabPushStepHandler implements OrekitFixedStepHandler {
		MatlabInterface mi;
		Vector3D[] position_history;
		
		Vector3D[] velocity_history;
		public matlabPushStepHandler(MatlabInterface mi) {
			this.mi = mi;
		}
		@Override
		public void handleStep(SpacecraftState currentState, boolean isLast)
				throws PropagationException {
			// TODO Auto-generated method stub
			try {
				matlabInterfacePushPV(currentState);
			}catch(Exception e){
				e.printStackTrace();
				System.err.println(e.getMessage());
			}
		}
		@Override
		public void init(SpacecraftState s0, AbsoluteDate t)
				throws PropagationException {

			try {
				mi.eval("earthModel3D");
			}catch (Exception e){
				e.printStackTrace();
		        System.err.println(e.getMessage());
			}
		}

		void matlabInterfacePushPV(SpacecraftState scstate) throws MatlabInvocationException, OrekitException {
			try {
				TimeStampedPVCoordinates scCoordinates = scstate.getPVCoordinates(FramesFactory.getITRF(IERSConventions.IERS_2010, true));
				
				Vector3D pVec =  scCoordinates.getPosition();
				Vector3D vVec =  scCoordinates.getVelocity();
				AbsoluteDate date = scCoordinates.getDate();
				
				
				mi.getProxy().setVariable("Time", scCoordinates.getDate().toString());
				
				mi.getProxy().setVariable("Position_x", pVec.getX());
				mi.getProxy().setVariable("Position_y", pVec.getY());
				mi.getProxy().setVariable("Position_z", pVec.getZ());
				
				mi.getProxy().setVariable("vel_x", scstate.getPVCoordinates().getVelocity().getX());
				mi.getProxy().setVariable("vel_y", scstate.getPVCoordinates().getVelocity().getY());
				mi.getProxy().setVariable("vel_z", scstate.getPVCoordinates().getVelocity().getZ());
				mi.getProxy().setVariable("period", scstate.getKeplerianPeriod());
				
				TimeStampedPVCoordinates sunPos = CelestialBodyFactory.getSun().getPVCoordinates(scCoordinates.getDate(), FramesFactory.getITRF(IERSConventions.IERS_2010, true));
				mi.getProxy().setVariable("Sun_Position", sunPos.getPosition().toArray());
				//mi.getProxy().setVariable("BatteryState", scstate.getAdditionalState("BatteryState")[0]);
				
				mi.getProxy().eval("cubeTransform");
				
			}catch(Exception e) {
				e.printStackTrace();
		        System.err.println(e.getMessage());
		    }	
		}
		
	}
	// dedicated perigee detector handler - only prints perigee time
	public static class PerigeeHandler implements EventHandler<ApsideDetector> {

        public Action eventOccurred(final SpacecraftState s, final ApsideDetector detector,
                                    final boolean increasing) {
            if (increasing) {
                System.out.println("Perigee Time" + s.getDate());
                System.out.println("Satellite mass: " + s.getMass());
                //fire = true;
                return Action.CONTINUE;
            } else {
                return Action.CONTINUE;
            }
        }

        public SpacecraftState resetState(final ApsideDetector detector, final SpacecraftState oldState) {
            return oldState;
        }

    }
	
	private static boolean fire = false;
	private static boolean eclipse = false;

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	/**
	 * 
	 * @param mi
	 * @throws OrekitException 
	 */
	public static SpacecraftState runImpulseManeuver(MatlabInterface mi, KeplerianOrbit keplerOrbit) throws MatlabInvocationException, OrekitException  {
		
		// Get simulation parameters from Matlab:
		Object[] returningObject = mi.returningEval("numericalIntegratorSettings()", 5);
        double positionTolerance = ((double[])returningObject[0])[0];
        double minStep  = ((double[])returningObject[1])[0];
        double maxstep =  ((double[])returningObject[2])[0];
        double duration = ((double[])returningObject[3])[0];
        double outputStepSize = ((double[])returningObject[4])[0];
        
		// Build the propagator model:
		final OrbitType propagationType = OrbitType.KEPLERIAN;
		final double[][] tolerances =
                NumericalPropagator.tolerances(positionTolerance, keplerOrbit, propagationType);
        AdaptiveStepsizeIntegrator integrator =
                new DormandPrince853Integrator(minStep, maxstep, tolerances[0], tolerances[1]);
        //AbstractIntegrator integrator = new ClassicalRungeKuttaIntegrator(outputStepSize);   
		
		
        NumericalPropagator numericPropagator = new NumericalPropagator(integrator);
		numericPropagator.setOrbitType(propagationType);
		
		
		
		// Force Model:
		// 				reduced to perturbing gravity field:        
        final NormalizedSphericalHarmonicsProvider provider =
                GravityFieldFactory.getNormalizedProvider(10, 10);
        ForceModel holmesFeatherstone =
                new HolmesFeatherstoneAttractionModel(FramesFactory.getITRF(IERSConventions.IERS_2010,
                                                                            true),
                                                      provider);
        
        //				third body perturbations:
        //ForceModel MoonAsThirdBody = new ThirdBodyAttraction(CelestialBodyFactory.getMoon());
        //ForceModel SunAsThirdBody = new ThirdBodyAttraction(CelestialBodyFactory.getSun());
        
        //				atmospheric drag:
        /*
        ForceModel atmosphericDrag = new DragForce(new HarrisPriester(CelestialBodyFactory.getSun(),
                new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
                        Constants.WGS84_EARTH_FLATTENING,
                        FramesFactory.getITRF(IERSConventions.IERS_2010, true))),
                        new BoxAndSolarArraySpacecraft(0.1, 0.1, 0.1, CelestialBodyFactory.getSun(), 0, Vector3D.PLUS_J, 2.2, 0.0, 0.0));
        */
		// Add force model to the propagator:
		numericPropagator.addForceModel(holmesFeatherstone);
        //numericPropagator.addForceModel(atmosphericDrag);
        //numericPropagator.addForceModel(MoonAsThirdBody);
        //numericPropagator.addForceModel(SunAsThirdBody);
	
        // Initialize spacecraft state:
		SpacecraftState initialState = new SpacecraftState(keplerOrbit, 
				new Attitude(FramesFactory.getEME2000(),
						new TimeStampedAngularCoordinates(keplerOrbit.getDate(), 
								new PVCoordinates(new Vector3D(10,10), new Vector3D(1, 2)), 
								new PVCoordinates(new Vector3D(15,3),  new Vector3D(1, 2)))));
		
		SpacecraftState thrustedSat = initialState;
		
		// Event detection (apogee and perigee):		
		final ApsideDetector perigee = new ApsideDetector(1.e-6, keplerOrbit).withHandler(new PerigeeHandler());
		perigee.init(initialState, keplerOrbit.getDate());
		final ApsideDetector apogee = new ApsideDetector(1.e-6, keplerOrbit).withHandler(new ApogeeHandler());
		apogee.init(initialState, keplerOrbit.getDate());

		// Define ImpulseManeuver:
		Vector3D deltaVSat = new Vector3D(1, 0 ,0);
		double isp = 60.0;
		numericPropagator.setAttitudeProvider(new LofOffset(initialState.getFrame(), LOFType.TNW));
		final ImpulseManeuver<ApsideDetector> perigeeboost = new ImpulseManeuver<ApsideDetector>(perigee, numericPropagator.getAttitudeProvider(), deltaVSat , isp);
		final ImpulseManeuver<ApsideDetector> apogeeboost = new ImpulseManeuver<ApsideDetector>(apogee, numericPropagator.getAttitudeProvider(), deltaVSat , isp);

		// Add event to be detected to the propagator:
        //numericPropagator.addEventDetector(perigee);
        numericPropagator.addEventDetector(perigeeboost);
        numericPropagator.addEventDetector(apogeeboost);

               
        // Set up operating mode for the propagator as master mode with fixed step and specialized step handler:
		new matlabPushStepHandler(mi);
        numericPropagator.setMasterMode(outputStepSize, new matlabPushStepHandler(mi));
        
        // Set up initial state in the propagator:
        numericPropagator.setInitialState(thrustedSat);
        
        // Extrapolate from the initial to the final date:
        SpacecraftState finalState = numericPropagator.propagate(keplerOrbit.getDate().shiftedBy(duration));
        
        return finalState;
		
	}



public static SpacecraftState runThrustManeuver(MatlabInterface mi, KeplerianOrbit keplerOrbit) throws MatlabInvocationException, OrekitException  {
	
	// Get simulation parameters from Matlab:
	Object[] returningObject = mi.returningEval("numericalIntegratorSettings()", 5);
    double positionTolerance = ((double[])returningObject[0])[0];
    double minStep  = ((double[])returningObject[1])[0];
    double maxstep =  ((double[])returningObject[2])[0];
    double duration = ((double[])returningObject[3])[0];
    double outputStepSize = ((double[])returningObject[4])[0];
    
    // Convert to equinoctial orbit for computeDerivtaives function:
 		//final EquinoctialOrbit equiOrbit = (EquinoctialOrbit) OrbitType.EQUINOCTIAL.convertType(keplerOrbit);
 		
	// Build the propagator model:
	final OrbitType propagationType = OrbitType.CARTESIAN;
	
	final double[][] tolerances =
            NumericalPropagator.tolerances(positionTolerance, keplerOrbit, propagationType);
    AdaptiveStepsizeIntegrator integrator =
            new DormandPrince853Integrator(minStep, maxstep, tolerances[0], tolerances[1]);
    //AbstractIntegrator integrator = new ClassicalRungeKuttaIntegrator(outputStepSize);
            
    NumericalPropagator numericPropagator = new NumericalPropagator(integrator);
	numericPropagator.setOrbitType(propagationType);
	
	
	
	// Force Model:
	// 				reduced to perturbing gravity field:      
	
    final NormalizedSphericalHarmonicsProvider provider =
            GravityFieldFactory.getNormalizedProvider(10, 10);
    ForceModel holmesFeatherstone =
            new HolmesFeatherstoneAttractionModel(FramesFactory.getITRF(IERSConventions.IERS_2010,
                                                                        true),
                                                  provider);
    
    //				third body perturbations:
    //ForceModel MoonAsThirdBody = new ThirdBodyAttraction(CelestialBodyFactory.getMoon());
    //ForceModel SunAsThirdBody = new ThirdBodyAttraction(CelestialBodyFactory.getSun());
    
    //				atmospheric drag:
    
    ForceModel atmosphericDrag = new DragForce(new HarrisPriester(CelestialBodyFactory.getSun(),
            new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
                    Constants.WGS84_EARTH_FLATTENING,
                    FramesFactory.getITRF(IERSConventions.IERS_2010, true))),
                    new SphericalSpacecraft(0.01, 2.2, 0, 0));
//                    new BoxAndSolarArraySpacecraft(0.1, 0.1, 0.1, CelestialBodyFactory.getSun(), 0, Vector3D.PLUS_J, 2.2, 0.0, 0.0));
    
	// Add force model to the propagator:
	numericPropagator.addForceModel(holmesFeatherstone);
    numericPropagator.addForceModel(atmosphericDrag);
    //numericPropagator.addForceModel(MoonAsThirdBody);
    //numericPropagator.addForceModel(SunAsThirdBody);

	
		
    // Initialize spacecraft state:
	SpacecraftState initialState = new SpacecraftState(keplerOrbit, 
			new Attitude(FramesFactory.getEME2000(),
					new TimeStampedAngularCoordinates(keplerOrbit.getDate(), 
							new PVCoordinates(new Vector3D(10,10), new Vector3D(1, 2)), 
							new PVCoordinates(new Vector3D(15,3),  new Vector3D(1, 2)))),1.0);
	
	
	
	// Event detection (apogee and perigee):		
	/*
	final ApsideDetector perigee = new ApsideDetector(1.e-6, keplerOrbit).withHandler(new PerigeeHandler());
	perigee.init(initialState, keplerOrbit.getDate());
	final ApsideDetector apogee = new ApsideDetector(1.e-6, keplerOrbit).withHandler(new ApogeeHandler());
	apogee.init(initialState, keplerOrbit.getDate());
	*/
	// Add event to be detected to the propagator:
    //numericPropagator.addEventDetector(perigee);
    //numericPropagator.addEventDetector(apogee);
	//numericPropagator.addEventDetector(perigee);
	// shift apogee detector in time, to detect event 5% prior and after apogee for apogee boost:
    //numericPropagator.addEventDetector(new EventShifter<ApsideDetector>(apogee, true, -initialState.getKeplerianPeriod()/20*9, -initialState.getKeplerianPeriod()/20));

	//SpacecraftState thrustedSat = initialState;
	initialState = initialState.addAdditionalState("Thrust", 0,0,0);
	
	// Define ThrustManeuver:
	AdditionalEquations ThrustEq =  new AdditionalEquations() {
		
		@Override
		public double[] computeDerivatives(SpacecraftState s, double[] pDot)
				throws OrekitException {
				//double[] velocity_norm = s.getPVCoordinates().getVelocity().normalize().toArray();
				/*Vector3D velocity_change = s.getPVCoordinates().getVelocity().normalize();
				velocity_change = velocity_change.scalarMultiply(0.00002);
				OrbitType type = s.getOrbit().getType();
				CartesianOrbit cart_temp = (CartesianOrbit) type.convertType(s.getOrbit());
				cart_temp.getPVCoordinates().getVelocity().normalize().add(velocity_change);
				EquinoctialOrbit equinoc_dott = (EquinoctialOrbit) type.convertType(cart_temp);
				equinoc_dott.getJacobianWrtCartesian(type, jacobian);*/
				
			
			//double[] equinoc_dot = {cart_temp.getA()-s.getA(),cart_temp.getEquinoctialEx()-s.getEquinoctialEx(),cart_temp.getEquinoctialEy()-s.getEquinoctialEy(),cart_temp.getHx()-s.getHx(),cart_temp.getHy()-s.getHy(),cart_temp.getLv()-s.getLv(),0.0};
			//if (fire){
				//thrust[6] = -0.000000002;
				//thrust[0] = 1;
				//System.out.println(s.getLv());
				//fire = false;
			//}
			//System.out.println("Mass: " + s.getMass());
			
			// rv_dot is in cartesian orbital elements {r_x,r_y,r_z,v_x,v_y,v_z,m} and mass m! 
			double[] rv_dot = {0.0,0.0,0.0,0.0,0.0,0.0,0.0};
			if (fire){
				// get velocity direction:
				Vector3D velocity_norm = s.getPVCoordinates(FramesFactory.getEME2000()).getVelocity().normalize();
				// create vector in opposite direction of velocity with magnitude of instantaneous acceleration of thrusters:
				velocity_norm = velocity_norm.scalarMultiply(-4e-6);
				//rv_dot = {0.0,0.0,0.0,velocity_norm.getX(),velocity_norm.getY(), velocity_norm.getZ(),0.0};
				rv_dot[3] = velocity_norm.getX();
				rv_dot[4] = velocity_norm.getY();
				rv_dot[5] = velocity_norm.getZ();
				rv_dot[6] = -7.7e-11;
			}
			//return null;
			return rv_dot;
			//return thrust_vector.toArray();
		}
		
		@Override
		public String getName() {
			return "Thrust";
		}
	};

	numericPropagator.addAdditionalEquations(ThrustEq);
	
	/*PVCoordinatesProvider earth = CelestialBodyFactory.getEarth();
	PVCoordinatesProvider sun = CelestialBodyFactory.getSun();
	final EclipseDetector eclipseState = new EclipseDetector(sun, 0, earth, Constants.WGS84_EARTH_EQUATORIAL_RADIUS).
			withHandler(new EventHandler<EclipseDetector>(){

				@Override
				public org.orekit.propagation.events.handlers.EventHandler.Action eventOccurred(
						SpacecraftState s, EclipseDetector detector,
						boolean increasing) throws OrekitException {
					// TODO Auto-generated method stub
					System.out.println("Battery State: " + Arrays.toString(s.getAdditionalState("BatteryState")));
					return Action.CONTINUE;
				}
	
				@Override
				public SpacecraftState resetState(EclipseDetector detector,
						SpacecraftState oldState) throws OrekitException {
					// TODO Auto-generated method stub
					return oldState;
				}
				
			}); 
	eclipseState.init(initialState, keplerOrbit.getDate());
	numericPropagator.addEventDetector(eclipseState);
	
	initialState = initialState.addAdditionalState("BatteryState", 0.20);
	AdditionalEquations batteryEquations = new AdditionalEquations() {
		@Override
		public String getName() {
			return "BatteryState";
		}
		
				
		@Override
		public double[] computeDerivatives(SpacecraftState s, double[] charging)
				throws OrekitException {
															
			if (eclipseState.g(s)<0){
            	charging[0] = -0.0001; 	// discharging rate per second?
            }else{
            	charging[0] = 0.0001;	// charging rate per second?
            }
			return null;
		}
	};
	numericPropagator.addAdditionalEquations(batteryEquations);*/
	
	


           
    // Set up operating mode for the propagator as master mode with fixed step and specialized step handler:
	new matlabPushStepHandler(mi);
    numericPropagator.setMasterMode(outputStepSize, new matlabPushStepHandler(mi));
    
    // Set up initial state in the propagator:
    numericPropagator.setInitialState(initialState);
    
    // Extrapolate from the initial to the final date:
    SpacecraftState finalState = numericPropagator.propagate(keplerOrbit.getDate().shiftedBy(duration));
    //System.out.println("getorbittype" + numericPropagator.getOrbitType());
    return finalState;
	
	}

public static SpacecraftState runConstantDeOrbitManeuver(MatlabInterface mi, KeplerianOrbit keplerOrbit) throws MatlabInvocationException, OrekitException{
	// Get simulation parameters from Matlab:
		Object[] returningObject = mi.returningEval("numericalIntegratorSettings()", 5);
	    double positionTolerance = ((double[])returningObject[0])[0];
	    double minStep  = ((double[])returningObject[1])[0];
	    double maxstep =  ((double[])returningObject[2])[0];
	    double duration = ((double[])returningObject[3])[0];
	    double outputStepSize = ((double[])returningObject[4])[0];
	    
	    // Build the propagator model:
		final OrbitType propagationType = OrbitType.CARTESIAN;
		
		final double[][] tolerances =
	            NumericalPropagator.tolerances(positionTolerance, keplerOrbit, propagationType);
	    AdaptiveStepsizeIntegrator integrator =
	            new DormandPrince853Integrator(minStep, maxstep, tolerances[0], tolerances[1]);
	    	            
	    NumericalPropagator numericPropagator = new NumericalPropagator(integrator);
		numericPropagator.setOrbitType(propagationType);
		
		// Force Model:
		// 				reduced to perturbing gravity field:      
		
	    final NormalizedSphericalHarmonicsProvider provider =
	            GravityFieldFactory.getNormalizedProvider(10, 10);
	    ForceModel holmesFeatherstone =
	            new HolmesFeatherstoneAttractionModel(FramesFactory.getITRF(IERSConventions.IERS_2010,
	                                                                        true),
	                                                  provider);
	    
	    //				third body perturbations:
	    //ForceModel MoonAsThirdBody = new ThirdBodyAttraction(CelestialBodyFactory.getMoon());
	    //ForceModel SunAsThirdBody = new ThirdBodyAttraction(CelestialBodyFactory.getSun());
	    
	    //				atmospheric drag:
	    
	    ForceModel atmosphericDrag = new DragForce(new HarrisPriester(CelestialBodyFactory.getSun(),
	            new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
	                    Constants.WGS84_EARTH_FLATTENING,
	                    FramesFactory.getITRF(IERSConventions.IERS_2010, true))),
	                    new SphericalSpacecraft(0.01, 2.2, 0, 0));
	    // Add force model to the propagator:
		numericPropagator.addForceModel(holmesFeatherstone);
	    numericPropagator.addForceModel(atmosphericDrag);
	    //numericPropagator.addForceModel(MoonAsThirdBody);
	    //numericPropagator.addForceModel(SunAsThirdBody);

		
			
	    // Initialize spacecraft state:
		SpacecraftState initialState = new SpacecraftState(keplerOrbit, 
				new Attitude(FramesFactory.getEME2000(),
						new TimeStampedAngularCoordinates(keplerOrbit.getDate(), 
								new PVCoordinates(new Vector3D(10,10), new Vector3D(1, 2)), 
								new PVCoordinates(new Vector3D(15,3),  new Vector3D(1, 2)))),1.0);
	    
		// Event detection (apogee and perigee):		
		/*
		final ApsideDetector perigee = new ApsideDetector(1.e-6, keplerOrbit).withHandler(new PerigeeHandler());
		perigee.init(initialState, keplerOrbit.getDate());
		final ApsideDetector apogee = new ApsideDetector(1.e-6, keplerOrbit).withHandler(new ApogeeHandler());
		apogee.init(initialState, keplerOrbit.getDate());
		*/
		// Add event to be detected to the propagator:
	    //numericPropagator.addEventDetector(perigee);
	    //numericPropagator.addEventDetector(apogee);
		//numericPropagator.addEventDetector(perigee);
		// shift apogee detector in time, to detect event 5% prior and after apogee for apogee boost:
	    //numericPropagator.addEventDetector(new EventShifter<ApsideDetector>(apogee, true, -initialState.getKeplerianPeriod()/20*9, -initialState.getKeplerianPeriod()/20));

		// Define ThrustManeuver:
		
		//use 2 NanoFEEP thrusters with 2 uN thrust each:
		final double thrust = 2e-6;
		final NanoFEEP myNanoFEEP1 = new NanoFEEP(new Vector3D(0, 0), new Vector3D(0, 0));
		//NanoFEEP myNanoFEEP2 = new NanoFEEP(new Vector3D(0, 0), new Vector3D(0, 0));
		initialState = initialState.addAdditionalState("Thrust", 0,0,0);
		AdditionalEquations ThrustEq =  new AdditionalEquations() {
			
			@Override
			public double[] computeDerivatives(SpacecraftState s, double[] pDot)
					throws OrekitException {
				double[] rv_dot = {0.0,0.0,0.0,0.0,0.0,0.0,0.0};
				if (s.getMass()>0.99800)
				{
					// get velocity direction:
					Vector3D velocity_norm = s.getPVCoordinates(FramesFactory.getEME2000()).getVelocity().normalize();
					
					// create vector in opposite direction of velocity with magnitude of instantaneous acceleration of thrusters:
					velocity_norm = velocity_norm.scalarMultiply(-2*thrust);
					rv_dot[3] = velocity_norm.getX();
					rv_dot[4] = velocity_norm.getY();
					rv_dot[5] = velocity_norm.getZ();
					rv_dot[6] = 2*myNanoFEEP1.getFlowRate(thrust);
				}
				//return null;
				return rv_dot;
			}
			
			@Override
			public String getName() {
				return "Thrust";
			}
		};
	numericPropagator.addAdditionalEquations(ThrustEq);
	
	// Set up operating mode for the propagator as master mode with fixed step and specialized step handler:
	new PropagatorsForMatlabControl.matlabPushFinalHandler(mi);
    numericPropagator.setMasterMode(outputStepSize, new PropagatorsForMatlabControl.matlabPushFinalHandler(mi));
    
    // Set up initial state in the propagator:
    numericPropagator.setInitialState(initialState);
    
    // Extrapolate from the initial to the final date:
    SpacecraftState finalState = numericPropagator.propagate(keplerOrbit.getDate().shiftedBy(duration));
    //System.out.println("getorbittype" + numericPropagator.getOrbitType());
    return finalState;
	}

public static SpacecraftState runApogeeBoostDeOrbitManeuver(MatlabInterface mi, KeplerianOrbit keplerOrbit) throws MatlabInvocationException, OrekitException{
		// Setup Numerical Propagator:
		NumericalPropagator numericPropagator = setupSimulation.setupNumPropSingleSatFixedStep(mi, keplerOrbit);
		
		// Setup Force Models:
		// 				reduced to perturbing gravity field:      
		
	    final NormalizedSphericalHarmonicsProvider provider =
	            GravityFieldFactory.getNormalizedProvider(10, 10);
	    ForceModel holmesFeatherstone =
	            new HolmesFeatherstoneAttractionModel(FramesFactory.getITRF(IERSConventions.IERS_2010,
	                                                                        true),
	                                                  provider);
	    
	    //				third body perturbations:
	    //ForceModel MoonAsThirdBody = new ThirdBodyAttraction(CelestialBodyFactory.getMoon());
	    //ForceModel SunAsThirdBody = new ThirdBodyAttraction(CelestialBodyFactory.getSun());
	    
	    //				atmospheric drag:
	    
	    ForceModel atmosphericDrag = new DragForce(new HarrisPriester(CelestialBodyFactory.getSun(),
	            new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
	                    Constants.WGS84_EARTH_FLATTENING,
	                    FramesFactory.getITRF(IERSConventions.IERS_2010, true))),
	                    new SphericalSpacecraft(0.01, 2.2, 0, 0));
	    // Add force model to the propagator:
		numericPropagator.addForceModel(holmesFeatherstone);
	    numericPropagator.addForceModel(atmosphericDrag);
	    //numericPropagator.addForceModel(MoonAsThirdBody);
	    //numericPropagator.addForceModel(SunAsThirdBody);

		
			
	    // Initialize spacecraft state:
		SpacecraftState initialState = new SpacecraftState(keplerOrbit, 
				new Attitude(FramesFactory.getEME2000(),
						new TimeStampedAngularCoordinates(keplerOrbit.getDate(), 
								new PVCoordinates(new Vector3D(10,10), new Vector3D(1, 2)), 
								new PVCoordinates(new Vector3D(15,3),  new Vector3D(1, 2)))),1.0);
	    
		// Event detection (apogee and perigee):		
		/*
		final ApsideDetector perigee = new ApsideDetector(1.e-6, keplerOrbit).withHandler(new PerigeeHandler());
		perigee.init(initialState, keplerOrbit.getDate());
		*/
		final ApsideDetector apogee = new ApsideDetector(1.e-6, keplerOrbit).withHandler(new ApogeeHandler());
		apogee.init(initialState, keplerOrbit.getDate());
		
		// Add event to be detected to the propagator:
	    //numericPropagator.addEventDetector(perigee);
	    //numericPropagator.addEventDetector(apogee);
		//numericPropagator.addEventDetector(perigee);
		// shift apogee detector in time, to detect event 5% prior and after apogee for apogee boost:
	    //numericPropagator.addEventDetector(new EventShifter<ApsideDetector>(apogee, true, -initialState.getKeplerianPeriod()/20*9, -initialState.getKeplerianPeriod()/20));
	    numericPropagator.addEventDetector(new EventShifter<ApsideDetector>(apogee, true, -initialState.getKeplerianPeriod()/20*8, -initialState.getKeplerianPeriod()/20*2));

	    
		// Define ThrustManeuver:
		
		//use 2 NanoFEEP thrusters with 2 uN thrust each:
		
		//NanoFEEP myNanoFEEP2 = new NanoFEEP(new Vector3D(0, 0), new Vector3D(0, 0));
		initialState = initialState.addAdditionalState("Thrust", 0,0,0);
		AdditionalEquations ThrustEq =  new AdditionalEquations() {
			
			@Override
			public double[] computeDerivatives(SpacecraftState s, double[] pDot)
					throws OrekitException {
				double[] rv_dot = {0.0,0.0,0.0,0.0,0.0,0.0,0.0};
				if (s.getMass()>0.99800 & fire)
				{
					// get velocity direction:
					Vector3D velocity_norm = s.getPVCoordinates(FramesFactory.getEME2000()).getVelocity().normalize();
					
					// create vector in opposite direction of velocity with magnitude of instantaneous acceleration of thrusters:
					velocity_norm = velocity_norm.scalarMultiply(-num_thrusters*thrust);
					rv_dot[3] = velocity_norm.getX();
					rv_dot[4] = velocity_norm.getY();
					rv_dot[5] = velocity_norm.getZ();
					rv_dot[6] = num_thrusters*myNanoFEEP1.getFlowRate(thrust);
				}
				//return null;
				return rv_dot;
			}
			
			@Override
			public String getName() {
				return "Thrust";
			}
		};
	numericPropagator.addAdditionalEquations(ThrustEq);
	
	// Power consumption:
	
	
	
	// Set up operating mode for the propagator as master mode with fixed step and specialized step handler:
    numericPropagator.setMasterMode(setupSimulation.getOutputStepSize(), new PropagatorsForMatlabControl.matlabPushFinalHandler(mi));
    
    // Set up initial state in the propagator:
    numericPropagator.setInitialState(initialState);
    
    // Extrapolate from the initial to the final date:
    SpacecraftState finalState = numericPropagator.propagate(keplerOrbit.getDate().shiftedBy(setupSimulation.getduration()));
    
    return finalState;
	}

public static SpacecraftState runAnalyticalPropagator(MatlabInterface mi, KeplerianOrbit keplerOrbit) throws PropagationException, MatlabInvocationException {
	Object[] returningObject;
	returningObject = mi.returningEval("setNumericalPropagatorSettings()", 5);
	
    //positionTolerance = ((double[])returningObject[0])[0];
    //minstep  = ((double[])returningObject[1])[0];
    //maxstep =  ((double[])returningObject[2])[0];
    double duration =  ((double[])returningObject[3])[0];
    double outputStepSize = ((double[])returningObject[4])[0];
	//String propagatorType = (String) returningObject[2];
	// Simple extrapolation with Keplerian motion
	KeplerianPropagator keplerPropagator = new KeplerianPropagator(keplerOrbit);
	
	// Set the propagator to slave mode (could be omitted as it is the
	// default mode)
	keplerPropagator.setMasterMode(outputStepSize, new PropagatorsForMatlabControl.matlabPushFinalHandler(mi));
	SpacecraftState finalState = keplerPropagator.propagate(keplerOrbit.getDate().shiftedBy(duration));
	return finalState;
}
}
