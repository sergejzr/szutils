package de.l3s.util.analyze.diversity.impl;

import java.util.List;
import java.util.Vector;

import de.l3s.util.analyze.diversity.rdj.DivDocument2;
import de.l3s.util.analyze.diversity.rdj.Diversity;
import de.l3s.util.analyze.diversity.rdj.JaccardDivDocument;
import de.l3s.util.analyze.diversity.rdj.SimilarityComparator;


/**
 * Computes diversity using naive All-Pairs approach
 * 
 * @author Zerr, algorithm Fan Deng
 * 
 */
public class AllPairsDJ extends Diversity{
	/*
	 * this file stores the functions related to precisely computing the
	 * diversity of a given input line[][], each line[i][] is an array of
	 * intergers Fan Deng, deng@L3S.de, Dec. 2009
	 */
	public AllPairsDJ(List<DivDocument2> docs, double error, double confidence,
			SimilarityComparator comp) {
		super((docs), error, confidence, comp);

	}



	/*
	 * Given an 2-d array line[][], comparing the Jaccard similarity of all line
	 * pairs (line[i] & line[j]) where i<j, and return the sum of similarities
	 * of those pairs; line[0][0] stored the # of lines
	 */
	public double jsSum(List<DivDocument2> docs) {

		double result = 0.0;

		for (int i = 0; i < docs.size(); i++) {
			for (int j = i + 1; j < docs.size(); j++) {

				Double curres = getSimilarityComparator().similarity(
						docs.get(i), docs.get(j));

				result += curres;
			}
		}
		return result;
	}

	@Override
	public double getRDJ() {

		setCollection(getCollection());
		return (2 * jsSum(getCollection()) / getCollection().size())
				/ (getCollection().size() - 1);

	}

}
