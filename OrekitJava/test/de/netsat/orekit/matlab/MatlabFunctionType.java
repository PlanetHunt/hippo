package de.netsat.orekit.matlab;

public enum MatlabFunctionType {
	Plot("Plots the data in matlab", "plotOrbitElements(timestamp, orbital_elements)", true),
	Calc("Calculates the distance", "calculateDistance(a,b)", false);

	private String help;
	private String functionName;
	private boolean atOnce;

	/**
	 * The MatlabFunctionType is the functions that are called in each step in
	 * Matlab or after it is finished.
	 * 
	 * @param help
	 * @param functionName
	 * @param atOnce
	 */
	MatlabFunctionType(String help, String functionName, boolean atOnce) {
		this.help = help;
		this.functionName = functionName;
		this.atOnce = atOnce;
	}

	/**
	 * Sets the help for the called matlab function
	 * 
	 * @param help
	 */
	public void setHelp(String help) {
		this.help = help;
	}

	/**
	 * Returns the help of a Matlab function
	 * 
	 * @return {@link String}
	 */
	public String getHelp() {
		return this.help;
	}

	/**
	 * Sets the Name of a Matlab function, it is the function string that will
	 * be sent to Matlab with the given variables.
	 * 
	 * @param functionName
	 */
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	/**
	 * Returns the function that will called for a given MatlabFunctionType.
	 * 
	 * @return {@link String}
	 */
	public String getFunctionName() {
		return this.functionName;
	}

	/**
	 * Sets the setting of the function if it should be called at the end of the
	 * propagation steps or at every step.
	 * 
	 * @param atOnce
	 */
	public void SetAtOnce(boolean atOnce) {
		this.atOnce = atOnce;
	}

	/**
	 * Returns the AtOnce setting of the given matlab function.
	 * 
	 * @return {@link Boolean}
	 */
	public boolean getAtOnce() {
		return this.atOnce;
	}

}
