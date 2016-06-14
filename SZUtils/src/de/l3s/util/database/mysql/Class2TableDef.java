package de.l3s.util.database.mysql;

import java.lang.reflect.Field;




public class Class2TableDef {
public static String defineTableFor(Class t, String tablename) 
{
	StringBuilder sb=new StringBuilder();
	StringBuilder sb1=new StringBuilder();
	if(tablename==null){
	 tablename = t.getName();
	}
	
	sb1.append(
			"public void store(List<"+t.getCanonicalName()+"> objects) throws StorageException\n" +
			"{\n"+
			"for ("+t.getCanonicalName()+" object : objects) {\n" +
			"ChainableSortedMap<String, Object> insertObject = createObject();\n");
	
	
	
	
	for(Field f:t.getDeclaredFields())
	{
		String type = f.getClass().getName();
		if(sb.length()>0)sb.append(",");
		sb.append("`"+f.getName()+"` varchar(255) collate utf8_unicode_ci NOT NULL default ''");
		char[] chrs = f.getName().toCharArray();
		sb1.append("insertObject.put(\""+f.getName()+"\", object.get"+new String(chrs[0]+"").toUpperCase()+""+(f.getName().substring(1))+"());\n");
	}
	sb1.append("addCached(insertObject);\n" +
			"}\n" +
			"flush();\n" +
			"}\n");
	System.out.println(sb1);
	sb.append(") ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci");
	return "CREATE TABLE `"+tablename+"` ("+sb.toString();
}
}
