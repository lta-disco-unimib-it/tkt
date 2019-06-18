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
public abstract class Clock {
   
    public abstract long getId();
    public abstract boolean isRelativeClock();
    public abstract boolean isAbsoluteClock();
   
}
