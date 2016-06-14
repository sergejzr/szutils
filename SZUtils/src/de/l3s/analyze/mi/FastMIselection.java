package de.l3s.analyze.mi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Vector;



import de.l3s.util.date.TimeUtils;

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
public class FastMIselection {

	public enum listtype {
		LIST_POSITIVE, LIST_NEGATIVE
	}

	private ArrayList<FastTerms> positiveTrainList;
	private ArrayList<FastTerms> negativeTrainList;

	private ArrayList<String> positiveTermLists;
	private ArrayList<String> negativeTermLists;

	private HashMap<String, Integer> mapA;
	private HashMap<String, Integer> mapB;
	// private HashMap posFastTerms;
	// private HashMap negFastTerms;

	private int numberOfDocs;
	private int posNumber;
	private int negNumber;
	private List<TermMI> posResultList;
	private List<TermMI> negResultList;
	private ArrayList<TermMI> negResultListConst;
	static final private double mathlog2=Math.log(2);

	/**
	 * Konstruktor
	 * 
	 * @param aPositiveTrainList
	 *            positive Beispiele faer Dokumente als "FastTerms"-Objecte
	 * @param aNegativeTrainList
	 *            negative Beispiele faer Dokumente als "FastTerms"-Objecte
	 */
	public FastMIselection(ArrayList<FastTerms> aPositiveTrainList,
			ArrayList<FastTerms> aNegativeTrainList) {
		positiveTrainList = aPositiveTrainList;
		negativeTrainList = aNegativeTrainList;
		posNumber = positiveTrainList.size();
		negNumber = negativeTrainList.size();
		numberOfDocs = posNumber + negNumber;
	}

	public ArrayList<String> getTopNposResultList() {
		ArrayList<String> resultSet = new ArrayList<String>(posResultList.size()+5);

		for (ListIterator<TermMI> iter = posResultList.listIterator(); iter
				.hasNext(); ) {
			TermMI tmi = iter.next();
			resultSet.add(tmi.term);
		}
		return resultSet;
	}

	public ArrayList<String> getTopNnegResultList() {
		ArrayList<String> resultSet = new ArrayList<String>(negResultList.size()+5);
	
		for (ListIterator<TermMI> iter = negResultList.listIterator(); iter
				.hasNext(); ) {
			TermMI tmi = iter.next();
			resultSet.add(tmi.term);
		}
		return resultSet;
	}

	public Hashtable<String, Double> getTopNposResultScoreList() {

	
		Hashtable<String, Double> resultSet = new Hashtable<String, Double>(posResultList.size()+5);
		for (ListIterator<TermMI> iter = posResultList.listIterator(); iter
				.hasNext(); ) {
			TermMI tmi = iter.next();
			resultSet.put(tmi.term, tmi.value);
		}
		return resultSet;
	}

	public Hashtable<String, Double> getTopNnegResultScoreList() {

	
		Hashtable<String, Double> resultSet = new Hashtable<String, Double>(negResultList.size()+5);
		for (ListIterator<TermMI> iter = negResultList.listIterator(); iter
				.hasNext(); ) {
			TermMI tmi = iter.next();
			resultSet.put(tmi.term, tmi.value);
		}
		return resultSet;
	}

	public ArrayList<String> getTopNposResultList(int n) {
		ArrayList<String> resultSet = new ArrayList<String>(n+5);
		int i = 0;
		for (ListIterator<TermMI> iter = posResultList.listIterator(); iter
				.hasNext() && i < n; i++) {
			TermMI tmi = iter.next();
			resultSet.add(tmi.term);
		}
		return resultSet;
	}

	public ArrayList<String> getTopNnegResultList(int n) {
		ArrayList<String> resultSet = new ArrayList<String>(n+5);
		int i = 0;
		for (ListIterator<TermMI> iter = negResultList.listIterator(); iter
				.hasNext() && i < n; i++) {
			TermMI tmi = iter.next();
			resultSet.add(tmi.term);
		}
		return resultSet;
	}

