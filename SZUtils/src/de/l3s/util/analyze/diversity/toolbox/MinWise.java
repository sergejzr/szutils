package de.l3s.util.analyze.diversity.toolbox;

import java.util.Random;

import de.l3s.util.analyze.diversity.rdj.JaccardDivDocument;



public class MinWise {
	/***
	 * use linear hashing as min-wise independent permutation, given a set of
	 * numbers, return the number with the minimum hash value deng@L3S.de, Dec.
	 * 2009.
	 */

	static Random myrand = new Random();
	/*
	 * int minHash(int d, int a[], int x[]); int [][] initMinHash(int l, int d);
	 */

	static int primeMOD = 2147483647; // = 2^31 - 1, a prime
	static int HL = 31;

	/*
	 * Linear hashing from MassDal: return (a*x + b) % primeMOD
	 */
	static long hash31(long a, long b, long x) {

		long result;
		long lresult;

		// return a hash of x using a and b mod (2^31 - 1)
		// may need to do another mod afterwards, or drop high bits
		// depending on d, number of bad guys
		// 2^31 - 1 = 2147483647

		// result = ((long long) a)*((long long) x)+((long long) b);
		result = (a * x) + b;
		result = ((result >> HL) + result) & primeMOD;
		lresult = (long) result;

		return (lresult);
	}

	/*
	 * min-hash a set of IDs (32-bit integers): apply hash31 on each number in
	 * the set s, return the ID with the minimum hash value as the output; s[0]
	 * stores the number of IDs in the set; a[] is the minHash parameters
	 */
	static int minHash31(long[] r, JaccardDivDocument document) {
		int  t, result = 0, min = 0;
		min = Integer.MAX_VALUE;
		for (Object o : document) {
			t = (int) hash31(r[0], r[1], o.hashCode());
			if (t < min) {
				min = t;
				result = o.hashCode();
			}

		}

		return result;
	}

	/*
	 * Initialize l d-degree minHash functions h[0]..h[l-1], return the hash
	 * functions, (parameter array, basicly a set of random numbers generated
	 * from lrand48())
	 */
	static long[][] initMinHash(int l, int d) {
		int i, j;

		long[][] r = new long[l][];// (int **) calloc(l, sizeof(int *));
		for (i = 0; i < l; i++) {
			r[i] = new long[d + 1];// (int *) calloc(d+1, sizeof(int));
			for (j = 0; j <= d; j++)
				r[i][j] = Math.abs(myrand.nextLong());
		}
		return r;
	}

}
