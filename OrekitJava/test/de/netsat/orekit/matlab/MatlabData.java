package de.netsat.orekit.matlab;

import java.util.ArrayList;
import java.util.List;

public class MatlabData {

	private String name;
	private String type;
	private List<Double> scalarList;
	private List<double[]> vectorList;
	private boolean atOnce;
	private double scalarValue;
	private double[] vectorValue;

	/**
	 * Only use when, the atOnce is set to true.
	 * 
	 * @param name
	 * @param type
	 * @param atOnce
	 */
	public MatlabData(String name, String type, boolean atOnce) {
		this.name = name;
		this.type = type;
		this.atOnce = atOnce;
		if (this.atOnce) {
			if (this.type.equals("d")) {
				this.scalarList = new ArrayList<Double>();
			} else {
				this.vectorList = new ArrayList<double[]>();
			}
		}
	}

	/**
	 * Creates the MatlabData parameters. This is for step-by-step mode.
	 * 
	 * @param name
	 * @param type
	 */
	public MatlabData(String name, String type) {
		this(name, type, false);
	}

	/**
	 * Set the value in a right way.
	 * 
	 * @param value
	 */
	public void setValue(Object value) {
		if (this.atOnce) {
			if (this.type.equals("d")) {
				this.scalarList.add((Double) value);
			} else {
				this.vectorList.add((double[]) value);
			}
		} else {
			if (this.type.equals("d")) {
				this.scalarValue = (double) value;
			} else {
				this.vectorValue = (double[]) value;
			}

		}
	}

	/**
	 * returns the value of the Matlab, it could be a double or an array and so
	 * on.
	 * 
	 * @return {@link Object}
	 */
	public Object getValue() {
		if (this.type.equals("d")) {
			return this.scalarValue;
		} else {
			return this.vectorValue;
		}

	}

	/**
	 * Get Value of the list
	 */
	public Object getValueList() {
		if (this.type.equals("d")) {
			return this.getScalarList();
		} else {
			return this.getVectorList();
		}
	}

	/**
	 * Returns the type of the Matlab Data. At this time only it can be dd or d
	 * 
	 * @return {@link String}
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Returns the name of the Matlab Data.
	 * 
	 * @return {@link String}
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the scalarlist of the Matlab data, usable only with atOnce.
	 * Option
	 * 
	 * @return
	 */
	public double[] getScalarList() {
		return this.ListToDoubleArray(this.scalarList);
	}

	/**
	 * Returns the Vectorlist of the Matlab data, usable only with atOnce.
	 * 
	 * @return
	 */
	public double[][] getVectorList() {
		return this.ListToDoubleDoubleArray(this.vectorList);
	}

	/**
	 * Converts an array list doubles to an actual array.
	 * 
	 * @param l
	 * @return double[]
	 */
	public double[] ListToDoubleArray(List<Double> l) {
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
	public String[] ListToStringArray(List<String> l) {
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
	public double[][] ListToDoubleDoubleArray(List<double[]> l) {
		double[][] toRet = new double[l.size()][];
		int i = 0;
		for (double[] d : l) {
			toRet[i++] = d;
		}
		return toRet;
	}

}
