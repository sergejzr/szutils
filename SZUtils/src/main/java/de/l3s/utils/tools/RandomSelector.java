package de.l3s.utils.tools;

import java.util.Date;
import java.util.Random;

public class RandomSelector {
	double proc;
	private Random ran;
	double factor=1000000;
	int range;
	public RandomSelector(double proc,long seed) {
		this.proc=proc/100.;
		
		 ran = new Random(seed);
	}
	
	public RandomSelector(int population,long nrtoselect) {
		this((nrtoselect*100./population));
		
	}
	
	public RandomSelector(double proc) {
	this(proc, (new Date()).getTime());
	}
	
	public boolean isNext()
	{
		double rand=ran.nextDouble();
		return rand<=proc;
	}
	
public static void main(String[] args) {
	RandomSelector r=new RandomSelector(399758,300);
	int good=0,bad=0;
	for(int i=0;i<399758;i++)
	{
		if(r.isNext()) good++; else bad++;
	}
	
	System.out.println("good="+good+", bad="+bad);
}

}
