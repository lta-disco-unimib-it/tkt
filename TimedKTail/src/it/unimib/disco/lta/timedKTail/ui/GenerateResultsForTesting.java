package it.unimib.disco.lta.timedKTail.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateResultsForTesting {

	private int _executions[] = new int[]{ 10 };
	
	private int politicsOneVal=3;
	private double kPoliticsOneVal=0.0;

	private double _deltaForRangeCalculation[] = new double[]{ 0.05, 0.1, 0.15, 0.2, 0.25, 0.5, 0.75, 1.0 };
	private double _normalDistributionConfidence[] = new double[] { 0.95, 0.99 };

private String prefix = "TEST_T";

	private int executions = 10;

	private boolean[] TF = new boolean[]{true,false};
	
	private boolean[] _validateAbsoluteClocks = TF;

	private boolean includeNestedCallsTime = true;

	private boolean validateAfterMerging = false;

	int i = 0;

	private boolean DISTURBATE;	
	
	private void generateResult(File dest) throws IOException {
		
		BufferedWriter bw = new BufferedWriter( new FileWriter( dest ) );
		
		for ( int size=10; size<=100; size+=10 ){
			
			
			
			{
				
				int politicsMultyVal = 3;
				double normalDistributionConfidence=0;
				
				for ( boolean validateAbsoluteClocks : _validateAbsoluteClocks ){
					i=0;
					for ( double deltaForRangeCalculation : _deltaForRangeCalculation ){
						generateResultLine(bw, size, validateAbsoluteClocks, deltaForRangeCalculation, politicsMultyVal, normalDistributionConfidence);
					}
				}
				
			}
			
			
			int politicsMultyVal = 4;
			double deltaForRangeCalculation=0;
			
				for ( boolean validateAbsoluteClocks : _validateAbsoluteClocks ){
					i=0;
					for ( double normalDistributionConfidence : _normalDistributionConfidence ){
						generateResultLine(bw, size, validateAbsoluteClocks, deltaForRangeCalculation, politicsMultyVal, normalDistributionConfidence);
					}
				}
			

		}
		
		
		bw.close();
		
	}


	public void generateResultLine(BufferedWriter bw, int size, boolean validateAbsoluteClocks,
			double deltaForRangeCalculation, int politicsMultyVal, double normalDistributionConfidence) throws IOException {
		String head = prefix+size;
		
		
		
		String key = head
				+","+executions+","
				+politicsOneVal+","+kPoliticsOneVal+","+politicsMultyVal+","+deltaForRangeCalculation+","+normalDistributionConfidence+","
				+validateAbsoluteClocks+","
				+includeNestedCallsTime+","
				+validateAfterMerging;	
		
		if ( i == 8 ){
			i=0;
		}
		
		
		int _Valid = (size / 10) * (10-i);
		int _Invalid = size - _Valid;
		
		if ( DISTURBATE ){
			_Invalid = size / 10 * (10-i);
			_Valid = size - _Invalid;
		}
	
		i = (i+1);
		
		int _unmatchedEvents = _Invalid/2;
		int _violatedGuards = _Invalid-_unmatchedEvents;
		int _missingClocks = 0;
		int _nonFinalStates = 0;
		
		int _Nodes = 10;
		int _Transitions= 10 ;
		int _inferenceTime=10;
		int avgValidationTime=10;
		int _performedMerges=10;
		int _ignoredMerges=10;
		int _ValidNoGuards = size;
		int _InvalidNoGuards = 0;
		
		String result = _Valid+","+_Invalid+","
		+_unmatchedEvents+","+_violatedGuards+","+_missingClocks+","+_nonFinalStates+","
		+_Nodes+","+_Transitions+","+_inferenceTime+","+avgValidationTime+","+_performedMerges+","+_ignoredMerges+","
		+_ValidNoGuards+","+_InvalidNoGuards;
		
		
		String resultLine = key+","+result;
		
		bw.write(resultLine);
		bw.newLine();
	}
	
	
	public static void main(String[] args) throws IOException {
		File destFolder = new File( args[0] );

		for ( int N=1; N<=5; N++ ){
			String senFile = N+".sensitivity.timedKTail.results.csv";
			String specFile = N+".specificity.timedKTail.results.csv";

			GenerateResultsForTesting grt = new GenerateResultsForTesting();
			grt.generateResult(new File( destFolder, senFile) );

			grt = new GenerateResultsForTesting();
			grt.DISTURBATE=true;
			grt.generateResult(new File( destFolder, specFile ) );

		
		}
	}

}
