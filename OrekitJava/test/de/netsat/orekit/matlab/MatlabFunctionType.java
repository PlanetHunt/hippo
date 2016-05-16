package de.netsat.orekit.matlab;

public enum MatlabFunctionType {
	Plot("Plots the data in matlab", "plotOrbitElements(timestamp, orbital_elements)", true, false),
	FTC_CALC_APOGEE("Calculate the delta V in apogee", "FTCCalcApogeeDv(orbital_elements)", false, true),
	FTC_CALC_PERIGEE("Caculate the delta V in perigee", "FTCCalcPerigeeDv(orbital_elements)", false, true),
	FTC_CALC_TRUE_LAT_90("Calculate the delta v for true latitude of Nighty", "FTCCalcLatArgNinetyDv(orbital_elements)", false, true),
	FTC_CALC_TRUE_LAT_0("Calculate the delta v for true latitude of Zero", "FTCCalcLatArgZeroDv(orbital_elements)", false, true),
	MATLAB_STEP_HANDLER("Does the steps handling in Matlab, without usage of events.", "matlabStepHandler(orbital_elements, mean_orbital_elements, mean_orbital_elements_eckstein, timestamp, current_mass, last_step_flag)",false, false);

	private String help;
	private String functionName;
	private boolean atOnce;
	private boolean atEvent;

	/**
	 * The MatlabFunctionType is the functions that are called in each step in
	 * Matlab or after it is finished.
	 * 
	 * @param help
	 * @param functionName
	 * @param atOnce
	 */
	MatlabFunctionType(String help, String functionName, boolean atOnce, boolean atEvent) {
		this.help = help;
		this.functionName = functionName;
		this.atOnce = atOnce;
		this.atEvent = atEvent;
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
	
	/**
	 * Sets the atEvent variable.
	 */
	public void setAtEvent(boolean atEvent){
		this.atEvent = atEvent;
	}
	
	/**
	 * Returns the atEvent variable.
	 */
	public boolean getAtEvent(){
		return this.atEvent;
	}
	
}
