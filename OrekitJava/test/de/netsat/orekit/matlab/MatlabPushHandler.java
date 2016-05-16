package de.netsat.orekit.matlab;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.orekit.errors.OrekitException;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.DateDetector;
import org.orekit.time.AbsoluteDate;

import matlabcontrol.MatlabInvocationException;

public abstract class MatlabPushHandler {

	protected final MatlabInterface mi;
	protected final SensorDataType[] options;
	protected final MatlabFunctionType[] matlabFunctions;
	protected final boolean atOnce;
	protected final EventCalculator eventCal;
	protected final PropulsionSystem propulsionSystem;
	protected HashSet<MatlabData> dataList;
	protected SatelliteSensorCalculator spc;
	protected Method method;
	protected final ConstantValues constants;
	protected boolean debug;

	public MatlabPushHandler(final MatlabInterface mi, final SensorDataType[] options,
			final MatlabFunctionType[] matlabFunctions, final boolean atOnce, final PropulsionSystem propulsionSystem,
			final EventCalculator eventCal, final ConstantValues constants) {
		this.mi = mi;
		this.options = options;
		this.matlabFunctions = matlabFunctions;
		this.atOnce = atOnce;
		this.eventCal = eventCal;
		this.propulsionSystem = propulsionSystem;
		this.dataList = new HashSet<MatlabData>();
		this.constants = constants;
		this.debug = true;
	}

