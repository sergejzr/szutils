package de.l3s.analyze.mi;



public  class TermMI implements Comparable<TermMI> {
	public String term;
	public double value;

	public TermMI(String term, double value) {
		this.term = term;
		this.value = value;
	}



	@Override
	public int compareTo(TermMI tm) {
	
		if (this.value < tm.value) {
			return 1;
		} else if (this.value > tm.value) {
			return -1;
		} else {
			return 0;
		}
	}
}

