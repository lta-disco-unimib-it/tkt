package it.unimib.disco.lta.timedKTail.util;

import java.util.HashMap;



public class Pool<T> {

	private HashMap<T, T> pool = new HashMap<T,T>();
	
	public T getCached( T nonCached ){
		T cached = pool.get(nonCached);
		if ( cached == null ){
			cached = nonCached;
			pool.put(cached, cached);
		}
		
		return cached;
	}
}
