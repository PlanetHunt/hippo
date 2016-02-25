package de.netsat.orekit.matlab;

import java.util.Hashtable;

import matlabcontrol.MatlabInvocationException;

public class MatlabInitialSettings {

	private MatlabInterface mi;
	private Hashtable<String, Object> settings;
	private MatlabVariableType[] initialValuesTemplate;
	private String matlabFunction;

	/**
	 * Creates the Matlab Initial Settings.
	 * 
	 * @param mi
	 * @throws MatlabInvocationException
	 */
	public MatlabInitialSettings(MatlabInterface mi, MatlabVariableType[] initailValuesTemplate, String matlabFunction,
			final double mu) throws MatlabInvocationException {
		this.mi = mi;
		this.mi.getProxy().setVariable("muVal", mu);
		this.initialValuesTemplate = initailValuesTemplate;
		this.matlabFunction = matlabFunction;
		this.settings = new Hashtable<String, Object>();
		this.setSettings();
	}

	/**
	 * sets the Matlab interface.
	 * 
	 * @param mi
	 */
	public void setMatlabInterface(MatlabInterface mi) {
		this.mi = mi;
	}

	/**
	 * Returns the Matlab interface.s
	 * 
	 * @return
	 */
	public MatlabInterface getMatlabInterface() {
		return this.mi;
	}

	/**
	 * Returns the initialSettings from Matlab.
	 * 
	 * @return
	 */
	public Hashtable<String, Object> getSettings() {
		return this.settings;
	}

	/**
	 * Sets the settings after they are retrieved from Matlab,
	 * 
	 * @throws MatlabInvocationException
	 */
	public void setSettings() throws MatlabInvocationException {
		int params = this.initialValuesTemplate.length;
		Object[] matlabResults = mi.getProxy().returningEval(this.getMatlabFunction(), params);
		for (MatlabVariableType val : this.initialValuesTemplate) {
			if (((double[]) matlabResults[val.getPlace()]).length > 1) {
				this.settings.put(val.getVarName(), ((double[]) matlabResults[val.getPlace()]));
			} else {
				this.settings.put(val.getVarName(), ((double[]) matlabResults[val.getPlace()])[0]);
			}
		}
	}

	/**
	 * Sets the Matlab function string that returns the initial settings.
	 * 
	 * @param matlabFunction
	 */
	public void setMatlabFunction(String matlabFunction) {
		this.matlabFunction = matlabFunction;
	}

	/**
	 * Returns the Matlab function string that returns the initial settings.
	 * 
	 * @return
	 */
	public String getMatlabFunction() {
		return this.matlabFunction;
	}

}
