/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.JTMTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;


/**
 * 
 * @author AleX
 */
public class Node implements java.io.Serializable, Cloneable{
    private static final long serialVersionUID = 00000002L;
    private static long countNode=0;
    private final long id;
    
    //INITIAL, INTERNAL, FINAL
    private boolean initialState;
    private boolean finalState;
    
	private HashSet<Transition> aT = new HashSet<>();
    
    
    transient public int visits = 0;
	private boolean deleted = false;
	private Transition lastTransitionAdded;

	public boolean isDeleted() {
		return deleted;
	}



	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}



	public Node(boolean initial){
        this.id=countNode++;
    }
    

    
    public long getId(){
        return this.id;
    }
    
	public Transition getLastTransitionAdded() {
		return lastTransitionAdded;
	}
    
    public void addTransition(Transition t){
    	if ( aT.contains(t) ){
    		throw new IllegalStateException("Already containing "+t);
    	}
    	lastTransitionAdded=t;
        aT.add(t);
    }
    
    public void deleteTransition(Transition t){
        aT.remove(t);
    }
    
    public void deleteTransitions(Collection<Transition> t){
        aT.removeAll(t);
    }
    
    public Collection<Transition> getTransitionsExit(){
        return aT;
    }
    
    public Transition[] getArrayTransitionsExit(){
    	ArrayList<Transition> ts = new ArrayList<>();
    	ts.addAll(aT);
    	
        return ts.toArray(new Transition[ts.size()]);
    }
    
    @Override
    public boolean equals(Object n){
    	if ( n == null ){
    		return false;
    	}
    	
    	if ( ! ( n instanceof Node ) ){
    		return false;
    	}
    	
        Node n1=(Node)n;
        boolean same = ( this.id==n1.getId() );
        
        
        if ( same ){
        	if ( this.initialState != n1.initialState ||
        			this.finalState != n1.finalState ){
        		throw new IllegalStateException("Should not have different instances of nodes with same id.");
        	}
        }
        
        return same;
    }
  
    
    
    public boolean isFinalState() {
		return finalState;
	}

	public void setFinalState(boolean value) {
		finalState = value;
	}



	public List<Transition> getTransitions(Activity activity, boolean isEnter) {
		ArrayList<Transition> ts = new ArrayList<>();
		for( Transition t : aT ){
			if ( t.getActivity().equals(activity) && t.isBegin() == isEnter ){
				ts.add(t);
			}
		}
		return ts;
	}



	public boolean isInitialState() {
		return initialState;
	}
	
	public void setInitialState(boolean value) {
		initialState = value;
	}



	@Override
	public String toString() {
		return ""+this.getId();
	}


	public Node copy(){
		try {
			return (Node) clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		Node copy = (Node) super.clone();
		copy.aT = new HashSet<>();
		copy.aT.addAll(aT);
		return copy;
	}
	
	
}
