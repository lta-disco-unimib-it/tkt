/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.JTMTime;

/**
 *
 * @author AlexP
 */
public interface Operation {
    
    public boolean evaluate(long val1);
    
    @Override
    public abstract String toString();

	public String prettyPrint(String c);
    
}
