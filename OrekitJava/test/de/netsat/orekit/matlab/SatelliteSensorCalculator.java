package de.netsat.orekit.matlab;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.models.earth.GeoMagneticElements;
import org.orekit.models.earth.GeoMagneticField;
import org.orekit.propagation.SpacecraftState;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.IERSConventions;
import org.orekit.utils.TimeStampedPVCoordinates;

public class SatelliteSensorCalculator {

	private SpacecraftState state;
	private TimeStampedPVCoordinates tsc;
	private Vector3D pVec;
	private Vector3D vVec;
	private Vector3D sunPos;
	private Frame itrf;
	private AbsoluteDate date;

	/**
	 * The Constructor method.
	 * 
	 * @param state
	 * @throws OrekitException
	 */
	public SatelliteSensorCalculator(SpacecraftState state) throws OrekitException {
		this.state = state;
		this.setTimeStampedPVCoordinates();
		this.setDate();
		this.setITRF();
		this.setSunPosition();
	}

	/**
	 * Sets the timestampCoordinates with the help of spacecraft state.
	 * 
	 * @param state
	 * @throws OrekitException
	 */
	public void setTimeStampedPVCoordinates() throws OrekitException {
		this.tsc = this.state.getPVCoordinates(FramesFactory.getITRF(IERSConventions.IERS_2010, true));
	}

	/**
	 * Returns the timeStampedPVCoordinated calculated before.
	 * 
	 * @return tsc
	 * @throws OrekitException
	 */
	public TimeStampedPVCoordinates getTimeStampedPVCoordinates() throws OrekitException {
		return this.tsc;
	}

	/**
	 * Sets the Velocity vector for the given step.
	 * 
	 * @param tsc
	 * 
	 */
	public void setVelocityVector() {
		this.vVec = this.tsc.getVelocity();
	}

	/**
	 * Sets the position vector for the given step.
	 * 
	 * @param tsc
	 */
	public void setPostionVector() {
		this.pVec = this.tsc.getPosition();
	}

	/**
	 * 
	 * @return pVec position vector.
	 */
	public Vector3D getPositionVector() {
		return this.pVec;
	}

	/**
	 * 
	 * @return vVect Velocity vector.
	 */
	public Vector3D getVelocityVector() {
		return this.vVec;
	}

	/**
	 * Converts the ECI Coordinates (R;V) to Latitude, Longitude, Altitude
	 * (L;L;A). The point it uses the ITRF (Inertial Terrestrial reference frame
	 * for the frame.
	 * 
	 * @param ECICoordinates
	 * @param oae
	 * @param date
	 * @return
	 * @throws OrekitException
	 */
	public static GeodeticPoint getLLA(Vector3D ECICoordinates, OneAxisEllipsoid oae, AbsoluteDate date)
			throws OrekitException {
		return oae.transform(ECICoordinates, FramesFactory.getITRF(IERSConventions.IERS_2010, true), date);
	}

	/**
	 * Calculates the magnetic field in a given ECI points.
	 * 
	 * @param ECICoordinates
	 * @param oae
	 * @param date
	 * @param model
	 * @return
	 * @throws OrekitException
	 */
	public Vector3D calculateMagenticField(OneAxisEllipsoid oae, AbsoluteDate date,
			GeoMagneticField model) throws OrekitException {
		GeodeticPoint geop = getLLA(this.getPositionVector() , oae, date);
		// The altitude which is delivered by the getLLA function is in m it
		// should be converted to KM.
		double altitude = geop.getAltitude() / 1000;
		double latitude = geop.getLatitude();
		double longtitude = geop.getLongitude();
		GeoMagneticElements geome = model.calculateField(Math.toDegrees(latitude), Math.toDegrees(longtitude),
				altitude);
		return geome.getFieldVector();
	}

	/**
	 * Set the timestamp from the TimestampPVCoordinates
	 */
	public void setDate() {
		this.date = this.tsc.getDate();
	}

	/**
	 * Get the Absoulte date.
	 * @return Absolute
	 */
	public AbsoluteDate getDate() {
		return this.date;
	}

	/**
	 * Set the Frame Type.
	 * @throws OrekitException
	 */
	public void setITRF() throws OrekitException {
		this.itrf = FramesFactory.getITRF(IERSConventions.IERS_2010, true);
	}

	/**
	 * Get the frame Type
	 * @return
	 */
	public Frame getITRF() {
		return this.itrf;
	}

	/**
	 * Sets the sun position.
	 * @throws OrekitException
	 */
	public void setSunPosition() throws OrekitException {
		TimeStampedPVCoordinates sunPos = CelestialBodyFactory.getSun().getPVCoordinates(this.getDate(), this.getITRF());
		this.sunPos = sunPos.getPosition();
	}

}
