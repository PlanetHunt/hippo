package de.netsat.orekit.matlab;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.errors.OrekitException;
import org.orekit.models.earth.GeoMagneticElements;
import org.orekit.models.earth.GeoMagneticField;
import org.orekit.models.earth.GeoMagneticFieldFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.OrbitType;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.ApsideDetector;
import org.orekit.propagation.events.EventDetector;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.TimeStampedPVCoordinates;

import de.netsat.orekit.matlab.eventhandler.ApsideDetectionHandler;
import de.netsat.orekit.matlab.eventhandler.LatitudeArgumentDetectionHandler;

public class SatelliteSensorCalculator {

	private SpacecraftState state;
	private TimeStampedPVCoordinates tsc;
	private Vector3D pVec;
	private Vector3D vVec;
	private Vector3D sunPos;
	private Vector3D magneticField;
	private AbsoluteDate date;
	private GeoMagneticField model;
	private ConstantValues constants;
	private KeplerianOrbit keplerianOrbit;
	private double[] latitudeArgumentNinetyDetections;
	private SensorDataType[] options;
	private double semiMajorAxis;
	private double eccentricity;
	private double inclination;
	private double argumentOfPerigee;
	private double raan;
	private double trueAnomaly;
	private double[] orbitalElements;
	private double seconds;
	private int minute;
	private int hour;
	private int day;
	private int month;
	private int year;
	private EventCalculator eventCal;
	private double[] latitudeArgumentZeroDetections;
	private double[] apogeeDetections;
	private double[] perigeeDetections;
	private double meanAnomaly;
	private double mass;

	/**
	 * The Constructor method. The order here is important as some are
	 * Requirements for the others.
	 * 
	 * @TODO Make the function work without any order or add dependencies on the
	 *       fly
	 * @param state
	 * @param eventCal
	 * @throws OrekitException
	 */
	public SatelliteSensorCalculator(SpacecraftState state, SensorDataType[] options, EventCalculator eventCal)
			throws OrekitException {
		this.state = state;
		this.options = options;
		this.eventCal = eventCal;
		this.constants = new ConstantValues();
		this.apogeeDetections = null;
		this.perigeeDetections = null;
		this.setTimeStampedPVCoordinates();
		for (SensorDataType s : this.options) {
			this.setSensorDataType(s.name());
		}
	}

	/**
	 * Maps the Setters with the name of the SensorDataType A very simple
	 * Dependency Injection.
	 * 
	 * @throws OrekitException
	 */
	public void setSensorDataType(String name) throws OrekitException {
		switch (name) {
		case "SUN":
			this.setDate();
			this.setSunPosition();
			break;
		case "VELOCITY":
			this.setTimeStampedPVCoordinates();
			this.setVelocityVector();
			break;
		case "POSITION":
			this.setTimeStampedPVCoordinates();
			this.setPostionVector();
			break;
		case "MAGNETIC_FIELD":
			this.setDate();
			this.setYear();
			this.setPostionVector();
			this.setGeoMagneticField();
			this.setMagneticField();
			break;
		case "ORBITAL_ELEMENTS":
			this.setDate();
			this.setKeplerianOrbit();
			this.setSemiMajorAxis();
			this.setInclination();
			this.setEccentrcity();
			this.setArgumentOfPerigee();
			this.setRaan();
			this.setMeanAnomly();
			this.setTrueAnomaly();
			this.setOrbitalElements();
			break;
		case "SMA":
			this.setDate();
			this.setKeplerianOrbit();
			this.setSemiMajorAxis();
			break;
		case "ECC":
			this.setDate();
			this.setKeplerianOrbit();
			this.setEccentrcity();
			break;
		case "INC":
			this.setDate();
			this.setKeplerianOrbit();
			this.setInclination();
			break;
		case "RAA":
			this.setDate();
			this.setKeplerianOrbit();
			this.setRaan();
			break;
		case "ARG":
			this.setDate();
			this.setKeplerianOrbit();
			this.setArgumentOfPerigee();
			break;
		case "TRU":
			this.setDate();
			this.setKeplerianOrbit();
			this.setTrueAnomaly();
			break;
		case "TIMESTAMP":
			this.setDate();
			this.setYear();
			this.setMonth();
			this.setDay();
			this.setHour();
			this.setMinute();
			this.setSeconds();
			break;
		case "PX":
			this.setPostionVector();
			break;
		case "PY":
			this.setPostionVector();
			break;
		case "PZ":
			this.setPostionVector();
			break;
		case "VX":
			this.setVelocityVector();
			break;
		case "VY":
			this.setVelocityVector();
			break;
		case "VZ":
			this.setVelocityVector();
			break;
		case "DETECT_PERIGEE":
			this.setApsideDetections();
			break;
		case "DETECT_APOGEE":
			this.setApsideDetections();
			break;
		case "DETECT_LATARG_ZERO":
			this.setLatitudeArgumentZeroDetections();
			break;
		case "DETECT_LATARG_NINETY":
			this.setLatitudeArgumentNinetyDetections();
			break;
		case "CURRENT_MASS":
			this.setCurrentMass();
			break;
		}
	}

