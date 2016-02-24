
package de.netsat.orekit.matlab;

import org.orekit.orbits.KeplerianOrbit;

import matlabcontrol.MatlabInvocationException;

public class InitialStateFactory {

	private boolean orbitFromMatlab;
	private boolean stateFromMatlab;
	private boolean propagationFromMatlab;
	private PropagationSettingType propagationSettings;
	private InitialOrbit initOrbit;
	private MatlabInterface mi;
	private KeplerianOrbit orbit;

	public InitialStateFactory(boolean orbitFromMatlab, boolean stateFromMatlab, boolean propagationFromMatlab,
			MatlabInterface mi) {
		this.orbitFromMatlab = orbitFromMatlab;
		this.stateFromMatlab = stateFromMatlab;
		this.propagationFromMatlab = propagationFromMatlab;
		this.mi = mi;
	}

	public boolean getOrbitFromMatlab() {
		return orbitFromMatlab;
	}

	public void setOrbitFromMatlab(boolean orbitFromMatlab) {
		this.orbitFromMatlab = orbitFromMatlab;
	}

	public boolean getStateFromMatlab() {
		return stateFromMatlab;
	}

	public void setStateFromMatlab(boolean stateFromMatlab) {
		this.stateFromMatlab = stateFromMatlab;
	}

	public boolean getPropagationFromMatlab() {
		return propagationFromMatlab;
	}

	public void setPropagationFromMatlab(boolean propagationFromMatlab) {
		this.propagationFromMatlab = propagationFromMatlab;
	}

	/**
	 * Sets the propagationSettings.
	 * 
	 * @param type
	 * @throws MatlabInvocationException
	 */
	public void setPropgationSetting(PropagationSettingType type) throws MatlabInvocationException {
		if (this.propagationFromMatlab) {
			this.propagationSettings.setFromMatlab(this.mi, "setNumericalPropagatorSettings()", 5, 0, 1, 2, 3, 4);
		} else {
			this.propagationSettings = type;
		}
	}

	/**
	 * Returns the propagation setting for the start of the propagator
	 * 
	 * @return {@link PropagationSettingType}
	 */
	public PropagationSettingType getPropagationSettings() {
		return this.propagationSettings;
	}

	/**
	 * Sets the initial orbit for the given orbiter. If it is not from matlab,
	 * in the same folder exist a tle.txt
	 * 
	 * @param orbit
	 * @param satNum
	 * @throws Exception
	 */
	public void setOrbit(int satNum) throws Exception {
		if (this.orbitFromMatlab) {
			this.orbit = this.initOrbit.getKeplerianOrbit();
		} else {
			this.orbit = this.initOrbit.getKeplerianOrbit(null, null, "tle.txt");
		}
	}

	/**
	 * Returns the initial orbit which is used for the propagation.
	 * 
	 * @return {@link KeplerianOrbit}
	 */
	public KeplerianOrbit getOrbit() {
		return this.orbit;
	}

}
