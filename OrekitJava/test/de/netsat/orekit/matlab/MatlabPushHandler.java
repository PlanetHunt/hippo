package de.netsat.orekit.matlab;

import de.netsat.orekit.actuator.Tools;
import de.netsat.orekit.matlab.SatelliteSensorCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.orekit.errors.OrekitException;
import org.orekit.errors.PropagationException;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.sampling.OrekitFixedStepHandler;
import org.orekit.time.AbsoluteDate;

import matlabcontrol.MatlabInvocationException;

public class MatlabPushHandler implements OrekitFixedStepHandler {

	MatlabInterface mi;
	String[] options;

	private Misc misc;
	private SatelliteSensorCalculator spc;
	private ConstantValues constants;
	private boolean atOnce;
	/*
	 * This part is used only when atOnce parameter is active. It is also a good
	 * way to find out how the parameters would look like in Matlab even if they
	 * are not pushed at once.
	 */
	private List<double[]> timeList;
	private List<double[]> magneticFieldList;
	private List<double[]> sunPositionList;
	private List<double[]> positionList;
	private List<double[]> velocityList;
	private List<Double> semiMajorAxisList;
	private List<Double> eccentricityList;
	private List<Double> inclinationList;
	private List<Double> argumentOfPerigeeList;
	private List<Double> raanList;
	private List<Double> trueAnomalyList;
	private Map<String, List<?>> listOftheLists;

	/**
	 * The constructor which needs atOnce set. It is the one that should be used
	 * If the user wants to have all the propagated data sent to Matlab at once.
	 * 
	 * @param mi
	 * @param options
	 * @param atOnce
	 */
	public MatlabPushHandler(MatlabInterface mi, String[] options, boolean atOnce) {
		this.mi = mi;
		this.options = options;
		this.atOnce = atOnce;
		this.misc = new Misc();
		/* Initiate the list only if needed. */
		if (this.atOnce) {
			timeList = new ArrayList<double[]>();
			magneticFieldList = new ArrayList<double[]>();
			sunPositionList = new ArrayList<double[]>();
			positionList = new ArrayList<double[]>();
			velocityList = new ArrayList<double[]>();
			semiMajorAxisList = new ArrayList<Double>();
			eccentricityList = new ArrayList<Double>();
			inclinationList = new ArrayList<Double>();
			argumentOfPerigeeList = new ArrayList<Double>();
			raanList = new ArrayList<Double>();
			trueAnomalyList = new ArrayList<Double>();
			for (String o : this.options) {
				switch (o) {
				case "timestamp":
					listOftheLists.put(o, timeList);
					break;

				case "magnetic_field":
					listOftheLists.put(o, magneticFieldList);
					break;
				}
			}
		}
	}

	/**
	 * The constructor with atOnce parameters not set.
	 * 
	 * @param mi
	 * @param options
	 */
	public MatlabPushHandler(MatlabInterface mi, String[] options) {
		this(mi, options, false);
	}

