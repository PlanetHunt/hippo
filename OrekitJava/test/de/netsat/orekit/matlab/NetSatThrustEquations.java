package de.netsat.orekit.matlab;

import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.orekit.errors.OrekitException;
import org.orekit.frames.LOFType;
import org.orekit.frames.LocalOrbitalFrame;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.integration.AdditionalEquations;
import org.orekit.utils.PVCoordinates;

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
	private double[] thrustDirection;
	private double massLoss;
	private double outputStepSize;
	private boolean globalFire;

	public NetSatThrustEquations(String name, String type, boolean fire, int thrusterNum, double thrust,
			double[] thrustDirection, double massLoss, double outputStepSize) {
		this.globalFire = true;
		this.name = name;
		this.type = type;
		this.fire = fire;
		this.thrusterNum = thrusterNum;
		this.thrust = thrust;
		this.thrustDirection = thrustDirection;
		this.massLoss = massLoss;
		this.outputStepSize = outputStepSize;
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
	 * Calculates the effect of the thrust on the spacecraft.
	 * 
	 * @param s
	 * @param thrust
	 * @param thursterNumber
	 * @param massLoss
	 * @param thrustDirection
	 * @return
	 * @throws OrekitException
	 */
	public double[] calculateThrustEffects(SpacecraftState s, double thrust, int thrusterNumber, double massLoss,
			double[] thrustDirection) throws OrekitException {
		double[] mainStates = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		double defValue = 0;
		double signValue = 0;
		for (double t : thrustDirection) {
			if (Math.abs(t) > defValue) {
				defValue = Math.abs(t);
				signValue = Math.signum(t);
			}
		}
		Vector3D thrustDirectionVector = new Vector3D(thrustDirection);
		System.out.println(thrustDirectionVector.toString());
		// LocalOrbitalFrame localLVLH = new LocalOrbitalFrame(s.getFrame(),
		// LOFType.LVLH, s.getOrbit(), "LVLH");
		// PVCoordinates pVSatLVLH = s.getFrame().getTransformTo(localLVLH,
		// s.getDate())
		// .transformPVCoordinates(s.getPVCoordinates());
		// PVCoordinates thrustDirectionLVLH = new
		// PVCoordinates(pVSatLVLH.getPosition(), thrustDirectionVector);
		// PVCoordinates thrustDirectionInertial =
		// localLVLH.getTransformTo(s.getFrame(), s.getDate())
		// .transformPVCoordinates(thrustDirectionLVLH);
		// Vector3D velocityNormal =
		// thrustDirectionInertial.getVelocity();//.normalize();
		// PVCoordinates test = s.getFrame().getTransformTo(localLVLH,
		// s.getDate())
		// .transformPVCoordinates(thrustDirectionInertial);
		// System.out.println(test.getVelocity().toString());
		// System.out.println(velocityNormal.toString());
		System.out.println();
		// thrust = thrust * signValue;
		// thrust = thrust * -1;
		// velocityNormal = velocityNormal.scalarMultiply(thrusterNumber *
		// thrust);
		// mainStates[3] = velocityNormal.getX();// / (this.outputStepSize / 6);
		// mainStates[4] = velocityNormal.getY();// / (this.outputStepSize / 6);
		// mainStates[5] = velocityNormal.getZ();// / (this.outputStepSize / 6);
		// thrustDirectionVector =
		// thrustDirectionVector.scalarMultiply(thrusterNumber * thrust);
		System.out.println(thrustDirectionVector.toString());

		mainStates[3] = thrustDirectionVector.getX();// / (this.outputStepSize /
														// 6);
		mainStates[4] = thrustDirectionVector.getY();// / (this.outputStepSize /
														// 6);
		mainStates[5] = thrustDirectionVector.getZ();// / (this.outputStepSize /
														// 6);
		mainStates[6] = massLoss / (this.outputStepSize / 6);
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
		if (this.type.equals("experimental")) {
			if (this.fire & this.globalFire) {
				System.out.println("We are fireing");
				this.setFire(false);
				this.globalFire = false;
				NanoFEEP nanoFeep = new NanoFEEP(new Vector3D(0, 0), new Vector3D(0, 0));
				// Times 1000 should be removed afterward, this is only for
				// testing.
				// double massLoss =
				// nanoFeep.getFlowRate(Math.abs(this.thrust));
				System.out.println("MassLoss:" + massLoss);
				return this.calculateThrustEffects(s, this.getThrust(), this.getThrustNum(), this.massLoss,
						getThrustDirection());
				// return null;
			}
		}
		if (this.type.equals("unlimited")) {
			if (this.fire) {
				this.fire = false;
				this.setFire(false);
				return this.calculteThrustEffects(s, this.getVeloctiyVector());
			}
		}
		if (this.type.equals("feep")) {
			/** Implementing the nanoFEEP thruster happens here. **/
			NanoFEEP nanoFeep = new NanoFEEP(new Vector3D(0, 0), new Vector3D(0, 0));
			if (s.getMass() > 0.99800 & this.fire) {
				this.setFire(false);
				return this.calculateThrustEffects(s, this.thrust, this.thrusterNum,
						this.thrusterNum * nanoFeep.getFlowRate(this.thrust));
			}
		} else if (this.type.equals("constant")) {
			if (this.fire) {
				this.setFire(false);
				return this.calculateThrustEffects(s, this.thrust, thrusterNum, this.constantMassLoss);
			}
		}
		return null;
	}

	private double[] getThrustDirection() {
		return this.thrustDirection;
	}

	public void setThrustDirection(double[] thrustDirection) {
		this.thrustDirection = thrustDirection;
	}

	public double[] getVeloctiyVector() {
		return this.velocityVector;
	}

	public void setVelocityVector(double[] velocityVector) {
		this.velocityVector = velocityVector;
	}

}
