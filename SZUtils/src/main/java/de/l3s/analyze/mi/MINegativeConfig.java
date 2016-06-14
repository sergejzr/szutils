package de.l3s.analyze.mi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class MINegativeConfig {

	ArrayList<String> negativeTermLists;
	HashMap<String, Integer> mapB; Hashtable<String, TermMI> globalidx;

	public MINegativeConfig(ArrayList<String> negativeTermLists,
			HashMap<String, Integer> mapB, Hashtable<String, TermMI> globalidx) {
		super();
		this.negativeTermLists = negativeTermLists;
		this.mapB = mapB;
		this.globalidx = globalidx;
	}

	public ArrayList<String> getNegativeTermLists() {
		return negativeTermLists;
	}

	public HashMap<String, Integer> getMapB() {
		return mapB;
	}

	public Hashtable<String, TermMI> getGlobalidx() {
		return globalidx;
	}

}
