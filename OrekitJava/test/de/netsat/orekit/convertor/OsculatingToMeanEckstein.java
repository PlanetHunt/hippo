package de.netsat.orekit.convertor;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;
import org.orekit.errors.OrekitException;
import org.orekit.forces.gravity.potential.GravityFieldFactory;
import org.orekit.orbits.CircularOrbit;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.orbits.OrbitType;
import org.orekit.orbits.PositionAngle;
import org.orekit.propagation.SpacecraftState;
import org.orekit.utils.Constants;

/**
 * This is another implementation for the Osculating to mean algorithm. It uses
 * the Spirindonova paper with the help of Jan Hogland Master thesis.
 * 
 * @author Pouyan Azari
 *
 */
public class OsculatingToMeanEckstein {

	private double beta;
	private double g2;
	private double h;
	private double inc;
	private double l;
	private double lambda;
	private double lambdaPrim;
	private double raan;
	private SpacecraftState s;
	private double sma;
	private double threshold;

	/**
	 * Osculating to mean which uses the Space-craft state data to do the
	 * conversion. This can be used directly.
	 * 
	 * @param orbit
	 * @param s
	 * @param threshold
	 * @throws Exception
	 */
	public OsculatingToMeanEckstein(SpacecraftState s, OrbitType orbitType, double threshold) throws Exception {
		this.setSpacecraftState(s);
		this.threshold = threshold;
		if (orbitType == OrbitType.CIRCULAR) {
			CircularOrbit orbit = (CircularOrbit) s.getOrbit();
			this.sma = orbit.getA();
			this.h = orbit.getCircularEx();
			this.l = orbit.getCircularEy();
			this.raan = orbit.getRightAscensionOfAscendingNode();
			this.lambda = orbit.getAlphaM();
			this.inc = orbit.getI();
		} else if (orbitType == OrbitType.KEPLERIAN) {
			KeplerianOrbit orbit = (KeplerianOrbit) s.getOrbit();
			this.sma = orbit.getA();
			this.h = orbit.getE() * FastMath.cos(orbit.getPerigeeArgument());
			this.l = orbit.getE() * FastMath.sin(orbit.getPerigeeArgument());
			this.raan = orbit.getRightAscensionOfAscendingNode();
			this.lambda = orbit.getMeanAnomaly() + orbit.getPerigeeArgument();
			this.inc = orbit.getI();
		} else {
			throw new Exception("This converter only supports Circular and Keplerian Orbits");
		}
		this.calculateHelpingFucntions();

	}

	/**
	 * Osculating to mean which uses the Space-craft state data to do the
	 * conversion. This can be used directly.
	 * 
	 * @param s
	 * @param orbitType
	 * @throws Exception
	 */
	public OsculatingToMeanEckstein(SpacecraftState s, OrbitType orbitType) throws Exception {
		this(s, orbitType, 0.0001);
	}

	/**
	 * Calculates the derivatives with help of Lagrangian method. The resulted
	 * array will have the following order.<br>
	 * 0, 1, 2, 3, 4, 5 = > da, dh, dl, di, draan, dlambda
	 * 
	 * @return
	 */
	public double[] calculateDerivatives() {
		double[] results = new double[6];
		results[0] = calculateSemiMajorAxisDerivative();
		results[1] = calculateHDerivative();
		results[2] = calculateLDerivative();
		results[3] = calculateInclinationDerivative();
		results[4] = calculateRAANDerivative();
		results[5] = calculateLambdaDerivative();
		return results;
	}

