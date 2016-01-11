package de.netsat.orekit.matlab;

import org.apache.commons.math3.ode.nonstiff.AdaptiveStepsizeIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math3.ode.nonstiff.RungeKuttaIntegrator;
import org.orekit.errors.PropagationException;
import org.orekit.orbits.Orbit;
import org.orekit.orbits.OrbitType;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.propagation.numerical.NumericalPropagator;

public enum PropagatorDataType {

	NUMERICAL_KEPLERIAN_ADAPTIVE("numerical", "adaptiveStep", OrbitType.KEPLERIAN), 
	NUMERICAL_KEPLERIAN_RUNGEKUTTA("numerical", "rungeKutta", OrbitType.KEPLERIAN), 
	NUMERICAL_CIRCULAR_ADAPTIVE("numerical", "adaptiveStep", OrbitType.CIRCULAR), 
	NUMERICAL_CIRCULAR_RUNGEKUTTA("numerical", "rungeKutta", OrbitType.CIRCULAR), 
	NUMERICAL_EQUINOCTIAL_ADAPTIVE("numerical", "adaptiveStep", OrbitType.EQUINOCTIAL), 
	NUMERICAL_EQUINOCTIAL_RUNGEKUTTA("numerical", "rungeKutta", OrbitType.EQUINOCTIAL), 
	NUMERICAL_CARTESIAN_ADPATIVE("numerical", "adaptiveStep", OrbitType.CARTESIAN), 
	NUMERICAL_CARTESIAN_RUNGEKUTTA("numerical","rungeKutta", OrbitType.CARTESIAN),
	ANALYTICAL_SIMPLE_KEPLERIAN("analytical", "keplerian", OrbitType.KEPLERIAN),
	ANALYTICAL_ECKSTEIN_KEPLERIAN("analytical", "eckstein", OrbitType.KEPLERIAN);
	
	private double stepSize;
	private double duration;
	private double maxStep;
	private double minStep;
	private double positionTolerance;
	private String propagationType;
	private String integratorType;
	private OrbitType orbitType;
	private Orbit orbit;
	private double[][] tolerances;
	private NumericalPropagator np;
	private AdaptiveStepsizeIntegrator assi;
	private RungeKuttaIntegrator rki;
	private KeplerianPropagator kp;

	/**
	 * Constructor class.
	 * 
	 * @param help
	 */
	PropagatorDataType(String propagationType, String integratorType, OrbitType orbitType) {
		this.propagationType = propagationType;
		this.integratorType = integratorType;
		this.orbitType = orbitType;
	}

	/**
	 * Sets the propagator an hand the given data.
	 * 
	 * @throws PropagationException
	 */
	void setPropagator() throws PropagationException {
		if (this.propagationType == "numerical") {
			if (this.integratorType == "adaptiveStep") {
				this.setTolerances();
				this.setAdaptiveStepSizeIntegrator(new DormandPrince853Integrator(this.getMinStep(), this.getMaxStep(),
						this.getTolerance()[0], this.getTolerance()[1]));
				this.setNumericalPropagator(new NumericalPropagator(this.getAdaptiveStepSizeIntegrator()));
			} else {
				this.setTolerances();
				this.setRungeKuttaIntegrator(new ClassicalRungeKuttaIntegrator(this.getStepSize()));
				this.setNumericalPropagator(new NumericalPropagator(getRungeKuttaIntegrator()));
			}
		}
		if(this.propagationType == "analytical"){
			if(this.integratorType == "keplerian"){
				this.setTolerances();
				this.setKeplerianPropagator(new KeplerianPropagator(this.getOrbit()));
			}
/*			else if(this.integratorType == "eckstein"){
				
			}*/
		}
	}

	/**
	 * Sets the simple keplerianPropagator.
	 * 
	 * @param kp 
	 */
	public void setKeplerianPropagator(KeplerianPropagator kp){
		this.kp = kp;
	}
	
	/**
	 * Returns the simple KeplerianPropagator
	 * @return {@link KeplerianPropagator}
	 */
	public KeplerianPropagator getKeplerianPropagator(){
		return this.kp;
	}
	
	/**
	 * Sets the numerical propagator for the Data
	 * 
	 * @param np
	 */
	public void setNumericalPropagator(NumericalPropagator np) {
		this.np = np;
	}

	/**
	 * Returns the numerical propagator
	 * 
	 * @return {@link NumericalPropagator}
	 */
	public NumericalPropagator getNumericalPropagator() {
		return this.np;
	}

