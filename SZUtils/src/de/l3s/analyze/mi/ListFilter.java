package de.l3s.analyze.mi;

public interface ListFilter {
	public boolean accept(String classlable, String term, Double score, Integer frequency);

}
