package de.netsat.orekit.matlab;

import matlabcontrol.MatlabInvocationException;

import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.PositionAngle;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;

public class loadScripts {
	
	public static KeplerianOrbit getKeplerOrbit(MatlabInterface mi, int sat_nr) throws OrekitException, MatlabInvocationException {
		
		String s = "getKeplerSat(mu,";
			s = s.concat(String.valueOf(sat_nr));
			s = s.concat(")");
		Object[] returningObject = mi.returningEval(s, 2);
		double[] elements = (double[]) returningObject[0];
		double[] timevec = (double[]) returningObject[1];
				
		Frame inertialFrame = FramesFactory.getEME2000();

		TimeScale utc = TimeScalesFactory.getUTC();
		AbsoluteDate initialDate = new AbsoluteDate((int) timevec[0], (int) timevec[1], (int) timevec[2], (int) timevec[3],
				(int) timevec[4], timevec[5], utc);
		System.out.println("Date: " + initialDate.toString());
		return new KeplerianOrbit(elements[0], elements[1], elements[2], elements[3], elements[4], elements[5], PositionAngle.MEAN,
				inertialFrame, initialDate, Constants.EIGEN5C_EARTH_MU);
	}
	
	public static KeplerianOrbit createKeplerOrbit(MatlabInterface mi) throws MatlabInvocationException, OrekitException{
		Object[] returningObject;
		returningObject = mi.returningEval("getVariables(a(end),e(end),in(end),omega(end),raan(end),mean_anomaly(end),date)", 1);
		double[] elements = (double[]) returningObject[0];
		
		Frame inertialFrame = FramesFactory.getEME2000();

		TimeScale utc = TimeScalesFactory.getUTC();
		AbsoluteDate initialDate = new AbsoluteDate((int) elements[6], (int) elements[7], (int) elements[8], (int) elements[9],
				(int) elements[10], elements[11], utc);
		return new KeplerianOrbit(elements[0], elements[1], elements[2], elements[3], elements[4], elements[5], PositionAngle.MEAN,
				inertialFrame, initialDate, Constants.EIGEN5C_EARTH_MU);
	}
}