	public HashSet<String> getTopNposResults(int n) {
		HashSet<String> resultSet = new HashSet<String>(n+5);
		int i = 0;
		for (ListIterator<TermMI> iter = posResultList.listIterator(); iter
				.hasNext() && i < n; i++) {
			TermMI tmi = iter.next();
			resultSet.add(tmi.term);
		}
		return resultSet;
	}

	public HashSet<String> getTopNnegResults(int n) {
		HashSet<String> resultSet = new HashSet<String>(n+5);
		int i = 0;
		for (ListIterator<TermMI> iter = negResultList.listIterator(); iter
				.hasNext() && i < n; i++) {
			TermMI tmi = iter.next();
			resultSet.add(tmi.term);
		}
		return resultSet;
	}

	public void computePositiveAndNegativeMIvalues(ArrayList<String> negList, HashMap<String, Integer> bmap, Hashtable<String, TermMI> globalidx) {
		buildTermLists(negList);
		scanTermLists(bmap);
		computeValues(globalidx,false );
	}
	

	
	public MINegativeConfig computePositiveAndNegativeConfig() {
		buildTermLists();
		scanTermLists();
		Hashtable<String, TermMI> globalidx;
		computeValues(globalidx=new Hashtable<String, TermMI>(), true);
		return new MINegativeConfig(negativeTermLists,mapB,globalidx);
	}
	
	
	long summillibuild=0;
	long summilliscan=0;
	long summillicompute=0;
	int cntall=0;
	public void computePositiveAndNegativeMIvalues(MINegativeConfig config) {
		Date start=new Date();
		buildTermLists(config.getNegativeTermLists());
		Date end=new Date();
		summillibuild+=end.getTime()-start.getTime();
		 start=new Date();
		scanTermLists(config.getMapB());
		 end=new Date();
			summilliscan+=end.getTime()-start.getTime();
			start=new Date();
		computeValues(config.getGlobalidx(),false );
		 end=new Date();
			summillicompute+=end.getTime()-start.getTime();
		cntall++;
		
		if(cntall%1000==0)
		{
			System.out.println("our times for  "+cntall+" records:");
			System.out.println("summillibuild='"+TimeUtils.millisToLongDHMS(summillibuild)+"', summilliscan='"+TimeUtils.millisToLongDHMS(summilliscan)+"', summillicompute='"+TimeUtils.millisToLongDHMS(summillicompute)+"'");
			
		}
	}
	
	public void computePositiveAndNegativeMIvalues() {
		buildTermLists();
		scanTermLists();
		computeValues();
	}

	private void buildTermLists(ArrayList<String> negList) {
		positiveTermLists = new ArrayList<String>();
	
		for(FastTerms FastTerms:positiveTrainList )
		{
			ArrayList<String> termList = new ArrayList<String>();
			HashSet<String> FastTermset = new HashSet<String>();
			 ArrayList<TermPos> tpl = FastTerms.getTermPosList();
			 for(TermPos tPos:tpl)
			 {
				 String term = tPos.getTerm();
					FastTermset.add(term);
			 }
			termList.addAll(FastTermset);
			positiveTermLists.addAll(termList);
		
		}
		negativeTermLists=negList;
		if(negativeTermLists==null){
			
		negativeTermLists = new ArrayList<String>();
		
		for(FastTerms FastTerms:negativeTrainList)
		{

			ArrayList<String> termList = new ArrayList<String>();
			HashSet<String> FastTermset = new HashSet<String>();

			 ArrayList<TermPos> tpl = FastTerms.getTermPosList();
			 
			 for(TermPos tPos:tpl)
			 {
					String term = tPos.getTerm();
					FastTermset.add(term);	
			 }

			termList.addAll(FastTermset);
			negativeTermLists.addAll(termList);
		
		}
	}
		
	}
	
