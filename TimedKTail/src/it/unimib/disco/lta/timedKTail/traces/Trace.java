/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.traces;

import java.util.ArrayList;

/**
 *
 * @author AleX
 */
public class Trace {
    private static long countTrace=0;
    private final long id_trace;
    private final ArrayList<Event> trace;
	
    private long traceN;
	private String filePath;
    
    private Trace(String filePath,long traceN){
        this.id_trace = countTrace++;
        this.filePath=filePath;
        this.traceN = traceN;
        trace = new ArrayList();
    }
    
    public String getFilePath() {
		return filePath;
	}

	public static Trace getTrace(String filePath,long traceN){
        return new Trace(filePath,traceN);
    }
    
    public void addEvent(Event e){
        trace.add(e);
    }
    public ArrayList<Event> getEvents(){
        return trace;
    }
    public long getIdTrace(){
        return this.id_trace;
    }
    public long getSize(){
        return trace.size();
    }
    public Event getEvent(int l){
        return trace.get(l);
    }
    
    public boolean esiste(int l){
        return l > 0 && l < trace.size();
    }
    
    public boolean inesistente(int l){
        return l >= trace.size();
    }

	@Override
	public String toString() {
		return "[Trace path:"+filePath+" traceN:"+traceN+"]";
	}
    
    
}
