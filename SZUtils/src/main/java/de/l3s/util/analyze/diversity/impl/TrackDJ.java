package de.l3s.util.analyze.diversity.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import de.l3s.util.analyze.diversity.rdj.DivDocument2;
import de.l3s.util.analyze.diversity.rdj.Diversity;
import de.l3s.util.analyze.diversity.rdj.JaccardDivDocument;
import de.l3s.util.analyze.diversity.toolbox.JaccardSimilarityComparator;
import de.l3s.util.analyze.diversity.toolbox.Median;

public class TrackDJ extends Diversity {

	public TrackDJ(List<DivDocument2> docs, double error,
			double confidence) {
		super((docs), error, confidence,
				new JaccardSimilarityComparator());
	}



	@Override
	public double getRDJ() {

		int line[][] = new int[getCollection().size()][];

		for (int i = 0; i < line.length; i++) {
			JaccardDivDocument doc = (JaccardDivDocument)getCollection().get(i);
			line[i] = new int[doc.size()];

			int y = 0;
			for (Object o : doc) {
				line[i][y++] = o.hashCode();
			}
		}
		return computeRDJOldStyle(line, getError(), getConfidence());

	}

	public static void main(String[] args) {

	}

	public double computeRDJOldStyle(int[][] line, double eps, double del) {

		for (int i = 0; i < line.length; i++) {
			Arrays.sort(line[i]);
		}

		// init L min-hash functions, each one is a (d+1)-dimensional vector
		// int [][] r = initMinHash(L, d);

		// int shortLineNum = 0;
		// int longLineNum = 0;
		// int shortU=4927394;

		/*
		 * for (int i=1; i<=line[0][0]; i++) { if (line[i][0] > shortU)
		 * longLineNum ++; else shortLineNum++; }
		 */
		int[][] shortLine = line;
		int[][] longLine = new int[0][];

		// shortLineNum = line.length; longLineNum =0;

		int[] result = extractLineStatistics(shortLine);
		extractLineStatistics(longLine);

		// bufRdj = 0; simSum = 0;
		double bufRdj = trackDjBuf(shortLine, result, eps, del);

		double simSum = bufRdj * shortLine.length * (shortLine.length - 1) / 2;

		// compute the sum of jaccard similarity of all pairs between
		// shortLine[][] and loneLine[][]
		for (int i = 0; i < shortLine.length; i++) {
			for (int j = 0; j < longLine.length; j++) {
				simSum += jacSim(shortLine[i], longLine[j]);
			}
		}

		// compute the sum of jaccard similarity of all pairs in loneLine[][]
		for (int i = 0; i < longLine.length; i++) {
			for (int j = 0; j < longLine.length; j++) {
				simSum += jacSim(longLine[i], longLine[j]);
			}
		}

		int n = line.length;
		bufRdj = simSum * 2 / n / (n - 1);
		// double bufDj = ( bufRdj*n*(n-1) + n ) / n / n;

		return bufRdj;

	}

	/*
	 * Read input line[][], find the minID, maxID, maxLineSize and store them in
	 * line[0][1..3]
	 */
	int[] extractLineStatistics(int line[][]) {
		int i, j, t1, t2, maxLineSize = 0;
		int[] ret = new int[3];
		if (line.length == 0) {
			ret[0] = 0;
			ret[1] = 0;
			ret[2] = 0;
			return ret;
		}

		// find the min & max ID in the data set
		t1 = line[0][0];
		t2 = line[0][0];
		for (i = 0; i < line.length; i++) {
			if (line[i].length > maxLineSize)
				maxLineSize = line[i].length;
			for (j = 0; j < line[i].length; j++) {
				if (line[i][j] < t1)
					t1 = line[i][j];
				else if (line[i][j] > t2)
					t2 = line[i][j];
			}
		}
		ret[0] = t1;
		ret[1] = t2;
		ret[2] = maxLineSize;

		return ret;
	}

	/*
	 * Initialize l d-degree minHash functions h[0]..h[l-1], return the hash
	 * functions, (parameter array, basicly a set of random numbers generated
	 * from lrand48())
	 */
	int[][] initMinHash(int l, int d) {
		int i, j;
		Random rand = new Random(12345);

		int r[][] = new int[l][];

		for (i = 0; i < l; i++) {
			r[i] = new int[d + 1];
			for (j = 0; j <= d; j++)
				r[i][j] = rand.nextInt();
		}
		return r;
	}

	/*
	 * Given the max item, initialize a buffer storing the frequencies of each
	 * distinct item in an item stream; the buffer is an array; each entry
	 * freq[i] shows the frequency of item i;
	 */
	int[] initFreqBuf(int maxItem) {
		int i;
		int[] result;
		result = new int[maxItem + 1];
		for (i = 0; i <= maxItem; i++) {
			result[i] = 0;
		}
		return result;
	}

