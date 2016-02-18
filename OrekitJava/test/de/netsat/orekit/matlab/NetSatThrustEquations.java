package de.netsat.orekit.matlab;

import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.orekit.errors.OrekitException;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.integration.AdditionalEquations;

import de.netsat.orekit.actuator.NanoFEEP;

/**
 * This class implements the {@link AdditionalEquations}, so the thruster effect
 * on the space-craft can be simulated. There is also a possibility to propagate
 * the additional state here to. To perform this action fill the pDot array in
 * the computeDerivatives. When propagating additional states, using one of
 * these for the whole additional states is more recommended, because then there
 * is way for the state to interact which each other.
 * 
 * @author Pouyan Azari
 *
 */
public class NetSatThrustEquations implements AdditionalEquations {

	private String name;
	private String type;
	private boolean fire;
	private int thrusterNum;
	private double thrust;
	private double constantMassLoss;
	private ConstantValues constants;
	private double[] velocityVector;

	public NetSatThrustEquations(String name, String type, boolean fire, int thrusterNum, double thrust) {
		this.name = name;
		this.type = type;
		this.fire = fire;
		this.thrusterNum = thrusterNum;
		this.thrust = thrust;
	}

	/**
	 * Returns the constant mass loss for the thruster with such property. Only
	 * applicable for constant thruster type.
	 * 
	 * @return
	 */
	public double getConstantMassLoss() {
		return this.constantMassLoss;
	}

	/**
	 * Sets the constant Mass loss for the give thruster. Only applicable for
	 * constant thruster type.
	 * 
	 * @param constantMassLoss
	 */
	public void setConstantMassLoss(double constantMassLoss) {
		this.constantMassLoss = constantMassLoss;
	}

	/**
	 * Returns the thrust set for the given equation.
	 * 
	 * @return
	 */
	public double getThrust() {
		return this.thrust;
	}

	/**
	 * Sets the thrust to the given value
	 * 
	 * @param thrust
	 */
	public void setThrust(double thrust) {
		this.thrust = thrust;
	}

	/**
	 * Returns the number of the thruster used in the equation.
	 * 
	 * @return
	 */
	public int getThrustNum() {
		return this.thrusterNum;
	}

	/**
	 * Sets the number of the thruster to be used in the equation.
	 * 
	 * @param thrusterNum
	 */
	public void setThrusterNum(int thrusterNum) {
		this.thrusterNum = thrusterNum;
	}

	/**
	 * Returns the type of the thrust, specific for the maneuver.
	 * 
	 * @return
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Sets the type of the thrust, for the maneuver.
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Returns the firing parameter. If the the thruster is firing.
	 * 
	 * @return
	 */
	public boolean getFire() {
		return this.fire;
	}

	/**
	 * Sets the firing parameter for the Thruster Equation.
	 * 
	 * @param fire
	 */
	public void setFire(boolean fire) {
		this.fire = fire;
	}

	/** {@inheritDoc} */
	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	public double[] calculteThrustEffects(SpacecraftState s, double[] velocityVector) {
		double[] mainStates = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		mainStates[3] = velocityVector[0];
		mainStates[4] = velocityVector[1];
		mainStates[5] = velocityVector[2];
		return mainStates;

	}

	/**
	 * Calculate the Thrust effect on the {@link SpacecraftState}. At this only
	 * homogeneous thruster are supported. Not homogeneous thrusters setting
	 * need a change here.
	 * 
	 * @param s
	 * @param thrust
	 * @param thrusterNumbers
	 * @param massLoss
	 * @return
	 * @throws OrekitException
	 * @throws MathArithmeticException
	 */
	public double[] calculateThrustEffects(SpacecraftState s, double thrust, int thrusterNumber, double massLoss)
			throws MathArithmeticException, OrekitException {
		double[] mainStates = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		Vector3D velocityNormal = s.getPVCoordinates(this.constants.getITRF()).getVelocity().normalize();
		velocityNormal = velocityNormal.scalarMultiply(-thrusterNumber * thrust);
		mainStates[3] = velocityNormal.getX();
		mainStates[4] = velocityNormal.getY();
		mainStates[5] = velocityNormal.getZ();
		mainStates[6] = thrusterNumber * massLoss;
		return mainStates;
	}

	/**
	 * Sets the name for the additional equations. To have the additional state
	 * parameters also working the name should be the same as the additional
	 * state.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/** {@inheritDoc} */
	public double[] computeDerivatives(SpacecraftState s, double[] pDot) throws OrekitException {
		if (this.type.equals("unlimited")) {
			if (this.fire) {
				this.fire = false;
				return this.calculteThrustEffects(s, this.getVeloctiyVector());
			}
		}
		if (this.type.equals("feep")) {
			/** Implementing the nanoFEEP thruster happens here. **/
			NanoFEEP nanoFeep = new NanoFEEP(new Vector3D(0, 0), new Vector3D(0, 0));
			if (s.getMass() > 0.99800 & this.fire) {
				return this.calculateThrustEffects(s, this.thrust, this.thrusterNum,
						this.thrusterNum * nanoFeep.getFlowRate(this.thrust));
			}
		} else if (this.type.equals("constant")) {
			if (this.fire) {
				return this.calculateThrustEffects(s, this.thrust, thrusterNum, this.constantMassLoss);
			}
		}
		return null;
	}

	public double[] getVeloctiyVector() {
		return this.velocityVector;
	}

	public void setVelocityVector(double[] velocityVector) {
		this.velocityVector = velocityVector;
	}

}
