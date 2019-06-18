/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.JTMTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Guardia dell'automa temporizzato
 * @author AleX
 */
public class Clause implements java.io.Serializable {
    private static final long serialVersionUID = 00000001L;
    private static long countClause=0;
    private final long id;
    private Clock clock;
    private Operation operation;

    private static final Logger logger = LogManager.getLogger(Clause.class);
    
    
    public Clause(Clock clock, Operation operation){
        this.id=this.countClause++;
        this.clock=clock;
        this.operation=operation;
    }
    

    public long getId(){
        return this.id;
    }

    public Clock getClock(){
        return clock;
    }
    

    
    public boolean getIntervallo(){
        return this.operation instanceof Interval;
    }
    
    public Operation getOperation(){
        return operation;
    }
    public void setOperation(Operation o){
        this.operation=o;
    }
    

    
    public void setClock(Clock ck){
        this.clock=ck;
    }
    

    
    public boolean evaluateClause(long val){
    	return operation.evaluate(val);
    }
    
    
    @Override
    public String toString(){
    	String c;
        if(clock instanceof AbsoluteClock){
            c="T";
            
        }else{
            c="CK"+clock.getId();
        }
        return operation.prettyPrint(c);

    }
    
//    @Override
//    public boolean equals(Object c){
//        Clause clause = (Clause)c;
//        if(clause.getId() == this.id){
//            return true;
//        }else{
//            return false;
//        }
//    }
    
}
