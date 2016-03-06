package de.netsat.orekit.matlab;

import org.orekit.errors.OrekitException;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.PositionAngle;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.ApsideDetector;
import org.orekit.propagation.events.EclipseDetector;
import org.orekit.propagation.events.EventDetector;
import org.orekit.propagation.events.handlers.EventHandler;
import org.orekit.time.AbsoluteDate;

import de.netsat.orekit.matlab.ConstantValues;
import de.netsat.orekit.matlab.eventhandler.ApsideDetectionHandler;
import de.netsat.orekit.matlab.eventhandler.LatitudeArgumentDetectionHandler;

import org.orekit.utils.Constants;

public class EventCalculator {
	ConstantValues constants;
	EventDetector eclipse;
	ApsideDetector apogee;
	ApsideDetector perigee;
	EventDetector positionAngle;
	private KeplerianOrbit startOrbit;
	private AbsoluteDate startDate;
	private SpacecraftState startState;
	private LatitudeArgumentDetector latArgZero;
	private LatitudeArgumentDetector latArgNinety;

	public EventCalculator(SpacecraftState state, AbsoluteDate startDate, KeplerianOrbit startOrbit,
			final ConstantValues constants) throws OrekitException {
		this.startOrbit = startOrbit;
		this.startDate = startDate;
		this.startState = state;
		this.constants = constants;
		this.setEclipseEvenetDetector();
		this.setApogeeEventDetector();
		this.setNetSatLatitudeArgumentDetectorZero();
		this.setNetSatLatitudeArgumentDetectorNinety();
	}

	public EventCalculator() throws OrekitException {
		this.constants = new ConstantValues();
		this.setEclipseEvenetDetector();
	}

	/**
	 * Sets the eclipse event detector
	 */
	public void setEclipseEvenetDetector() {
		EclipseDetector nightDayEvent = new EclipseDetector(this.constants.getSun(), 0.0, this.constants.getEarth(),
				Constants.WGS84_EARTH_EQUATORIAL_RADIUS).withHandler(new EventHandler<EclipseDetector>() {

					@Override
					public org.orekit.propagation.events.handlers.EventHandler.Action eventOccurred(SpacecraftState s,
							EclipseDetector detector, boolean increasing) throws OrekitException {
						// TODO Auto-generated method stub
						if (increasing) {
							System.out.println("I out eclipse now!");
						} else {
							System.out.println("I am in eclipse now!");
						}
						return Action.CONTINUE;
					}

					@Override
					public SpacecraftState resetState(EclipseDetector detector, SpacecraftState oldState)
							throws OrekitException {
						// TODO Auto-generated method stub
						return oldState;
					}

				});
		this.eclipse = nightDayEvent;
	}

	/**
	 * Sets the positionAngle event for the eventCalculator Use the increasing
	 * only
	 * 
	 * @return {@link LatitudeArgumentDetector}
	 */
	public LatitudeArgumentDetector createNetSatLatitudeArgumentDetectorEvent(final double angle) {
		LatitudeArgumentDetectionHandler latArgDetHandler = new LatitudeArgumentDetectionHandler(angle);
		LatitudeArgumentDetector posAngDet = new LatitudeArgumentDetector(this.startOrbit.getType(), PositionAngle.TRUE,
				angle).withHandler(latArgDetHandler);
		return posAngDet;
	}

	/**
	 * Sets the Event of Latitude Argument detector equal Zero.
	 */
	public void setNetSatLatitudeArgumentDetectorZero() {
		this.latArgZero = this.createNetSatLatitudeArgumentDetectorEvent(0);
	}

	/**
	 * Sets the Event of Latitude Argument detector equal to Ninety degrees.
	 */
	public void setNetSatLatitudeArgumentDetectorNinety() {
		this.latArgNinety = this.createNetSatLatitudeArgumentDetectorEvent(Math.PI / 2);
	}

	/**
	 * Sets Apogee event detector for the Orbit.
	 */
	public void setApogeeEventDetector() {
		ApsideDetectionHandler apsideDetectionHandler = new ApsideDetectionHandler();
		ApsideDetector apogee = new ApsideDetector(1.e-6, this.startOrbit).withHandler(apsideDetectionHandler);
		apogee.init(this.startState, this.startDate);
		this.apogee = apogee;
	}

	/**
	 * Returns the eclipse event detector which can be added to the propgator.
	 * 
	 * @return {@link EventDetector} eclipse
	 */
	public EventDetector getEclipseEventDetecor() {
		return this.eclipse;
	}

	/**
	 * Returns the apogee detector.
	 */
	public ApsideDetector getApogeeEventDetector() {
		return this.apogee;
	}

	/**
	 * Returns the Latitude Arguments Events of the Propagation.
	 * 
	 * @param angle
	 *            (in Degrees !!!)
	 * @return
	 */
	public LatitudeArgumentDetector getLatArg(double angle) {
		if (angle == 0) {
			return this.latArgZero;
		} else {
			return this.latArgNinety;
		}

	}
}
