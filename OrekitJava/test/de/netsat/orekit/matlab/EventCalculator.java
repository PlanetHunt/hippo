package de.netsat.orekit.matlab;

import org.orekit.errors.OrekitException;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.EclipseDetector;
import org.orekit.propagation.events.EventDetector;
import org.orekit.propagation.events.handlers.EventHandler;

import de.netsat.orekit.matlab.ConstantValues;
import org.orekit.utils.Constants;

public class EventCalculator {
	ConstantValues constants;
	EventDetector eclipse;

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
						System.out.println("I am in eclipse now!");
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
	 * Returns the eclipse event detector which can be added to the propgator.
	 * 
	 * @return {@link EventDetector} eclipse
	 */
	public EventDetector getEclipseEventDetecor() {
		return this.eclipse;
	}
}
