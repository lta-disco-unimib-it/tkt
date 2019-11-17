/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.ui;

import static it.unimib.disco.lta.timedKTail.ui.Main.drawGraph1;
import static it.unimib.disco.lta.timedKTail.ui.Main.loadAutomata;
import static it.unimib.disco.lta.timedKTail.ui.Main.validateTrace;

import java.io.PrintWriter;

import it.unimib.disco.lta.timedKTail.JTMTime.TimedAutomata;

/**
 *
 * @author AleX
 */
public class VisualizeAutomata {
    static String path;// = "save/timedAutomata/";
    
    public static void main(String[] args){
    	path = args[0];
    	
    	String validationFolder = null;
    	if ( args.length > 1 ){
    		validationFolder = args[1];
    	}
    	
        TimedAutomata ta = loadAutomata(path);
    	drawGraph1(ta,"Stadio2");
        
    	if ( validationFolder != null){
    		PrintWriter pw = new PrintWriter(System.out);
    		validateTrace(ta,validationFolder,null,true,false, true, pw);
    		pw.close();
    	}
        
    }
    
}
