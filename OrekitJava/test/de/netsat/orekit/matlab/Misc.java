package de.netsat.orekit.matlab;

import java.util.List;

public class Misc {

	/**
	 * Converts an array list doubles to an actual array.
	 * 
	 * @param l
	 * @return double[]
	 */
	public static double[] ListToDoubleArray(List<Double> l) {
		double[] toRet = new double[l.size()];
		int i = 0;
		for (Double d : l)
			toRet[i++] = d;
		return toRet;
	}

	/**
	 * Converts an array list of string to an actual array
	 * 
	 * @param l
	 * @return double[]
	 */
	public static String[] ListToStringArray(List<String> l) {
		String[] toRet = new String[l.size()];
		int i = 0;
		for (String d : l)
			toRet[i++] = d;
		return toRet;
	}

	/**
	 * Converts the list of double[] array to an actual array.
	 * 
	 * @param l
	 * @return double[][]
	 */
	public static double[][] ListToDoubleDoubleArray(List<double[]> l) {
		double[][] toRet = new double[l.size()][];
		int i = 0;
		for(double[] d: l){
			toRet[i++] = d;
		}
		return toRet;
	}
}
