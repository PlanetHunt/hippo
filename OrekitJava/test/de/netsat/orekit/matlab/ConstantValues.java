package de.netsat.orekit.matlab;

import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.errors.OrekitException;
import org.orekit.forces.gravity.potential.GravityFieldFactory;
import org.orekit.forces.gravity.potential.NormalizedSphericalHarmonicsProvider;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.PVCoordinatesProvider;

public class ConstantValues {

	private final double mu;
	private final double earthRaduis;
	private final double earthFlattening;
	private final OneAxisEllipsoid oae;
	private final Frame eci;
	private final TimeScale timeScale;
	private final PVCoordinatesProvider sun;
	private final PVCoordinatesProvider earth;
	private final NormalizedSphericalHarmonicsProvider gravityProvider;

	public ConstantValues() throws OrekitException {
		this.earthRaduis = Constants.WGS84_EARTH_EQUATORIAL_RADIUS;
		this.earthFlattening = Constants.WGS84_EARTH_FLATTENING;
		this.gravityProvider = GravityFieldFactory.getNormalizedProvider(10, 10);
		this.mu = GravityFieldFactory.getNormalizedProvider(2, 0).getMu();
		this.eci = FramesFactory.getEME2000();
		this.oae = new OneAxisEllipsoid(this.earthRaduis, this.earthFlattening, this.eci);
		this.sun = CelestialBodyFactory.getSun();
		this.earth = CelestialBodyFactory.getEarth();
		this.timeScale = TimeScalesFactory.getUTC();
	}

	/**
	 * 
	 * @return mu.
	 */
	public final double getMu() {
		return this.mu;
	}

	/**
	 * Gets the One Axis Body Ellipsoid
	 * 
	 * @return {@link OneAxisEllipsoid}
	 */
	public final OneAxisEllipsoid getBodyEllipsoid() {
		return this.oae;
	}

	/**
	 * Get the frame Type
	 * 
	 * @return
	 */
	public final Frame getEci() {
		return this.eci;
	}

	/**
	 * Get the timescale set
	 * 
	 * @return {@link TimeScale}
	 */
	public final TimeScale getTimeScale() {
		return this.timeScale;
	}

	/**
	 * Returns the Earth
	 * 
	 * @return {@link PVCoordinatesProvider} earth
	 */
	public final PVCoordinatesProvider getEarth() {
		return this.earth;
	}

	/**
	 * Returns the sun
	 * 
	 * @return {@link PVCoordinatesProvider} sun
	 */
	public final PVCoordinatesProvider getSun() {
		return this.sun;
	}

	/**
	 * Returns the Earth radius from WGS84 Model
	 * 
	 * @return {@link Double}
	 */
	public final double getEarthRadius() {
		return this.earthRaduis;
	}

	/**
	 * Returns the Earth flattening from WGS84 Model
	 * 
	 * @return {@link Double}
	 */
	public final double getEarthFlattening() {
		return this.earthFlattening;
	}

	/**
	 * Returns the gravity Provider for the propagation.
	 * 
	 * @return {@link NormalizedSphericalHarmonicsProvider}
	 */
	public final NormalizedSphericalHarmonicsProvider getGravityProvider() {
		return this.gravityProvider;
	}

}
