/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.JTMTime;

/**
 *Guardia dell'automa temporizzato
 * @author AleX
 */
public class Reset implements java.io.Serializable {
    private static final long serialVersionUID = 00000001L;
    private long countReset=0;
    private final long id;
    private final Clock clock;
    
    public Reset(Clock c){
        this.id=countReset++;
        this.clock=c;
    }
    
    public static Reset getReset(Clock clock){
        return new Reset(clock);
    }
    
    /**
     * restituisce id del reset
     * @return id del reset
     */
    public long getId(){
        return this.id;
    }
    
    /**
     * restituisce clock associato al reset
     * @return id del clock
     */
    public Clock getClock(){
        return this.clock;
    }
    //mettere nella classe clock il toString con CK davanti al getID
    @Override
    public String toString(){
        return "CK"+clock.getId()+":=0";
    }
}
