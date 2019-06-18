/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.traces;

/**
 *
 * @author AleX
 */
public class Statistica {
    public static long clausolaSingola;
    public static long clausolaMulty;
    
    public static void incrementaSingola(){
        clausolaSingola++;
    }
    public static void incrementaMulty(){
        clausolaMulty++;
    }
    public static void reset(){
        clausolaSingola=0;
        clausolaMulty=0;
    }
    public static void getStatistica(){
        double ps=0;
        double pm=0;
        System.out.println("Statistica creazione TimedAutomata:");
        System.out.println("Clausole singole: "+clausolaSingola);
        System.out.println("Clausola multy: "+clausolaMulty);
        ps = clausolaSingola / (clausolaSingola + clausolaMulty);
        ps = ps*100;
        pm = clausolaMulty / (clausolaSingola + clausolaMulty);
        pm = pm*100;
        System.out.println("Percentuale Singole: "+ps+" %");
        System.out.println("Percentuale multy: "+pm+" %");
        
    }
    
}
