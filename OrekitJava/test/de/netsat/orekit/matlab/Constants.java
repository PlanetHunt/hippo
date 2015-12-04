package de.netsat.orekit.matlab;

import org.orekit.errors.OrekitException;
import org.orekit.forces.gravity.potential.GravityFieldFactory;
import org.orekit.forces.gravity.potential.NormalizedSphericalHarmonicsProvider;

public class Constants {

	private double mu;

	public Constants() throws OrekitException {
		mu = this.calculateMu();
		this.setMu(mu);
	}

	/**
	 * Calculates the MU from the NormalizedSphericalHarmonicsProvider
	 * 
	 * @return double
	 * @throws OrekitException
	 */
	public double calculateMu() throws OrekitException {
		NormalizedSphericalHarmonicsProvider provider = GravityFieldFactory.getNormalizedProvider(2, 0);
		return provider.getMu();
	}

	/**
	 * Sets the mu in constants.
	 * 
	 * @param mu
	 * @throws OrekitException
	 */
	public void setMu(double mu) throws OrekitException {
		if (mu != this.mu) {
			this.mu = mu;
		}
	}

	/**
	 * 
	 * @return mu.
	 */
	public double getMu() {
		return this.mu;
	}
}
