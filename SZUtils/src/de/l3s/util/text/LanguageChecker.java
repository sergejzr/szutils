package de.l3s.util.text;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Vector;

import org.knallgrau.utils.textcat.TextCategorizer;

public class LanguageChecker {



	public boolean isEnglish(String str, int step) {
		TextCategorizer guesser = new TextCategorizer();
		String classval = guesser.categorize(str);

		if (classval.compareTo("english") == 0) {
			return true;
		}
		if(step==0) return false;

		Map<String, Integer> map = guesser.getCategoryDistances(str);

		Vector<String> tags = new Vector<String>();
		for (String key : map.keySet()) {
			tags.add(key);
		}
		class LangComparator implements Comparator<String> {
			private Map<String, Integer> map;

			LangComparator(Map<String, Integer> map) {
				this.map = map;
			}

			public int compare(String o1, String o2) {

				return map.get(o1) - map.get(o2);
			}

		}
		Collections.sort(tags, new LangComparator(map));

		

		int pl = 0;
		for (int i = 0; i < tags.size(); i++) {
			if (tags.elementAt(i).compareTo("english") == 0) {
				pl = i;
			}
		}

		return pl < step;
		/*
		 * if(stop>pl){return true;} return false;
		 */
	}
}
