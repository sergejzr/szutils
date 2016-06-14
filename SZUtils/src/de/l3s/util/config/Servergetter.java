package de.l3s.util.config;

import java.io.File;
import java.util.Hashtable;

public class Servergetter {
	
public enum Server{GODZILLA,ATHENA,OUT, LOCALHOST}
private static Hashtable<String, Server> idx=new Hashtable<String, Servergetter.Server>();
static
{
	idx.put("/data/zerr",Server.GODZILLA);
	idx.put("/home/sergej",Server.ATHENA);
	idx.put("/home/zerr/out.server",Server.OUT);
	idx.put("E:\\",Server.LOCALHOST);
	
	}
public static Server whereAmI()
{
	for(String path:idx.keySet())
	{
		File tst=new File(path);
		if(tst.exists())
		{
			return idx.get(path);
		}
	}
	return null;
}
}
