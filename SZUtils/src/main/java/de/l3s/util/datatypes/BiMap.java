package de.l3s.util.datatypes;

import java.util.HashMap;
import java.util.Map;

public class BiMap<T,G> extends HashMap<T, G>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2309750932L;
	HashMap<G, T> values=new HashMap<G, T>();
	

	@Override
	public G put(T key, G value) {
		super.put(key, value);
		values.put(value, key);
		return null;
	}
	
	@Override
	public void putAll(Map<? extends T, ? extends G> m) {
		super.putAll(m);
		
		for(T key:m.keySet())
		{
			G val = m.get(key);
			values.put(val,key);
		}
		
	}
	@Override
	public G remove(Object key) {
		G val = super.remove(key);
		values.remove(val);
		return val;
	}
	
	
	@Override
	public void clear() {
		super.clear();
		values.clear();
		
	}
	
	@Override
	public boolean containsValue(Object value) {
	
		return values.containsKey(value);
	}
	
	public T getKey(G value)
	{
		return values.get(value);
	}

	public void out(int i) {
		int cnt=0;
		
		for(T key:keySet())
		{
			if(cnt>i){return;}
			System.out.println(key+"=>"+get(key));
			cnt++;
		}
		
	}
	
	/*
	@Override
	public boolean containsKey(Object key) {
	
		return keys.containsKey(key);
	}
	
	@Override
	public Set<java.util.Map.Entry<T, G>> entrySet() {
		
		return keys.entrySet();
	}
	@Override
	public G get(Object key) {
		
		return keys.get(key);
	}
	@Override
	public boolean isEmpty() {
		
		return keys.isEmpty();
	}
	@Override
	public Set<T> keySet() {
		// TODO Auto-generated method stub
		return keys.keySet();
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public Collection<G> values() {
		// TODO Auto-generated method stub
		return null;
	}
*/
}

