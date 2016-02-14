package de.netsat.orekit.matlab.eventhandler;

import org.orekit.errors.OrekitException;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.ApsideDetector;
import org.orekit.propagation.events.handlers.EventHandler;
import org.orekit.time.AbsoluteDate;

public class ApsideDetectionHandler implements EventHandler<ApsideDetector> {

	private AbsoluteDate apsideCrossingDate;
	private double radius;
	private boolean isApogee;

	@Override
	public SpacecraftState resetState(ApsideDetector detector, SpacecraftState oldState) throws OrekitException {
		return oldState;
	}

	@Override
	public Action eventOccurred(SpacecraftState s, ApsideDetector detector, boolean increasing) throws OrekitException {
		this.apsideCrossingDate = s.getDate();
		this.radius = s.getPVCoordinates().getPosition().getNorm();
		System.out.println(this.apsideCrossingDate + "\n");
		System.out.println(this.radius + "\n");
		if (increasing) {
			System.out.println("Increasing \n");
			this.isApogee = false;
			return Action.CONTINUE;

		} else {
			System.out.println("Decreasing \n");
			this.isApogee = true;
			return Action.CONTINUE;

		}
	}

	/**
	 * Sets the radius at the apside detection event.
	 * 
	 * @param radius
	 */
	public void setRadius(double radius) {
		this.radius = radius;
	}

	/**
	 * Returns the radius of the apside detection event.
	 * 
	 * @return
	 */
	public double getRadius() {
		return this.radius;
	}

	/**
	 * Sets the toggle for the apogee, if true the value is apogee false is
	 * perigee
	 * 
	 * @param isApogee
	 */
	public void setIsApogee(boolean isApogee) {
		this.isApogee = isApogee;
	}

	/**
	 * Returns the toggle for the apogee, if true the value is apogee false is
	 * perigee
	 * 
	 * @return {@link Boolean}
	 */
	public boolean getIsApogee() {
		return this.isApogee;
	}

	/**
	 * Sets the apside crossing detection date.
	 * 
	 * @param date
	 */
	public void setApsideCrossingDetectionDate(AbsoluteDate date) {
		this.apsideCrossingDate = date;
	}

	/**
	 * Returns the apside crossing detection date for the crossing.
	 * 
	 * @return {@link AbsoluteDate}
	 */
	public AbsoluteDate getApsideCrossingDetectionDate() {
		return this.apsideCrossingDate;
	}

}
