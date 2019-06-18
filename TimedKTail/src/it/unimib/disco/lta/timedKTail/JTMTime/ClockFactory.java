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
public class ClockFactory implements java.io.Serializable {
    private static final long serialVersionUID = 00000001L;
    private static long countClock=1;
    
    //Fabrizio-fix: now there is just one absolute clock
    private static AbsoluteClock absoluteClock = new AbsoluteClock(0);
    
    public static Clock getClockAbsolute(){
        return absoluteClock;
    }
    
    public static Clock getClockRelative(){
        return new RelativeClock(countClock++);
    }
    
}
