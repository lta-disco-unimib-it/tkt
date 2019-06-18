package it.unimib.disco.lta.timedKTail.algorithm;

import java.util.HashMap;
import java.util.List;

import it.unimib.disco.lta.timedKTail.JTMTime.Transition;

public class PendingCallsFactory {

	public static final PendingCallsFactory INSTANCE = new PendingCallsFactory();
	private HashMap<String, PendingCallsSequence> ids = new HashMap<>();
	
	public PendingCallsSequence buildSequence(List<Transition> transitions) {
		if ( transitions.size() == 0 ){
			throw new IllegalArgumentException("Empty list of transitions");
		}
		
		StringBuffer sb = new StringBuffer();
		
		for ( Transition t : transitions ){
			sb.append(t.getActivity());
			sb.append(":");
			sb.append(t.getType());
			sb.append(";");
		}
		
		String key = sb.toString();
		PendingCallsSequence id = ids.get(key);
		
		if ( id == null ){
			id = new PendingCallsSequence(key);
			ids.put(key, id);
		}
		
		return id;
	}
}
