package de.netsat.orekit.actuator;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * This class should be implemented by all available actuators.
 * @author alexanderkramer
 *
 */
public abstract class Actuator {
	private final Vector3D position;
	private final Vector3D direction;
	
	public Actuator(Vector3D position, Vector3D direction) {
		this.position = position;
		this.direction = direction;
	}
	
	/**
	 * Return the direction of the actuator axis. For thrusters, this is the
	 * thrust direction - for torquers this is the rotational axis.
	 * @return
	 */
	public Vector3D getDirection() {
		return direction;
	}
	
	/**
	 * Return the position of the actuator relative to geometric spacecraft center.
	 */
	public Vector3D getPosition() {
		return position;
	}

	/** Return current power consumption in mW.
	 */
	public abstract double getPowerConsumption_mW(double internalActuatorValue);
}
