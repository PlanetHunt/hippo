package de.netsat.orekit.matlab;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import org.orekit.errors.OrekitException;
import org.orekit.errors.OrekitMessages;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.OrbitType;
import org.orekit.orbits.PositionAngle;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.AbstractDetector;
import org.orekit.propagation.events.handlers.EventHandler;
import org.orekit.propagation.events.handlers.StopOnIncreasing;

/**
 * Finder for the PositionAngle crossing through out the Orbit. * The detector
 * is based on anomaly for {@link OrbitType#KEPLERIAN Keplerian} orbits,
 * latitude argument for {@link OrbitType#CIRCULAR circular} orbits, or
 * longitude argument for {@link OrbitType#EQUINOCTIAL equinoctial} orbits. It
 * does not support {@link OrbitType#CARTESIAN Cartesian} orbits. The angles can
 * be either {@link PositionAngle#TRUE true}, {link {@link PositionAngle#MEAN
 * mean} or {@link PositionAngle#ECCENTRIC eccentric} angles.
 * 
 * @author Luc Maisonobe
 * @author Pouyan Azari
 * @TODO This is released in a new Orekit version 7.1, this code is a rewrite to
 *       make it work with the 7.0
 * @see https://www.orekit.org/forge/projects/orekit/repository/revisions/master
 *      /entry/src/main/java/org/orekit/propagation/events/PositionAngleDetector
 *      .java
 */
public class LatitudeArgumentDetector extends AbstractDetector<LatitudeArgumentDetector> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* Final variables can only be set once. */
	/** Orbit type of the defining angle */
	private final OrbitType orbitType;

	/** Type of the position angle */
	private final PositionAngle positionAngle;

	/** Fixed angle to be crossed */
	private final double angle;

	/** Sign to apply for the angle difference */
	private double sign;

	/** Previous angle difference */
	private double previousDelta;

	/**
	 * Build a new detector. It uses the default values for maximal checking
	 * interval and also the convergence threshold.
	 * 
	 * @param orbitType
	 * @param positionAngle
	 * @param angle
	 */
	public LatitudeArgumentDetector(final OrbitType orbitType, final PositionAngle positionAngle,
			final double angle) {
		this(DEFAULT_MAXCHECK, DEFAULT_THRESHOLD, orbitType, positionAngle, angle);
	}

	/**
	 * A complete builder for a new position angle detector. It uses the private
	 * constructor.
	 * 
	 * @param maxCheck
	 * @param threshold
	 * @param orbitType
	 * @param positionAngle
	 * @param angle
	 */
	public LatitudeArgumentDetector(final double maxCheck, final double threshold, final OrbitType orbitType,
			final PositionAngle positionAngle, final double angle) {
		this(maxCheck, threshold, DEFAULT_MAX_ITER, new StopOnIncreasing<LatitudeArgumentDetector>(), orbitType,
				positionAngle, angle);
	}

	/**
	 * The complete private Builder, every builder of this class inherit from
	 * this builder.
	 * 
	 * @param maxCheck
	 * @param threshold
	 * @param maxIter
	 * @param handler
	 * @param orbitType
	 * @param positionAngle
	 * @param angle
	 */
	private LatitudeArgumentDetector(final double maxCheck, final double threshold, final int maxIter,
			final EventHandler<LatitudeArgumentDetector> handler, final OrbitType orbitType,
			final PositionAngle positionAngle, final double angle) {
		super(maxCheck, threshold, maxIter, handler);
		this.orbitType = orbitType;
		this.positionAngle = positionAngle;
		this.angle = angle;
		this.sign = +1.0;
		this.previousDelta = Double.NaN;
	}

	@Override
	protected LatitudeArgumentDetector create(final double newMaxCheck, final double newThreshold,
			final int newMaxIter, final EventHandler<LatitudeArgumentDetector> newHandler) {
		return new LatitudeArgumentDetector(newMaxCheck, newThreshold, newMaxIter, newHandler, orbitType,
				positionAngle, angle);
	}

	@Override
	public double g(SpacecraftState s) throws OrekitException {
		final double currentAngle;
		switch (orbitType) {
		case KEPLERIAN:
			currentAngle = ((KeplerianOrbit) orbitType.convertType(s.getOrbit())).getAnomaly(positionAngle);
			break;
		default:
			System.out.println("This function only allows keplerian orbits!");
			throw new OrekitException(OrekitMessages.ORBIT_A_E_MISMATCH_WITH_CONIC_TYPE);
		}
		// TrueAnomaly = TrueLatitude - ArgOfPerigee
		// f = theta - omega
		double delta = MathUtils.normalizeAngle(sign
				* (currentAngle - angle + ((KeplerianOrbit) orbitType.convertType(s.getOrbit())).getPerigeeArgument()),
				0.0);
		if (FastMath.abs(delta - previousDelta) > FastMath.PI) {
			sign = -sign;

			delta = MathUtils
					.normalizeAngle(
							sign * (currentAngle - angle
									+ ((KeplerianOrbit) orbitType.convertType(s.getOrbit())).getPerigeeArgument()),
							0.0);
		}
		previousDelta = delta;

		return delta;

	}

	/**
	 * Returns the type of Orbit (Cartesian, Keplerian, circular or
	 * equinoctical)
	 * 
	 * @return {@link OrbitType}
	 */
	public OrbitType getOrbitType() {
		return this.orbitType;
	}

	/**
	 * Returns position angle type (Mean, true or ...)
	 * 
	 * @return {@link PositionAngle}
	 */
	public PositionAngle getPositionAngle() {
		return positionAngle;
	}

	/**
	 * Returns the angle that should be crossed.
	 * 
	 * @return {@link Double}
	 */
	public double getAngle() {
		return this.angle;
	}

}