	/**
	 * Evaluate the function name given and run it to set the desired values. It
	 * uses reflect and invoke options.
	 * 
	 * @param functionName
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public Object evaluateFunction(String functionName) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		this.method = this.spc.getClass().getMethod(functionName);
		return this.method.invoke(this.spc);
	}

	/**
	 * The objects that have been chosen to be synchronized with Matlab are
	 * handled here.
	 * 
	 * @param state
	 * @throws OrekitException
	 * @throws MatlabInvocationException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public void evaluateOptions(SpacecraftState state)
			throws OrekitException, MatlabInvocationException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		try {
			this.spc = new SatelliteSensorCalculator(state, this.options, this.eventCal);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.spc.setTimeStampedPVCoordinates();
		for (SensorDataType s : this.options) {
			MatlabData mld = null;
			if (this.atOnce) {
				mld = this.findDataInDataList(s.getNameInMatlab());
				if (mld != null) {
					mld.setValue(this.evaluateFunction(s.getFunctionName()));
				} else {
					mld = new MatlabData(s.getNameInMatlab(), s.getType(), this.atOnce);
					mld.setValue(this.evaluateFunction(s.getFunctionName()));
					this.addToDataList(mld);

				}
			} else {
				mld = new MatlabData(s.getNameInMatlab(), s.getType(), this.atOnce);
				mld.setValue(this.evaluateFunction(s.getFunctionName()));
				this.addToDataList(mld);
			}
		}
		if (!this.atOnce) {
			this.PushAllDataToMatlab();
			this.dataList.clear();

			/* Applying Matlab Results */
			ArrayList<NetSatThrustEvent> thrustEvents = new ArrayList<NetSatThrustEvent>();
			for (MatlabFunctionType ft : this.matlabFunctions) {
				if (!ft.getAtOnce()) {
					if (ft == MatlabFunctionType.MATLAB_STEP_HANDLER) {
						Object[] result = this.runMatlabFunction(ft.getFunctionName(), 1);
						thrustEvents = this.getThrustEventsFromMatlab(result);
						this.applyThrusts(thrustEvents);

					} else {
						System.out.println(this.runMatlabFunction(ft.getFunctionName(), 1));
					}
				}

			}
		}

	}

	/**
	 * Applies the thrust on the Propulsion System
	 * 
	 * @param thrustEvents
	 */
	public void applyThrusts(final ArrayList<NetSatThrustEvent> thrustEvents) {
		for (int i = 0; i < thrustEvents.size(); i++) {
			NetSatThrustEvent te = thrustEvents.get(i);
			this.propulsionSystem.setDirection(te.getThrustDirection());
			((DateDetector) this.propulsionSystem.getEventsDetectors()[0]).addEventDate(te.getEventStart());
			((DateDetector) this.propulsionSystem.getEventsDetectors()[1]).addEventDate(te.getEventEnd());
			if (this.debug) {
				System.out.println(
						"Flag=1 (setting thrustStartWindow = " + te.getEventStart().toString() + " thrustEndWindow = "
								+ te.getEventEnd().toString() + " Direction = " + te.getThrustDirection().toString());
			}
		}
	}

	/**
	 * Returns the sorted thrust events from Matlab. The parameters Matlab
	 * return are in 16 numbers batch.
	 * 
	 * S->StartDate, E->EndDAte, F->Fire F X Y Z SY SM SD SH Sm Ss EY EM ED EH
	 * Em Es 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15
	 * 
	 * @param results
	 * @return {@link NetSatThrustEvent}
	 */
	public ArrayList<NetSatThrustEvent> getThrustEventsFromMatlab(final Object[] results) {
		ArrayList<NetSatThrustEvent> thrustEvents = new ArrayList<NetSatThrustEvent>();
		final double[] result = (double[]) results[0];
		for (int i = 0; i < result.length; i = i + 16) {
			/* If Fire flag is set. */
			if (result[i] == 1.0) {
				AbsoluteDate startDate = new AbsoluteDate((int) (result[i + 4]), (int) (result[i + 5]),
						(int) (result[i + 6]), (int) (result[i + 7]), (int) (result[i + 8]), (result[i + 9]),
						this.constants.getTimeScale());

				AbsoluteDate endDate = new AbsoluteDate((int) (result[i + 10]), (int) (result[i + 11]),
						(int) (result[i + 12]), (int) (result[i + 13]), (int) (result[i + 14]), (result[i + 15]),
						this.constants.getTimeScale());

				thrustEvents.add(new NetSatThrustEvent(startDate, endDate,
						new Vector3D(result[i + 1], result[i + 2], result[i + 3])));

			}
		}
		Collections.sort(thrustEvents, NetSatThrustEvent.comparator);
		return thrustEvents;
	}

	/**
	 * Finds the data in the dataList
	 * 
	 * @param name
	 * @return
	 */
	public MatlabData findDataInDataList(String name) {
		for (MatlabData d : this.dataList) {
			if (d.getName().equals(name)) {
				return d;
			}
		}
		return null;
	}

	/**
	 * Add Data List
	 * 
	 * @param mld
	 */
	public void addToDataList(MatlabData mld) {
		MatlabData d = this.findDataInDataList(mld.getName());
		if (d != null) {
			this.dataList.remove(d);
			d.setValue(mld.getValue());
			this.dataList.add(d);
		} else {
			this.dataList.add(mld);
		}
	}

	/**
	 * Set the variable in the Matlab
	 * 
	 * @param name
	 * @param value
	 * @throws MatlabInvocationException
	 */
	public void setVariableInMatlab(String name, Object value) throws MatlabInvocationException {
		this.mi.getProxy().setVariable(name, value);
	}

	/**
	 * Run a Matlab Function
	 * 
	 * @param name
	 * @return Object: should be casted.
	 * @throws MatlabInvocationException
	 */
	public void runMatlabFunction(String name) throws MatlabInvocationException {
		this.runMatlabFunction(name, 1);
	}

	/**
	 * Runs the matlab function complete
	 * 
	 * @param name
	 * @param params
	 * @return
	 * @throws MatlabInvocationException
	 */
	public Object[] runMatlabFunction(String name, int params) throws MatlabInvocationException {
		return mi.getProxy().returningEval(name, params);
	}

	/**
	 * Push the data list in Matlab.
	 * 
	 * @throws MatlabInvocationException
	 */
	public void PushAllDataToMatlab() throws MatlabInvocationException {
		for (MatlabData d : this.dataList) {
			if (!this.atOnce) {
				this.setVariableInMatlab(d.getName(), d.getValue());
			} else {
				this.setVariableInMatlab(d.getName(), d.getValueList());
			}
		}
	}

}
