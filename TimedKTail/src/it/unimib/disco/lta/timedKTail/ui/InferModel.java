/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.ui;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import it.unimib.disco.lta.timedKTail.JTMTime.TimedAutomata;
import it.unimib.disco.lta.timedKTail.util.JavaRunner;

public class InferModel {
	
	public static void main(String[] args) throws IOException{		
		File destFile = new File ( args[0] );

		String fTraces = args[1];

		TimedAutomata ta = Main.inferTimedAutomata(fTraces);
		
		Main.saveAutomata(ta,destFile);
		
	}
	
	
	
	public static TimedAutomata inferAutomataInSeparateProcess(String destFile, String fTraces, int politicsOneVal, 
			double kPoliticsOneVal, 
			int politicsMultyVal, double deltaForRangeCalculation, double normalDistributionConfidence, 
			boolean inferAbsoluteClocks, boolean includeNestedCallsTime, boolean validateAfterMerging) {
		
		ArrayList<String> parameters = new ArrayList<String>();
		parameters.add( destFile );
		parameters.add( fTraces );
		
		ArrayList<String> options = new ArrayList<String>();
		addOption(options, "Xmx", "4096M");
		addOption(options, Main.TKT_POLICY_ONE, politicsOneVal);
		addOption(options, Main.TKT_POLICY_K_POLICY_ONE_VAL, kPoliticsOneVal);
		addOption(options, Main.TKT_INTERVAL_INFERENCE_POLICY, politicsMultyVal);
		addOption(options, Main.TKT_POLICY_MIN_MAX_INCREASE_FACTOR, deltaForRangeCalculation);
		addOption(options, Main.TKT_POLICY_NORMAL_DISTRIBUTION_CONFIDENCE, normalDistributionConfidence);
		addOption(options, Main.TKT_POLICY_DERIVE_GLOBAL_CLOCK, inferAbsoluteClocks);
		addOption(options, Main.TKT_POLICY_INCLUDE_NESTED_CALLS_TIME, includeNestedCallsTime );
		addOption(options, Main.TKT_POLICY_VALIDATE_AFTER_MERGING, validateAfterMerging );
		
		try {
			JavaRunner.runMainInClass(InferModel.class, options, parameters, 0, null, true, null, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Main.loadAutomata(destFile);
	}
	
	
	

	public static void addOption(ArrayList<String> options, String constant, int politicsOneVal) {
		addOption(options, constant, ""+politicsOneVal);
	}
	
	public static void addOption(ArrayList<String> options, String constant, boolean politicsOneVal) {
		addOption(options, constant, ""+politicsOneVal);
	}
	
	public static void addOption(ArrayList<String> options, String constant, double politicsOneVal) {
		addOption(options, constant, ""+politicsOneVal);
	}
	
	public static void addOption(ArrayList<String> options, String constant, String politicsOneVal) {
		options.add("-D"+constant+"="+politicsOneVal);
	}


}
