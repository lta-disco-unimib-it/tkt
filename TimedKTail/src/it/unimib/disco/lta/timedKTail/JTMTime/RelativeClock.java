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
public class RelativeClock extends Clock implements java.io.Serializable{
    private static final long serialVersionUID = 00000001L;
    private final long myId;
    
    protected RelativeClock(long id){
        this.myId=id;
    }

    @Override
    public long getId() {
        return myId;
    }

    @Override
    public boolean isRelativeClock() {
        return true;
    }

    @Override
    public boolean isAbsoluteClock() {
        return false;
    }
   
    
}
