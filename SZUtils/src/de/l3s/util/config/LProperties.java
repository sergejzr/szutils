package de.l3s.util.config;

import java.util.Hashtable;

import de.l3s.util.config.CrawlProperties.props;

public abstract class LProperties extends Hashtable<Object, Object>{

/**
	 * 
	 */
	private static final long serialVersionUID = 7997790246175126343L;

public Integer getInt(Object key)
{
	Object val = get(key);
	if(val==null) return null;
	return (Integer) val;
}

public Double getDouble(Object key)
{
	Object val = get(key);
	if(val==null) return null;
	return (Double) val;
}

public String getString(Object key)
{
	Object val = get(key);
	if(val==null) return null;
	return (String) val;
}

public Boolean getBoolean(props key) {
	Object val = get(key);
	if(val==null) return null;
	return (Boolean) val;
};
}
