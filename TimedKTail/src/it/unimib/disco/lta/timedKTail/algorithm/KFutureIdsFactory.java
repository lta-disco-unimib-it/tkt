package it.unimib.disco.lta.timedKTail.algorithm;

import java.util.HashMap;
import java.util.List;

import it.unimib.disco.lta.timedKTail.JTMTime.Transition;

public class KFutureIdsFactory {

	public static final KFutureIdsFactory INSTANCE = new KFutureIdsFactory();
	private HashMap<String, Integer> ids = new HashMap<>();
	
	public int getId(List<Transition> transitions) {
		String key = toString(transitions);
		Integer id = ids.get(key);
		
		if ( id == null ){
			id = ids.size();
			ids.put(key, id);
		}
		
		return id;
	}

	public static String toString(List<Transition> transitions) {
		StringBuffer sb = new StringBuffer();
		
		for ( Transition t : transitions ){
			sb.append(t.getActivity());
			sb.append(":");
			sb.append(t.getType());
			sb.append(";");
		}
		
		String key = sb.toString();
		return key;
	}
}
