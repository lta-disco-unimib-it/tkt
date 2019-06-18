/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.JTMTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Transizione dell'automa temporizzato
 * @author AleX
 */
public class Transition implements java.io.Serializable {
    private static final long serialVersionUID = 00000001L;
    
    public static final boolean BEGIN = true;
	public static final boolean END = false;
    
    private static long countTransition=0;
    private final long id;
    private Node nodeFrom;
    private Node nodeTo;
    private Activity activity;
    
    private final Guard guard;
    private Set<Reset> aReset  = new HashSet<>(); 
    private Set<Long> resettedClocks = new HashSet<>();
	private String stringId;
	private boolean begin; 
    
    public Transition(Node nodeFrom, Node nodeTo, Activity activity, boolean isBegin){
        this.id=countTransition++;
        this.nodeFrom=nodeFrom;
        this.nodeTo=nodeTo;
        this.activity=activity;
        this.begin=isBegin;
        guard=Guard.getGuard();
        stringId=String.valueOf(id);
    }
    


    /**
     * Imposta il nodo di partenza della transizione
     * @param n nodo di partenza
     */
    public void setNodeFrom(Node n){
        this.nodeFrom=n;
    }
    
    /**
     * Imposta nodo di arrivo della transizione
     * @param n nodo di arrivo
     */
    public void setNodeTo(Node n){
        this.nodeTo=n;
    }
    
    /**
     * imposta id_attività della transizione
     * @param activity imposta la attività
     */
    public void setActivity(Activity activity){
        this.activity=activity;
    }
    

    
    /**
     * restituisce id della transizione
     * @return id della transizione
     */
    public Long getId(){
        return this.id;
    }
    
    /**
     * restituisce nodo di partenza della transizione
     * @return nodo di partenza
     */
    public Node getNodeFrom(){
        return this.nodeFrom;
    }
    
    /**
     * restituisce nodo di arrivo della transizione
     * @return nodo di arrivo
     */
    public Node getNodeTo(){
        return this.nodeTo;
    }
    
    /**
     * restituisce attività della transizione
     * @return attività
     */
    public Activity getActivity(){
        return this.activity;
    }
    
    public boolean isBegin(){
    	return begin;
    }
        
    public void addClause(Clause g){
        guard.addClause(g);
    }

    public Guard getGuard(){
        return this.guard;
    }

    public void deleteClause(Clause g){
        this.guard.deleteClause(g);
    }
    
    public void addReset(Reset r){
        aReset.add(r);
        resettedClocks.add(r.getClock().getId());
    }
    
    public Set<Reset> getResets(){
        return this.aReset;
    }

    public void deleteReset(Reset r){
        aReset.remove(r);
        resettedClocks.remove(r.getClock().getId());
    }
    
    @Override
    public String toString(){
        return getNodeFrom()+" -> "+getNodeTo()+" : "+activity.getName() + "("+ getType() + ")" + stampGuard()+stampReset();
    }
    
    public boolean hasClockOnReset(Clock ck){
        return resettedClocks.contains(ck.getId());
    }
    
    public Reset getResetDatoClock(Clock ck){
        for(Reset r:aReset){
            if(r.getClock().getId()==ck.getId()){
                return r;
            }
        }
        System.out.println("ATTENZIONE RETURN FALSE DA GETRESETDATOCLOCK");
        return null;
    }
    
    private String stampReset(){
        String label="";
        for(Reset r:aReset){
            label=label+r.toString()+";";
        }
        return label;
    }
    private String stampGuard(){
        return guard.toString(); 
    }



	public boolean sameActivityAndType(Transition t2) {
		return this.getActivity().equals(t2.getActivity() ) 
				&& this.begin == t2.begin;
	}



	public String getStringId() {
		return stringId;
	}



	public boolean isEnd() {
		return ! begin;
	}

	
	public String getType() {
		return begin ? "B" : "E";
	}



}
