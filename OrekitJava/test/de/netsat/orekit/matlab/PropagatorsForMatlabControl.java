package de.netsat.orekit.matlab;

import java.util.ArrayList;
import java.util.List;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.orekit.attitudes.Attitude;
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
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.ApsideDetector;
import org.orekit.propagation.events.EventShifter;
import org.orekit.propagation.integration.AdditionalEquations;
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.propagation.sampling.OrekitFixedStepHandler;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;
import org.orekit.utils.PVCoordinates;
import org.orekit.utils.TimeStampedAngularCoordinates;
import org.orekit.utils.TimeStampedPVCoordinates;

import de.netsat.orekit.NetSatConfiguration;
import de.netsat.orekit.setupSimulation;
import de.netsat.orekit.actuator.NanoFEEP;
import de.netsat.orekit.actuator.Tools;
import de.netsat.orekit.matlab.Maneuvers.ApogeeHandler;

public class PropagatorsForMatlabControl {
	
	private static boolean fire = false;
	private static double thrust = 19e-6;
	private static double num_thrusters =2;
	private static NanoFEEP myNanoFEEP1 = new NanoFEEP(new Vector3D(0, 0), new Vector3D(0, 0));
	private static boolean notDoneYet = true;
	
	public static SpacecraftState setupSpacecraftState(){
		SpacecraftState initialState = null;
		
		return initialState;
	}

	public static SpacecraftState runAdaptiveStepPropagator(MatlabInterface mi, KeplerianOrbit keplerOrbit) throws MatlabInvocationException, OrekitException{
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
    numericPropagator.setMasterMode(setupSimulation.getOutputStepSize(), new matlabPushFinalHandler(mi));
    
    // Set up initial state in the propagator:
    numericPropagator.setInitialState(initialState);
    
    // Extrapolate from the initial to the final date:
    SpacecraftState finalState = numericPropagator.propagate(keplerOrbit.getDate().shiftedBy(setupSimulation.getduration()));
    
    return finalState;
	}
	
