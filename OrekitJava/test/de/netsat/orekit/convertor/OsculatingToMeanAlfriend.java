package de.netsat.orekit.convertor;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
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
 * @license MIT
 */
public class OsculatingToMeanAlfriend {

	private final double sma;
	private final double inc;
	private final double ecc;
	private final double raan;
	private final double aop;
	private final double tano;
	private final boolean shortPeriod;
	private final boolean meanToOsc;
	private final double gammaTwo;
	private final double eta;
	private final double aR;
	private final double mano;
	private double gammaTwoPrim;
	private double ePrimCosM;
	private double ePrimSinM;
	private double omegaISin;
	private double omegaICos;
	private boolean longPeriod;

	/**
	 * The constructor class Equations 1 & 2
	 * 
	 * @tag checked
	 * @param initSemiMajorAxis
	 * @param initInclination
	 * @param initEccentrictiy
	 * @param initRAAN
	 * @param initArgumentOfPerigee
	 * @param initTrueAnomlay
	 * @param initMeanAnomaly
	 * @param shortPeriod
	 * @param longPeriod
	 * @param meanToOsc
	 */
	public OsculatingToMeanAlfriend(final double initSemiMajorAxis, final double initInclination, final double initEccentrictiy,
			final double initRAAN, final double initArgumentOfPerigee, final double initTrueAnomlay,
			final double initMeanAnomaly, final boolean shortPeriod, final boolean longPeriod,
			final boolean meanToOsc) {
		this.sma = initSemiMajorAxis;
		this.inc = initInclination;
		this.ecc = initEccentrictiy;
		this.raan = initRAAN;
		this.aop = initArgumentOfPerigee;
		this.tano = initTrueAnomlay;
		this.mano = initMeanAnomaly;
		this.shortPeriod = shortPeriod;
		this.longPeriod = longPeriod;
		this.meanToOsc = meanToOsc;
		/* Placeholder for the parameters to be set in next steps */
		this.ePrimCosM = 0;
		this.ePrimSinM = 0;
		this.omegaICos = 0;
		this.omegaISin = 0;
		/* Equation 1 */
		if (!meanToOsc) {
			this.gammaTwo = -1 * Constants.EGM96_EARTH_C20 / 2.0
					* FastMath.pow((Constants.EGM96_EARTH_EQUATORIAL_RADIUS / this.sma), 2);
		} else {
			this.gammaTwo = (Constants.EGM96_EARTH_C20 / 2.0)
					* FastMath.pow((Constants.EGM96_EARTH_EQUATORIAL_RADIUS / this.sma), 2);
		}
		/* Equation 2 */
		this.eta = FastMath.sqrt(1 - FastMath.pow(this.ecc, 2));
		this.aR = (1 + this.ecc * FastMath.cos(this.tano)) / FastMath.pow(this.eta, 2);
		this.gammaTwoPrim = this.gammaTwo / FastMath.pow(this.eta, 4);
	}

	/**
	 * Returns the eta
	 * 
	 * @tag checked
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
		return sma;
	}

	/**
	 * Returns the initial inclination
	 * 
	 * @return the initInclination
	 */
	public double getInitInclination() {
		return inc;
	}

	/**
	 * Returns the initial eccentricity
	 * 
	 * @return the initEccentricity
	 */
	public double getInitEccentricity() {
		return ecc;
	}

	/**
	 * Returns the initial right ascension of ascending node longPeriod
	 * 
	 * @return the iniRAAN
	 */
	public double getInitRAAN() {
		return raan;
	}

	/**
	 * Returns the initial argument of perigee
	 * 
	 * @return the initArgumentOfPerigee
	 */
	public double getInitArgumentOfPerigee() {
		return aop;
	}

