package de.l3s.util.analyze.diversity.impl;

import java.util.List;
import java.util.Random;
import java.util.Vector;

import de.l3s.util.analyze.diversity.rdj.DivDocument2;
import de.l3s.util.analyze.diversity.rdj.Diversity;
import de.l3s.util.analyze.diversity.rdj.JaccardDivDocument;
import de.l3s.util.analyze.diversity.rdj.SimilarityComparator;
import de.l3s.util.analyze.diversity.toolbox.Median;


public class SampleDJ extends Diversity {
	public SampleDJ(List<DivDocument2> docs, double error, double confidence,
			SimilarityComparator comp) {
		super(docs, error, confidence, comp);
		// TODO Auto-generated constructor stub
	}
	
	/*
	 * Sample line pairs in line[][] uniformly at random, and return the
	 * diversity index estimate with relative error bound epsilon and confidence
	 * (1 - delta), W is the # number of consecutive trials before stop testing
	 */
	double randomSampling(List<DivDocument2> docs, double epsilon, double delta,
			int W) {

		if(docs.size()<2){return -1.;}
		double rdj = 0.0;
		int r1 = 0;
		int r2 = (int) Math.ceil(Math.log(1 / delta) / Math.log(2));
		double jsSum[] = new double[(int) r2];
		Random r = new Random(124);

		double abs_error;
		do {
			for (int i = 0; i < r2; i++) {
				for (int j = 1; j <= W; j++) {
					// randomly pick 2 distinct docs
					int a = r.nextInt(docs.size());
					int b = r.nextInt(docs.size());

					while (a == b)
						b = r.nextInt(docs.size());

					jsSum[i] += getSimilarityComparator().similarity(
							docs.get(a), docs.get(b));

				}
			}

			r1 += W;

			// get the current estimate (average of the r2 jsSum[])

			for (int i = 0; i < r2; i++) {
				rdj += jsSum[i];
			}

			rdj = (rdj / ((double) r2)) / ((double) r1);

			// get the current estimate (median of the r2 jsSum[])
			rdj = Median.median(jsSum) / r1;

			// estimate the absolute error bound
			abs_error = 1 / Math.sqrt((double) r1);

		} while (abs_error / Math.abs(rdj - abs_error) > epsilon);

		return rdj;
	}

	@Override
	public double getRDJ() {
		return randomSampling(getCollection(), getError(), getConfidence(), 100);
	}
}
