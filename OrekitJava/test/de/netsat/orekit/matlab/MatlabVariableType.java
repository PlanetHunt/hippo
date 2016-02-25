package de.netsat.orekit.matlab;

public enum MatlabVariableType {
	INITIAL_DATE("initialDate", 0, "initOrbit"),
	INITIAL_ORBIT("initialOrbit", 1, "initOrbit"),
	THRUST_NUMBER("thrusterNumber", 2, "thruster"),
	THRUST("thrust", 3,"thruster"),
	STARTING_MASS("startingMass", 4, "initState"),
	POSITION_TOLERANCE("positionTolerance", 5, "initProp"),
	MIN_STEP("minStep",6,"initProp"),
	MAX_STEP("maxStep",7,"initProp"),
	DURATION("duration", 8, "initState"),
	STEP_SIZE("stepSize", 9, "initProp");

	private String varName;
	private int place;
	private String group;
	private MatlabVariableType(String varName, int place, String group) {
		this.varName = varName;
		this.place = place;
		this.group = group;
	}
	
	/**
	 * Returns the variable name of the Matlab Variable
	 * @return
	 */
	public String getVarName(){
		return this.varName;
	}
	
	/**
	 * Sets the Variable name of the Matlab Variable
	 * @param varName
	 */
	public void setVarName(String varName){
		this.varName = varName;
	}
	/**
	 * Sets the place (order) of the variable in a given matlab function.
	 * @param place
	 */
	public void setPlace(int place){
		this.place = place;
	}
	
	/**
	 * Returns the place (order) of the given variable in the matlab function
	 * @return
	 */
	public int getPlace(){
		return this.place;
	}
	
	/**
	 * Sets the group of the given variable.
	 * @param group
	 */
	public void setGroup(String group){
		this.group = group;
	}
	
	/**
	 * Returns the group of the given variable.
	 * @return
	 */
	public String getGroup(){
		return this.group;
	}
}
