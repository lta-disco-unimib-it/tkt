/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.algorithm;

import it.unimib.disco.lta.timedKTail.JTMTime.*;
import it.unimib.disco.lta.timedKTail.tests.utils.TestUtils;
import it.unimib.disco.lta.timedKTail.traces.Trace;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Classe K-Tail Base
 * @author AleX
 */
public class TimedKTail implements it.unimib.disco.lta.timedKTail.traces.InferenceMethod{
    private static final Logger logger = LogManager.getLogger(TimedKTail.class);
    private TimedAutomata ta;
    private final int k;
    private Policy poli;
    
	private long maxTraceLen;
	private TimedAutomataFactory taf;
	private PhaseI p1;
	PhaseII phase2;
	
	private long processedTraces;
	
	public TimedKTail(Integer k, Policy poli){
	
		taf = new TimedAutomataFactory("KTail", poli.isUseCaching(), poli.isCachePendingCalls() );
		ta = taf.getTimedAutomata();
		
        this.k=k;
        this.poli=poli;
        
        p1 = new PhaseI(this.taf);
        phase2 = new PhaseII(ta,k,poli);
        
        if ( poli.isUseIncrementalMerging() ){
        	p1.setDoMergeInExistingPath(false);
        }
    }

    @Override
    public void elaborateTrace(Trace t){
        
    	
    	try {
        p1.elaborateTrace(t, poli.isIncludeNestedCallsTime(), false );
        
        long traceSize = t.getSize();
        if ( traceSize > maxTraceLen ){
        	maxTraceLen = traceSize;
        }
        
    	} catch ( Throwable e ) {
    		System.err.println("Exception while processing: "+t.getFilePath()+". Already processed "+processedTraces+" traces.");
    		throw e;
    	}
    	
    	if ( poli.isUseIncrementalMerging() ){
    		performIncrementalMerging();
    	}
    	
    	processedTraces++;
    }
    
    private void performIncrementalMerging() {
    	logger.info("Performing incremental merging");
        phase2.performStateMerging();    
	}

	@Override
    public void resolve(){
    	poli.setMaxVisitDepth((int)(2*maxTraceLen));  	
        this.ta=phase2.resolve();      
    }
    
    
    @Override
    public TimedAutomata getTimedAutomata(){
        return this.ta;
    }
}
