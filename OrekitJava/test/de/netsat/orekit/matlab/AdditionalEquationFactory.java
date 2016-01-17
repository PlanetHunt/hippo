package de.netsat.orekit.matlab;

import org.orekit.errors.OrekitException;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.integration.AdditionalEquations;

public class AdditionalEquationFactory implements AdditionalEquations {

	private String name;
	double[] rvDot = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
	private boolean isValid;

	public AdditionalEquationFactory(String name) {
		this.name = name;
	}

	/**
	 * Calculate the effect of the additional equation on the mass.
	 * 
	 * @param s
	 * @return {@link Double}
	 */
	private double calculateMass(SpacecraftState s) {
		return 0;
	}

	/**
	 * Calculate the effect of the additional equation on the Position in X.
	 * 
	 * @param s
	 * @return
	 */
	private double calculateRX(SpacecraftState s) {
		return 0;
	}

	/**
	 * Calculate the effect of the additional equation on the Position in Y.
	 * 
	 * @param s
	 * @return
	 */
	private double calculateRY(SpacecraftState s) {
		return 0;
	}

	/**
	 * Calculate the effect of the additional equation on the Position in Z.
	 * 
	 * @param s
	 * @return
	 */
	private double calculateRZ(SpacecraftState s) {
		return 0;
	}

	/**
	 * Calculate the effect of the additional equation on the Velocity in X.
	 * 
	 * @param s
	 * @return
	 */
	private double calculateVX(SpacecraftState s) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Calculate the effect of the additional equation on the Velocity in Y.
	 * 
	 * @param s
	 * @return
	 */
	private double calculateVY(SpacecraftState s) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Calculate the effect of the additional equation on the Velocity in Z.
	 * 
	 * @param s
	 * @return
	 */
	private double calculateVZ(SpacecraftState s) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double[] computeDerivatives(SpacecraftState s, double[] pDot) throws OrekitException {
		if (this.isValid) {
			this.rvDot[0] = calculateRX(s);
			this.rvDot[1] = calculateRY(s);
			this.rvDot[2] = calculateRZ(s);
			this.rvDot[3] = calculateVX(s);
			this.rvDot[4] = calculateVY(s);
			this.rvDot[5] = calculateVZ(s);
			this.rvDot[6] = calculateMass(s);
		}
		return this.rvDot;
	}

	/**
	 * 
	 * @return
	 */
	public boolean getIsValid() {
		return this.isValid;
	}

	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * 
	 * @return
	 */
	public double[] getRvDot() {
		return rvDot;
	}

	/**
	 * 
	 * @param isValid
	 */
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

}