	@Override
	public void handleStep(SpacecraftState currentState, boolean isLast) throws PropagationException {
		// TODO Auto-generated method stub
		try {
			matlabInterfacePushPV(currentState);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
	}

	void matlabInterfacePushPV(SpacecraftState scstate) throws MatlabInvocationException, OrekitException {
		try {
			this.evaluateOptions(scstate);

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
	}

	@Override
	public void init(SpacecraftState s0, AbsoluteDate t) throws PropagationException {
		// TODO Auto-generated method stub

	}

	public void evaluateOptions(SpacecraftState state) throws OrekitException, MatlabInvocationException {
		this.spc = new SatelliteSensorCalculator(state);
		this.constants = new ConstantValues();
		this.spc.setTimeStampedPVCoordinates();
		for (String opt : this.options) {
			if (opt.equals("mu")) {
				this.setMuInMatlab("mu");
			}
			if (opt.equals("position")) {
				this.spc.setPostionVector();
				this.setPXInMatlab("p_x");
				this.setPYInMatlab("p_y");
				this.setPZInMatlab("p_z");

			}
			if (opt.equals("velocity")) {
				this.spc.setVelocityVector();
				this.setVXInMatlab("v_x");
				this.setVYInMatlab("v_y");
				this.setVZInMatlab("v_z");

			}
			if (opt.equals("timestamp")) {
				this.setDateInMatlab("timestamp");

			}
			if (opt.equals("magnetic_field")) {
				this.setMagenticFieldInMatlab("magnet_field");
			}
			if (opt.equals("sun")) {
				this.setSunPositionInMatlab("sun_position");
			}
		}
	}

	/**
	 * Sets the values in the list, only usable when atOnce option set.
	 * 
	 * @TODO Exception handling, but not only this one, the whole codebase.
	 * @param value
	 * @param l
	 */
	private <T> void setValueInList(T value, List<T> l) {
		l.add(value);
	}

	/**
	 * Sets the Geomagnetic Field in Matlab
	 * 
	 * @param name
	 * @throws MatlabInvocationException
	 * @throws OrekitException
	 */
	private void setMagenticFieldInMatlab(String name) throws MatlabInvocationException, OrekitException {
		this.setIfNull(name, "mag_field");
		this.setVariableInMatlab(name, this.spc.calculateMagenticField());
	}

	/**
	 * Sets the X Coordinate of the Velocity in Matlab
	 * 
	 * @param name
	 * @throws OrekitException
	 * @throws MatlabInvocationException
	 */
	public void setVXInMatlab(String name) throws OrekitException, MatlabInvocationException {
		this.setIfNull(name, "v_x");
		this.setVariableInMatlab(name, this.spc.getVelocityVector().getX());
	}

	/**
	 * Sets the Y Coordinate of the Velocity in Matlab
	 * 
	 * @param name
	 * @throws OrekitException
	 * @throws MatlabInvocationException
	 */
	public void setVYInMatlab(String name) throws OrekitException, MatlabInvocationException {
		this.setIfNull(name, "v_y");
		this.setVariableInMatlab(name, this.spc.getVelocityVector().getY());
	}

	/**
	 * Sets the Z Coordinate of the Velocity in Matlab
	 * 
	 * @param name
	 * @throws OrekitException
	 * @throws MatlabInvocationException
	 */
	public void setVZInMatlab(String name) throws OrekitException, MatlabInvocationException {
		this.setIfNull(name, "v_z");
		this.setVariableInMatlab(name, this.spc.getVelocityVector().getZ());
	}

	/**
	 * Sets the Z Coordinate of the Position in Matlab
	 * 
	 * @param name
	 * @throws OrekitException
	 * @throws MatlabInvocationException
	 */
	public void setPZInMatlab(String name) throws OrekitException, MatlabInvocationException {
		this.setIfNull(name, "p_z");
		this.setVariableInMatlab(name, this.spc.getPositionVector().getZ());
	}

	/**
	 * Sets the Y Coordinate of the Position in Matlab
	 * 
	 * @param name
	 * @throws OrekitException
	 * @throws MatlabInvocationException
	 */
	public void setPYInMatlab(String name) throws OrekitException, MatlabInvocationException {
		this.setIfNull(name, "p_y");
		this.setVariableInMatlab(name, this.spc.getPositionVector().getY());
	}

	/**
	 * Sets the X Coordinate of the Position in Matlab
	 * 
	 * @param name
	 * @throws OrekitException
	 * @throws MatlabInvocationException
	 */
	public void setPXInMatlab(String name) throws OrekitException, MatlabInvocationException {
		this.setIfNull(name, "p_x");
		this.setVariableInMatlab(name, this.spc.getPositionVector().getX());
	}

	/**
	 * Set the variable in Matlab.
	 * 
	 * @param name
	 * @param value
	 * @param l
	 */
	private <T> void setVariableInMatlab(String name, T value, List<T> l) throws MatlabInvocationException {
		if (this.atOnce) {
			if (l != null) {
				this.setValueInList(value, l);
			} else {
				mi.getProxy().setVariable(name, value);
			}
		} else {
			mi.getProxy().setVariable(name, value);
		}
	}

	/**
	 * Set the variable in the Matlab
	 * 
	 * @param name
	 * @param value
	 * @throws MatlabInvocationException
	 */
	public <T> void setVariableInMatlab(String name, T value) throws MatlabInvocationException {
		List<T> l = null;
		this.setVariableInMatlab(name, value, l);
	}

	/**
	 * Checks if an string is set if not, set it with default value.
	 * 
	 * @param var
	 * @param value
	 * @return
	 */
	public String setIfNull(String var, String value) {
		if (var == null || var.isEmpty()) {
			var = value;
		}
		return var;
	}

	/**
	 * Set the Mu in Matlab
	 * 
	 * @param name
	 * @throws MatlabInvocationException
	 * @throws OrekitException
	 */
	public void setMuInMatlab(String name) throws MatlabInvocationException, OrekitException {
		this.setIfNull(name, "mu");
		this.setVariableInMatlab(name, this.constants.getMu());
	}

	/**
	 * Set the timestamp of the state Matlab
	 * 
	 * @param name
	 * @throws MatlabInvocationException
	 */
	public void setDateInMatlab(String name) throws MatlabInvocationException {
		this.setIfNull(name, "timestamp");
		this.setVariableInMatlab(name, this.spc.getGeorgianDateAsArray());
	}

	/**
	 * Sets the sun position of the state in Matlab
	 * 
	 * @param name
	 * @throws MatlabInvocationException
	 */
	public void setSunPositionInMatlab(String name) throws MatlabInvocationException {
		this.setIfNull(name, "sun_position");
		this.setVariableInMatlab(name, this.spc.getSunPosition());
	}

	/**
	 * Returns the position as double vector.
	 * 
	 * @return double[]
	 * @throws MatlabInvocationException
	 */
	public void setPositionAsVectorInMatlab(String name) throws MatlabInvocationException {
		this.setIfNull(name, "position");
		this.setVariableInMatlab(name, this.spc.getPositionVector().toArray());
	}

	/**
	 * Returns the velocity as double vector.
	 * 
	 * @return double[]
	 */
	public double[] getVelocityAsVector() {
		return this.spc.getVelocityVector().toArray();
	}

}