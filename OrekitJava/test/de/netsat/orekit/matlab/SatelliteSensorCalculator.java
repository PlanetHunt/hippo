package de.netsat.orekit.matlab;

import java.util.Vector;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.errors.OrekitException;
import org.orekit.models.earth.GeoMagneticElements;
import org.orekit.models.earth.GeoMagneticField;
import org.orekit.models.earth.GeoMagneticFieldFactory;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.EventDetector;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.TimeStampedPVCoordinates;

public class SatelliteSensorCalculator {

	private SpacecraftState state;
	private TimeStampedPVCoordinates tsc;
	private Vector3D pVec;
	private Vector3D vVec;
	private Vector3D sunPos;
	private AbsoluteDate date;
	private GeoMagneticField model;
	private ConstantValues constants;
	private EventCalculator evecalc;

	/**
	 * The Constructor method.
	 * 
	 * @param state
	 * @throws OrekitException
	 */
	public SatelliteSensorCalculator(SpacecraftState state) throws OrekitException {
		this.state = state;
		this.constants = new ConstantValues();
		this.setTimeStampedPVCoordinates();
		this.setPostionVector();
		this.setVelocityVector();
		this.setDate();
		this.evecalc = new EventCalculator();
		this.setSunPosition();
		this.setGeoMagneticField();
	}

	/**
	 * Sets the timestampCoordinates with the help of spacecraft state.
	 * 
	 * @param state
	 * @throws OrekitException
	 */
	public void setTimeStampedPVCoordinates() throws OrekitException {
		this.tsc = this.state.getPVCoordinates(this.constants.getITRF());
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
	public GeodeticPoint getLLA() throws OrekitException {
		return this.constants.getBodyEllipsoid().transform(this.getPositionVector(), this.constants.getITRF(),
				this.getDate());
	}

	/**
	 * Calculates the magnetic field in a given ECI points.
	 *
	 * @return
	 * @throws OrekitException
	 */
	public double[] calculateMagenticField() throws OrekitException {
		GeodeticPoint geop = getLLA();
		// The altitude which is delivered by the getLLA function is in m it
		// should be converted to KM.
		double altitude = geop.getAltitude() / 1000;
		double latitude = geop.getLatitude();
		double longtitude = geop.getLongitude();
		GeoMagneticElements geome = this.getGeoMagnitcField().calculateField(Math.toDegrees(latitude),
				Math.toDegrees(longtitude), altitude);
		return geome.getFieldVector().toArray();
	}

	/**
	 * Set the timestamp from the TimestampPVCoordinates
	 */
	public void setDate() {
		this.date = this.tsc.getDate();
	}

	/**
	 * Get the Absolute date.
	 * 
	 * @return Absolute
	 */
	public AbsoluteDate getDate() {
		return this.date;
	}

	/**
	 * Get the Julian Date
	 * 
	 * @return date
	 */
	public double getSecondsfromJ() {
		AbsoluteDate start = new AbsoluteDate(2010, 1, 1, 0, 0, 0.00, this.constants.getTimeScale());
		System.out.println(start.toString());
		return this.getDate().offsetFrom(start, this.constants.getTimeScale());
	}

	/**
	 * Sets the sun position. It considers the eclipse events when doing the
	 * calculation.
	 * 
	 * @throws OrekitException
	 */
	public void setSunPosition() throws OrekitException {
		EventDetector eclipseDetector = this.evecalc.getEclipseEventDetecor();
		if (eclipseDetector.g(this.state) > 0) {
			TimeStampedPVCoordinates sunPos = CelestialBodyFactory.getSun().getPVCoordinates(this.getDate(),
					this.constants.getITRF());
			this.sunPos = sunPos.getPosition();

		} else {
			this.sunPos = new Vector3D(0, 0, 0);
			System.out.println(this.sunPos.toString());
		}
	}

	/**
	 * Gets the sun position.
	 * 
	 * @return
	 * @return {@link Vector3D}
	 */
	public double[] getSunPosition() {
		return this.sunPos.toArray();
	}

	/**
	 * Set the Geo-Magnetic Field Model.
	 * 
	 * @throws OrekitException
	 */
	public void setGeoMagneticField() throws OrekitException {
		this.model = GeoMagneticFieldFactory.getWMM(this.getYear());
	}

	/**
	 * Gets the geo-magnetic field model.
	 * 
	 * @return {@link GeoMagneticField}
	 */
	public GeoMagneticField getGeoMagnitcField() {
		return this.model;
	}

	/**
	 * Gets the year from the date which came from the state of the satellite.
	 * 
	 * @return {@link Integer}
	 */
	public int getYear() {
		return this.getDate().getComponents(this.constants.getTimeScale()).getDate().getYear();
	}

	/**
	 * Get the month
	 */
	public int getMonth() {
		return this.getDate().getComponents(this.constants.getTimeScale()).getDate().getMonth();
	}

	/**
	 * get the day
	 * 
	 * @return
	 */
	public int getDay() {
		return this.getDate().getComponents(this.constants.getTimeScale()).getDate().getDay();
	}

	/**
	 * get the hour
	 * 
	 * @return
	 */
	public int getHour() {
		return this.getDate().getComponents(this.constants.getTimeScale()).getTime().getHour();
	}

	/**
	 * get the minutes
	 * 
	 * @return
	 */
	public int getMinute() {
		return this.getDate().getComponents(this.constants.getTimeScale()).getTime().getMinute();
	}

	/**
	 * get the Seconds
	 * 
	 * @return
	 */
	public double getSeconds() {
		return this.getDate().getComponents(this.constants.getTimeScale()).getTime().getSecond();
	}

	/**
	 * 
	 * @return
	 */
	public double[] getGeorgianDateAsArray() {
		double[] dateArray = new double[6];
		dateArray[0] = (double) this.getYear();
		dateArray[1] = (double) this.getMonth();
		dateArray[2] = (double) this.getDay();
		dateArray[3] = (double) this.getHour();
		dateArray[4] = (double) this.getMinute();
		dateArray[5] = this.getSeconds();
		return dateArray;
	}

}
