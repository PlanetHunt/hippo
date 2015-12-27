package de.netsat.orekit.matlab;

public enum SensorDataType {
	SUN("sun_position", "dd", "getSunPositionAsArray"),  //set
	VELOCITY("velocity", "dd", "getVelocityVectorAsArray"), //set
	POSITION("position", "dd", "getPositionVectorAsArray"), //set
	MAGNETIC_FIELD("magnetic_field", "dd", "getMagneticFieldAsArray"), //set
	ORBITAL_ELEMENTS("orbital_elements", "dd", "getOrbitalElements"), 
	TIMESTAMP("timestamp", "dd", "getTimeStampAsArray"), 
	TIMESTAMP_UNIX("timestamp","d", "getTimestamp"), 
	MU("mu", "d", "getMu"), 
	PX("px", "d", "getPx"), 
	PY("py", "d", "getPy"), 
	PZ("pz", "d", "getPZ"), 
	VX("vx", "d", "getVX"), 
	VY("vy", "d", "getVY"), 
	VZ("vz", "d", "getVZ"), 
	SMA("sma", "d", "getSemiMajorAxis"), 
	ECC("ecc", "d", "getEccentricity"), 
	INC("inc", "d", "getInclination"), 
	ARG("arg","d", "getArgumentOfPerigee"), 
	RAA("raa", "d", "getRaan"), 
	MAN("MAN", "d", "getMeanAnomaly");

	private String name_in_matlab;
	private String type;
	private String function_name;

	/**
	 * Sensor DataTypes declare the way, this application needs different
	 * parameters from the Orekit framework. d mean an scalar double, and dd
	 * mean an array of doubles. eccentricity
	 * 
	 * @param name
	 * @param type
	 */
	SensorDataType(String name, String type, String function_name) {
		this.name_in_matlab = name;
		this.type = type;
		this.function_name = function_name;
	}

	/**
	 * Returns the type of the SensorDataType
	 * 
	 * @return {@link String}
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Returns the name of the SensorDataType
	 * 
	 * @return {@link String}
	 */
	public String getNameInMatlab() {
		return this.name_in_matlab;
	}

	/**
	 * Returns the function name need to be called to retrieve the data from
	 * Orekit.
	 * 
	 * @return {@link String}
	 */
	public String getFunctionName() {
		return this.function_name;
	}
}