	/**
	 * Sets the mass for that timestep.
	 */
	private void setCurrentMass() {
		this.mass = this.state.getMass();
	}
	
	/**
	 * Returns the mass for that timestep.
	 */
	public double getCurrentMass(){
		return this.mass;
	}

	/**
	 * Sets the Latitude Argument crossing with 90 degrees event detections in
	 * placeholder.
	 * 
	 * @throws OrekitException
	 */
	private void setLatitudeArgumentNinetyDetections() throws OrekitException {
		LatitudeArgumentDetector ninetyDet = this.eventCal.getLatArg(90);
		LatitudeArgumentDetectionHandler ninetyHandler = (LatitudeArgumentDetectionHandler) ninetyDet.getHandler();
		if (ninetyHandler.getLatitudeArgumentEventDate() != null) {
			this.latitudeArgumentNinetyDetections = this
					.getGenericTimeStampAsArray(ninetyHandler.getLatitudeArgumentEventDate());
			ninetyHandler.setLatitudeArgumentEventDate(null);
		} else {
			this.latitudeArgumentNinetyDetections = new double[6];
		}

	}

	/**
	 * Returns the Latitude Argument crossing with 90 degrees event detections
	 * for Matlab,
	 * 
	 * @return {@link Double} array
	 */
	public double[] getLatitudeArgumentNinetyDetections() {
		return this.latitudeArgumentNinetyDetections;
	}

	/**
	 * Sets the Latitude Argument crossing with zeros degrees detections event
	 * in placeholder.
	 */
	private void setLatitudeArgumentZeroDetections() {
		LatitudeArgumentDetector zeroDet = this.eventCal.getLatArg(0);
		LatitudeArgumentDetectionHandler zeroHandler = (LatitudeArgumentDetectionHandler) zeroDet.getHandler();
		if (zeroHandler.getLatitudeArgumentEventDate() != null) {
			this.latitudeArgumentZeroDetections = this
					.getGenericTimeStampAsArray(zeroHandler.getLatitudeArgumentEventDate());
			zeroHandler.setLatitudeArgumentEventDate(null);
		} else {
			this.latitudeArgumentZeroDetections = new double[6];
		}

	}

	/**
	 * Returns the Latitude Argument crossing detections with 0 degrees event
	 * for Matlab.
	 * 
	 * @return {@link Double} array
	 */
	public double[] getLatitudeArgumentZeroDetections() {
		return this.latitudeArgumentZeroDetections;
	}

