package it.unimib.disco.lta.timedKTail.algorithm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class KFutureComposite implements KFuture {

	private List<KFuture> futures = new ArrayList<KFuture>();
	
	public KFutureComposite(LinkedList<KFuture> futures) {
		this.futures.addAll(futures);
	}

	public void add(KFuture f){
		if ( f == null ){
			throw new IllegalArgumentException();
		}
		futures.add(f);
	}
	
	@Override
	public boolean intersectNotNull(KFuture future) {
		for( KFuture kf : futures ){
			if ( kf.intersectNotNull(future) ){
				return true;
			}
		}
		return false;
	}

}
