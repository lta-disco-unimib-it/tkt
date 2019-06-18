/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.JTMTime;

/**
 *
 * @author AleX
 */
public abstract class OperationBinary {
    
    public abstract boolean evaluate(long max,long min,long daConfrontare);
    @Override
    public abstract String toString();
}
