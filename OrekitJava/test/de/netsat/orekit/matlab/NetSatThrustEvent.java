package de.netsat.orekit.matlab;

import java.util.Comparator;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.orekit.time.AbsoluteDate;

public class NetSatThrustEvent {
	private final AbsoluteDate eventStart;
	private final AbsoluteDate eventEnd;
	private final Vector3D thrustDirection;

	/**
	 * 
	 * @param eventStart
	 * @param eventEnd
	 * @param thrustDirection
	 */
	public NetSatThrustEvent(AbsoluteDate eventStart, AbsoluteDate eventEnd, Vector3D thrustDirection) {
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.thrustDirection = thrustDirection;
	}

	/**
	 * 
	 * @return
	 */
	public final AbsoluteDate getEventStart() {
		return this.eventStart;
	}

	/**
	 * 
	 * @return
	 */
	public final AbsoluteDate getEventEnd() {
		return this.eventEnd;
	}

	public static Comparator<NetSatThrustEvent> comparator = new Comparator<NetSatThrustEvent>() {

		@Override
		public int compare(NetSatThrustEvent o1, NetSatThrustEvent o2) {
			return (o1.eventStart.compareTo(o2.eventStart));
		}

	};

	/**
	 * 
	 * @return
	 */
	public final Vector3D getThrustDirection() {
		return this.thrustDirection;
	}
}
