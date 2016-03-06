package de.netsat.orekit.matlab;

import org.orekit.errors.OrekitException;
import org.orekit.errors.PropagationException;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.sampling.OrekitStepHandler;
import org.orekit.propagation.sampling.OrekitStepInterpolator;
import org.orekit.time.AbsoluteDate;

public class MatlabVariablePushHandler extends MatlabPushHandler implements OrekitStepHandler {

	/**
	 * Matlab Push Handler with all Parameters.
	 * 
	 * @param mi
	 * @param options
	 * @param matlabFunctions
	 * @param atOnce
	 * @param propulsionSystem
	 * @param eventCal
	 * @param constants
	 */
	public MatlabVariablePushHandler(final MatlabInterface mi, final SensorDataType[] options,
			final MatlabFunctionType[] matlabFunctions, final boolean atOnce, final PropulsionSystem propulsionSystem,
			final EventCalculator eventCal, final ConstantValues constants) {
		super(mi, options, matlabFunctions, atOnce, propulsionSystem, eventCal, constants);
	}

	/**
	 * A simpler Matlab pushhandler
	 * 
	 * @param mi
	 * @param options
	 * @param matlabFunctions
	 * @param constants
	 */
	public MatlabVariablePushHandler(MatlabInterface mi, SensorDataType[] options, MatlabFunctionType[] matlabFunctions,
			ConstantValues constants) {
		super(mi, options, matlabFunctions, false, null, null, constants);
	}

	@Override
	public void handleStep(OrekitStepInterpolator interpolator, boolean isLast) throws PropagationException {
		SpacecraftState currentState = null;
		try {
			currentState = interpolator.getInterpolatedState();
		} catch (OrekitException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (!isLast) {
			try {
				System.out.println("Still propagating...");
				this.setVariableInMatlab("last_step_flag", 0);
				/*
				 * Will add the sensor data to the to at once array or send the
				 * data directly to Matlab
				 */
				this.evaluateOptions(currentState);
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println(e.getMessage());
			}
		} else {
			try {
				System.out.println("We are in the Last Step.");
				this.setVariableInMatlab("last_step_flag", 1);
				this.evaluateOptions(currentState);
				/* Send the all data if it should be */
				if (this.atOnce) {
					this.PushAllDataToMatlab();
				}
				/* Run the Matlab function which should be run at last */
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

}