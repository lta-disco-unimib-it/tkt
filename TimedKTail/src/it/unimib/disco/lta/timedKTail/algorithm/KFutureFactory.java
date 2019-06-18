package it.unimib.disco.lta.timedKTail.algorithm;

import java.util.HashMap;
import java.util.LinkedList;

import it.unimib.disco.lta.timedKTail.JTMTime.Transition;

public class KFutureFactory {

	public static final KFutureFactory INSTANCE = new KFutureFactory();

	public HashMap<Integer,KFuture> builtFutures = new HashMap<Integer,KFuture>();
	
	public KFuture create(LinkedList<Transition> transitions) {
		int id = KFutureIdsFactory.INSTANCE.getId(transitions);
		KFuture kf = builtFutures.get(id);
		if ( kf == null ){
			kf = new KFutureSingle(transitions);
			builtFutures.put(id, kf);
		}
		
		return kf;
	}

}
