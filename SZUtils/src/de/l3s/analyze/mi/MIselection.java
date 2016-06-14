package de.l3s.analyze.mi;

import java.io.FilterOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;







//import meta.dbaccess.*; 
//import meta.dbinterfaces.*; 

//import weka.core.*; 
//import weka.classifiers.*; 
//import weka.classifiers.j48.*;
//import weka.filters.*; 

//import org.biojava.stats.svm.*;
//import org.biojava.stats.svm.tools.*;

//import meta.sparse.*; 

/**
 * Klasse zur MI-Selektion fuer binaere Klassifikation
 */
public class MIselection {

	public enum listtype {
		LIST_POSITIVE, LIST_NEGATIVE
	}

	private ArrayList positiveTrainList;
	private ArrayList negativeTrainList;

	private ArrayList positiveTermLists;
	private ArrayList negativeTermLists;

	private HashMap mapA;
	private HashMap mapB;
	// private HashMap posTerms;
	// private HashMap negTerms;
	private ArrayList posListMI;
	private ArrayList negListMI;
	private int numberOfDocs;
	private int posNumber;
	private int negNumber;
	private ArrayList posResultList;
	private ArrayList negResultList;
	Hashtable<String, Integer> cnts=new Hashtable<String, Integer>();

	/**
	 * Konstruktor
	 * 
	 * @param aPositiveTrainList
	 *            positive Beispiele faer Dokumente als "Terms"-Objecte
	 * @param aNegativeTrainList
	 *            negative Beispiele faer Dokumente als "Terms"-Objecte
	 */
	public MIselection(ArrayList aPositiveTrainList,
			ArrayList aNegativeTrainList) {
		setup(aPositiveTrainList,aNegativeTrainList);
	}
public	Hashtable<String, Integer> getStats(){return cnts;}
	private void setup(ArrayList aPositiveTrainList,
			ArrayList aNegativeTrainList) {
		positiveTrainList = aPositiveTrainList;
		negativeTrainList = aNegativeTrainList;
		posNumber = positiveTrainList.size();
		negNumber = negativeTrainList.size();
		numberOfDocs = posNumber + negNumber;
		
	}

	public MIselection(ArrayList<ArrayList<String>> aPositiveTrainList,
			ArrayList<ArrayList<String>>  aNegativeTrainList, boolean strings) {
		
		ArrayList<Terms> positiveList = new ArrayList<Terms>();
		ArrayList<Terms> negativeList = new ArrayList<Terms>();
		
		
		
		
		
		for(ArrayList<String> entry:aPositiveTrainList)
		{
			ArrayList<TermPos> photo1pos = new ArrayList<TermPos>();
			for(String s:entry)
			{
				photo1pos.add(new TermPos(s, 0));
			}
			
			positiveList.add(new Terms(photo1pos));
		}
		for(ArrayList<String> entry:aNegativeTrainList)
		{
			ArrayList<TermPos> photo1pos = new ArrayList<TermPos>();
			for(String s:entry)
			{
				photo1pos.add(new TermPos(s,0));
			}
			
			negativeList.add(new Terms(photo1pos));
		}
		
		setup(positiveList, negativeList);
		
		
	}

	public ArrayList getTopNposResultList()
	{
		ArrayList resultSet = new ArrayList();
		int i = 0;
		for (ListIterator iter = posResultList.listIterator(); iter.hasNext()
				; i++) {
			TermMI tmi = (TermMI) iter.next();
			resultSet.add(tmi.term);
		}
		return resultSet;
	}	
	
	public ArrayList getTopNnegResultList()
	{
		ArrayList resultSet = new ArrayList();
		int i = 0;
		for (ListIterator iter = negResultList.listIterator(); iter.hasNext()
				; i++) {
			TermMI tmi = (TermMI) iter.next();
			resultSet.add(tmi.term);
		}
		return resultSet;
	}
	