	/**
	 * Returns the initial true anomaly
	 * 
	 * @return the initTrueAnomaly
	 */
	public double getInitTrueAnomaly() {
		return tano;
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
	 * Calculates the new semiMajorAxis from the given initial values. Equation
	 * 3
	 * 
	 * @tag checked
	 * @return semiMajorAxis
	 */
	public double calculateSemiMajorAxis() {

		double newSma = this.sma * (1 + this.gammaTwo * ((3 * FastMath.pow(FastMath.cos(this.inc), 2) - 1)
				* (FastMath.pow(this.aR, 3) - FastMath.pow(this.eta, -3))
				+ 3 * (1 - FastMath.pow(FastMath.cos(this.inc), 2)) * FastMath.pow(this.aR, 3)
						* FastMath.cos(2 * this.aop + 2 * this.tano)));
		return newSma;
	}

	/**
	 * Calculate the short period corrections to the eccentricity. Equation 5 in
	 * the paper.
	 * 
	 * @tag checked
	 * @return {@link Double}
	 */
	public double calculateEccentrcityShortPeriod() {
		double eSP1 = ((3 * FastMath.pow(FastMath.cos(this.inc), 2) - 1) / (FastMath.pow(this.eta, 6)))
				* ((this.ecc * this.eta) + (this.ecc / (1 + this.eta)) + 3 * FastMath.cos(this.tano)
						+ 3 * this.ecc * FastMath.pow(FastMath.cos(this.tano), 2)
						+ FastMath.pow(this.ecc, 2) * FastMath.pow(FastMath.cos(this.tano), 3));

		double eSP2 = 3 * (1 - FastMath.pow(FastMath.cos(this.inc), 2) / FastMath.pow(this.eta, 6))
				* (this.ecc + 3 * FastMath.cos(this.tano) + 3 * this.ecc * FastMath.pow(FastMath.cos(this.tano), 2)
						+ FastMath.pow(this.ecc, 2) * FastMath.pow(FastMath.cos(this.tano), 3))
				* FastMath.cos(2 * this.aop + 2 * this.tano);

		double eSP3 = (1 - FastMath.pow(FastMath.cos(this.inc), 2))
				* (3 * FastMath.cos(2 * this.aop + this.tano) + FastMath.cos(this.aop + 3 * this.tano));

		double eSP = (FastMath.pow(this.eta, 2) / 2)
				* (this.gammaTwo * eSP1 + this.gammaTwo * eSP2 - this.gammaTwoPrim * eSP3);
		return eSP;
	}

	/**
	 * Calculate the long term correction to the eccentricity. Equation 6
	 * 
	 * @return
	 */
	public double calculateEccentricityLongPeriod() {
		double eLP = ((this.gammaTwoPrim * this.ecc * FastMath.pow(this.eta, 2) * FastMath.cos(2 * this.aop)) / (8))
				* (1 - 11 * FastMath.pow(FastMath.cos(this.inc), 2) - 40 * (FastMath.pow(FastMath.cos(this.inc), 4))
						/ (1 - 5 * FastMath.pow(FastMath.cos(this.inc), 2)));
		return eLP;
	}

	/**
	 * Calculate the long term correction to inclination Equation 6
	 * 
	 * @return
	 */
	public double calculateInclinationLongPerid() {
		double iLP = (-(this.ecc) / (FastMath.pow(this.eta, 2) * FastMath.tan(this.inc)))
				* this.calculateEccentricityLongPeriod();
		return iLP;
	}

	/**
	 * Calculate the long term corrections to the Mean Anomaly, Equation 9
	 * 
	 * @return
	 */
	public double calculateMeanAnomalyLongPeriod() {
		double mLP = (this.gammaTwoPrim * FastMath.pow(this.eta, 3))
				/ (8) * (1 - 11 * FastMath.pow(FastMath.cos(this.inc), 2) - 40
						* (FastMath.pow(FastMath.cos(this.inc), 4)) / (1 - 5 * FastMath.pow(FastMath.cos(this.inc), 2)))
				* FastMath.sin(2 * this.aop);
		return mLP;
	}

	/**
	 * Calculate the short period correction to inclination Equation 7
	 * 
	 * @return
	 */
	public double calculateInclinationShortPeriod() {
		double inclinationSP = 0.5 * this.gammaTwoPrim * FastMath.cos(this.inc)
				* (3 * FastMath.cos(2 * this.aop + 2 * this.tano)
						+ 3 * this.ecc * FastMath.cos(2 * this.aop + this.tano)
						+ this.ecc * FastMath.cos(2 * this.aop + 3 * this.tano));
		return inclinationSP;
	}

	/**
	 * Calculates the short period corrections the mean anomaly. Equation 9 in
	 * the Paper.
	 * 
	 * @tag checked
	 * @return
	 */
	public double calculateMeanAnomalyShortPeriod() {
		double mSP = (-1 * (this.gammaTwoPrim * FastMath.pow(this.eta, 3)) / (4 * this.ecc))
				* (2 * (3 * FastMath.pow(FastMath.cos(this.inc), 2) - 1)
						* (FastMath.pow(this.aR * this.eta, 2) + this.aR + 1) * FastMath.sin(this.tano)
						+ 3 * (1 - FastMath.pow(FastMath.cos(this.inc), 2))
								* ((-1 * FastMath.pow(this.aR * this.eta, 2) - this.aR + 1)
										* FastMath.sin(2 * this.aop + this.tano) + (FastMath.pow(this.aR * this.eta, 2)
												+ this.aR + 1 / 3) * FastMath.sin(2 * this.aop + 3 * this.tano)));
		return mSP;
	}

	/**
	 * Calculate the relation between the eccentricity and the mean anomaly
	 * Equation 9
	 * 
	 * @tag checked
	 */
	public void calculateEM() {
		this.ePrimCosM = (this.ecc + this.calculateEccentrcityShortPeriod() + this.calculateEccentricityLongPeriod())
				* FastMath.cos(this.mano)
				- this.ecc * (this.calculateMeanAnomalyShortPeriod() + this.calculateMeanAnomalyLongPeriod())
						* FastMath.sin(this.mano);
		this.ePrimSinM = (this.ecc + this.calculateEccentrcityShortPeriod() + this.calculateEccentricityLongPeriod())
				* FastMath.sin(this.mano)
				- this.ecc * (this.calculateMeanAnomalyShortPeriod() + this.calculateMeanAnomalyLongPeriod())
						* FastMath.cos(this.mano);
	}

	/**
	 * Calculates new eccentricity. Equation 10
	 * 
	 * @tag checked
	 * @return
	 */
	public double calculateEccentricity() {
		if (this.ePrimCosM == 0 || this.ePrimSinM == 0) {
			this.calculateEM();
		}
		double e = FastMath.sqrt(FastMath.pow(this.ePrimCosM, 2) + FastMath.pow(this.ePrimSinM, 2));
		return e;
	}

	/**
	 * Calculates the new mean anomaly. Equation 10
	 * 
	 * @tag checked
	 * @return
	 */
	public double calculateMeanAnomaly() {
		if (this.ePrimCosM == 0 || this.ePrimSinM == 0) {
			this.calculateEM();
		}
		double mA = FastMath.atan2(this.ePrimSinM, this.ePrimCosM);
		return mA;
	}

	/**
	 * Calculate the short period corrections to the RAAN. Equation 12
	 * 
	 * @tag checked
	 * @return
	 */
	public double calculateRAANShortPeriod() {
		double rAANSP = -1 * ((this.gammaTwoPrim * FastMath.cos(this.inc)) / (2))
				* (6 * (this.tano - this.mano + this.ecc * FastMath.sin(this.tano))
						- 3 * FastMath.sin(2 * this.aop + 2 * this.tano)
						- 3 * this.ecc * FastMath.sin(2 * this.aop + this.tano)
						- this.ecc * FastMath.sin(2 * this.aop + 3 * this.tano));
		return rAANSP;
	}

	/**
	 * Calculate the intermediate values for the relation between RAAN and
	 * Inclination Equation 13
	 * 
	 * @tag checked
	 */
	public void calculateOmegaI() {
		this.omegaISin = (FastMath.sin(this.inc / 2)
				+ 0.5 * FastMath.cos(this.inc / 2) * this.calculateInclinationShortPeriod()) * FastMath.sin(this.raan)
				+ FastMath.sin(this.inc / 2) * this.calculateRAANShortPeriod() * FastMath.cos(this.raan);

		this.omegaICos = (FastMath.sin(this.inc / 2)
				+ 0.5 * FastMath.cos(this.inc / 2) * this.calculateInclinationShortPeriod()) * FastMath.cos(this.raan)
				- FastMath.sin(this.inc / 2) * this.calculateRAANShortPeriod() * FastMath.sin(this.raan);

	}

	/**
	 * Calculate the new inclination Equation 14
	 * 
	 * @tag checked
	 * @return
	 */
	public double calculateInclination() {
		if (this.omegaISin == 0 || this.omegaICos == 0) {
			this.calculateOmegaI();
		}
		double newInclination = 2
				* FastMath.asin(FastMath.sqrt(FastMath.pow(this.omegaISin, 2) + FastMath.pow(this.omegaICos, 2)));
		return newInclination;
	}

	/**
	 * Calculate the new RAAN Equation 14
	 * 
	 * @tag checked
	 * @return
	 */
	public double calculateRAAN() {
		if (this.omegaISin == 0 || this.omegaICos == 0) {
			this.calculateOmegaI();
		}
		double newRAAN = FastMath.atan2(this.omegaISin, this.omegaICos);
		return newRAAN;
	}

	/**
	 * Calculate the short period corrections to the argument of perigee
	 * Equation 17 & 18 & 19
	 * 
	 * @tag checked
	 * @return
	 */
	public double calculateArguemntOfPerigeeShortPeriod() {
		double omegaSP1 = (this.gammaTwoPrim * FastMath.pow(this.eta, 2)) / (4 * this.ecc)
				* (2 * (3 * FastMath.pow(FastMath.cos(this.inc), 2) - 1)
						* (FastMath.pow(this.aR * this.eta, 2) + this.aR + 1) * FastMath.sin(this.tano)
						+ 3 * (1 - FastMath.pow(FastMath.cos(this.inc), 2))
								* ((-1 * FastMath.pow(this.aR * this.eta, 2) - this.aR + 1)
										* FastMath.sin(2 * this.aop + this.tano)
										+ (FastMath.pow(this.aR * this.eta, 2) + this.aR + 1 / 3)
												* FastMath.sin(2 * this.aop + 3 * this.tano)));

		double omegaSP2 = (this.gammaTwoPrim / 4) * (6 * (5 * FastMath.pow(FastMath.cos(this.inc), 2) - 1)
				* (this.tano - this.mano + this.ecc + FastMath.sin(this.tano))
				+ (3 - 5 * FastMath.pow(FastMath.cos(this.inc), 2)) * (3 * FastMath.sin(2 * this.aop + 2 * this.tano))
				+ (3 * this.ecc + FastMath.sin(2 * this.aop + this.tano)
						+ this.ecc * FastMath.sin(2 * this.aop + 3 * this.tano)));

		return omegaSP1 + omegaSP2;
	}

	/**
	 * Calculates the new Argument of Perigee Equation 20 to 22
	 * 
	 * @tag checked
	 * @return
	 */
	public double calculateArguemntOfPerigee() {
		double transformedAnomalies = this.mano + this.calculateMeanAnomalyShortPeriod() + this.aop
				+ this.calculateArguemntOfPerigeeShortPeriod() + this.raan + this.calculateRAANShortPeriod();
		double inBetween = transformedAnomalies - this.calculateMeanAnomaly() - calculateRAAN();
		return inBetween;

	}

	/**
	 * Calculates all the orbital element needed. sma, ecc, inc, aop, raa, man
	 * => 0,1,2,3,4,5
	 * 
	 * @return
	 */
	public double[] caculateAll() {
		double[] result = new double[6];
		result[0] = calculateSemiMajorAxis();
		result[1] = calculateEccentricity();
		result[2] = calculateInclination();
		result[3] = MathUtils.normalizeAngle(calculateArguemntOfPerigee(), FastMath.PI);
		result[4] = calculateRAAN();
		result[5] = calculateMeanAnomaly();
		return result;
	}

}
