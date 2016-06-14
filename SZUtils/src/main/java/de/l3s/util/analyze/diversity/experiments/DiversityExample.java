package de.l3s.util.analyze.diversity.experiments;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import de.l3s.util.analyze.diversity.impl.AllPairsDJ;
import de.l3s.util.analyze.diversity.impl.SampleDJ;
import de.l3s.util.analyze.diversity.impl.TrackDJ;
import de.l3s.util.analyze.diversity.rdj.DivDocument2;
import de.l3s.util.analyze.diversity.rdj.Diversity;
import de.l3s.util.analyze.diversity.rdj.JaccardDivDocument;
import de.l3s.util.analyze.diversity.toolbox.JaccardSimilarityComparator;



public class DiversityExample {

	public static void main(String[] args) {

		List<DivDocument2> collection = generateCollection(1000,10,20);

		double error = .005, confidentiality = .95;
		
		JaccardSimilarityComparator similarityComparator = new JaccardSimilarityComparator();
		
		Diversity dj1 = new AllPairsDJ(collection, error, confidentiality,similarityComparator);
		System.out.println("RDJ:" + dj1.getRDJ());

		Diversity dj2 = new SampleDJ(collection, error, confidentiality,similarityComparator);
		System.out.println("RDJ:" + dj2.getRDJ());

		//TracjDJ works only with Jaccard similarity measure.
		Diversity dj3 = new TrackDJ(collection, confidentiality, confidentiality);
		System.out.println("RDJ:" + dj3.getRDJ());

	}

	private static List<DivDocument2> generateCollection(int numOfDocuments, int maxDoclength, int vocabularySize) {
		List<DivDocument2> collecion = new Vector<DivDocument2>();

		Random r = new Random(19);
		int[][] line = new int[numOfDocuments][];

		for (int i = 0; i < line.length; i++) {
			int len = r.nextInt(maxDoclength) + 1;
			JaccardDivDocument d = new JaccardDivDocument();
			line[i] = new int[len];
			for (int y = 0; y < len; y++) {
				int curval = r.nextInt(vocabularySize);
				d.add(curval + "");
				line[i][y] = curval;
			}
			Arrays.sort(line[i]);
			collecion.add(d);
		}
		return collecion;
	}
}