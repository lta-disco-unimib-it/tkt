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
public class Tris<V1,V2,V3> implements java.io.Serializable {
    private static final long serialVersionUID = 00000001L;
    private final V1 v1;
    private final V2 v2;
    private final V3 v3;
    
    public Tris(V1 v1,V2 v2, V3 v3){
        this.v1=v1;
        this.v2=v2;
        this.v3=v3;
    }
    public V1 getV1(){
        return this.v1;
    }
    public V2 getV2(){
        return this.v2;
    }
    public V3 getV3(){
        return this.v3;
    }
}
