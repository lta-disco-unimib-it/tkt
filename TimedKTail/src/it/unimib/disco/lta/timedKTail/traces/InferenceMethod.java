/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.traces;

import it.unimib.disco.lta.timedKTail.JTMTime.TimedAutomata;

/**
 *
 * @author AlexP
 */
public interface InferenceMethod {
    
    public void elaborateTrace(Trace t);
    public void resolve();
    
    public TimedAutomata getTimedAutomata();
    
}
