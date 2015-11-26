package de.netsat.orekit.actuator;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public abstract class Thruster extends Actuator {
	public Thruster(Vector3D position, Vector3D direction) {
		super(position, direction);
	}
	
	/**
	 * Get the mass flow rate of one thruster.
	 * 
	 * @param thrust (N)
	 * @return flowrate (negative, kg/s)
	 */
	public abstract double getFlowRate(double thrust);

	/**
	 * Get the specific impulse of one thruster.
	 * 
	 * @param thrust (N)
	 * @return ISp (s)
	 */
	public abstract double getISP(double thrust);
}
