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
public class Activity implements java.io.Serializable {
    private static final long serialVersionUID = 00000001L;
    private final String name;
    
    public Activity(String activity){
        this.name=activity;
    }
    
    public String getName(){
        return this.name;
    }

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		
		if ( this == obj ){
			return true;
		}
		
		if ( obj == null ){
			return false;
		}
		
		if ( ! ( obj instanceof Activity ) ){
			return false;
		}
		
		Activity _obj = (Activity) obj;
		
		return name.equals(_obj.name);
	}

	@Override
	public String toString() {
		return name;
	}
    

    
}
