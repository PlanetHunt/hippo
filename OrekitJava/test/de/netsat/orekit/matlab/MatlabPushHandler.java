package de.netsat.orekit.matlab;

import de.netsat.orekit.matlab.SatelliteSensorCalculator;

import java.util.HashSet;
import java.util.Set;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.orekit.errors.OrekitException;
import org.orekit.errors.PropagationException;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.DateDetector;
import org.orekit.propagation.sampling.OrekitFixedStepHandler;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;

import matlabcontrol.MatlabInvocationException;

public class MatlabPushHandler implements OrekitFixedStepHandler {

	private MatlabInterface mi;
	private SensorDataType[] options;
	private MatlabFunctionType[] matlabFunctions;
	private SatelliteSensorCalculator spc;
	private boolean atOnce;
	private Set<MatlabData> dataList;
	private EventCalculator eventCal;
	Method method;
	private NetSatThrustEquations thrustEquation;
	private boolean fire;
	private PropulsionSystem prop;

	/**
	 * The constructor which needs atOnce set. It is the one that should be used
	 * If the user wants to have all the propagated data sent to Matlab at once.
	 * 
	 * @param mi
	 * @param options
	 * @param atOnce
	 */
	public MatlabPushHandler(MatlabInterface mi, SensorDataType[] options, MatlabFunctionType[] matlabFunctions,
			boolean atOnce, EventCalculator eventCal, NetSatThrustEquations thrustEquation) {
		this.mi = mi;
		this.options = options;
		this.atOnce = atOnce;
		this.eventCal = eventCal;
		this.matlabFunctions = matlabFunctions;
		this.thrustEquation = thrustEquation;
		this.dataList = new HashSet<MatlabData>();
		this.fire = true;

	}

	public MatlabPushHandler(MatlabInterface mi, SensorDataType[] options, MatlabFunctionType[] matlabFunctions,
			boolean atOnce, PropulsionSystem prop) {
		this.mi = mi;
		this.options = options;
		this.matlabFunctions = matlabFunctions;
		this.atOnce = atOnce;
		this.prop = prop;
	}

	/**
	 * The constructor with atOnce parameters not set.
	 * 
	 * @param mi
	 * @param options
	 */
	public MatlabPushHandler(MatlabInterface mi, SensorDataType[] options, MatlabFunctionType[] matlabFunctions) {
		this(mi, options, matlabFunctions, false, null, null);
	}

	@Override
	public void handleStep(SpacecraftState currentState, boolean isLast) throws PropagationException {
		if (!isLast) {
			try {
				this.setVariableInMatlab("last_step_flag", 0);
				this.evaluateOptions(currentState);
				//System.out.println("Still propagating..." + currentState.getDate());
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println(e.getMessage());
			}
		} else {
			try {
				this.setVariableInMatlab("last_step_flag", 1);
				System.out.println("We are in the Last Step.");
				// this.PushAllDataToMatlab();
				this.evaluateOptions(currentState);
				/* Run the Matlab functions at the end of the propagation */
				for (MatlabFunctionType ft : this.matlabFunctions) {
					if (ft.getAtOnce()) {
						this.runMatlabFunction(ft.getFunctionName());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println(e.getMessage());
			}
		}
	}

	@Override
	public void init(SpacecraftState s0, AbsoluteDate t) throws PropagationException {
		// TODO Auto-generated method stub

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
		this.spc = new SatelliteSensorCalculator(state, this.options, this.eventCal);
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
		}
		double[] thrustDirection = { 0, 0, 0 };
		/* Run the Matlab function at every step. */
		for (MatlabFunctionType ft : this.matlabFunctions) {
			if (!ft.getAtOnce()) {
				if (ft == MatlabFunctionType.MATLAB_STEP_HANDLER) {
					Object[] result = this.runMatlabFunction(ft.getFunctionName(), 4);
					double matlabFire = (((double[]) result[0])[0]);
					if (matlabFire == 1.0) {
						thrustDirection[0] = ((double[]) result[1])[0];
						thrustDirection[1] = ((double[]) result[1])[1];
						thrustDirection[2] = ((double[]) result[1])[2];
						double[] startDateArray = ((double[]) result[2]);
						double[] endDateArray = ((double[]) result[3]);
						this.prop.setDirection(new Vector3D(thrustDirection));
						AbsoluteDate startDate = new AbsoluteDate((int) startDateArray[0], (int) startDateArray[1],
								(int) startDateArray[2], (int) startDateArray[3], (int) startDateArray[4],
								startDateArray[5], TimeScalesFactory.getUTC());
						AbsoluteDate endDate = new AbsoluteDate((int) endDateArray[0], (int) endDateArray[1],
								(int) endDateArray[2], (int) endDateArray[3], (int) endDateArray[4], endDateArray[5],
								TimeScalesFactory.getUTC());
						((DateDetector) this.prop.getEventsDetectors()[0]).addEventDate(startDate);
						((DateDetector) this.prop.getEventsDetectors()[1]).addEventDate(endDate);
						// this.thrustEquation.setFire(this.fire);
						// this.fire = false;

						//this.thrustEquation.setThrustDirection(thrustDirection);
					} else {
						this.thrustEquation.setFire(false);
						//thrustDirection[0] = 0;
						//thrustDirection[1] = 0;
						//thrustDirection[2] = 0;
						//this.thrustEquation.setThrustDirection(thrustDirection);
					}
				} else {
					Object[] a = this.runMatlabFunction(ft.getFunctionName(), 1);
					double c = ((double[]) a[0])[0];
					System.out.println(c);
				}
			}
		}
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