	private void buildTermLists() {
		positiveTermLists = new ArrayList<String>();
	
		for(FastTerms FastTerms:positiveTrainList )
		{
			ArrayList<String> termList = new ArrayList<String>();
			HashSet<String> FastTermset = new HashSet<String>();
			 ArrayList<TermPos> tpl = FastTerms.getTermPosList();
			 for(TermPos tPos:tpl)
			 {
				 String term = tPos.getTerm();
					FastTermset.add(term);
			 }
			termList.addAll(FastTermset);
			positiveTermLists.addAll(termList);
		
		}

		if(negativeTermLists==null){
			
		negativeTermLists = new ArrayList<String>();
		
		for(FastTerms FastTerms:negativeTrainList)
		{

			ArrayList<String> termList = new ArrayList<String>();
			HashSet<String> FastTermset = new HashSet<String>();

			 ArrayList<TermPos> tpl = FastTerms.getTermPosList();
			 
			 for(TermPos tPos:tpl)
			 {
					String term = tPos.getTerm();
					FastTermset.add(term);	
			 }

			termList.addAll(FastTermset);
			negativeTermLists.addAll(termList);
		
		}
	}
		
	}
	
	private void scanTermLists(HashMap<String, Integer> bmap) {
		mapA = new HashMap<String, Integer>(positiveTermLists.size());
	
		// System.out.println(positiveTermLists.size());
		// System.out.println(negativeTermLists.size());
		for(String term:positiveTermLists)
		{
			// System.out.println(term);
			if (mapA.containsKey(term)) {
				int value = mapA.get(term).intValue();
				value++;
				mapA.put(term, value);
			} else {
				mapA.put(term, 1);
			}
		
		}
mapB=bmap;
		if(mapB==null){
			mapB = new HashMap<String, Integer>();
		
		for(String term:negativeTermLists)
		{
			// System.out.println(term);
			if (mapB.containsKey(term)) {
				int value = mapB.get(term).intValue();
				value++;
				mapB.put(term, (value));
			} else {
				mapB.put(term, 1);
			}
		
		}
		}

		// System.out.println(mapA.size());
		// System.out.println(mapB.size());
	}
	
	private void scanTermLists() {
		mapA = new HashMap<String, Integer>(positiveTermLists.size());
	
		// System.out.println(positiveTermLists.size());
		// System.out.println(negativeTermLists.size());
		for(String term:positiveTermLists)
		{
			// System.out.println(term);
			if (mapA.containsKey(term)) {
				int value = mapA.get(term).intValue();
				value++;
				mapA.put(term, (value));
			} else {
				mapA.put(term, 1);
			}
		
		}

		if(mapB==null){
			mapB = new HashMap<String, Integer>();
		
		for(String term:negativeTermLists)
		{
			// System.out.println(term);
			if (mapB.containsKey(term)) {
				int value = mapB.get(term).intValue();
				value++;
				mapB.put(term, (value));
			} else {
				mapB.put(term, 1);
			}
		}
		}

		// System.out.println(mapA.size());
		// System.out.println(mapB.size());
	}

