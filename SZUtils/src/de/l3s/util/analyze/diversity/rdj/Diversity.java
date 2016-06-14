package de.l3s.util.analyze.diversity.rdj;

import java.util.List;
import java.util.Vector;

/**
 * Diversity Computation interface.
 * @author Zerr, algorithm Fan Deng
 *
 */
public abstract class Diversity {
	private List<DivDocument2> collection = new Vector<DivDocument2>();
	private double error;
	private double confidence;
	SimilarityComparator similarityComparator;
	public Diversity(List<DivDocument2> docs, double error, double confidence, SimilarityComparator comp) {
		this.setCollection(docs);
		this.error=error;
		this.confidence=confidence;
		similarityComparator=comp;
	
	}
	public SimilarityComparator getSimilarityComparator() {
		return similarityComparator;
	}
	public double getError() {
		return error;
	}
	public void setError(double error) {
		this.error = error;
	}
	public double getConfidence() {
		return confidence;
	}
	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}
	public abstract double getRDJ();
	public List<DivDocument2> getCollection() {
		return collection;
	}
	public void setCollection(List<DivDocument2> collection) {
		this.collection = collection;
	}
}
