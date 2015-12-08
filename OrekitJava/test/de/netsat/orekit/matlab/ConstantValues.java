package de.netsat.orekit.matlab;

import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.errors.OrekitException;
import org.orekit.forces.gravity.potential.GravityFieldFactory;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.time.TAIScale;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;
import org.orekit.utils.PVCoordinatesProvider;

public class ConstantValues {

	private double mu;
	private OneAxisEllipsoid oae;
	private Frame itrf;
	private TAIScale timescale;
	private PVCoordinatesProvider sun;
	private PVCoordinatesProvider earth;

	public ConstantValues() throws OrekitException {
		this.setMu();
		this.setITRF();
		this.setBodyEllipsoid();
		this.setTimeScale();
		this.setEarth();
		this.setSun();
	}

	/**
	 * Sets the mu in constants.
	 * 
	 * @throws OrekitException
	 */
	public void setMu() throws OrekitException {
		this.mu = GravityFieldFactory.getNormalizedProvider(2, 0).getMu();
	}

	/**
	 * 
	 * @return mu.
	 */
	public double getMu() {
		return this.mu;
	}

	/**
	 * Sets the One Axis Body Ellipsoid with WGS84
	 */
	public void setBodyEllipsoid() {
		try {
			this.oae = new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS, Constants.WGS84_EARTH_FLATTENING,
					this.getITRF());
		} catch (NullPointerException e) {
			System.out.println(e);
		}
	}

	/**
	 * Gets the One Axis Body Ellipsoid
	 * 
	 * @return {@link OneAxisEllipsoid}
	 */
	public OneAxisEllipsoid getBodyEllipsoid() {
		return this.oae;
	}

	/**
	 * Set the Frame Type.
	 * 
	 * @throws OrekitException
	 */
	public void setITRF() throws OrekitException {
		this.itrf = FramesFactory.getITRF(IERSConventions.IERS_2010, true);
	}

	/**
	 * Get the frame Type
	 * 
	 * @return
	 */
	public Frame getITRF() {
		return this.itrf;
	}

	/**
	 * Set the timescale.
	 * 
	 */
	public void setTimeScale() {
		this.timescale = TimeScalesFactory.getTAI();
	}

	/**
	 * Get the timescale set
	 * 
	 * @return {@link TimeScale}
	 */
	public TimeScale getTimeScale() {
		return this.timescale;
	}

	/**
	 * set the earth
	 * 
	 * @throws OrekitException
	 */
	public void setEarth() throws OrekitException {
		this.earth = CelestialBodyFactory.getEarth();
	}

	/**
	 * Returns the Earth
	 * 
	 * @return {@link PVCoordinatesProvider} earth
	 */
	public PVCoordinatesProvider getEarth() {
		return this.earth;
	}

	/**
	 * Set the sun
	 * 
	 * @throws OrekitException
	 */
	public void setSun() throws OrekitException {
		this.sun = CelestialBodyFactory.getSun();
	}

	/**
	 * Returns the sun
	 * 
	 * @return {@link PVCoordinatesProvider} sun
	 */
	public PVCoordinatesProvider getSun() {
		return this.sun;
	}

}
