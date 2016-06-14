package de.l3s.util.analyze.diversity.toolbox;

import java.util.Set;

public class Jaccard {
	public static double jaccardSimilarity(Set<?> d1, Set<?> d2) {
		int overlap = 0;
		Set<?> shortd, longd;
		if (d1.size() > d2.size()) {
			shortd = d2;
			longd = d1;
		} else {
			shortd = d1;
			longd = d2;
		}
		for (Object d : shortd) {
			if (longd.contains(d)) {
				overlap++;
			}
		}
		return (overlap * 1.) / (d1.size() + d2.size());
	}
}