	/**
	 * Calculate the X component of the eccentricity (h) derivative. Equation
	 * 5.4
	 * 
	 * @return
	 */
	public double calculateHDerivative() {
		double dh = -1 * 3 / (2 * this.lambdaPrim) * this.g2
				* ((1 - (7 / 4) * FastMath.pow(this.beta, 2)) * FastMath.sin(this.lambda)
						+ (1 - 3 * FastMath.pow(this.beta, 2)) * this.l * FastMath.sin(2 * this.lambda)
						+ (-1.5 + 2 * FastMath.pow(this.beta, 2)) * this.h * FastMath.cos(2 * this.lambda)
						+ (7 / 12) * FastMath.pow(this.beta, 2) * FastMath.sin(3 * this.lambda)
						+ (17 / 8) * FastMath.pow(this.beta, 2)
						+ (this.l * FastMath.sin(4 * this.lambda) - this.h * FastMath.sin(4 * this.lambda)));
		return dh;
	}

	/**
	 * Calculates the needed helping functions for the given Lagrangian
	 * derivative calculation.
	 */
	public void calculateHelpingFucntions() {
		this.g2 = Constants.EGM96_EARTH_C20 * FastMath.pow(Constants.EGM96_EARTH_EQUATORIAL_RADIUS / this.sma, 2);
		this.beta = FastMath.sin(this.inc);
		this.lambdaPrim = 1 - (3 / 2) * this.g2 * (3 - 4 * this.beta);
	}

	/**
	 * Calculate the Y component of the eccentricity (l) derivative. Equation
	 * 5.4
	 * 
	 * @return
	 */
	public double calculateLDerivative() {
		double dl = -1 * 3 / (2 * this.lambdaPrim) * this.g2
				* ((1 - (5 / 4) * FastMath.pow(this.beta, 2)) * FastMath.cos(this.lambda)
						+ 0.5 * (3 - 5 * FastMath.pow(this.beta, 2)) * this.l * FastMath.cos(2 * this.lambda)
						+ (2 - 1.5 * FastMath.pow(this.beta, 2)) * this.h * FastMath.sin(2 * this.lambda)
						+ (7 / 12) * FastMath.pow(this.beta, 2) * FastMath.cos(3 * this.lambda)
						+ (17 / 8) * FastMath.pow(this.beta, 2)
								* (l * FastMath.sin(4 * this.lambda) + h * FastMath.cos(4 * this.lambda)));
		return dl;
	}

	/**
	 * Calculates the semi-Major axis derivative with help of Lagrangian method.
	 * Equation 5.4
	 * 
	 * @return
	 */
	public double calculateSemiMajorAxisDerivative() {
		double da = -1 * (3 * this.sma) / (2 * this.lambdaPrim) * this.g2
				* ((2 - (7 / 2) * FastMath.pow(this.beta, 2)) * this.l * FastMath.cos(this.lambda)
						+ (2 - 2.5 * FastMath.pow(this.beta, 2)) * this.h * FastMath.sin(this.lambda)
						+ FastMath.pow(this.beta, 2) + FastMath.cos(2 * this.lambda)
						+ 3.5 * FastMath.pow(this.beta, 2)
								* (this.l * FastMath.cos(3 * this.lambda) + this.h * FastMath.sin(3 * this.lambda)))
				+ (3 * this.sma / 4) * FastMath.pow(this.g2, 2) * FastMath.pow(this.beta, 2)
						* (7 * (2 - 3 * FastMath.pow(this.beta, 2)) * FastMath.cos(3 * this.lambda)
								+ FastMath.pow(this.beta, 2) * FastMath.cos(4 * this.lambda));
		return da;
	}

	/**
	 * Calculates the Inclination derivative with help of Lagrangian method.
	 * 
	 * @return
	 */
	public double calculateInclinationDerivative() {
		double id = -1 * 3 / (4 * this.lambdaPrim) * this.g2 * this.beta * FastMath.sqrt(1 - FastMath.pow(this.beta, 2))
				* (-this.l * FastMath.cos(this.lambda) + this.h * FastMath.cos(this.lambda)
						+ FastMath.cos(2 * this.lambda)
						+ (7 / 3) * (this.l * FastMath.cos(3 * this.lambda) + this.h * FastMath.sin(3 * this.lambda)));
		return id;
	}

