/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.ui;


import static it.unimib.disco.lta.timedKTail.ui.Main.loadAutomata;
import static it.unimib.disco.lta.timedKTail.ui.Main.validateTrace;

import it.unimib.disco.lta.timedKTail.JTMTime.TimedAutomata;





public class ValidateTraces {


    static String path;// = "save/timedAutomata/";
    
    public static void main(String[] args){
    	path = args[0];
    	
    	String validationFolder = null;
    	if ( args.length > 1 ){
    		validationFolder = args[1];
    	}
    	
        TimedAutomata ta = loadAutomata(path);
    	
        boolean checkGuards = Boolean.parseBoolean(System.getProperty("checkGuards", "true"));
        boolean checkForGuardsNotReset = Boolean.parseBoolean(System.getProperty("checkForGuardsNotReset", "false"));
        boolean validateAbsoluteClocks = Boolean.parseBoolean(System.getProperty("validateAbsoluteClocks", "true"));
        
        
    	if ( validationFolder != null){
    		validateTrace(ta,validationFolder,validateAbsoluteClocks,checkForGuardsNotReset,checkGuards);
    	}
        
    }


}
