package de.netsat.orekit.matlab;

import de.netsat.orekit.matlab.SatelliteSensorCalculator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import org.orekit.propagation.sampling.OrekitStepHandler;
import org.orekit.propagation.sampling.OrekitStepInterpolator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;

import matlabcontrol.MatlabInvocationException;

public class MatlabAdaptivePushHandler implements OrekitStepHandler {

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
	public MatlabAdaptivePushHandler(MatlabInterface mi, SensorDataType[] options, MatlabFunctionType[] matlabFunctions,
			boolean atOnce, EventCalculator eventCal) {
		this.mi = mi;
		this.options = options;
		this.atOnce = atOnce;
		this.eventCal = eventCal;
		this.matlabFunctions = matlabFunctions;
		this.dataList = new HashSet<MatlabData>();
		this.fire = true;

	}

	public MatlabAdaptivePushHandler(MatlabInterface mi, SensorDataType[] options, MatlabFunctionType[] matlabFunctions,
			boolean atOnce, PropulsionSystem prop, EventCalculator eventCal) {
		this.mi = mi;
		this.options = options;
		this.matlabFunctions = matlabFunctions;
		this.atOnce = atOnce;
		this.prop = prop;
		this.eventCal = eventCal;
		this.dataList = new HashSet<MatlabData>();
	}

	/**
	 * The constructor with atOnce parameters not set.
	 * 
	 * @param mi
	 * @param options
	 */
	public MatlabAdaptivePushHandler(MatlabInterface mi, SensorDataType[] options,
			MatlabFunctionType[] matlabFunctions) {
		this(mi, options, matlabFunctions, false, null, null);
	}

	@Override
	public void handleStep(OrekitStepInterpolator interpolator, boolean isLast) {
		SpacecraftState currentState = null;
		try {
			currentState = interpolator.getInterpolatedState();
		} catch (OrekitException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (!isLast) {
			try {
				this.setVariableInMatlab("last_step_flag", 0);
				this.evaluateOptions(currentState);
				System.out.println("Still propagating..." + currentState.getDate());
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
		ArrayList<NetSatThrustEvent> thrustEvents = new ArrayList<NetSatThrustEvent>();
		/* Run the Matlab function at every step. */
		for (MatlabFunctionType ft : this.matlabFunctions) {
			if (!ft.getAtOnce()) {
				if (ft == MatlabFunctionType.MATLAB_STEP_HANDLER) {
					Object[] result = this.runMatlabFunction(ft.getFunctionName(), 1);
					for (int i = 0; i < ((double[]) result[0]).length; i = i + 16) {
						double matlabFire = (((double[]) result[0])[i]);
						if (matlabFire == 1.0) {
							thrustDirection[0] = ((double[]) result[0])[i + 1];
							thrustDirection[1] = ((double[]) result[0])[i + 2];
							thrustDirection[2] = ((double[]) result[0])[i + 3];
							AbsoluteDate startDate = new AbsoluteDate((int) (((double[]) result[0])[i + 4]),
									(int) (((double[]) result[0])[i + 5]), (int) (((double[]) result[0])[i + 6]),
									(int) (((double[]) result[0])[i + 7]), (int) (((double[]) result[0])[i + 8]),
									(((double[]) result[0])[i + 9]), TimeScalesFactory.getUTC());
							AbsoluteDate endDate = new AbsoluteDate((int) (((double[]) result[0])[i + 10]),
									(int) (((double[]) result[0])[i + 11]), (int) (((double[]) result[0])[i + 12]),
									(int) (((double[]) result[0])[i + 13]), (int) (((double[]) result[0])[i + 14]),
									(((double[]) result[0])[i + 15]), TimeScalesFactory.getUTC());
							NetSatThrustEvent tmpEvent = new NetSatThrustEvent(startDate, endDate,
									new Vector3D(thrustDirection));
							thrustEvents.add(tmpEvent);
							// this.prop.setDirection();
							// ((DateDetector)
							// this.prop.getEventsDetectors()[0]).addEventDate(startDate);
							// ((DateDetector)
							// this.prop.getEventsDetectors()[1]).addEventDate(endDate);
							// System.out.println("Flag=1 (setting
							// thrustStartWindow = " + startDate.toString()
							// + " thrustEndWindow = " + endDate.toString() +
							// " Direction = "
							// + new Vector3D(thrustDirection).toString());
						} else {
							// this.thrustEquation.setFire(false);
							// thrustDirection[0] = 0;
							// thrustDirection[1] = 0;
							// thrustDirection[2] = 0;
							// this.thrustEquation.setThrustDirection(thrustDirection);
						}
					}
					// For loop ends
					Collections.sort(thrustEvents, NetSatThrustEvent.comparator);
					for (int i = 0; i < thrustEvents.size(); i++) {
						this.prop.setDirection(thrustEvents.get(i).getThrustDirection());
						((DateDetector) this.prop.getEventsDetectors()[0])
								.addEventDate(thrustEvents.get(i).getEventStart());
						((DateDetector) this.prop.getEventsDetectors()[1])
								.addEventDate(thrustEvents.get(i).getEventEnd());
						System.out.println(
								"Flag=1 (setting thrustStartWindow = " + thrustEvents.get(i).getEventStart().toString()
										+ " thrustEndWindow = " + thrustEvents.get(i).getEventEnd().toString()
										+ " Direction = " + thrustEvents.get(i).getThrustDirection().toString());
					}
					thrustEvents = null;
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