	/**
	 * Sets the date and the radius of the apogee and perigee in the time of
	 * detection. The first six index of the array are datetime the last one is
	 * the Radius in m.
	 */
	private void setApsideDetections() {
		ApsideDetector apogeeDet = this.eventCal.getApogeeEventDetector();
		ApsideDetectionHandler apogeeHandler = (ApsideDetectionHandler) apogeeDet.getHandler();
		if (apogeeHandler.getApsideCrossingDetectionDate() != null) {
			double[] results = new double[7];
			double[] datetime = this.getGenericTimeStampAsArray(apogeeHandler.getApsideCrossingDetectionDate());
			for (int i = 0; i < datetime.length; i++) {
				results[i] = datetime[i];
			}
			results[6] = apogeeHandler.getRadius();
			if (apogeeHandler.getIsApogee()) {
				this.apogeeDetections = results;
			} else {
				this.perigeeDetections = results;
			}
			apogeeHandler.setRadius(0);
			apogeeHandler.setApsideCrossingDetectionDate(null);
		} else {
			if (apogeeDetections == null) {
				this.apogeeDetections = new double[7];
			}
			if (perigeeDetections == null) {
				this.perigeeDetections = new double[7];
			}
		}

	}

	/**
	 * Returns the date and the radius of the apogee in the time of detection.
	 * The first six index of the array are datetime the last one is the Radius
	 * in m.
	 * 
	 * @return {@link Double} array
	 */
	public double[] getPerigeeDetetions() {
		return this.perigeeDetections;
	}

	/**
	 * Returns the date and the radius of the perigee in the time of detection.
	 * The first six index of the array are datetime the last one is the Radius
	 * in m.
	 * 
	 * @return {@link Double} array
	 */
	public double[] getApogeeDetections() {
		return this.apogeeDetections;
	}

	/**
	 * Set the seconds.
	 */
	private void setSeconds() {
		this.seconds = this.getDate().getComponents(this.constants.getTimeScale()).getTime().getSecond();

	}

	/**
	 * Set the minutes
	 */
	private void setMinute() {
		this.minute = getDate().getComponents(this.constants.getTimeScale()).getTime().getMinute();

	}

	/**
	 * Set the hour
	 */
	private void setHour() {
		this.hour = this.getDate().getComponents(this.constants.getTimeScale()).getTime().getHour();

	}

	/**
	 * Set the day
	 */
	private void setDay() {
		this.day = this.getDate().getComponents(this.constants.getTimeScale()).getDate().getDay();

	}

	/**
	 * Set the Month
	 */
	private void setMonth() {
		this.month = this.getDate().getComponents(this.constants.getTimeScale()).getDate().getMonth();

	}

