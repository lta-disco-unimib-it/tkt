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
 * @author AleX
 */
public class Interval implements Operation , java.io.Serializable {
    private static final long serialVersionUID = 00000001L;
    private static final Logger logger = LogManager.getLogger(Interval.class);
	private long high;
	private long low;
    


    @Override
    public String toString() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Interval(long low, long high) {
    	if ( low > high ){
    		throw new IllegalArgumentException("Wrong interval: "+low+" <= x <= "+high);
    	}
		this.low = low;
		this.high =  high;
	}
    
    @Override
    public boolean evaluate(long val) {
    	if ( ClausesConfig.minimalUpperBound > high ){
    		return true;
    	}
    	
    	 if( (val<=high) && (low <= val)){
         	if ( logger.isDebugEnabled() ){
         		logger.debug("Guardia Rispettata");
         	}
             return true;
         }else{
         	if ( logger.isDebugEnabled() ){
         		logger.debug("Guardia VIOLATA, valore: "+val);
         	}
             return false;
         }
    	 
        
    }

	@Override
	public String prettyPrint(String c) {
		return low+"<= "+c+" <= "+high;
	}

	@Override
	public boolean equals(Object obj) {
		if ( obj == null ){
			return false;
		}
		
		if ( ! ( obj instanceof Interval ) ){
			return false;
		}
		
		Interval rhs = (Interval) obj;
		
		
		return rhs.high==high && rhs.low == low;
	}


    
}
