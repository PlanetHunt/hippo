package de.netsat.orekit.matlab;

import java.util.Hashtable;

import org.apache.commons.math3.ode.nonstiff.AdaptiveStepsizeIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math3.ode.nonstiff.RungeKuttaIntegrator;
import org.orekit.errors.PropagationException;
import org.orekit.orbits.Orbit;
import org.orekit.orbits.OrbitType;
import org.orekit.propagation.numerical.NumericalPropagator;

public class InitiatePropagators {

	private String propagatorType;
	private double stepSize;
	private Double positionTolerance;
	private Double minStep;
	private Double maxStep;
	private Orbit initialOrbit;
	private double[][] tolerances;
	private OrbitType orbitType;
	private AdaptiveStepsizeIntegrator adaptiveStepPropagator;
	private RungeKuttaIntegrator rungeKuttaPropagtor;
	private NumericalPropagator numericalPropagator;

	/**
	 * Create the Initiate Propagators Object.
	 * 
	 * @param propagatorType
	 * @param options
	 */
	public InitiatePropagators(String PropagatorType, Hashtable<String, Double> options, Orbit initialOrbit) {
		this.propagatorType = PropagatorType;
		this.initialOrbit = initialOrbit;
		this.orbitType = this.initialOrbit.getType();
		try {
			this.stepSize = options.get("stepSize");

			if (this.propagatorType == "adaptiveStep") {
				this.positionTolerance = options.get("positionTolerance");
				this.minStep = options.get("minStep");
				this.maxStep = options.get("maxStep");
				this.setTolerances();
				this.setAdaptivePropagator(new DormandPrince853Integrator(this.getMinStep(), this.getMaxStep(),
						this.getTolerance()[0], this.getTolerance()[1]));
				this.numericalPropagator = new NumericalPropagator(adaptiveStepPropagator);
			} else {
				this.setRungeKuttaPropagator(new ClassicalRungeKuttaIntegrator(this.stepSize));
				this.numericalPropagator = new NumericalPropagator(rungeKuttaPropagtor);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * Sets the basic AdaptiveStepPropagator.
	 * 
	 * @param adaptive
	 */
	public void setAdaptivePropagator(AdaptiveStepsizeIntegrator adaptive) {
		this.adaptiveStepPropagator = adaptive;
	}

	/**
	 * Returns the basic AdpativeStepPropagator.
	 * 
	 * @return
	 */
	public AdaptiveStepsizeIntegrator getAdaptiveStepPropagator() {
		return this.adaptiveStepPropagator;
	}

	/**
	 * Sets the rungeKutta Propagator.
	 * 
	 * @param rungeKutta
	 */
	public void setRungeKuttaPropagator(RungeKuttaIntegrator rungeKutta) {
		this.rungeKuttaPropagtor = rungeKutta;
	}

	/**
	 * Returns the basic RungeKutta Propagator.
	 * 
	 * @return
	 */
	public RungeKuttaIntegrator getRungeKuttaPropagator() {
		return this.rungeKuttaPropagtor;
	}

	/**
	 * Returns the min step size in seconds for the Propagator. (Only applicable
	 * for AdaptiveStepSize)
	 * 
	 * @return
	 */
	public double getMinStep() {
		return this.minStep;
	}

	/**
	 * Sets the max step size in seconds for the Propagator. (Only applicable
	 * for AdaptiveStepSize)
	 * 
	 * @param minStep
	 */
	public void setMinStep(double minStep) {
		this.minStep = minStep;
	}

	/**
	 * Returns the max step size in seconds for the Propagator. (Only applicable
	 * for AdaptiveStepSize)
	 * 
	 * @return
	 */
	public double getMaxStep() {
		return this.maxStep;
	}

	/**
	 * Sets the max step size in seconds for the Propagator. (Only applicable
	 * for AdaptiveStepSize).
	 * 
	 * @param maxStep
	 */
	public void setMaxStep(double maxStep) {
		this.maxStep = maxStep;
	}

	/**
	 * Returns the tolerances for the Propagator. (Only applicable for
	 * AdaptiveStepSize)
	 *
	 * @return
	 */
	public double[][] getTolerance() {
		return this.tolerances;
	}

	/**
	 * Sets the position tolerance value for the Propagator. (Only applicable
	 * for AdaptiveStepSize)
	 * 
	 * @param positionTolerance
	 */
	public void setPositionTolerance(double positionTolerance) {
		this.positionTolerance = positionTolerance;
	}

	/**
	 * Returns the tolerance for the Propagator. (Only applicable for
	 * AdpativeStepSize)
	 * 
	 * @return
	 */
	public double getPositionTolerance() {
		return this.positionTolerance;
	}

	/**
	 * Sets the tolerance for the Propagator. (Only applicable for
	 * AdaptiveStepSize)
	 */
	public void setTolerances() throws PropagationException {
		this.tolerances = NumericalPropagator.tolerances(this.getPositionTolerance(), this.getInitialOrbit(),
				this.getOrbitType());
	}

	/**
	 * Returns the Orbit Type for the Propagator.
	 * 
	 * @return
	 */
	public OrbitType getOrbitType() {
		return this.orbitType;
	}

	/**
	 * Sets the Orbit Type for the Propagator.
	 * 
	 * @param orbitType
	 */
	public void setOrbitType(OrbitType orbitType) {
		this.orbitType = orbitType;
	}

	/**
	 * Returns the initialOrbit for the Propagator. (Only applicable for
	 * AdaptiveStepSize)
	 * 
	 * @return
	 */
	public Orbit getInitialOrbit() {
		return this.initialOrbit;
	}

	/**
	 * Sets the initialOrbit for the Propagator. (Only applicable for
	 * AdaptiveStepSize)
	 * 
	 * @param initialOrbit
	 */
	public void setInitialOrbit(Orbit initialOrbit) {
		this.initialOrbit = initialOrbit;
	}

	/**
	 * Sets the stepSize for the Propagator (Only Applicable to RungeKutta)
	 * 
	 * @param stepSize
	 */
	public void setStepSize(double stepSize) {
		this.stepSize = stepSize;
	}

	/**
	 * Returns the stepSize for the Propagator (Only applicable to RungeKutta)
	 * 
	 * @return
	 */
	public double getStepSize() {
		return this.stepSize;
	}

	/**
	 * Returns the created basic numerical Propagator.
	 * 
	 * @return
	 */
	public NumericalPropagator getNumericalPropagator() {
		return this.numericalPropagator;
	}

	/**
	 * Sets the created basic numerical Propagator.
	 * 
	 * @param np
	 */
	public void setNumericalPropagator(NumericalPropagator np) {
		this.numericalPropagator = np;
	}

}
