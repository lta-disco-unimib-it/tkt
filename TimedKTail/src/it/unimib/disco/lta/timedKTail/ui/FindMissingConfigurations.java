package it.unimib.disco.lta.timedKTail.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FindMissingConfigurations {


	public static void main(String[] args) throws IOException {

		

		LoadCrossvalidationResultsFilter lcr = new LoadCrossvalidationResultsFilter();
		
		
		List<File> files = new ArrayList<>();
		for ( int i = 0; i < args.length; i++){
			File results = new File( args[i] );
			files.add(results);
		}

		lcr.loadDataInFiles( files );
		
		HashSet<String> observed = lcr.getObservedConfigurations();
		
		for ( String config : observed ){
			System.out.println(config);
		}
		
//		buildExpectedKeys();
	}

//	private static void buildExpectedKeys() {
//		Set<String> expectedKeys = new HashSet<String>();
//		
//		String folderWithKFold = c[i++];
//		Integer executions = Integer.valueOf( c[i++] );
//		Integer politicsOneVal = Integer.valueOf(  c[i++] );
//		Double kPoliticsOneVal = Double.valueOf(  c[i++] );
//		Integer politicsMultyVal = Integer.valueOf( c[i++] );
//		Double deltaForRangeCalculation = Double.valueOf(c[i++] );
//		Double normalDistributionConfidence = Double.valueOf(c[i++] );
//		for ( inferAbsoluteClocks 
//
//
//		boolean[] TF = new boolean[]{ true, false };
//		boolean[] F = new boolean[]{ false };
//		
//		for ( boolean includeNestedCallsTime : TF ){
//			for ( boolean validateAfterMerging : F ){
//				
//			}	
//		}
//		
//
//
//
//
//
//		String foldSizes[] = {"10","20","30","40","50","60","70","80","90","100"};
//		
//		for ( String runs : foldSizes ){
//			
//		}
//		if ( runs.equals("all") ){
//			runs="T";
//		}
//		String prj = folderWithKFold.split("_")[1];
//
//
//		String key = runs+"_"+politicsOneVal+"_"+kPoliticsOneVal+"_"+politicsMultyVal+"_"+deltaForRangeCalculation+"_"+normalDistributionConfidence+"_"+inferAbsoluteClocks;
//
//	}

	
//	return (
//			getExperimentKey( folderWithKFold )
//			+","
//			+_Valid+","+_Invalid+","
//			+_unmatchedEvents+","+_violatedGuards+","+_missingClocks+","+_nonFinalStates+","
//			+_Nodes+","+_Transitions+","+_inferenceTime+","+avgValidationTime+","+_performedMerges+","+_ignoredMerges+","
//			+_ValidNoGuards+","+_InvalidNoGuards
//			);
//}



}
