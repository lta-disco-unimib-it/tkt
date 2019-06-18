/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.traces;

import it.unimib.disco.lta.timedKTail.JTMTime.Transition;

/**
 *
 * @author AleX
 */
public class Event {
    
    private final String id_task;
    private final String id_attivita;
    private final boolean begin;    
    private final long tempo;
    private final String info_supp;
    
	private long executionTime = -1;
	
	private Event correspondingBegin;
	private Transition firedTransition;
	
	
    
    public Event getCorrespondingBegin() {
		return correspondingBegin;
	}

	public void setCorrespondingBegin(Event correspondingBegin) {
		if ( begin ){
			throw new IllegalStateException("Cannot associate a corresponding event to an event of type begin");
		}
		this.correspondingBegin = correspondingBegin;
	}

	public Event(String id_task, String id_attivita, boolean isEnter, long tempo, String info_supp){
        this.id_task=id_task;
        this.id_attivita=id_attivita;
        this.begin = isEnter;
        this.tempo=tempo;
        this.info_supp=info_supp;
    }
    
    public Event(String id_task, String attivita, boolean isEnter, long tempo){
        this(id_task, attivita, isEnter, tempo, "");
    }
    
    public String getId_task(){
        return this.id_task;
    }
    
    public String getAttivita(){
        return this.id_attivita;
    }
    
    public long getTimestamp(){
        return this.tempo;
    }
    
    public String getInfo_supp(){
        return this.info_supp;
    }  
    
    @Override
    public String toString(){
        if(this.getInfo_supp().equals("")){
            return "Task:"+this.id_task+" Attivita':"+this.id_attivita+" Tipologia:"+
                    this.getTipologia()+" T:"+this.getTimestamp();
        }else{
            return "Task:"+this.id_task+" Attivita':"+this.id_attivita+" Tipologia:"+
                    this.getTipologia()+" T:"+this.getTimestamp()+" Info:"+this.getInfo_supp();
        }
    }

	public String getTipologia() {
		return begin ? "B" : "E";
	}

	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}

	public long getExecutionTime() {
		return executionTime;
	}

	public boolean isBegin() {
		return begin;
	}

	public boolean isEnd() {
		return ! begin;
	}

	public void setFiredTransition(Transition t) {
		firedTransition = t;
	}

	public Transition getFiredTransition() {
		return firedTransition;
	}
	
}