	private void computeValues() {
		computeValues(null,true);
	}
	private void computeValues( Hashtable<String, TermMI> globalidx,boolean sortnegative ) {
		posResultList = new ArrayList<TermMI>();
		negResultList = new ArrayList<TermMI>();
		// posFastTerms = new HashMap();
		// negFastTerms = new HashMap();
		HashSet<String> allFastTerms = new HashSet<String>();
		allFastTerms.addAll(mapA.keySet());
		allFastTerms.addAll(mapB.keySet());
		// System.out.println(allFastTerms.size());
		ArrayList<String> allFastTermsList = new ArrayList<String>();
		allFastTermsList.addAll(allFastTerms);
		
		mapA.size();
		mapB.size();
		
		
		
		 negResultListConst = new ArrayList<TermMI>();
		 ArrayList<TermMI> negResultListToSort = new ArrayList<TermMI>();
		 
		 Hashtable<String, TermMI> sortedidx = new Hashtable<String, TermMI>();
		 boolean computefulllist=globalidx!=null&&globalidx.size()==0;

		 
		for(String term:allFastTermsList)
		{
			Integer ia=mapA.get(term);
			double a = 0.;
			if (ia!=null) {
				a = ia.doubleValue();
			}
			Integer ib = mapB.get(term);
			double b=0.;
			if (ib!=null) {
				b = ib.doubleValue();
			}
			double n = (double) numberOfDocs;
			double c = (double) posNumber - a;
			double d = (double) negNumber - b;
			
			
			if(computefulllist){
			TermMI cached = globalidx.get(term);
			if(cached==null)
			{
				globalidx.put(term, cached=new TermMI(term, (b / n)
						* Math.log(b * n / ((b + 0) * (b + d))) / mathlog2));
			}
			negResultListConst.add(cached);
			}
			if (a != 0) {
				posResultList.add(new TermMI(term, (a / n)
						* Math.log(a * n / ((a + b) * (a + c))) / mathlog2));
			}
			
		if(sortnegative){
			if (b != 0) {
				
			
				if(a==0)
				{
				
					
				}else
				{
				
					TermMI cached = new TermMI(term, (b / n)
							* Math.log(b * n / ((b + a) * (b + d))) / mathlog2);
					sortedidx.put(term, cached);
					negResultListToSort.add(cached);
				}
				/*
				negResultList.add(new TermMI(term, (b / n)
						* Math.log(b * n / ((b + a) * (b + d))) / mathlog2));
						* */

			}
		}
		
		}

		if(computefulllist)
		{
			Collections.sort(negResultListConst);
		}
		Collections.sort(posResultList);
	if(sortnegative){
		//(Collections.sort(negResultList);
		Collections.sort(negResultListToSort);
		HashSet<List<TermMI>> s=new HashSet<List<TermMI>>();
		s.add(negResultListConst);
		s.add(negResultListToSort);
		negResultList=merge(s,sortedidx);
		
	} else
	{
		//negResultList=new ArrayList<FastMIselection.TermMI>();
	}
	}
/*
	private ArrayList<TermMI> merge(ArrayList<TermMI> negResultListConst,
			ArrayList<TermMI> negResultListToSort,
			Hashtable<String, Double> sortedidx) {
		
		ArrayList<TermMI> ret=new ArrayList<FastMIselection.TermMI>();
		
		return ret;
	}
*/
	public static List<TermMI> merge(Set<List<TermMI>> lists,Hashtable<String, TermMI> winner) {
	    int totalSize = 0; // every element in the set
	    for (List<TermMI> l : lists) {
	            totalSize += l.size();
	    }

	    List<TermMI> result = new ArrayList<TermMI>(totalSize);

	    List<TermMI> lowest;

	    while (result.size() < totalSize) { // while we still have something to add
	            lowest = null;

	            for (List<TermMI> l : lists) {
	            	
	                    if (! l.isEmpty()) {
	                            if (lowest == null) {
	                                    lowest = l;
	                            }
	                            else if (l.get(0).compareTo(lowest.get(0)) <= 0) {
	                                    lowest = l;
	                            }
	                    }
	            }
	            TermMI t = lowest.get(0);
	            TermMI tc = winner.get(t.term);
	            TermMI toadd = lowest.get(0);
	            lowest.remove(0);
	            if(tc!=null&&tc.compareTo(t)!=0){ 
	            	continue;
	            }
	           
	            result.add(toadd);
	            
	          
	    }
	    return result;}
	
	// Tupel aus Termen und MI, Wert
	


