/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.algorithm;
import it.unimib.disco.lta.timedKTail.JTMTime.*;

import java.util.ArrayList;
/**
 *
 * @author AleX
 */
public class Future {
	private ArrayList<Transition> tA;

	public Future(){
		tA=new ArrayList();
	}
	public boolean addTransition(Transition t){
		return tA.add(t);
	}
	public ArrayList<Transition> getTransitions(){
		return tA;
	}
	public Transition getTransition(int i){
		return tA.get(i);
	}

	public int size(){
		return tA.size();
	}

	@Override
	public boolean equals(Object future){
		if( ! (future instanceof  it.unimib.disco.lta.timedKTail.algorithm.Future ) ){
			return false;
		}
		
		Future f = (Future)future;
		if(f.size()!=this.size()){
			return false;
		}else{
			for(int i=0;i<f.size();i++){
				//verifico se oggetto i-esimo ha la stessa attività e lo stesso tipo
				if( !( (equalType(f.getTransition(i),this.getTransition(i))) 
						&& (equalActivity(f.getTransition(i),this.getTransition(i))) ) ){
					return false;
				}
			}
			return true;
		}


	}

	private boolean equalActivity(Transition t, Transition t1){
		return(t.getActivity().equals(t1.getActivity()));
	}
	
	private boolean equalType(Transition t, Transition t1){
		return(t.isBegin() == t1.isBegin() );
	}
	
	@Override
	public String toString() {
		String s = "";
		for ( Transition t : tA ){
			s+= t.toString()+";";
		}
		return s;
	}

	
}
