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
public class ObserverValidateTrace extends ObserverTraceIm implements ObserverTrace{
        
    private static final Logger logger = LogManager.getLogger(ObserverValidateTrace.class);
    private final int id_observer;
    private Trace trace;
    private final Validation validation;
    private long valida;
    private long nTrace;
    
    public ObserverValidateTrace(int id,Validation v){
        this.id_observer=id;
        this.validation=v;
        valida=0;
        nTrace=0;
    }

    public int getIDRegisterObserver(){
        return id_observer;
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

    }

    @Override
    public void Error(String s) {
        logger.fatal(s);
    }
    
    public long getRisultatoValidazione(){
        logger.debug("Ritorno risultato validazione: "+valida);
        return valida;
    }
    
    public void processTrace(){
        nTrace++;
        logger.info("Inizio processo di validazione traccia n: "+nTrace);
        
//        System.out.println("Valido traccia num: "+nTrace);
        
         List<ValidationError> ris = this.validation.validateTraceReturnAllErrors(trace);
        
         ValidationError mainError = validation.identifyError(ris);
        
        if ( mainError == null ){
        	logger.info("Detected Valid trace: "+trace);
        	validTraces.add( trace );
        } else {
        	logger.info("Detected Invalid trace: "+trace+" automata: "+this.validation.getTimedAutomata().getName() );
        	invalidTraces.add( trace );
        	allErrors.put( trace, ris );
        	mainErrors.put( trace, mainError );
        }
       
    }
    
    
    private Map<Trace,ValidationError> mainErrors = new HashMap<Trace,ValidationError>();
    private List<Trace> validTraces = new ArrayList<Trace>();
    private List<Trace> invalidTraces = new ArrayList<Trace>();
    private Map<Trace,List<ValidationError>> allErrors = new HashMap<>();
    
	public List<Trace> getValidTraces() {
		return validTraces;
	}
	
	public List<Trace> getInvalidTraces() {
		return invalidTraces;
	}
    
	public Map<Trace, List<ValidationError>> getErrors() {
		return allErrors;
	}
	
	public  Collection<ValidationError> getMainErrors() {
		return mainErrors.values();
	}

	public ValidationError getError(Trace t) {
		return mainErrors.get(t);
	}
	
	public List<ValidationError> getErrors(Trace t) {
		return allErrors.get(t);
	}
	
	
	public Set<Entry<Trace, ValidationError>> getErrorMapEntries() {
		return mainErrors.entrySet();
	}

}
