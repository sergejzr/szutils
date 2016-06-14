package de.l3s.util.gnuplot;

import java.io.File;

public class PrecisionRecallGraph extends GnuplotGraph{
	Double BEP;

	public PrecisionRecallGraph(String title,File outputfile, Double BEP) {
		setXrange("0.0:1.0");
		setYrange("0.0:1.0");
		setXlable("Recall");
		setYlable("Precision");
		setOutputFile(outputfile);
		setTitle(title);
		this.BEP=BEP;
		
	}
	public PrecisionRecallGraph() {
		setXrange("0.0:1.0");
		setYrange("0.0:1.0");
		setXlable("Recall");
		setYlable("Precision");
		setTitle(title);

		
	}
	@Override
	protected void enhanceDeclaration(StringBuilder sb) {

	sb.append("plot '-' with linespoints notitle, \"-\" with points pt 7 ps 1.7 title \"BEP "+Math.round(getBEP()*100)/100.+"\"\n\n");
		
		
	}

	private Double getBEP() {
		// TODO Auto-generated method stub
		return BEP;
	}

	public static void main(String[] args) {
		PrecisionRecallGraph pg=new PrecisionRecallGraph("haarface",new File("E:\\gnuplottest\\haarfaces-frontalface_default-area.eps"),0.6543);
		System.out.println(pg.getHeader());
		System.out.println(pg.getDeclarations());
	
	}
	public void setBEP(double BEP) {
		this.BEP=BEP;
		
	}
	@Override
	protected void enhanceHeader(StringBuilder sb) {
		sb.append("set key inside left bottom\n");
	}
	
	
	
	
}