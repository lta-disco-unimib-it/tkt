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
public interface ParserI {
    
    public ObserverTraceIm getObserver();
    
    public void readFolder(String ind);
    
    public void readFile(String ind);

}
