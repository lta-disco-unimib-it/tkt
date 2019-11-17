/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.ui;


import static it.unimib.disco.lta.timedKTail.ui.Main.loadAutomata;
import static it.unimib.disco.lta.timedKTail.ui.Main.validateTrace;

import java.io.File;
import java.io.PrintWriter;

import org.apache.commons.io.output.NullWriter;

import it.unimib.disco.lta.timedKTail.JTMTime.TimedAutomata;





public class ValidateTraces {


    static String path;// = "save/timedAutomata/";
    
    public static void main(String[] args){
    	path = args[0];
    	
    	if ( args.length == 0 ){
    		printUsage();
        	return;
    	}
    	
    	String validationFolder = null;
    	if ( args.length > 1 ){
    		validationFolder = args[1];
    	} 
    	
        TimedAutomata ta = loadAutomata(path);
    	
        boolean checkGuards = Boolean.parseBoolean(System.getProperty("checkGuards", "true"));
        boolean checkForGuardsNotReset = Boolean.parseBoolean(System.getProperty("checkForGuardsNotReset", "false"));
        boolean validateAbsoluteClocks = Boolean.parseBoolean(System.getProperty("validateAbsoluteClocks", "true"));

        boolean printValidationResults = Boolean.parseBoolean(System.getProperty("printValidationResults", "true"));
        
        String validationOutput = System.getProperty("validationOutputFile", "TkT.validationResults.csv");
        
        PrintWriter wr;
        if ( printValidationResults ){
        	wr = new PrintWriter(System.out);
        } else {
        	wr = new PrintWriter(new NullWriter());
        }
        
        
       
        
    	if ( validationFolder != null){
    		File validationOutputFile = null;
    		if ( ! validationOutput.isEmpty() ){
    			validationOutputFile = new File ( validationOutput );
    		}
    		validateTrace(ta,validationFolder,validationOutputFile,validateAbsoluteClocks,checkForGuardsNotReset, checkGuards, wr);
    		
    	}
        
    	wr.close();
    }

	private static void printUsage() {
		System.out.println("Usage:");
		System.out.println("\tjava -cp tkt.jar [OPTIONS] "+ValidateTraces.class.getCanonicalName()+" <TA> <FolderWithTraces>");
		System.out.println("");
		System.out.println("\t\t<TA>\t Timed Automata in .jtml format");
		System.out.println("\t\t<FolderWithTraces>\t Path to folder with traces to be validated agianst automata.");
		System.out.println("");
		System.out.println("Options can be provided as Java System options, i.e., by passing the argument -Doption=value. A list of options follows.");
		System.out.println("\tcheckGuards\t	Verify guard conditions. Default value is \"true\".");
		System.out.println("\tcheckForGuardsNotReset\t	Check if guards were not reset in the traversed path. Default value is \"false\".");
		System.out.println("\tvalidateAbsoluteClocks\t  Validate absolute clocks against the guards for absolute clocks in the automata. Default value is \"true\".");
		System.out.println("\tprintValidationResults\t  Print validation results on console. Default is \"true\".");
		System.out.println("\tvalidationOutputFile\t  Name of the file containing validation results. Default is \"TkT.validationResults.csv\"");
		System.out.println("\tuseMethodExcutionTime\t  Consider only the time spent in the method under analysis. Default is \"false\".");
		System.out.println("\ttkt.collectOnlyMainError\t Trace only the error which is supposed to be the main error in the trace. In the presence of non-deterministic TA this is the error that occurs in the longest path. Default is \"false\".");
		System.out.println("\tshowOtherErrors\t 	Print all the errors, including the ones different than the main error. Default is \"false\".");
	}


}
