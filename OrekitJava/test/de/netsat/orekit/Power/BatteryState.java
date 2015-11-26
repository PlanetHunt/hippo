package de.netsat.orekit.Power;


public class BatteryState {
	/*
	PVCoordinatesProvider earth = CelestialBodyFactory.getEarth();
	PVCoordinatesProvider sun = CelestialBodyFactory.getSun();
	final EclipseDetector toEclipseState = new EclipseDetector(sun, Constants.SUN_RADIUS, earth, Constants.WGS84_EARTH_EQUATORIAL_RADIUS).
			withHandler(new EventHandler<EclipseDetector>(){

				@Override
				public Action eventOccurred(SpacecraftState s, EclipseDetector detector,
						boolean increasing) throws OrekitException {
					// TODO Auto-generated method stub
					if(increasing) {
						eclipse = false;
						System.out.println(s.getDate() + " : event occurred, leaving eclipse => switching to day law");
					}
					else {
						eclipse = true;
						System.out.println(s.getDate() + " : event occurred, entering eclipse => switching to night law");
					}
					return Action.CONTINUE;
				}
	
				@Override
				public SpacecraftState resetState(EclipseDetector detector,
						SpacecraftState oldState) throws OrekitException {
					// TODO Auto-generated method stub
					return oldState;
				}
				
			}); 
	toEclipseState.init(initialState, keplerOrbit.getDate());
	numericPropagator.addEventDetector(toEclipseState);
	
	initialState = initialState.addAdditionalState("BatteryState", 0.80*61776000);
	AdditionalEquations batteryEquations = new AdditionalEquations() {
		@Override
		public String getName() {
			return "BatteryState";
		}
		
				
		@Override
		public double[] computeDerivatives(SpacecraftState s, double[] charging)
				throws OrekitException {
			
			// Reset derivative:
			charging[0] = 0;
			
			if (!eclipse && s.getAdditionalState("BatteryState")[0]<61776000){
            		System.out.println(s.getDate() + " : charging, BatteryState:" + s.getAdditionalState("BatteryState")[0]);
            	charging[0] += 1485.5; 	// charging rate per second?
            }else if(!eclipse){
            	charging[0] = 0;	// nothing
            	}
            if(eclipse && s.getAdditionalState("BatteryState")[0]>0) charging[0] -= 300;
			
			if(fire) charging[0] = charging[0]-myNanoFEEP1.getPowerConsumption_mW(thrust)*2;
			
			
			eclipse_func.add(s.getAdditionalState("BatteryState")[0]);
			Time_adaptive.add(s.getDate().toString());
			
			return null;
		}
	};
	numericPropagator.addAdditionalEquations(batteryEquations);
	*/
}
