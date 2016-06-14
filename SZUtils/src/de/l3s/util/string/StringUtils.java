package de.l3s.util.string;

import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	public static String join(String separator, String[] array, int startidx,
			int num) {
		StringBuffer sbBuffer = new StringBuffer();
		int idx = 0;
		int cnt = 0;
		for (String s : array) {
			if (idx++ < startidx) {
				continue;
			}
			if (cnt > num) {
				break;
			}
			if (sbBuffer.length() > 0) {
				sbBuffer.append(",");
			}
			sbBuffer.append(s);
			cnt++;
		}
		return sbBuffer.toString();
	}

	public static String readSubstring(String in, int startIdx,
			String startString, String endString) {
		int startOf = in.indexOf(startString, startIdx) + startString.length();
		int endOf = in.indexOf(endString, startOf);

		return in.substring(startOf, endOf);
	}

	public static String join(String separator, List<String> in) {
		String terms[] = new String[in.size()];
		in.toArray(terms);
		return join(separator, terms, 0, terms.length);
	}

	public static Vector<String> getWords(String cleaned) {Pattern pattern = Pattern.compile("[\\wßöäüÖÜÄ]+");
	
	Matcher matcher = pattern.matcher(cleaned);

	Vector<String> words=new Vector<String>();
	
	while (matcher.find()) {
		
		String word = matcher.group().trim();
		
		words.add(word);
	}
	return words;
	}

}
