/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.traces;
import static it.unimib.disco.lta.timedKTail.traces.Trace.getTrace;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author AleX
 */
public class ObserverTimedAutomataTraceBuilder extends ObserverTraceIm implements ObserverTrace{
    
    private static final Logger logger = LogManager.getLogger(ObserverTimedAutomataTraceBuilder.class);
    private final int id_observer;
    private Trace trace;
    private final InferenceMethod TecnicaInferenza;

    
    public ObserverTimedAutomataTraceBuilder(int id, InferenceMethod Tecnica){
        this.id_observer=id;
        this.TecnicaInferenza = Tecnica;

    }
    
    public int getIDRegisterObserver(){
        return id_observer;
    }

    @Override
    public void newEvent(Event e) {
        trace.addEvent(e);
//        logger.debug("Ricevuto Nuovo evento "+e.getAttivita());
    }

    @Override
    public void startTrace(String path, long nTrace) {
        //istanzio nuova Traccia
        trace = getTrace(path,nTrace);
        logger.debug("Inizio Nuova traccia");
    }

	@Override
    public void endTrace() {

    }

    @Override
    public void Error(String s) {
        logger.fatal(s);
    }
    
    public void processTrace(){
        logger.debug("Elaborazione della traccia");
        this.TecnicaInferenza.elaborateTrace(trace);    
    }


}
    

