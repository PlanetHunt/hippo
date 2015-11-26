package de.netsat.orekit.actuator;

import java.util.List;
import java.util.TreeMap;


public class Tools {
	/**
	 * Return an interpolated value from the given map
	 * @param entries
	 * @param x
	 * @return
	 */
	public static final double linearInterpolation(TreeMap<Double, Double> entries, double x) {
		double x1 = entries.lowerKey(x);
		double x2 = entries.higherKey(x);
		double y1 = entries.get(x1);
		double y2 = entries.get(x2);
		return y1 + (y2 - y1) / (x2-x1) * (x-x1);
	}
	
	public static double[] ListToDoubleArray(List<Double> l) {
		double[] toRet = new double[l.size()];
		int i = 0;
		for(Double d : l)
			toRet[i++] = d;
		return toRet;
	}
	
	public static String[] ListToStringArray(List<String> l) {
		String[] toRet = new String[l.size()];
		int i = 0;
		for(String d : l)
			toRet[i++] = d;
		return toRet;
	}
	
}
