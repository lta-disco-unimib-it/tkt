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
public interface ObserverTrace {
    
    public void newEvent(Event e);
    
    public void startTrace(String path, long nTrace);
    
    public void endTrace();
    
    public void Error(String s);

    
}