	public Hashtable<String, Double> getTopNposResultScoreList() {
	
		int i = 0;
		Hashtable<String, Double> resultSet=new Hashtable<String, Double>();
		for (ListIterator iter = posResultList.listIterator(); iter.hasNext()
				; i++) {
			TermMI tmi = (TermMI) iter.next();
			resultSet.put(tmi.term,tmi.value);
		}
		return resultSet;
	}
	
	public Hashtable<String, Double> getTopNnegResultScoreList() {
	
		int i = 0;
		Hashtable<String, Double> resultSet=new Hashtable<String, Double>();
		for (ListIterator iter = negResultList.listIterator(); iter.hasNext()
				; i++) {
			TermMI tmi = (TermMI) iter.next();
			resultSet.put(tmi.term,tmi.value);
		}
		return resultSet;
	}
	
	public ArrayList getTopNposResultList(int n) {
		ArrayList resultSet = new ArrayList();
		int i = 0;
		for (ListIterator iter = posResultList.listIterator(); iter.hasNext()
				&& i < n; i++) {
			TermMI tmi = (TermMI) iter.next();
			resultSet.add(tmi.term);
		}
		return resultSet;
	}

	public ArrayList getTopNnegResultList(int n) {
		ArrayList resultSet = new ArrayList();
		int i = 0;
		for (ListIterator iter = negResultList.listIterator(); iter.hasNext()
				&& i < n; i++) {
			TermMI tmi = (TermMI) iter.next();
			resultSet.add(tmi.term);
		}
		return resultSet;
	}

	public HashSet getTopNposResults(int n) {
		HashSet resultSet = new HashSet();
		int i = 0;
		for (ListIterator iter = posResultList.listIterator(); iter.hasNext()
				&& i < n; i++) {
			TermMI tmi = (TermMI) iter.next();
			resultSet.add(tmi.term);
		}
		return resultSet;
	}

	public HashSet getTopNnegResults(int n) {
		HashSet resultSet = new HashSet();
		int i = 0;
		for (ListIterator iter = negResultList.listIterator(); iter.hasNext()
				&& i < n; i++) {
			TermMI tmi = (TermMI) iter.next();
			resultSet.add(tmi.term);
		}
		return resultSet;
	}

	public void computePositiveAndNegativeMIvalues() {
		buildTermLists();
		scanTermLists();
		computeValues();
	}

	private void buildTermLists() {
		positiveTermLists = new ArrayList();
		negativeTermLists = new ArrayList();
		for (int i = 0; i < positiveTrainList.size(); i++) {
			ArrayList termList = new ArrayList();
			HashSet termSet = new HashSet();
			Terms terms = (Terms) positiveTrainList.get(i);
			ArrayList tpl = terms.getTermPosList();
			for (int j = 0; j < tpl.size(); j++) {
				TermPos tPos = (TermPos) tpl.get(j);
				String term = tPos.getTerm();
				termSet.add(term);
				Integer cnt = cnts.get(term);
				if(cnt==null) cnt=0;
				cnts.put(term, cnt+1);
			}
			termList.addAll(termSet);
			positiveTermLists.addAll(termList);
		}
		for (int i = 0; i < negativeTrainList.size(); i++) {
			ArrayList termList = new ArrayList();
			HashSet termSet = new HashSet();
			Terms terms = (Terms) negativeTrainList.get(i);
			ArrayList tpl = terms.getTermPosList();
			for (int j = 0; j < tpl.size(); j++) {
				TermPos tPos = (TermPos) tpl.get(j);
				String term = tPos.getTerm();
				termSet.add(term);
				Integer cnt = cnts.get(term);
				if(cnt==null) cnt=0;
				cnts.put(term, cnt+1);
			}
			termList.addAll(termSet);
			negativeTermLists.addAll(termList);
		}
	}

