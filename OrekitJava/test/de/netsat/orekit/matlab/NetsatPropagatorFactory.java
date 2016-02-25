package de.netsat.orekit.matlab;

import org.orekit.errors.PropagationException;
import org.orekit.orbits.Orbit;
import org.orekit.orbits.OrbitType;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.propagation.numerical.NumericalPropagator;

public class NetsatPropagatorFactory {

	private PropagatorDataType propagatorType;

	/**
	 * Propagator's Constructor Method
	 * 
	 * @param propagatorType
	 * @throws PropagationException
	 */
	public NetsatPropagatorFactory(PropagatorDataType propagatorType, double maxStep, double minStep, double duration,
			double stepSize, double positionTolerance, OrbitType orbitType) throws PropagationException {
		this.propagatorType = propagatorType;
		this.propagatorType.setDuration(duration);
		this.propagatorType.setMinStep(minStep);
		this.propagatorType.setMaxStep(maxStep);
		this.propagatorType.setDuration(duration);
		this.propagatorType.setStepSize(stepSize);
		this.propagatorType.setPositionTolerance(positionTolerance);
		this.propagatorType.setOrbitType(orbitType);
		this.propagatorType.setPropagator();
	}

	/**
	 * Returns the created numerical propagator with the settings set.
	 * 
	 * @return {@link NumericalPropagator}
	 */
	public NumericalPropagator getNumericalPropagator() {
		return this.propagatorType.getNumericalPropagator();
	}

	/**
	 * Returns the analytical propagator created for the simulation.
	 * 
	 * @return {@link KeplerianPropagator}
	 */
	public KeplerianPropagator getKeplerianPropagator() {
		return this.propagatorType.getKeplerianPropagator();
	}

	/**
	 * Returns the analytical propagator created for the simulation.
	 * 
	 * @return {@link KeplerianPropagator}
	 */
	public KeplerianPropagator getAnalyticalPropagator() {
		return this.propagatorType.getKeplerianPropagator();
	}

}
