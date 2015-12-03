package de.netsat.orekit.matlab;


import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.orekit.errors.OrekitException;
import org.orekit.errors.PropagationException;
import org.orekit.frames.FramesFactory;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.sampling.OrekitFixedStepHandler;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.IERSConventions;
import org.orekit.utils.TimeStampedPVCoordinates;

import matlabcontrol.MatlabInvocationException;

public class MatlabPushHandler implements OrekitFixedStepHandler {
	MatlabInterface mi;
	String[] options;
	private TimeStampedPVCoordinates tsc;
	private Vector3D pVec;
	private Vector3D vVec;

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

	/**
	 * Sets the timestampCoordinates with the help of spacecraft state.
	 * 
	 * @param state
	 * @throws OrekitException
	 */
	public void setTimeStampedPVCoordinates(SpacecraftState state) throws OrekitException {
		this.tsc = state.getPVCoordinates(FramesFactory.getITRF(IERSConventions.IERS_2010, true));
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
	public void setVelocityVector(TimeStampedPVCoordinates tsc) {
		this.vVec = tsc.getVelocity();
	}

	/**
	 * Sets the position vector for the given step.
	 * 
	 * @param tsc
	 */
	public void setPostionVector(TimeStampedPVCoordinates tsc) {
		this.pVec = tsc.getPosition();
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

	public void evaluateOptions(SpacecraftState state) throws OrekitException, MatlabInvocationException {
		this.setTimeStampedPVCoordinates(state);
		this.setVelocityVector(this.tsc);
		this.setPostionVector(this.tsc);
		for (String opt : this.options) {
			if (opt.equals("position")) {

			}
			if (opt.equals("velocity")) {

				this.setVXInMatlab("v_x");
				this.setVYInMatlab("v_y");
				this.setVZInMatlab("v_z");
			}
			if (opt.equals("time")) {

			}
			if (opt.equals("magnetic_field")) {

			}
			if (opt.equals("sun")) {

			}
		}
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
		this.setVariableInMatlab(name, this.getVelocityVector().getX());
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
		this.setVariableInMatlab(name, this.getVelocityVector().getY());
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
		this.setVariableInMatlab(name, this.getVelocityVector().getZ());
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
		this.setVariableInMatlab(name, this.getPositionVector().getZ());
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
		this.setVariableInMatlab(name, this.getPositionVector().getY());
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
		this.setVariableInMatlab(name, this.getPositionVector().getX());
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

}