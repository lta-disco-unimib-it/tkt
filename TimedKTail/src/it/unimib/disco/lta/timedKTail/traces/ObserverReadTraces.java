/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.traces;
import static it.unimib.disco.lta.timedKTail.traces.Trace.getTrace;
import it.unimib.disco.lta.timedKTail.JTMTime.Pair;
import it.unimib.disco.lta.timedKTail.validation.Validation;
import it.unimib.disco.lta.timedKTail.validation.Validation.ValidationError;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 * @author AleX
 */
public class ObserverReadTraces extends ObserverTraceIm implements ObserverTrace{
        
    private static final Logger logger = LogManager.getLogger(ObserverReadTraces.class);
   
    private Trace trace;
    private List<Trace> traces = new ArrayList<Trace>();
    
    public ObserverReadTraces(){
    }

    @Override
    public void newEvent(Event e) {
        trace.addEvent(e);
        logger.debug("Aggiungo nuovo evento alla traccia "+e.getAttivita());
    }

    @Override
    public void startTrace(String path, long nTrace) {
        //istanzio nuova Traccia
        trace = getTrace(path,nTrace);
        logger.debug("Inizio Nuova Traccia");
    }

    @Override
    public void endTrace() {
    	traces.add( trace );
    }

    @Override
    public void Error(String s) {
        logger.fatal(s);
    }
    

    
    public List<Trace> getTraces() {
		return traces;
	}

	public void processTrace(){
        
       
    }
    
}
