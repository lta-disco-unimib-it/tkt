package it.unimib.disco.lta.timedKTail.algorithm;

import java.io.Serializable;

public class PendingCallsSequence implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String key;

	public PendingCallsSequence(String key){
		this.key = key;
	}

	@Override
	public String toString() {
		return "[PendingCallSequence: "+key+"]";
	}
}
