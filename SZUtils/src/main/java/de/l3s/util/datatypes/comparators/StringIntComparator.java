package de.l3s.util.datatypes.comparators;

import java.util.Comparator;

import de.l3s.util.datatypes.pairs.StringIntPair;

public class StringIntComparator implements Comparator<StringIntPair> {

	@Override
	public int compare(StringIntPair o1, StringIntPair o2) {
		return o1.getInteger().compareTo(o2.getInteger());
	}

}
