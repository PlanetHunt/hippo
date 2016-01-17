package de.netsat.orekit.matlab;

import matlabcontrol.MatlabInvocationException;

public enum PropagationSettingType {
	FAST_1000(100.0, 0.001, 1000, 100, 1000),
	DUMMY(0, 0, 0, 0, 0);

	private double positionTolerance;
	private double minStep;
	private double maxStep;
	private double stepSize;
	private double duration;

	PropagationSettingType(double positionTolerance, double minStep, double maxStep, double stepSize, double duration) {
		this.positionTolerance = positionTolerance;
		this.minStep = minStep;
		this.maxStep = maxStep;
		this.stepSize = stepSize;
		this.duration = duration;
	}

	public double getPositionTolerance() {
		return positionTolerance;
	}

	public void setPositionTolerance(double positionTolerance) {
		this.positionTolerance = positionTolerance;
	}

	public double getMinStep() {
		return minStep;
	}

	public void setMinStep(double minStep) {
		this.minStep = minStep;
	}

	public double getMaxStep() {
		return maxStep;
	}

	public void setMaxStep(double maxStep) {
		this.maxStep = maxStep;
	}

	public double getStepSize() {
		return stepSize;
	}

	public void setStepSize(double stepSize) {
		this.stepSize = stepSize;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	/**
	 * Fills the {@link PropagationSettingType} with the matlab function
	 * returns. It also cares about the position of different parameters in
	 * Matlab returns.
	 * 
	 * @param mi
	 * @param matlabFunction
	 * @param numberOfArgs
	 * @param tolerancePositin
	 * @param minStepPosition
	 * @param maxStepPosition
	 * @param durationPosition
	 * @param stepSizePosition
	 * @throws MatlabInvocationException
	 */
	public void setFromMatlab(MatlabInterface mi, String matlabFunction, int numberOfArgs, int tolerancePositin,
			int minStepPosition, int maxStepPosition, int durationPosition, int stepSizePosition)
					throws MatlabInvocationException {
		Object[] returningObject = mi.returningEval(matlabFunction, numberOfArgs);
		this.positionTolerance = ((double[]) returningObject[tolerancePositin])[0];
		this.minStep = ((double[]) returningObject[minStepPosition])[0];
		this.maxStep = ((double[]) returningObject[maxStepPosition])[0];
		this.duration = ((double[]) returningObject[durationPosition])[0];
		this.stepSize = ((double[]) returningObject[stepSizePosition])[0];
	}

}