	public static Hashtable<listtype, Collection<String>> selectFeatures(
			int number, Connection con, String sql, String[] stopwords) {
		Hashtable<listtype, Collection<String>> ret = new Hashtable<listtype, Collection<String>>();

		Hashtable<String, String> stopwordstable = new Hashtable<String, String>();
		for (String stopWord : stopwords) {
			stopwordstable.put(stopWord, "");
		}

		Hashtable<listtype, ArrayList<FastTerms>> lists = FastMIselection
				.getTagLists(number, con, sql);

		ArrayList<FastTerms> positiveList = lists.get(listtype.LIST_POSITIVE);
		ArrayList<FastTerms> negativeList = lists.get(listtype.LIST_NEGATIVE);

		FastMIselection mi = new FastMIselection(positiveList, negativeList);
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

		Hashtable<listtype, ArrayList<FastTerms>> lists = FastMIselection
				.getTagLists(number, con, sql);

		ArrayList<FastTerms> positiveList = lists.get(listtype.LIST_POSITIVE);
		ArrayList<FastTerms> negativeList = lists.get(listtype.LIST_NEGATIVE);

		FastMIselection mi = new FastMIselection(positiveList, negativeList);
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
	static Hashtable<listtype, ArrayList<FastTerms>> getTagLists(int number,
			Connection con, String sql) {
		Hashtable<listtype, ArrayList<FastTerms>> ret = new Hashtable<listtype, ArrayList<FastTerms>>();
		ArrayList<FastTerms> positiveList = new ArrayList<FastTerms>();
		ArrayList<FastTerms> negativeList = new ArrayList<FastTerms>();
		ret.put(listtype.LIST_POSITIVE, positiveList);
		ret.put(listtype.LIST_NEGATIVE, negativeList);
	
		try {
			if (con == null) {
				con = null;// getyourconnection
			}
			if (sql == null)
				sql = " SELECT photoid, value, lable FROM XXXXX";// TODO: your
																	// table
			PreparedStatement st = null;
			ResultSet rs = null;
			int min = 100000;

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
			ArrayList<FastTerms> curlist = null;
			FastTerms FastTerms1pos = null;
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

				Vector<String> FastTerms = new Vector<String>();
				for (String term : FastTerms) {

					if (term.length() == 0) {
						// System.out.println("zero found");
						continue;
					}
					
					photo1pos.add(new TermPos(term, 0));
				}

				if (photo1pos.size() > 0) {
					FastTerms1pos = new FastTerms(photo1pos);
					curlist.add(FastTerms1pos);
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
	public static void main(String[] args) 
	{
		Date start=new Date();
		testMI();
		System.out.println("old: "+TimeUtils.millisToLongDHMS(start));
		testMI2();
		start=new Date();
		System.out.println("new: "+TimeUtils.millisToLongDHMS(start));
		
		
		
	}
private static void testMI(){ 
	


	ArrayList<Terms> positiveList = new ArrayList<Terms>();
	ArrayList<Terms> negativeList = new ArrayList<Terms>();

	// Texts about summer
	String[][] positivetexts = multiply(new String[][] {
			new String[] { "sun", "sommer", "sea", "and" },
			new String[] { "sun", "boat", "sea", "the" },
			new String[] { "sun", "sommer", "ice", "to" },
			new String[] { "sun", "cold", "sea", "to" },
			new String[] { "sun", "warm", "boat", "but" }, },10000);

	String[][] negativetexts = multiply(new String[][] {
			new String[] { "wind", "winter", "ice", "and" },
			new String[] { "wind", "skate", "ice", "the" },
			new String[] { "sun", "winter", "snow", "to" },
			new String[] { "wind", "warm", "skate", "to" },
			new String[] { "wind", "cold", "skate", "but" }, },10000);

	for (String[] entry : positivetexts) {
		ArrayList<TermPos> photo1pos = new ArrayList<TermPos>();
		for (int i = 0; i < entry.length; i++) {
			String term = entry[i];
			photo1pos.add(new TermPos(term, 0));// i may be whatever, not
												// need for MI
		}
		positiveList.add(new Terms(photo1pos));
	}

	for (String[] entry : negativetexts) {
		ArrayList<TermPos> photo1pos = new ArrayList<TermPos>();
		for (int i = 0; i < entry.length; i++) {
			String term = entry[i];
			photo1pos.add(new TermPos(term, i));// i may be whatever, not
												// need for MI
		}
		negativeList.add(new Terms(photo1pos));
	}

	MIselection mi = new MIselection(positiveList, negativeList);

	mi.computePositiveAndNegativeMIvalues();

	System.out.println("mi.getTopNposResults() "
			+ mi.getTopNposResultList());
	System.out.println("mi.getTopNnegResults() "
			+ mi.getTopNnegResultList());

	int top = 5;
	System.out.println("mi.getTopNposResults(" + top + ") "
			+ mi.getTopNposResultList(top));
	System.out.println("mi.getTopNnegResults(" + top + ") "
			+ mi.getTopNnegResultList(top));


}

private static String[][] multiply(String[][] strings, int times) {
	String[][] ret=new String[strings.length*times][];
	
	int z=0;
	for(int u=0;u<times;u++)
	for(String[] line:strings)
	{
		String newline[]=new String[line.length*times];
		for(int i=0;i<times;i++)
		{
			for(int y=0;y<line.length;y++)
			{
				newline[i*line.length+y]=line[y];
			}
		}
		ret[z]=newline;
		z++;
	}

	return ret;
}

private static void testMI2(){ 
	


	ArrayList<FastTerms> positiveList = new ArrayList<FastTerms>();
	ArrayList<FastTerms> negativeList = new ArrayList<FastTerms>();

	// Texts about summer
	String[][] positivetexts = multiply(new String[][] {
			new String[] { "sun", "sommer", "sea", "and" },
			new String[] { "sun", "boat", "sea", "the" },
			new String[] { "sun", "sommer", "ice", "to" },
			new String[] { "sun", "cold", "sea", "to" },
			new String[] { "sun", "warm", "boat", "but" }, },10000);

	String[][] negativetexts = multiply(new String[][] {
			new String[] { "wind", "winter", "ice", "and" },
			new String[] { "wind", "skate", "ice", "the" },
			new String[] { "sun", "winter", "snow", "to" },
			new String[] { "wind", "warm", "skate", "to" },
			new String[] { "wind", "cold", "skate", "but" }, },10000);

	for (String[] entry : positivetexts) {
		ArrayList<TermPos> photo1pos = new ArrayList<TermPos>();
		for (int i = 0; i < entry.length; i++) {
			String term = entry[i];
			photo1pos.add(new TermPos(term, 0));// i may be whatever, not
												// need for MI
		}
		positiveList.add(new FastTerms(photo1pos));
	}

	for (String[] entry : negativetexts) {
		ArrayList<TermPos> photo1pos = new ArrayList<TermPos>();
		for (int i = 0; i < entry.length; i++) {
			String term = entry[i];
			photo1pos.add(new TermPos(term, i));// i may be whatever, not
												// need for MI
		}
		negativeList.add(new FastTerms(photo1pos));
	}

	FastMIselection mi = new FastMIselection(positiveList, negativeList);

	mi.computePositiveAndNegativeMIvalues();

	System.out.println("mi.getTopNposResults() "
			+ mi.getTopNposResultList());
	System.out.println("mi.getTopNnegResults() "
			+ mi.getTopNnegResultList());

	int top = 5;
	System.out.println("mi.getTopNposResults(" + top + ") "
			+ mi.getTopNposResultList(top));
	System.out.println("mi.getTopNnegResults(" + top + ") "
			+ mi.getTopNnegResultList(top));


}

	public void printScores(int top) {

		NumberFormat formatter = new DecimalFormat("#0.00000000000000000000");

		System.out.println("pos(" + top + ")");
		Iterator<String> it = getTopNposResultList(top).iterator();
		Hashtable<String, Double> scores = getTopNposResultScoreList();
		while (it.hasNext()) {
			String term = it.next().toString();
			System.out
					.println(term + "\t" + formatter.format(scores.get(term)));

		}
		System.out.println();
		System.out.println("neg(" + top + ")");
		it = getTopNnegResultList(top).iterator();
		scores = getTopNnegResultScoreList();
		while (it.hasNext()) {
			String term = it.next().toString();
			System.out
					.println(term + "\t" + formatter.format(scores.get(term)));

		}

	}

	public ArrayList<String> getTopNposResultListPercent(double percentage) {
		int strcnt=mapA.size();
		int cutn=(int)(percentage*strcnt);
		return getTopNposResultList(cutn);
	}
	public ArrayList<String> getTopNnegResultListPercent(double percentage) {
		int strcnt=mapB.size();
		int cutn=(int)(percentage*strcnt);
		return getTopNnegResultList(cutn);
	}

}
