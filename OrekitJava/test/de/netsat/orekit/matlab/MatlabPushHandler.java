package de.netsat.orekit.matlab;

import de.netsat.orekit.matlab.SatelliteSensorCalculator;
import org.orekit.errors.OrekitException;
import org.orekit.errors.PropagationException;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.sampling.OrekitFixedStepHandler;
import org.orekit.time.AbsoluteDate;

import matlabcontrol.MatlabInvocationException;

public class MatlabPushHandler implements OrekitFixedStepHandler {
	MatlabInterface mi;
	String[] options;
	private SatelliteSensorCalculator spc;
	private ConstantValues constants;

	public MatlabPushHandler(MatlabInterface mi, String[] options) {
		this.mi = mi;
		this.options = options;
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
			// TimeStampedPVCoordinates scCoordinates = scstate
			// .getPVCoordinates(FramesFactory.getITRF(IERSConventions.IERS_2010,
			// true));
			// Vector3D pVec = scCoordinates.getPosition();
			// Vector3D vVec = scCoordinates.getVelocity();
			// AbsoluteDate date = scCoordinates.getDate();
			//
			// mi.getProxy().setVariable("Time",
			// scCoordinates.getDate().toString());
			// mi.getProxy().setVariable("Position_x", pVec.getX());
			// mi.getProxy().setVariable("Position_y", pVec.getY());
			// mi.getProxy().setVariable("Position_z", pVec.getZ());
			// mi.getProxy().setVariable("vel_x",
			// scstate.getPVCoordinates().getVelocity().getX());
			// mi.getProxy().setVariable("vel_y",
			// scstate.getPVCoordinates().getVelocity().getY());
			// mi.getProxy().setVariable("vel_z",
			// scstate.getPVCoordinates().getVelocity().getZ());
			// mi.getProxy().setVariable("period",
			// scstate.getKeplerianPeriod());
			// TimeStampedPVCoordinates sunPos =
			// CelestialBodyFactory.getSun().getPVCoordinates(scCoordinates.getDate(),
			// FramesFactory.getITRF(IERSConventions.IERS_2010, true));
			// mi.getProxy().setVariable("Sun_Position",
			// sunPos.getPosition().toArray());

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
	 * Set the variable in matltab (the variable should be the type double or
	 * could be casted to double.
	 * 
	 * @param mi
	 * @param name
	 * @param value
	 * @throws MatlabInvocationException
	 */
	public void setVariableInMatlab(String name, Object value) throws MatlabInvocationException {
		this.mi.getProxy().setVariable(name, value);
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

	public void setSunPositionInMatlab(String name) throws MatlabInvocationException {
		this.setIfNull(name, "sun_position");
		this.setVariableInMatlab(name, this.spc.getSunPosition());
	}
}