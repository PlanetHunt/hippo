package de.netsat.orekit.convertor;

import org.apache.commons.math3.util.FastMath;
import org.orekit.utils.Constants;

/**
 * Osculating <--> Mean. This class implements the Appendix 2 of the
 * 10.1007/s10569-013-9529-0 "Analytic characterization of measurement
 * uncertainty and initial orbit determination on orbital element
 * representations" by R. M. Weisman · M. Majji · K. T. Alfriend. It contains
 * both long and short period corrections for different orbital elements. This
 * is also the same method used in STK for Osculating to mean transformation.
 * 
 * @author Pouyan Azari
 *
 */
public class Corrections {

	private final double initSemiMajorAxis;
	private final double initInclination;
	private final double initEccentricity;
	private final double iniRAAN;
	private final double initArgumentOfPerigee;
	private final double initTrueAnomaly;
	private final boolean shortPeriod;
	private final boolean meanToOsc;
	private final double gammaTwo;
	private final double eta;
	private final double aR;

	public Corrections(final double initSemiMajorAxis, final double initInclination, final double initEccentrictiy,
			final double initRAAN, final double initArgumentOfPerigee, final double initTrueAnomlay,
			final boolean shortPeriod, final boolean meanToOsc) {
		this.initSemiMajorAxis = initSemiMajorAxis;
		this.initInclination = initInclination;
		this.initEccentricity = initEccentrictiy;
		this.iniRAAN = initRAAN;
		this.initArgumentOfPerigee = initArgumentOfPerigee;
		this.initTrueAnomaly = initTrueAnomlay;
		this.shortPeriod = shortPeriod;
		this.meanToOsc = meanToOsc;
		/* Equation one and Two */
		if (!meanToOsc) {
			this.gammaTwo = -1 * Constants.EGM96_EARTH_C20 / 2.0
					* FastMath.pow((Constants.EGM96_EARTH_EQUATORIAL_RADIUS / initSemiMajorAxis), 2);
		} else {
			this.gammaTwo = (Constants.EGM96_EARTH_C20 / 2.0)
					* FastMath.pow((Constants.EGM96_EARTH_EQUATORIAL_RADIUS / initSemiMajorAxis), 2);
		}
		this.eta = FastMath.sqrt(1 - FastMath.pow(initEccentrictiy, 2));
		this.aR = (1 + initEccentrictiy * FastMath.cos(initTrueAnomlay)) / FastMath.pow(this.eta, 2);
	}

	/**
	 * Returns the eta
	 * 
	 * @return
	 */
	public double getEta() {
		return this.eta;
	}

	/**
	 * Returns the Gamma two the Brouwers variable.
	 * 
	 * @return the gammaTwo
	 */
	public double getGammaTwo() {
		return gammaTwo;
	}

	/**
	 * If the corrections are for mean to Osc.
	 * 
	 * @return the meanToOsc
	 */
	public boolean isMeanToOsc() {
		return meanToOsc;
	}

	/**
	 * Returns the initial semi Major Axis
	 * 
	 * @return the initSemiMajorAxis
	 */
	public double getInitSemiMajorAxis() {
		return initSemiMajorAxis;
	}

	/**
	 * Returns the initial inclination
	 * 
	 * @return the initInclination
	 */
	public double getInitInclination() {
		return initInclination;
	}

	/**
	 * Returns the initial eccentricity
	 * 
	 * @return the initEccentricity
	 */
	public double getInitEccentricity() {
		return initEccentricity;
	}

	/**
	 * Returns the initial right ascension of ascending node
	 * 
	 * @return the iniRAAN
	 */
	public double getIniRAAN() {
		return iniRAAN;
	}

	/**
	 * Returns the initial argument of perigee
	 * 
	 * @return the initArgumentOfPerigee
	 */
	public double getInitArgumentOfPerigee() {
		return initArgumentOfPerigee;
	}

	/**
	 * Returns the initial true anomaly
	 * 
	 * @return the initTrueAnomaly
	 */
	public double getInitTrueAnomaly() {
		return initTrueAnomaly;
	}

	/**
	 * If returns the short period corrections.
	 * 
	 * @return the shortPeriod
	 */
	public boolean isShortPeriod() {
		return shortPeriod;
	}

	/**
	 * Calculates the semiMajorAxis correction from the given initial values.
	 * Automatically takes care of short or long period corrections. Equation 3
	 * from the paper.
	 * 
	 * @return {@link Double}
	 */
	public double calculateSemiMajorAxisCorrection() {

		return this.initSemiMajorAxis
				* (1 + this.getGammaTwo() * ((3 * FastMath.pow(FastMath.cos(this.initInclination), 2) - 1)
						* (FastMath.pow(this.aR, 3) - FastMath.pow(this.eta, -3))
						+ 3 * (1 - FastMath.pow(FastMath.cos(this.initInclination), 2)) * FastMath.pow(this.aR, 3)
								* FastMath.cos(2 * this.initArgumentOfPerigee + 2 * this.initTrueAnomaly)));
	}

}
