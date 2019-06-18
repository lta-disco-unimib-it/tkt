/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.algorithm;

import it.unimib.disco.lta.timedKTail.JTMTime.Transition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author AleX
 */
public class KFutureSingle implements KFuture {
	private List<Transition> transitions;
	private int id;
    
    public KFutureSingle(List<Transition> transitions){
    	this.id = KFutureIdsFactory.INSTANCE.getId( transitions );
        this.transitions=new ArrayList<>(transitions.size());
        this.transitions.addAll(transitions);
    }
    
    @Override
    public boolean intersectNotNull( KFuture future ){
    	if ( future instanceof KFutureSingle ){
    		return ((KFutureSingle)future).id == id;
    	}
    	if ( future == null ){
    		return false;
    	}
    	return future.intersectNotNull(this);
    }

	public List<Transition> getTransitions() {
		return transitions;
	}

	@Override
	public String toString() {
		return id+"="+KFutureIdsFactory.toString(transitions);
	}
    

}