	public static void main(String[] args) throws MatlabConnectionException, MatlabInvocationException {
		// TODO Auto-generated method stub
		Object[] MatlabRet;
		NetSatConfiguration.init();
		
		MatlabProxy proxy = null;
		MatlabInterface mi = new MatlabInterface(MatlabInterface.MATLAB_PATH, null);
		proxy = mi.getProxy();
		try {
			mi.eval("clear");
			mi.eval("clc");
		 	mi.eval("tic;");
            proxy.setVariable("mu", GravityFieldFactory.getNormalizedProvider(2, 0).getMu());
            proxy.setVariable("earth_radius", Constants.EGM96_EARTH_EQUATORIAL_RADIUS);
            mi.eval("InitialSettings");
            //OrbitChange = mi.returningEval("getVariables(D_a, D_e, D_in, D_omega, D_raan, D_mean_anomaly);", 1);
            //System.out.println("D_a = " + ((double[])OrbitChange[0])[0]);
            //while (notDoneYet) {
            KeplerianOrbit orbit = loadScripts.createKeplerOrbit(mi);
            //System.out.println("Semi-major axis (orbit) = " + orbit.getA());
            NumericalPropagator numProp = setupSimulation.setupNumPropSingleSat(mi, orbit, 1);
            //System.out.println("OrbitType (numProp) = " + numProp.getOrbitType().toString());
            
            SpacecraftState initialState = new SpacecraftState(orbit, new Attitude(FramesFactory.getEME2000(),
    						new TimeStampedAngularCoordinates(orbit.getDate(), 
    								new PVCoordinates(new Vector3D(10,10), new Vector3D(1, 2)), 
    								new PVCoordinates(new Vector3D(15,3),  new Vector3D(1, 2)))),1.0);
            
            
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
            numProp.addAdditionalEquations(ThrustEq);
            numProp.setMasterMode(setupSimulation.getOutputStepSize(), new matlabPushFinalHandler(mi));
            numProp.setInitialState(initialState);
            SpacecraftState finalState = numProp.propagate(orbit.getDate().shiftedBy(setupSimulation.getduration()));
            mi.eval("controlTest");
            MatlabRet = mi.returningEval("getVariables(notDone)", 1);
            notDoneYet = ((boolean[]) MatlabRet[0])[0];
			//}            
            // get keplerian elements for the satellite from matlab and create satellite object 
			//KeplerianOrbit uwe_3 = loadScripts.getKeplerOrbit(mi, 3);
			proxy.disconnect();
            
		} catch (OrekitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	public static NumericalPropagator setupNumPropSingleSat(){
		
		return null;
	}
	static class matlabPushFinalHandler  implements OrekitFixedStepHandler {
		MatlabInterface mi;
		
		
		List<String> Time = new ArrayList<String>();
		
		List<Double> Position_x = new ArrayList<Double>();
		List<Double> Position_y = new ArrayList<Double>();
		List<Double> Position_z = new ArrayList<Double>();
		List<Double> Velocity_x = new ArrayList<Double>();
		List<Double> Velocity_y = new ArrayList<Double>();
		List<Double> Velocity_z = new ArrayList<Double>();
		List<Double> a = new ArrayList<Double>();
		List<Double> e = new ArrayList<Double>();
		List<Double> in = new ArrayList<Double>();
		List<Double> omega = new ArrayList<Double>();
		List<Double> raan = new ArrayList<Double>();
		List<Double> M_a = new ArrayList<Double>();
		List<Double> T_a = new ArrayList<Double>();


		List<Double> Power_Consumption = new ArrayList<Double>();
		List<Double> Mass = new ArrayList<Double>();
		
		public matlabPushFinalHandler(MatlabInterface mi) {
			this.mi = mi;
		}
		
		@Override
		public void init(SpacecraftState s0, AbsoluteDate t)
				throws PropagationException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void handleStep(SpacecraftState currentState, boolean isLast)
				throws PropagationException {
			// TODO Auto-generated method stub
			if (!isLast){
				try {
					appendState(currentState);
					/*
					if(fire){
					System.out.println("Power consumption [mW]: " + myNanoFEEP1.getPowerConsumption_mW(thrust)*num_thrusters);
					} else System.out.println("Power consumption [mW]: 0");
					*/
				}catch(Exception e){
					e.printStackTrace();
					System.err.println(e.getMessage());
				}
			}
			else {
				matlabPushAllData();
			}
		}
		
		void appendState(SpacecraftState currentState) throws OrekitException{
			
			TimeStampedPVCoordinates scCoordinates = currentState.getPVCoordinates(FramesFactory.getITRF(IERSConventions.IERS_2010, true));
			
			Position_x.add(scCoordinates.getPosition().getX());
			Position_y.add(scCoordinates.getPosition().getY());
			Position_z.add(scCoordinates.getPosition().getZ());
			Velocity_x.add(scCoordinates.getVelocity().getX());
			Velocity_y.add(scCoordinates.getVelocity().getY());
			Velocity_z.add(scCoordinates.getVelocity().getZ());
			a.add(currentState.getA());
			e.add(currentState.getE());
			in.add(currentState.getI());
			omega.add(((KeplerianOrbit) OrbitType.KEPLERIAN.convertType(currentState.getOrbit())).getPerigeeArgument());
			raan.add(((KeplerianOrbit) OrbitType.KEPLERIAN.convertType(currentState.getOrbit())).getRightAscensionOfAscendingNode());
			M_a.add(((KeplerianOrbit) OrbitType.KEPLERIAN.convertType(currentState.getOrbit())).getMeanAnomaly());
			T_a.add(((KeplerianOrbit) OrbitType.KEPLERIAN.convertType(currentState.getOrbit())).getTrueAnomaly());
			Mass.add(currentState.getMass());
			if(fire){
			Power_Consumption.add(myNanoFEEP1.getPowerConsumption_mW(thrust)*num_thrusters);
			} else Power_Consumption.add((double) 0);
			Time.add(scCoordinates.getDate().toString());
			
		}
		
		void matlabPushAllData() {
			try {
				
				mi.getProxy().setVariable("Position_x", Tools.ListToDoubleArray(Position_x));
				mi.getProxy().setVariable("Position_y", Tools.ListToDoubleArray(Position_y));
				mi.getProxy().setVariable("Position_z", Tools.ListToDoubleArray(Position_z));
				mi.getProxy().setVariable("Velocity_x", Tools.ListToDoubleArray(Velocity_x));
				mi.getProxy().setVariable("Velocity_y", Tools.ListToDoubleArray(Velocity_y));
				mi.getProxy().setVariable("Velocity_z", Tools.ListToDoubleArray(Velocity_z));
				mi.getProxy().setVariable("a", Tools.ListToDoubleArray(a));
				mi.getProxy().setVariable("e", Tools.ListToDoubleArray(e));
				mi.getProxy().setVariable("in", Tools.ListToDoubleArray(in));
				mi.getProxy().setVariable("omega", Tools.ListToDoubleArray(omega));
				mi.getProxy().setVariable("raan", Tools.ListToDoubleArray(raan));
				mi.getProxy().setVariable("M_a", Tools.ListToDoubleArray(M_a));
				mi.getProxy().setVariable("T_a", Tools.ListToDoubleArray(T_a));
				mi.getProxy().setVariable("Mass", Tools.ListToDoubleArray(Mass));
				mi.getProxy().setVariable("Power_Consumption", Tools.ListToDoubleArray(Power_Consumption));


				mi.getProxy().setVariable("Time", Tools.ListToStringArray(Time));
				
			} catch (MatlabInvocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
