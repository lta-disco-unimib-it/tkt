/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.JTMTime;

/**
 * Contiene struttura di dati binaria, dati ridontati xchè già presenti
 * sulle transizioni.
 * Struttura dati che contiene per ogni clock le transizioni dove è presente.
 * @author AleX
 */
public class Pair<OC,OT> implements java.io.Serializable {
    private static final long serialVersionUID = 00000001L;
    private OC OC;
    private OT OT;
    
    public Pair(OC val1,OT val2){
        this.OC=val1;
        this.OT=val2;
    }
    
    /**
     * restituisce id del clock
     * @return id clock
     */
    public OC getVal1(){
        return this.OC;
    }
    
    /**
     * restituisce id transizione
     * @return id transizione
     */
    public OT getVal2(){
        return this.OT;
    } 

    public void setVal1(OC val1){
        this.OC=val1;
    }
    public void setVal2(OT val2){
        this.OT=val2;
    }

}
