/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.JTMTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author AlexP
 */
public class GreaterEqual  implements Operation, java.io.Serializable{
    private static final long serialVersionUID = 00000001L;
    private static final Logger logger = LogManager.getLogger(GreaterEqual.class);
	private long val2;
    
    public GreaterEqual( long val2 ){
    	this.val2 = val2;
    }
    
    @Override
    public boolean evaluate(long val1) {
        logger.debug("Verifica Guardia: "+val1+" >= "+val2);
        return (val1>=val2);
    }
    
    @Override
    public String toString(){
        return">=";
    }
    
	@Override
	public String prettyPrint(String c) {
		return c+" >= "+val2;
	}
}