	private void scanTermLists() {
		mapA = new HashMap();
		mapB = new HashMap();
		// System.out.println(positiveTermLists.size());
		// System.out.println(negativeTermLists.size());
		for (int i = 0; i < positiveTermLists.size(); i++) {
			String term = (String) positiveTermLists.get(i);
			// System.out.println(term);
			if (mapA.containsKey(term)) {
				int value = ((Integer) mapA.get(term)).intValue();
				value++;
				mapA.put(term, new Integer(value));
			} else {
				mapA.put(term, new Integer(1));
			}
		}
		for (int i = 0; i < negativeTermLists.size(); i++) {
			String term = (String) negativeTermLists.get(i);
			// System.out.println(term);
			if (mapB.containsKey(term)) {
				int value = ((Integer) mapB.get(term)).intValue();
				value++;
				mapB.put(term, new Integer(value));
			} else {
				mapB.put(term, new Integer(1));
			}
		}
		// System.out.println(mapA.size());
		// System.out.println(mapB.size());
	}

	private void computeValues() {
		posResultList = new ArrayList();
		negResultList = new ArrayList();
		// posTerms = new HashMap();
		// negTerms = new HashMap();
		HashSet allTerms = new HashSet();
		allTerms.addAll(mapA.keySet());
		allTerms.addAll(mapB.keySet());
		// System.out.println(allTerms.size());
		ArrayList allTermsList = new ArrayList();
		allTermsList.addAll(allTerms);
		for (int i = 0; i < allTermsList.size(); i++) {
			String term = (String) allTermsList.get(i);
			double a = 0;
			if (mapA.containsKey(term)) {
				a = ((Integer) mapA.get(term)).doubleValue();
			}
			double b = 0;
			if (mapB.containsKey(term)) {
				b = ((Integer) mapB.get(term)).doubleValue();
			}
			double n = (double) numberOfDocs;
			double c = (double) posNumber - a;
			double d = (double) negNumber - b;
			// posTerms.put(term, new Double(a/n *
			// Math.log(a*n/((a+b)+(a+c)))));
			// negTerms.put(term, new Double(b/n *
			// Math.log(b*n/((b+a)+(b+d)))));
			if (a != 0) {
				posResultList.add(new TermMI(term, (a / n)
						* Math.log(a * n / ((a + b) * (a + c))) / Math.log(2)));
			}
			if (b != 0) {
				negResultList.add(new TermMI(term, (b / n)
						* Math.log(b * n / ((b + a) * (b + d))) / Math.log(2)));

			}
		}
		Collections.sort(posResultList);
		Collections.sort(negResultList);

	}

	// Tupel aus Termen und MI, Wert
	private class TermMI implements Comparable {
		public String term;
		public double value;

		public TermMI(String term, double value) {
			this.term = term;
			this.value = value;
		}

		public int compareTo(Object o) {
			TermMI tm = (TermMI) o;
			if (this.value < tm.value) {
				return 1;
			} else if (this.value > tm.value) {
				return -1;
			} else {
				return 0;
			}
		}
	}