	/**
	 * Calculates the Right ascension of the ascending node derivative with help
	 * of Lagrangian method. Equation 5.4
	 * 
	 * @return
	 */
	public double calculateRAANDerivative() {
		double drAAN = -1 * (3 * this.sma) / (2 * this.lambdaPrim) * this.g2
				* FastMath.sqrt(1 - FastMath.pow(this.beta, 2))
				* ((7 / 2) * this.l * FastMath.sin(this.lambda) - 2.5 * this.h * FastMath.cos(this.lambda)
						- 0.5 * FastMath.sin(2 * this.lambda) - (7 / 6) * this.l * FastMath.sin(3 * this.lambda)
						+ (7 / 6) * this.h * FastMath.cos(3 * this.lambda));
		return drAAN;
	}

	/**
	 * Calculate the lambda Parameter (MeanAnomaly+ArgumentOfPerigee) derivative
	 * with help of the Lagrangian method. Equation 5.4
	 * 
	 * @return
	 */
	public double calculateLambdaDerivative() {
		double dlambda = -1 * (3 * this.sma) / (2 * this.lambdaPrim) * this.g2
				* ((10 - (119 / 8) * FastMath.pow(this.beta, 2)) * this.l * FastMath.sin(this.lambda)
						+ ((85 / 8) * FastMath.pow(this.beta, 2) - 9) * this.h * FastMath.cos(this.lambda)
						+ (2 * FastMath.pow(this.beta, 2)) * FastMath.sin(2 * this.lambda)
						+ (-(7 / 6) + (119 / 4) * FastMath.pow(this.beta, 2))
								* (this.l * FastMath.sin(3 * this.lambda) - this.h * FastMath.cos(3 * this.lambda))
				- (3 - (21 / 4) * FastMath.pow(this.beta, 2)) * this.l * FastMath.sin(this.lambda)
				+ (3 - (15 / 4) * FastMath.pow(this.beta, 2)) * this.h * FastMath.cos(this.lambda)
				- (3 / 4) * FastMath.pow(this.beta, 2) * FastMath.sin(2 * this.lambda)
				- (21 / 12) * FastMath.pow(this.beta, 2)
						* (this.l * FastMath.sin(3 * this.lambda) - this.h * FastMath.cos(3 * this.lambda)));
		return dlambda;
	}

	/**
	 * calculate new space-craft state after calculating the derivatives.
	 * 
	 * @return
	 * @throws OrekitException
	 */
	public SpacecraftState stateCalculator() throws OrekitException {
		double[] dervatives = this.calculateDerivatives();
		double a = this.sma + dervatives[0];
		double ex = this.h + dervatives[1];
		double ey = this.l + dervatives[2];
		double i = this.inc + dervatives[3];
		double raan = this.raan + dervatives[4];
		double alpha = this.lambda + dervatives[5];
		double mu = GravityFieldFactory.getNormalizedProvider(2, 0).getMu();
		Orbit circularOrbit = new CircularOrbit(a, ex, ey, i, raan, alpha, PositionAngle.MEAN, this.s.getFrame(),
				this.s.getDate(), mu);
		SpacecraftState state = new SpacecraftState(circularOrbit);
		return state;
	}

	/**
	 * Compares the state of the spacecraft with the initial state using their
	 * Cartesian coordinates.
	 * 
	 * @param newState
	 * @return
	 */
	public Vector3D[] compareWithIntialState(SpacecraftState newState) {
		Vector3D[] posVel = new Vector3D[2];
		Vector3D velNew = newState.getPVCoordinates().getVelocity();
		Vector3D posNew = newState.getPVCoordinates().getPosition();
		Vector3D pos = this.s.getPVCoordinates().getPosition();
		Vector3D vel = this.s.getPVCoordinates().getVelocity();
		posVel[0] = vel.add(velNew.negate());
		posVel[1] = pos.add(posNew.negate());
		return posVel;
	}