	/**
	 * Sets the AdaptiveStepSizeIntegrator for the propagator
	 * 
	 * @param integrator
	 */
	public void setAdaptiveStepSizeIntegrator(AdaptiveStepsizeIntegrator assi) {
		this.assi = assi;
	}

	/**
	 * Returns the AdaptiveStepSizeIntegrator for the propagator
	 * 
	 * @return
	 */
	public AdaptiveStepsizeIntegrator getAdaptiveStepSizeIntegrator() {
		return this.assi;
	}
	
	/**
	 * Sets the RungeKuttaIntegrator for the propagator
	 * 
	 * @param rki
	 */
	public void setRungeKuttaIntegrator(RungeKuttaIntegrator rki){
		this.rki = rki;
	}
	
	/**
	 * Returns the RungeKuttaIntegrator for the propagator
	 * 
	 * @return {@link RungeKuttaIntegrator}
	 */
	public RungeKuttaIntegrator getRungeKuttaIntegrator(){
		return this.rki;
	}

	/**
	 * Returns the minimum step for the propagator integrator
	 * 
	 * @return {@link Double}
	 */
	public double getMinStep() {
		return this.minStep;
	}

	/**
	 * Sets the minimum step for the propagator integrator
	 * 
	 * @param minStep
	 */
	public void setMinStep(double minStep) {
		this.minStep = minStep;
	}

	/**
	 * Returns the maximum step for the propagator integrator
	 *
	 * @return {@link Double}
	 */
	public double getMaxStep() {
		return this.maxStep;
	}

	/**
	 * Sets the maximum step for the propagator integrator
	 * 
	 * @param maxStep
	 */
	public void setMaxStep(double maxStep) {
		this.maxStep = maxStep;
	}

	/**
	 * Gets the tolerances for the given propagation.
	 *
	 * @return {@link Double}
	 */
	public double[][] getTolerance() {
		return this.tolerances;
	}

	/**
	 * Sets the tolerance for the given propagation.
	 */
	public void setTolerances() throws PropagationException {
		this.tolerances = NumericalPropagator.tolerances(this.getPositionTolerance(), this.getOrbit(),
				this.getOrbitType());
	}

	/**
	 * Sets the Orbit for the propagator initial states
	 * 
	 * @param orbit
	 */
	public void setOrbit(Orbit orbit) {
		this.orbit = orbit;
	}

	/**
	 * Returns the Orbit for the propagator initial states.
	 * 
	 * @return
	 */
	public Orbit getOrbit() {
		return this.orbit;
	}

	/**
	 * Sets the orbit type for the propagation
	 * 
	 * @param orbitType
	 */
	public void setOrbitType(OrbitType orbitType) {
		this.orbitType = orbitType;
	}

	/**
	 * Returns the orbit type of the propagation on hand.
	 * 
	 * @return {@link OrbitType}
	 */
	public OrbitType getOrbitType() {
		return this.orbitType;
	}

	/**
	 * Sets the position tolerance
	 * 
	 * @param positionTolerance
	 */
	public void setPositionTolerance(double positionTolerance) {
		this.positionTolerance = positionTolerance;
	}

	/**
	 * Returns the position tolerance
	 * 
	 * @return {@link Double}
	 */
	public double getPositionTolerance() {
		return this.positionTolerance;
	}

	/**
	 * Sets the type of the {@link PropagatorDataType}
	 * 
	 * @param propagationType
	 */
	public void setPropagationType(String propagationType) {
		this.propagationType = propagationType;
	}

	/**
	 * Returns the type of {@link PropagatorDataType}
	 * 
	 * @return {@link String}
	 */
	public String getPropagationType() {
		return this.propagationType;
	}

	/**
	 * Sets the duration of the propagation (in seconds)
	 * 
	 * @param duration
	 */
	public void setDuration(double duration) {
		this.duration = duration;
	}

	/**
	 * Returns the duration of the propagation (in seconds)
	 * 
	 * @return {@link Double}
	 */
	public double getDuration() {
		return this.duration;
	}

	/**
	 * Sets the step size of the propagation (in seconds)
	 * 
	 * @param stepSize
	 */
	public void setStepSize(double stepSize) {
		this.stepSize = stepSize;
	}

	/**
	 * Returns the step size of the propagation (in seconds)
	 * 
	 * @return {@link Double}
	 */
	public double getStepSize() {
		return this.stepSize;
	}
}