	double trackDjBuf(int line[][], int[] result, double epsilon, double delta) {
		int idFreqBuf[], d, i, j, l, n, L1, L2, id;
		double f2list[], aDouble, f2, bufRdj;

		L1 = (int) ((result[2] - 1) / epsilon / epsilon * 8);
		L2 = (int) Math.ceil(Math.log(1 / delta) / Math.log(2));

		idFreqBuf = initFreqBuf(result[1]);

		f2list = new double[L2];

		// init L min-hash functions, each one is a (d+1)-dimensional vector
		d = 1;
		int[][] r = initMinHash(L1 * L2, d);

		// init buffer index
		indexItemType itemIndex = null;
		indexItemType anItem = new indexItemType();

		n = line.length;
		f2 = 0.0; // only for the average of L iterations, useless for L1*L2
					// iterations

		/* get median f2 of L2 trials */
		for (j = 0; j < L2; j++) {
			// printf(" j=%ld \n", j);
			for (l = 0; l < L1; l++) {
				itemIndex = null;
				// printf(" l=%ld \n", l);
				for (i = 0; i < n; i++) {
					// insert the ID minHash to the frequency buffer and the id
					// index
					// if (i%90 == 0 && l%1000==0)
					// printf("j=%ld l=%ld i=%ld \n", j, l, i);

					// idFreqBuf[minHash31(r[(j-1)*L1 + l], line[i])]++; //use
					// MassDal has31(), L1*L2 iterations
					id = minHash31(r[(j) * L1 + l], line[i]);

					if (idFreqBuf[id] == 0) { // create an item storing the id
												// and link it to the index
						anItem = new indexItemType();
						anItem.item = id;
						anItem.next = itemIndex;
						itemIndex = anItem;
					}

					idFreqBuf[id]++;
				}
				// compute f2 based on the current frequency buffer and reset
				// all counters afterwards
				// aDouble = f2Buf(idFreqBuf, line[0][2]);
				aDouble = f2Buf(idFreqBuf, itemIndex);
				f2list[j] += aDouble;
			}
		}

		f2 = Median.median(f2list) / L1; // get median

		bufRdj = ((f2 - n) / n) / (n - 1);

		return bufRdj;
	}

	/*
	 * min-hash a set of IDs (32-bit integers): apply hash31 on each number in
	 * the set s, return the ID with the minimum hash value as the output; s[0]
	 * stores the number of IDs in the set; a[] is the minHash parameters
	 */
	int minHash31(int a[], int s[]) {
		int i, t, result, min;

		min = (int) hash31(a[0], a[1], s[0]);
		result = s[0];

		for (i = 0; i < s.length; i++) {
			t = (int) hash31(a[0], a[0], s[i]);
			if (t < min) {
				min = t;
				result = s[i];
			}
		}

		return result;
	}

	int HL = 31;
	int primeMOD = 2147483647; // = 2^31 - 1, a prime

	/*
	 * Linear hashing from MassDal: return (a*x + b) % primeMOD
	 */
	long hash31(long a, long b, long x) {

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
	 * Return the 2nd frequency moment (self-join size) of a given freqency
	 * buffer, and clean the buffer and index afterwards
	 */
	double f2Buf(int freqBuf[], indexItemType itemIndex) {
		int i;
		double tmp, result = 0.0;

		// indexItemType pp=null;
		indexItemType p = itemIndex;

		while (p != null) {
			// compute f2
			i = p.item;
			tmp = ((double) freqBuf[i]) * freqBuf[i];
			result += tmp;
			freqBuf[i] = 0; // reset the counter for the next use
			// delete the item from index
			// pp = p;
			p = p.next;
			// free(pp);
		}

		return result;
	}

	/*
	 * compute the Jaccard similarity of two sets a[] and b[]; i.e. return |a
	 * overlap b|/|a union b|
	 */
	double jacSim(int a[], int b[]) {
		int t = countOverlap(a, b);
		double result = ((double) t) / (a.length + b.length - t);
		return result;
	}

	/*
	 * count the number of common IDs of two increasingly sorted integer arrays,
	 * a[0] & b[0] stores the numbers of integers in the arrrays
	 */
	int countOverlap(int a[], int b[]) {
		int i = 0, j = 0, count = 0;
		while (i < a.length || j < b.length) {
			if (a[i] == b[j]) {
				count++;
				i++;
				j++;
				if (i >= a.length || j >= b.length)
					return count;
			} else if (a[i] < b[j]) {
				i++;
				if (i >= a.length)
					return count;
			} else if (a[i] > b[j]) {
				j++;
				if (j >= b.length)
					return count;
			}
		}
		return -1;
	}

}