	/**
	 * Calculate the maximal number for the PVDifference vector.
	 */
	public double getMaxPVDiff(Vector3D[] posVel) {
		Vector3D pos = posVel[0];
		Vector3D vel = posVel[1];
		double max = 0;
		for (double i : pos.toArray()) {
			if (FastMath.abs(i) > FastMath.abs(max)) {
				i = max;
			}
		}
		for (double i : vel.toArray()) {
			if (FastMath.abs(i) > FastMath.abs(max)) {
				i = max;
			}
		}
		return max;
	}

	/**
	 * Calculate the mean elements from the given osculating elements. It is
	 * done in an iterative way.
	 * 
	 * @return
	 * @throws OrekitException
	 */
	public SpacecraftState calculateMeanElement() throws OrekitException {
		SpacecraftState s = this.stateCalculator();
		Vector3D[] posVelDiffVec = compareWithIntialState(s);
		double posVelDiff = this.getMaxPVDiff(posVelDiffVec);
		while (posVelDiff > this.threshold) {
			CircularOrbit co = (CircularOrbit) s.getOrbit();
			this.setSma(co.getA());
			this.setH(co.getCircularEx());
			this.setL(co.getEquinoctialEy());
			this.setInc(co.getI());
			this.setRaan(co.getRightAscensionOfAscendingNode());
			this.setLambda(co.getAlpha(PositionAngle.MEAN));
			this.calculateHelpingFucntions();
			s = this.stateCalculator();
			posVelDiffVec = compareWithIntialState(s);
			posVelDiff = this.getMaxPVDiff(posVelDiffVec);
		}
		return s;
	}

	/**
	 * @return the beta
	 */
	public double getBeta() {
		return beta;
	}

	/**
	 * @return the g2
	 */
	public double getG2() {
		return g2;
	}

	/**
	 * @return the h
	 */
	public double getH() {
		return h;
	}

	/**
	 * @return the inc
	 */
	public double getInc() {
		return inc;
	}

	/**
	 * @return the l
	 */
	public double getL() {
		return l;
	}

	/**
	 * @return the lambda
	 */
	public double getLambda() {
		return lambda;
	}

	/**
	 * @return the lambdaPrim
	 */
	public double getLambdaPrim() {
		return lambdaPrim;
	}

	/**
	 * @return the raan
	 */
	public double getRaan() {
		return raan;
	}

	/**
	 * @return the sma
	 */
	public double getSma() {
		return sma;
	}

	/**
	 * 
	 * @return
	 */
	public SpacecraftState getSpacecraftState() {
		return s;
	}

	/**
	 * @param beta
	 *            the beta to set
	 */
	public void setBeta(double beta) {
		this.beta = beta;
	}

	/**
	 * @param g2
	 *            the g2 to set
	 */
	public void setG2(double g2) {
		this.g2 = g2;
	}

	/**
	 * @param h
	 *            the h to set
	 */
	public void setH(double h) {
		this.h = h;
	}

	/**
	 * @param inc
	 *            the inc to set
	 */
	public void setInc(double inc) {
		this.inc = inc;
	}

	/**
	 * @param l
	 *            the l to set
	 */
	public void setL(double l) {
		this.l = l;
	}

	/**
	 * @param lambda
	 *            the lambda to set
	 */
	public void setLambda(double lambda) {
		this.lambda = lambda;
	}

	/**
	 * @param lambdaPrim
	 *            the lambdaPrim to set
	 */
	public void setLambdaPrim(double lambdaPrim) {
		this.lambdaPrim = lambdaPrim;
	}

	/**
	 * @param raan
	 *            the raan to set
	 */
	public void setRaan(double raan) {
		this.raan = raan;
	}

	/**
	 * @param sma
	 *            the sma to set
	 */
	public void setSma(double sma) {
		this.sma = sma;
	}

	/**
	 * 
	 * @param s
	 */
	public void setSpacecraftState(SpacecraftState s) {
		this.s = s;
	}

}
