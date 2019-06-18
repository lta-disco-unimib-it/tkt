/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.traces;

/**
 *
 * @author AleX
 */
public abstract class  ObserverTraceIm {
        
    public abstract void newEvent(Event e);
    
    public abstract void startTrace(String path, long nTrace);
    
    public abstract void endTrace();
    
    public abstract void Error(String s);

    public abstract void processTrace();
    
}
