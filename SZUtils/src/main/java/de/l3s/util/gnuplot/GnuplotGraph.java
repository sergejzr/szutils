package de.l3s.util.gnuplot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public abstract class GnuplotGraph {

	String title, font="Helvetica", xlable, ylable, xrange, yrange, 
			outputImageFormat = "postscript enhanced eps";
	Integer fontsize=32;
	public Integer getFontsize() {
		return fontsize;
	}
	public void setFontsize(Integer fontsize) {
		this.fontsize = fontsize;
	}
	public String getTitlefont() {
		return titlefont;
	}
	public void setTitlefont(String titlefont) {
		this.titlefont = titlefont;
	}

	File datafile;
	File outputFile;
	private String titlefont="Helvetica,32";

	public  String getTitleFont() {
		// TODO Auto-generated method stub
		return titlefont;
	}
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
	}

	public String getXlable() {
		return xlable;
	}

	public void setXlable(String xlable) {
		this.xlable = xlable;
	}

	public String getYlable() {
		return ylable;
	}

	public void setYlable(String ylable) {
		this.ylable = ylable;
	}

	public String getXrange() {
		return xrange;
	}

	public void setXrange(String xrange) {
		this.xrange = xrange;
	}

	public String getYrange() {
		return yrange;
	}

	public void setYrange(String yrange) {
		this.yrange = yrange;
	}

	public File getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}

	public String getOutputImageFormat() {
		return outputImageFormat;
	}

	public void setOutputImageFormat(String outputImageFormat) {
		this.outputImageFormat = outputImageFormat;
	}

	public File getDatafile() {
		return datafile;
	}

	public void setDatafile(File datafile) {
		this.datafile = datafile;
	}

	public String getDeclarations()
	{
		StringBuilder sb=new StringBuilder();
		sb.append("set output \"" + getOutputFile()+"\"\n");
		sb.append("set title \""+getTitle()+"\" font \""+getTitleFont()+"\" noenhanced\n");
	
		enhanceDeclaration(sb);
		return sb.toString();
	}
	protected abstract void enhanceDeclaration(StringBuilder sb);
	protected abstract void enhanceHeader(StringBuilder sb);
	public void store(File output) throws IOException {
		FileWriter fw = new FileWriter(output);
		fw.write(getDeclarations());
		fw.flush();
		fw.close();
	}

	public String getHeader() {
		StringBuilder sb=new StringBuilder();
		sb.append("\nset terminal "+getOutputImageFormat() + " \""+getFont()+"\" "+getFontsize()+" \n");
		sb.append("set xlabel \""+getXlable()+"\"\n");
		sb.append("set ylabel \""+getYlable()+"\"\n");
		if(getXrange()!=null)
		sb.append("set xrange["+getXrange()+"]\n");
		if(getYrange()!=null)
		sb.append("set yrange["+getYrange()+"]\n");
		enhanceHeader(sb);
		return sb.toString();
	}
	
}
