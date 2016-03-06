package de.netsat.orekit.matlab;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.PositionAngle;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.propagation.analytical.tle.TLEPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;

import matlabcontrol.MatlabInvocationException;

public class InitialOrbit {
	// constant values are set the same for the whole project to remove the
	// Inconsistencies.
	private final ConstantValues constants;
	private final double[] orbitParams;
	private final double[] dateParams;

	/**
	 * 
	 * @param orbitParams
	 * @throws OrekitException
	 */
	public InitialOrbit(final double[] orbitParams, final double[] dateParams, final ConstantValues constants)
			throws OrekitException {
		this.constants = constants;
		this.orbitParams = orbitParams;
		this.dateParams = dateParams;
	}

	/**
	 * Return the date parameters.
	 * 
	 * @return
	 */
	public double[] getDateParams() {
		return this.dateParams;
	}

	/**
	 * Returns the orbital parameters.
	 * 
	 * @return
	 */
	public double[] getOrbitParams() {
		return this.orbitParams;
	}

	/**
	 * Returns the Keplerian Orbit from the Matlab Files.
	 * 
	 * @param mi
	 * @return {@link KeplerianOrbit}
	 * @throws MatlabInvocationException
	 * @throws OrekitException
	 */
	public KeplerianOrbit getKeplerianOrbit() throws MatlabInvocationException, OrekitException {
		TimeScale ts = this.constants.getTimeScale();
		Frame frame = this.constants.getEci();
		AbsoluteDate it = new AbsoluteDate((int) this.dateParams[0], (int) this.dateParams[1], (int) this.dateParams[2],
				(int) this.dateParams[3], (int) this.dateParams[4], this.dateParams[5], ts);
		return new KeplerianOrbit(this.orbitParams[0], this.orbitParams[1], this.orbitParams[2], this.orbitParams[3],
				this.orbitParams[4], this.orbitParams[5], PositionAngle.MEAN, frame, it, this.constants.getMu());
	}

	/**
	 * Returns the Keplerian Orbit from a TLE String. When read from a file, the
	 * file should have no more than 2 lines.
	 *
	 * @TODO incomplete
	 * @param tle
	 * @return {@link KeplerianOrbit}
	 * @throws Exception
	 */
	public KeplerianOrbit getKeplerianOrbit(String tleOne, String tleTwo, String fromFile) throws Exception {
		TLE tleData;

		if (fromFile != null) {
			ArrayList<String> fileContents = new ArrayList<String>();
			Charset charset = Charset.forName("UTF-8");
			for (String line : Files.readAllLines(Paths.get(fromFile), charset)) {
				fileContents.add(line);
			}
			if (fileContents.size() < 3) {
				tleData = new TLE(fileContents.get(0), fileContents.get(1));
			} else {
				throw new Exception("The TLE file can not have more than two elements.");
			}
		} else {
			tleData = new TLE(tleOne, tleTwo);
		}
		Propagator prop = TLEPropagator.selectExtrapolator(tleData);
		return new KeplerianOrbit(prop.getInitialState().getA(), prop.getInitialState().getE(),
				prop.getInitialState().getI(), tleData.getPerigeeArgument(), tleData.getRaan(),
				tleData.getMeanAnomaly(), PositionAngle.MEAN, this.constants.getEci(), tleData.getDate(),
				this.constants.getMu());
	}

}
