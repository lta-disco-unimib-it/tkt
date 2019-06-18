/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.JTMTime;

import java.util.Collection;
import java.util.HashSet;

/**
 *
 * @author AlexP
 */
public class Guard implements java.io.Serializable {
    private static final long serialVersionUID = 00000002L;
    private long countGuard=0;
    private final long idGuard;
    private final HashSet<Clause> aC=new HashSet<Clause>();
    
    private Guard(){
        idGuard=countGuard++;
    }
    public long getIdGuard(){
        return this.idGuard;
    }
    
    public static Guard getGuard(){
        return new Guard();
    }
    
    public void addClause(Clause c){
       aC.add(c);
    }
    
    public Collection<Clause> getClauses(){
        return aC;
    }
    
    public int getSizeClause(){
        return aC.size();
    }

    
    public boolean deleteClause(Clause c){
        return  aC.remove(c);
    }
    
    public boolean deleteClauses(Collection<Clause> c){
        return aC.removeAll(c);
    }
    
    @Override
    public String toString(){
        String label="";
        for(Clause c:aC){
            label=label+c.toString()+";";
        }
        return label;
    }
    
//	@Override
//	public boolean equals(Object obj) {
//		if ( obj == null ){
//			return false;
//		}
//		
//		if ( ! ( obj instanceof Guard ) ){
//			return false;
//		}
//		
//		Guard rhs = (Guard)obj;
//		if ( rhs.aC.size() != aC.size() ){
//			return false;
//		}
//		
//		for ( Clause rg : rhs.aC ){
//			if ( ! aC.contains(rg) ){
//				return false;
//			}
//		}
//		
//		return true;
//	}
    
}