	public static void test1() {

		try {
			// TODO:
			Connection con = null;// getyourconnection
			PreparedStatement st = con
					.prepareStatement("SELECT t.photoid,t.value FROM XXXXXXX");

			ResultSet posset = st.executeQuery();

			ArrayList positiveList = new ArrayList();

			String pid = "";
			Terms terms1pos = null;
			ArrayList photo1pos = null;
			while (posset.next()) {
				String photoid = posset.getString("photoid");

				if (pid.compareTo(photoid) != 0) {
					if (photo1pos != null) {
						positiveList.add(terms1pos);
					}
					pid = photoid;
					photo1pos = new ArrayList();
					terms1pos = new Terms(photo1pos);
				}

				photo1pos.add(new TermPos(posset.getString("value"), 0));
				if (posset.isLast()) {
					positiveList.add(terms1pos);
				}
			}

			ArrayList negativeList = new ArrayList();

			st = con.prepareStatement("SELECT t.photoid,t.value FROM `sample_privacy_info` i JOIN sample_tag t ON (i.photoid=t.photoid) WHERE i.isPrivate='yes' ORDER BY t.photoid ");

			posset = st.executeQuery();

			pid = "";
			terms1pos = null;
			photo1pos = null;
			while (posset.next()) {
				String photoid = posset.getString("photoid");

				if (pid.compareTo(photoid) != 0) {
					if (photo1pos != null) {
						negativeList.add(terms1pos);
					}
					pid = photoid;
					photo1pos = new ArrayList();
					terms1pos = new Terms(photo1pos);
				}

				photo1pos.add(new TermPos(posset.getString("value"), 0));
				if (posset.isLast()) {
					negativeList.add(terms1pos);
				}
			}

			MIselection mi = new MIselection(positiveList, negativeList);
			mi.computePositiveAndNegativeMIvalues();
			System.out.println("mi.getTopNposResults(20) "
					+ mi.getTopNposResults(20));
			System.out.println("mi.getTopNnegResults(20) "
					+ mi.getTopNnegResults(20));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("ready");
		/*
		 * ArrayList positiveList =
		 * NewsBase.getAllTermsList(NewsBase.getIDList("sci.med", 0 , 1000));
		 * ArrayList negativeList =
		 * NewsBase.getAllTermsList(NewsBase.getIDList("sci.space", 0 , 1000));
		 * System.out.println("Berechnung ...."); MIselection msel = new
		 * MIselection(positiveList, negativeList);
		 * msel.computePositiveAndNegativeMIvalues();
		 * System.out.println(msel.getTopNposResults(10));
		 * System.out.println("***************************************");
		 * System.out.println(msel.getTopNnegResults(10)); for (int i = 0; i <
		 * 100; i++) {
		 * System.out.println(((TermMI)msel.posResultList.get(i)).term + " = " +
		 * ((TermMI)msel.posResultList.get(i)).value); }
		 * System.out.println("================================"); for (int i =
		 * 0; i < 100; i++) {
		 * System.out.println(((TermMI)msel.negResultList.get(i)).term + " = " +
		 * ((TermMI)msel.negResultList.get(i)).value); }
		 */

	}

	public static Hashtable<listtype, Collection<String>> selectFeatures(
			int number, Connection con, String sql, String[] stopwords) {
		Hashtable<listtype, Collection<String>> ret = new Hashtable<listtype, Collection<String>>();

		Hashtable<String, String> stopwordstable = new Hashtable<String, String>();
		for (String stopWord : stopwords) {
			stopwordstable.put(stopWord, "");
		}

		Hashtable<listtype, ArrayList<Terms>> lists = MIselection.getTagLists(
				number, con, sql);

		ArrayList<Terms> positiveList = lists.get(listtype.LIST_POSITIVE);
		ArrayList<Terms> negativeList = lists.get(listtype.LIST_NEGATIVE);

		MIselection mi = new MIselection(positiveList, negativeList);
		mi.computePositiveAndNegativeMIvalues();
		ret.put(listtype.LIST_POSITIVE, mi.getTopNposResults(number));
		ret.put(listtype.LIST_NEGATIVE, mi.getTopNnegResults(number));
		return ret;
		/*
		 * System.out.println("mi.getTopNposResults(20) "+mi.getTopNposResults(20
		 * ));
		 * System.out.println("mi.getTopNnegResults(20) "+mi.getTopNnegResults
		 * (20));
		 */
	}

	public static Hashtable<listtype, Collection<String>> selectFeatureLists(
			int number, Connection con, String sql, String[] stopwords) {
		// Hashtable<String, HashSet<String>> ret = new Hashtable<String,
		// HashSet<String>>();
		Hashtable<listtype, Collection<String>> ret = new Hashtable<listtype, Collection<String>>();

		Hashtable<listtype, ArrayList<Terms>> lists = MIselection.getTagLists(
				number, con, sql);

		ArrayList<Terms> positiveList = lists.get(listtype.LIST_POSITIVE);
		ArrayList<Terms> negativeList = lists.get(listtype.LIST_NEGATIVE);

		MIselection mi = new MIselection(positiveList, negativeList);
		mi.computePositiveAndNegativeMIvalues();

		ret.put(listtype.LIST_POSITIVE, mi.getTopNposResultList(number));
		ret.put(listtype.LIST_NEGATIVE, mi.getTopNnegResultList(number));
		return ret;
		/*
		 * System.out.println("mi.getTopNposResults(20) "+mi.getTopNposResults(20
		 * ));
		 * System.out.println("mi.getTopNnegResults(20) "+mi.getTopNnegResults
		 * (20));
		 */
	}

	/**
	 * SQL has to be of the form: SELECT a as id, b as tags, c as title, d as
	 * description, e as lable lable can be {private,public,undecidable}
	 * 
	 * @param number
	 * 
	 * @param con
	 * @param stopwordstable
	 * @return
	 */
	public static Hashtable<listtype, ArrayList<Terms>> getTagLists(int number,
			Connection con, String sql) {
		Hashtable<listtype, ArrayList<Terms>> ret = new Hashtable<listtype, ArrayList<Terms>>();
		ArrayList<Terms> positiveList = new ArrayList<Terms>();
		ArrayList<Terms> negativeList = new ArrayList<Terms>();
		ret.put(listtype.LIST_POSITIVE, positiveList);
		ret.put(listtype.LIST_NEGATIVE, negativeList);
		int fcount = 0;
		try {
			if (con == null) {
				con = null;// getyourconnection
			}
			if (sql == null)
				sql = " SELECT photoid, value, lable FROM XXXXX";// TODO: your
																	// table
			PreparedStatement st = null;
			ResultSet rs = null;
			int min = 1000000;

			String selecttrain = "SELECT COUNT(*) as cnt,x.lable FROM (" + sql
					+ ") x GROUP BY lable ";

			st = con.prepareStatement(selecttrain);
			rs = st.executeQuery();

			while (rs.next()) {
				int curcnt = rs.getInt("cnt");
				String lable = rs.getString("lable");
				if (lable.compareTo("publc") == 0
						|| lable.compareTo("private") == 0) {
					if (min > curcnt) {
						min = curcnt;
					}
				}
			}
			min = 40000;
			rs.close();
			st.close();

			st = con.prepareStatement(sql);
			System.out.println(sql);
			rs = st.executeQuery();
			ArrayList<Terms> curlist = null;
			Terms terms1pos = null;
			ArrayList<TermPos> photo1pos = null;

			int cntall = 0;
			while (rs.next()) {
				cntall++;

				if (cntall % 1000 == 0) {
					System.out.println("MI got " + cntall);
				}

				String photoid = rs.getString("photoid");

				String lable = rs.getString("lable");

				if (photoid == null) {
					continue;
				}

				// tagstext=

				if (lable.compareTo("public") == 0) {
					if (negativeList.size() >= min) {
						continue;
					}
					curlist = negativeList;
				} else {
					if (lable.compareTo("private") == 0) {
						if (positiveList.size() >= min) {
							continue;
						}
						curlist = positiveList;

					} else {
						continue;
					}
				}

				photo1pos = new ArrayList<TermPos>();

				Vector<String> terms = new Vector<String>();// TODO: get your
															// term list
				// allfeatures.getSimpleTermList();

				for (String term : terms) {

					if (term.length() == 0) {
						// System.out.println("zero found");
						continue;
					}
					if (term.startsWith("childr")) {
						int z = 0;
						z++;
						z++;
					}
					photo1pos.add(new TermPos(term, 0));
				}

				if (photo1pos.size() > 0) {
					terms1pos = new Terms(photo1pos);
					curlist.add(terms1pos);
				}

			}
			rs.close();
			st.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * main-Methode zum Testen
	 */
	public static void main(String[] args) {
		
		
		ArrayList<Terms> positiveList = new ArrayList<Terms>();
		ArrayList<Terms> negativeList = new ArrayList<Terms>();
		
		//Texts about summer
		String[][] positivetexts=new String[][]
		                                  {
				new String[]{"sun","sommer","sea","and"},
				new String[]{"sun","boat","sea","the"},
				new String[]{"sun","sommer","ice","to"},
				new String[]{"sun","cold","sea","to"},
				new String[]{"sun","warm","boat","but"},
		                                 };
		
		String[][] negativetexts=new String[][]
			                                  {
					new String[]{"wind","winter","ice","and"},
					new String[]{"wind","skate","ice","the"},
					new String[]{"sun","winter","snow","to"},
					new String[]{"wind","warm","skate","to"},
					new String[]{"wind","cold","skate","but"},
			                                  };
		
		
		
		for(String[] entry:positivetexts)
		{
			ArrayList<TermPos> photo1pos = new ArrayList<TermPos>();
			for(int i=0;i<entry.length;i++)
			{
				String term=entry[i];
				photo1pos.add(new TermPos(term, 0));//i may be whatever, not need for MI
			}
			positiveList.add(new Terms(photo1pos));
		}
		
		for(String[] entry:negativetexts)
		{
			ArrayList<TermPos> photo1pos = new ArrayList<TermPos>();
			for(int i=0;i<entry.length;i++)
			{
				String term=entry[i];
				photo1pos.add(new TermPos(term, i));//i may be whatever, not need for MI
			}
			negativeList.add(new Terms(photo1pos));
		}
		
		
		
	

		
		MIselection mi = new MIselection(positiveList, negativeList);
		
		mi.computePositiveAndNegativeMIvalues();
		
		
		
		System.out.println("mi.getTopNposResults() "
				+ mi.getTopNposResultList());
		System.out.println("mi.getTopNnegResults() "
				+ mi.getTopNnegResultList());
		
		
		int top=5;
		System.out.println("mi.getTopNposResults("+top+") "
				+ mi.getTopNposResultList(top));
		System.out.println("mi.getTopNnegResults("+top+") "
				+ mi.getTopNnegResultList(top));

	}
	public void printScores(int top) {printScores(top, null,null,System.out, null);}
	
	public void printScores(int top, String poslable,String neglable, OutputStream os, ListFilter f) {
	
		NumberFormat formatter = new DecimalFormat("#0.00000000000000000000");     
	
		PrintStream ps=new PrintStream(os);
		if(poslable==null) poslable="pos";
		
		ps.println(poslable+"("+top+")");
		Iterator it = getTopNposResultList(top).iterator();
		Hashtable<String, Double> scores = getTopNposResultScoreList();
		while(it.hasNext())
		{
			String term=it.next().toString();
			if(f!=null&&!f.accept(poslable,term,scores.get(term),cnts.get(term))) continue;
			ps.println(term+"\t"+formatter.format(scores.get(term)));
			
			
		}
		ps.println();
		if(neglable==null) neglable="neg";
		ps.println((neglable)+"("+top+")");
		 it = getTopNnegResultList(top).iterator();
		 scores = getTopNnegResultScoreList();
		while(it.hasNext())
		{
			String term=it.next().toString();
			if(f!=null&&!f.accept(neglable,term,scores.get(term),cnts.get(term))) continue;
			ps.println(term+"\t"+formatter.format(scores.get(term)));
			
		}
		
	}
	public void printScoresReverse(int bottom) {
	
		NumberFormat formatter = new DecimalFormat("#0.00000000000000000000");     
	
		
	
		System.out.println("last pos("+bottom+")");
		List olist = getTopNposResultList();
		Collections.reverse(olist);
		olist=olist.subList(0, Math.min(olist.size(), bottom));
		Iterator it = olist.iterator();
		Hashtable<String, Double> scores = getTopNposResultScoreList();
		while(it.hasNext())
		{
			String term=it.next().toString();
			System.out.println(term+"\t"+formatter.format(scores.get(term)));
			
			
		}
		System.out.println();
		System.out.println("last neg("+bottom+")");
		 olist = getTopNnegResultList();
		Collections.reverse(olist);
		olist=olist.subList(0, Math.min(olist.size(), bottom));
		 it = olist.iterator();
		 scores = getTopNnegResultScoreList();
		while(it.hasNext())
		{
			String term=it.next().toString();
			System.out.println(term+"\t"+formatter.format(scores.get(term)));
			
		}
		
	}

}