	/**
	 * Set the Year
	 */
	private void setYear() {
		this.year = this.getDate().getComponents(this.constants.getTimeScale()).getDate().getYear();

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
	 * Returns the Position vector as a Vector3D object.
	 * 
	 * @return {@link Vector3D}
	 */
	public Vector3D getPositionVector() {
		return this.pVec;
	}

	/**
	 * Returns the position vector as an array of three doubles.
	 * 
	 * @return {@link Double}
	 */
	public double[] getPositionVectorAsArray() {
		return this.pVec.toArray();
	}

	/**
	 * Returns the velocity vector as Vector3D
	 * 
	 * @return {@link Vector3D}
	 */
	public Vector3D getVelocityVector() {
		return this.vVec;
	}

	/**
	 * Returns the velocity vector as an array of three doubles.
	 * 
	 * @return {@link Double}
	 */
	public double[] getVelocityVectorAsArray() {
		return this.vVec.toArray();
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
	 * Gets the year from the date which came from the state of the satellite.
	 * 
	 * @return {@link Integer}
	 */
	public int getYear() {
		return this.year;
	}

	/**
	 * Get the month
	 */
	public int getMonth() {
		return this.month;
	}

	/**
	 * get the day
	 * 
	 * @return
	 */
	public int getDay() {
		return this.day;
	}

	/**
	 * get the hour
	 * 
	 * @return
	 */
	public int getHour() {
		return this.hour;
	}

	/**
	 * get the minutes
	 * 
	 * @return
	 */
	public int getMinute() {
		return this.minute;
	}

	/**
	 * get the Seconds
	 * 
	 * @return
	 */
	public double getSeconds() {
		return this.seconds;
	}

	/**
	 * Returns the timestamp as an array of [y,m,d,h,m,s] all as doubles
	 * 
	 * @return
	 */
	public double[] getTimeStampAsArray() {
		double[] dateArray = new double[6];
		dateArray[0] = (double) this.getYear();
		dateArray[1] = (double) this.getMonth();
		dateArray[2] = (double) this.getDay();
		dateArray[3] = (double) this.getHour();
		dateArray[4] = (double) this.getMinute();
		dateArray[5] = this.getSeconds();
		return dateArray;
	}

	/**
	 * Converts the AbsoluteDate date to date Array for Matlab.
	 * 
	 * @param date
	 * @return
	 */
	public double[] getGenericTimeStampAsArray(AbsoluteDate date) {
		double[] dateArray = new double[6];
		dateArray[0] = (double) date.getComponents(this.constants.getTimeScale()).getDate().getYear();
		dateArray[1] = (double) date.getComponents(this.constants.getTimeScale()).getDate().getMonth();
		dateArray[2] = (double) date.getComponents(this.constants.getTimeScale()).getDate().getDay();
		dateArray[3] = (double) date.getComponents(this.constants.getTimeScale()).getTime().getHour();
		dateArray[4] = (double) date.getComponents(this.constants.getTimeScale()).getTime().getMinute();
		dateArray[5] = (double) date.getComponents(this.constants.getTimeScale()).getTime().getSecond();
		return dateArray;
	}

	/**
	 * Get the seconds from the Absolute date of 2010,1,1,00:00:00
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
		EventDetector eclipseDetector = this.eventCal.getEclipseEventDetecor();
		if (eclipseDetector.g(this.state) > 0) {
			TimeStampedPVCoordinates sunPos = CelestialBodyFactory.getSun().getPVCoordinates(this.getDate(),
					this.constants.getITRF());
			this.sunPos = sunPos.getPosition();

		} else {
			this.sunPos = new Vector3D(0, 0, 0);
		}
	}

	/**
	 * Returns the Sun position as a Vector3D.
	 * 
	 * @return {@link Vector3D}
	 */
	public Vector3D getSunPosition() {
		return this.sunPos;
	}

	/**
	 * Returns the Sun position as a an array of three doubles. 1:x, 2:y, 3:z
	 * 
	 * @return
	 * 
	 */
	public double[] getSunPositionAsArray() {
		return this.sunPos.toArray();
	}

	/**
	 * Set the GeoMagnetic Field Model.
	 * 
	 * @throws OrekitException
	 */
	public void setGeoMagneticField() throws OrekitException {
		this.model = GeoMagneticFieldFactory.getWMM(this.getYear());
	}

	/**
	 * Gets the Geomagnetic field model.
	 * 
	 * @return {@link GeoMagneticField}
	 */
	public GeoMagneticField getGeoMagnitcField() {
		return this.model;
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
	public void setMagneticField() throws OrekitException {
		GeodeticPoint geop = this.getLLA();
		/*
		 * The altitude which is delivered by the getLLA function is in m it
		 * should be converted to KM.
		 */
		double altitude = geop.getAltitude() / 1000;
		double latitude = geop.getLatitude();
		double longtitude = geop.getLongitude();
		GeoMagneticElements geome = this.getGeoMagnitcField().calculateField(Math.toDegrees(latitude),
				Math.toDegrees(longtitude), altitude);
		this.magneticField = geome.getFieldVector();
	}

	/**
	 * Returns the magnetic field as a Vector3D.
	 * 
	 * @return {@link Vector3D}
	 */
	public Vector3D getMagneticField() {
		return this.magneticField;
	}

	/**
	 * Returns the magnetic field as an array of three doubles in nT.
	 * 
	 * @return {@link Double}
	 */
	public double[] getMagneticFieldAsArray() {
		return this.magneticField.toArray();
	}

	/**
	 * sets the keplerianOrbit parameter for the satellite
	 */
	public void setKeplerianOrbit() {
		OrbitType ortype = OrbitType.KEPLERIAN;
		this.keplerianOrbit = (KeplerianOrbit) ortype.convertType(this.state.getOrbit());
	}

	/**
	 * * get Keplerian orbit
	 * 
	 * @return
	 */
	public KeplerianOrbit getKeplerianOrbit() {
		return this.keplerianOrbit;
	}

	/**
	 * Returns the Orbital elements as an array of 6 elements.
	 * sma,ecc,inc,arg,raa,tru,mea <= 0, 1, 2, 3, 4, 5, 6
	 * 
	 * @return {@link Double}
	 */
	public double[] getOrbitalElements() {
		return this.orbitalElements;
	}

	/**
	 * Sets the orbital Element Array with the help of existing set Orbital
	 * element in the SensorCalc.
	 */
	public void setOrbitalElements() {
		this.orbitalElements = new double[7];
		this.orbitalElements[0] = this.getSemiMajorAxis();
		this.orbitalElements[1] = this.getEccentricity();
		this.orbitalElements[2] = this.getInclination();
		this.orbitalElements[3] = this.getArgumentOfPerigee();
		this.orbitalElements[4] = this.getRaan();
		this.orbitalElements[6] = this.getTrueAnomaly();
		this.orbitalElements[5] = this.getMeanAnomaly();
	}

	/**
	 * Returns the arguments of Perigee in Rads.
	 * 
	 * @return {@link Double}
	 */
	public double getArgumentOfPerigee() {
		return this.argumentOfPerigee;
	}

	/**
	 * Returns the Raan in Rads.
	 * 
	 * @return {@link Double}
	 */
	public double getRaan() {
		return this.raan;
	}

	/**
	 * Returns the true anomaly in Rads.
	 * 
	 * @return {@link Double}
	 */
	public double getTrueAnomaly() {
		return this.trueAnomaly;
	}

	/**
	 * Returns the inclination in Rads.
	 * 
	 * @return {@link Double}
	 */
	public double getInclination() {
		return this.inclination;
	}

	/**
	 * Set the semi Major Axis.
	 */
	public void setSemiMajorAxis() {
		this.semiMajorAxis = this.getKeplerianOrbit().getA();
	}

	/**
	 * Returns the Semi-Major Axis in m.
	 * 
	 * @return {@link Double}
	 */
	public double getSemiMajorAxis() {
		return this.semiMajorAxis;
	}

	/**
	 * Sets the eccentricity for the step.
	 */
	public void setEccentrcity() {
		this.eccentricity = this.getKeplerianOrbit().getE();

	}

	/**
	 * Returns the eccentricity for the given step.
	 * 
	 * @return
	 */
	public double getEccentricity() {
		return this.eccentricity;
	}

	/**
	 * Sets the inclination for the given step. (in Rads)
	 */
	public void setInclination() {
		this.inclination = this.getKeplerianOrbit().getI();

	}

	/**
	 * Sets the Argument of Perigee for the given step. (in Rads)
	 */
	public void setArgumentOfPerigee() {
		this.argumentOfPerigee = this.getKeplerianOrbit().getPerigeeArgument();
	}

	/**
	 * Sets the Raan for the given step. (in Rads)
	 */
	public void setRaan() {
		this.raan = this.getKeplerianOrbit().getRightAscensionOfAscendingNode();
	}

	/**
	 * Sets the True Anomaly for the step. (in Rads)
	 */
	public void setTrueAnomaly() {
		this.trueAnomaly = this.getKeplerianOrbit().getTrueAnomaly();
	}

	/**
	 * Sets the Mean Anomaly for the given steps (in Rads)
	 */
	public void setMeanAnomly() {
		this.meanAnomaly = this.getKeplerianOrbit().getMeanAnomaly();
	}

	/**
	 * Returns the mean anomaly for the given step. (in Rads)
	 * 
	 * @return {@link Double}
	 */
	public double getMeanAnomaly() {
		return this.meanAnomaly;
	}

	/**
	 * Get the X axis value of the Position Vector
	 * 
	 * @return {@link Double}
	 */
	public double getPx() {
		return this.getPositionVector().getX();
	}

}
