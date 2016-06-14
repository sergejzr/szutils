package de.l3s.util.analyze.diversity.toolbox;

import de.l3s.util.analyze.diversity.rdj.DivDocument2;
import de.l3s.util.analyze.diversity.rdj.JaccardDivDocument;
import de.l3s.util.analyze.diversity.rdj.SimilarityComparator;

public class JaccardSimilarityComparator implements SimilarityComparator {

	@Override
	public double similarity(DivDocument2 doc1, DivDocument2 doc2) {
		return Jaccard.jaccardSimilarity((JaccardDivDocument)doc1,
				(JaccardDivDocument)doc2);
	}

}
