package de.netsat.orekit.matlab;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.orekit.errors.OrekitException;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.PositionAngle;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.propagation.analytical.tle.TLEPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;

import matlabcontrol.MatlabInvocationException;

public class InitialOrbit {
	// constant values are set the same for the whole project to remove the
	// Inconsistencies.
	private ConstantValues constants;

	/**
	 * Returns the Keplerian Orbit from the Matlab Files.
	 * 
	 * @param mi
	 * @return {@link KeplerianOrbit}
	 * @throws MatlabInvocationException
	 * @throws OrekitException
	 */
	public KeplerianOrbit getKeplerianOrbit(MatlabInterface mi, int satNum)
			throws MatlabInvocationException, OrekitException {
		String s = "getKeplerSat(mu," + String.valueOf(satNum) + ")";
		Object[] returningObject = mi.returningEval(s, 2);
		double[] elements = (double[]) returningObject[0];
		double[] timevec = (double[]) returningObject[1];

		TimeScale utc = TimeScalesFactory.getUTC();
		AbsoluteDate initialDate = new AbsoluteDate((int) timevec[0], (int) timevec[1], (int) timevec[2],
				(int) timevec[3], (int) timevec[4], timevec[5], utc);
		System.out.println("Date: " + initialDate.toString());
		return new KeplerianOrbit(elements[0], elements[1], elements[2], elements[3], elements[4], elements[5],
				PositionAngle.MEAN, this.constants.getITRF(), initialDate, this.constants.getMu());

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
				tleData.getMeanAnomaly(), PositionAngle.MEAN, this.constants.getITRF(), tleData.getDate(),
				this.constants.getMu());
	}

}
