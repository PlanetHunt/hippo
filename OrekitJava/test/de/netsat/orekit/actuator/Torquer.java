package de.netsat.orekit.actuator;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * Should be implemented by all torquers
 * @author alexanderkramer
 */
public abstract class Torquer extends Actuator {
	public Torquer(Vector3D position, Vector3D direction) {
		super(position, direction);
	}
}
