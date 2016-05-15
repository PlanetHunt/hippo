package de.netsat.orekit.matlab;

import de.netsat.orekit.convertor.OsculatingToMeanAlfriend;

/*
INPUT
7557100.72062949 SMA
0.0497835797798625 ECC
0.837879505992992 INC
0.201832161673552 ARGOFP
0.328875836079765 RAAN
2.41299972769282 TRUA
2.34482446293564 MEAA

MATLAB
7555063.28942522
0.0499785728264270
0.837749297473076
0.193267317144720
0.329279914036189
2.42131528629898
2.35349108456860

ME
7555057.49328457
0.0503692302539789
0.8377042653935799
0.20123024924316946
0.32928024128221206
2.3449951777195697
*/
public class CorrectionTest {
	public static void main(String[] args) {
		/* Semi Major Axis */
		double sma = 7.557100720629494e+06;
		/* Eccentricity */
		double ecc = 0.049783579779863;
		/* Inclination */
		double inc = 0.837879505992992;
		/* Argument of Perigee */
		double aop = 0.201832161673552;
		/* Right Ascension of Ascending Node */
		double raa = 0.328875836079765;
		/* Mean Anomaly */
		double man = 2.344824462935641;
		/* True Anomaly */
		double tan = 2.412999727692819;

		OsculatingToMeanAlfriend correct = new OsculatingToMeanAlfriend(sma, inc, ecc, raa, aop, tan, man, true, true, true);
		double[] result = correct.caculateAll();
		for (int i = 0; i < result.length; i++) {
			System.out.println(result[i]);
		}

	}

}
