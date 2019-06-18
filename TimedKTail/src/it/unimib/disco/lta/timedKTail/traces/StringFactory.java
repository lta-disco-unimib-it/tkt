package it.unimib.disco.lta.timedKTail.traces;

import it.unimib.disco.lta.timedKTail.util.Pool;

public class StringFactory extends Pool<String> {

	public static final StringFactory INSTANCE = new StringFactory();
	private static final boolean NO_CACHE = Boolean.getBoolean("stringFactory.noCache");

	@Override
	public String getCached(String nonCached) {
		if ( NO_CACHE ){
			return nonCached;
		}
		return super.getCached(nonCached);
	}
	
	
	
}
