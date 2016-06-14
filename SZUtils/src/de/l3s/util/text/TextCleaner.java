package de.l3s.util.text;

import java.util.List;
import java.util.Vector;

import de.l3s.util.config.CrawlProperties;


public class TextCleaner {
	
	LanguageChecker lch=new LanguageChecker();
public List<String> clean(List<String> input,CrawlProperties props)
{
	Vector<String> ret=new Vector<String>();
	Integer minlength = props.getInt(CrawlProperties.props.MINIMAL_TAG_LENGTH);
	Integer maxlength = props.getInt(CrawlProperties.props.MAXIMAL_TAG_LENGTH);
	Integer englishlevel  = props.getInt(CrawlProperties.props.ENGLISHLEVEL);
	Boolean alphanum  = props.getBoolean(CrawlProperties.props.ALPHAONLY);
	
	for(String s:input)
	{
		s=s.trim();
		if(alphanum!=null&&alphanum){s=makeAlpha(s);}
		if(minlength!=null&&s.length()<minlength){continue;}
		if(maxlength!=null&&s.length()> maxlength){continue;}
		if(englishlevel!=null&&!lch.isEnglish(s, englishlevel)){continue;}		
		ret.add(s);
	}
	
	return ret;
}

private  String makeAlpha(String str) {

	StringBuilder ret=new StringBuilder();
	for(int i=0;i<str.length();i++)
		{
		char chr=str.charAt(i);
		if(Character.isLetter(chr))
			{
			ret.append(chr);
			}
		}
	return ret.toString();

}

public List<String> clean(String[] split, CrawlProperties props) {
	Vector<String> tc=new Vector<String>();
	for(String s:split)
	{
		tc.add(s);
	}
	return clean(tc,props);
}
}
