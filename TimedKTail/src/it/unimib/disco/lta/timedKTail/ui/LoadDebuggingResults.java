package it.unimib.disco.lta.timedKTail.ui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import it.unimib.disco.lta.timedKTail.ui.LoadDebuggingResults.ExpectationGuard.ViolatedBoundary;
import it.unimib.disco.lta.timedKTail.util.WordCounter;

import java.util.Set;

import scala.Array;
import sun.text.normalizer.Trie.DataManipulate;

public class LoadDebuggingResults {
	
	public static abstract class Expectation {
		public abstract boolean matches(String splittedLine[]);
	}
	
	public static class ExpectationGuard extends Expectation {
		
		public enum ViolatedBoundary { UPPER, LOWER };
		
		private String method;
		private ViolatedBoundary vb;
		
		public ExpectationGuard( String method, ViolatedBoundary vb ){
			this.method = method;
			this.vb = vb;
		}
		
		public boolean matches(String splittedLine[]){
			
//0			commons-collections-413-traces_NEW_ALL_100,
//1			0,
//2			3,
//3			0.0,
//4			4,
//5			0.0,
//6			0.99,
//7			false,
//8			true,
//9			false,
//10			/home/ribonisuppa/TKT_ANALYSIS/commons-collections-413-traces_NEW/tracesDebugging/Tracce1/../org.apache.commons.collections.bidimap.AbstractDualBidiMap.EntrySet_failing/trace.15.csv,
//11			1,
//12			VIOLATED_GUARD,
//13			org.apache.commons.collections.bidimap.AbstractDualBidiMap.EntrySet.removeAll,
//14			320016,
//15			0<= CK253851 <= 2112,
//16			10267

			
				if ( ! "VIOLATED_GUARD".equals(splittedLine[12]) ){
					System.out.println(splittedLine[12]);
					return false;
				}
				
				Integer actualValue = Integer.valueOf( splittedLine[14] );
				
				String toProcess = splittedLine[15].replaceAll("<=", "").replaceAll("  ", " ");
				
				Integer upper = Integer.valueOf( toProcess.split(" ")[2] );
				if ( actualValue > upper ){
					if ( ! vb.equals(ViolatedBoundary.UPPER) ){
						System.out.println(actualValue + " > " + upper );
						return false;
					}
				} else {

					Integer lower = Integer.valueOf( toProcess.split(" ")[0] );
					if ( actualValue < lower ){
						if ( ! vb.equals(ViolatedBoundary.LOWER) ){
							System.out.println(actualValue + " < " + lower );
							return false;
						}
					}
				}
				
				
				return true;
		}
	}
	
	public static class Data {
		int erroneously_accpeted;
		int rejected_forWrongReason;
		int rejected_forGoodReason;
		public int totalUsefulAnomalies;
		public int totalUselessAnomalies;
		public boolean validateAbsoluteClocks;
		public boolean includeNestedCallsTime;
		public double deltaForRangeCalculation;
		public double normalDistributionConfidence;
		
		private Set<String> caseStudies = new HashSet<String>();
		
		public void addCaseStudy(String caseStudy) {
			caseStudies.add(caseStudy);
		}
	}

	public static void main(String[] args) throws IOException {

		String dest = args[0];


		File destFile = new File( dest );
//		if ( destFile.exists() ){
//			System.out.println("Destfile exists");
//			return;
//		}

		ArrayList<Data> results = new ArrayList<>();

		for ( int i = 1; i < args.length; i++){
			Collection<Data> values = loadDebuggingResults(args[i],args[++i]);
			results.addAll(values);
		}
		
		
		filterResultsAndWriteToFile(destFile, results, 0.0, 0.99, false, false);
		filterResultsAndWriteToFile(destFile, results, 0.0, 0.99, false, true);
		filterResultsAndWriteToFile(destFile, results, 0.0, 0.99, true, false);
		filterResultsAndWriteToFile(destFile, results, 0.0, 0.99, true, true);
		
		filterResultsAndWriteToFile(destFile, results, 0.05, 0.0, false, false);
		filterResultsAndWriteToFile(destFile, results, 0.05, 0.0, false, true);
		filterResultsAndWriteToFile(destFile, results, 0.05, 0.0, true, false);
		filterResultsAndWriteToFile(destFile, results, 0.05, 0.0, true, true);
		
		filterResultsAndWriteToFile(destFile, results, 1.0, 0.0, false, false);
		filterResultsAndWriteToFile(destFile, results, 1.0, 0.0, false, true);
		filterResultsAndWriteToFile(destFile, results, 1.0, 0.0, true, false);
		filterResultsAndWriteToFile(destFile, results, 1.0, 0.0, true, true);

	}

	public static void filterResultsAndWriteToFile(File destFile, ArrayList<Data> results,
			double deltaForRangeCalculation, double normalDistributionConfidence, boolean validateAbsoluteClocks,
			boolean includeNestedCallsTime) throws IOException {
		writeResultsToFile(true, new File( destFile, "deltaForRangeCalculation_"+deltaForRangeCalculation+"_normalDistributionConfidence_"+normalDistributionConfidence+"_validateAbsoluteClocks_"+validateAbsoluteClocks+"_includeNestedCallsTime_"+includeNestedCallsTime), filter( results, deltaForRangeCalculation, normalDistributionConfidence, validateAbsoluteClocks, includeNestedCallsTime ) );
	}

	private static List<Data> filter(ArrayList<Data> results, double deltaForRangeCalculation, double normalDistributionConfidence, boolean validateAbsoluteClocks, boolean includeNestedCallsTime) {
		ArrayList<Data> list = new ArrayList<Data>();
		
		for ( Data data : results ){
			
			System.out.println( data.deltaForRangeCalculation +" "+data.normalDistributionConfidence+" "+data.validateAbsoluteClocks+" "+data.includeNestedCallsTime);
			
			if ( data.deltaForRangeCalculation == deltaForRangeCalculation &&
					data.normalDistributionConfidence == normalDistributionConfidence &&
					data.validateAbsoluteClocks == validateAbsoluteClocks && 
					data.includeNestedCallsTime == includeNestedCallsTime ){
				list.add( data );
			}
		}
		
		return list;
	}

	public static void writeResultsToFile(boolean singleFile, File destFolder, List<Data> results) throws IOException {
		
		destFolder.mkdirs();
		
		String prefix = destFolder.getName().replace(".", "_");
		File destFile;
		
		if ( singleFile ){
			destFile = new File( destFolder.getParentFile(), "results.R");
		} else {
			destFile = new File( destFolder, "results.R");	
		}
		
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(destFile,true));
		
		bw.write("# "+destFolder.getName());
		bw.newLine();
		
		{
			bw.write(prefix+"_"+"erroneously_accepted = c( ");
			boolean first=true;
			for ( Data d : results ){
				if ( first ){
					first = false;
				} else {
					bw.write( "," );	
				}
				bw.write( " "+(double)d.erroneously_accpeted/(double)d.caseStudies.size() );
			}
			bw.write(");");

			bw.newLine();
			bw.newLine();
			bw.newLine();
		}
		
		{
			bw.write(prefix+"_"+"rejected_forWrongReason = c( ");
			boolean first=true;
			for ( Data d : results ){
				if ( first ){
					first = false;
				} else {
					bw.write( "," );	
				}
				bw.write( " "+(double)d.rejected_forWrongReason/(double)d.caseStudies.size() );
			}
			bw.write(");");
			

			bw.newLine();
			bw.newLine();
			bw.newLine();
		}
		
		
		{
			bw.write(prefix+"_"+"rejected_forGoodReason = c( ");
			boolean first=true;
			for ( Data d : results ){
				if ( first ){
					first = false;
				} else {
					bw.write( "," );	
				}
				bw.write( " "+(double)d.rejected_forGoodReason/(double)d.caseStudies.size() );
			}
			bw.write(");");

			bw.newLine();
			bw.newLine();
			bw.newLine();
		}
		
		
		{
			bw.write(prefix+"_"+"totalUsefulAnomalies = c( ");
			boolean first=true;
			for ( Data d : results ){
				if ( first ){
					first = false;
				} else {
					bw.write( "," );	
				}
				bw.write( " "+d.totalUsefulAnomalies );
			}
			bw.write(");");

			bw.newLine();
			bw.newLine();
			bw.newLine();
		}
		
		{
			bw.write(prefix+"_"+"totalUselessAnomalies = c( ");
			boolean first=true;
			for ( Data d : results ){
				if ( first ){
					first = false;
				} else {
					bw.write( "," );	
				}
				bw.write( " "+d.totalUselessAnomalies );
			}
			bw.write(");");
			
			bw.newLine();
			bw.newLine();
			bw.newLine();
		}
		
		{
			bw.write(prefix+"_"+"processesTraces = c( ");
			boolean first=true;
			for ( Data d : results ){
				if ( first ){
					first = false;
				} else {
					bw.write( "," );	
				}
				bw.write( " "+d.caseStudies.size() );
			}
			bw.write(");");
			
			bw.newLine();
			bw.newLine();
			bw.newLine();
		}
		
		bw.close();
	}

	private static Collection<Data> loadDebuggingResults(String file,String expectation) throws FileNotFoundException {
		
		ArrayList<Expectation> expectations = loadExpectedViolations( expectation );
		
		BufferedReader br = new BufferedReader(new FileReader( file ));
		
		String line = null;
		
		
		
		boolean previouslyObservedGoodReason = false;
		try {
			
			String prevAnomalousLine = null;
			
			Data prevData = null;
			
			while ( ( line = br.readLine() ) != null ){
				String[] content = line.split(",");
				
				
//0				/home/ribonisuppa/TKT_ANALYSIS/commons-collections-413-traces_NEW/tracesDebugging/Tracce1/../org.apache.commons.collections.bidimap.AbstractDualBidiMap.EntrySet_failing/trace.10.csv,
//1				ACCEPTED,
//2				commons-collections-413-traces_NEW_ALL_100,
//3				0,
//4				3,
//5				0.0,
//6				4,
//7				0.0,
//8				0.99,
//9				false,
//10				true,
//11				false
				


				
//				boolean validateAfterMerging = Boolean.parseBoolean(content[11]);
				
				if ( "ACCEPTED".equals(content[1]) ){
					
					double deltaForRangeCalculation = Double.parseDouble(content[7]);
					double normalDistributionConfidence = Double.parseDouble(content[8]);
					boolean validateAbsoluteClocks = Boolean.parseBoolean(content[9]);
					boolean includeNestedCallsTime = Boolean.parseBoolean(content[10]);
					
					Data data = getData( deltaForRangeCalculation, normalDistributionConfidence, validateAbsoluteClocks, includeNestedCallsTime );
					data.addCaseStudy( content[0] );
					
					data.erroneously_accpeted++;
					
					if ( prevAnomalousLine != null ){
						if ( previouslyObservedGoodReason ){ //check if at least one of the reported anomalies points the developer to the fault
							prevData.rejected_forGoodReason++;
						} else {
							System.out.println("REJECTED FOR WRONG REASON: "+file+": "+line);
							prevData.rejected_forWrongReason++;
						}

						previouslyObservedGoodReason = false;
					}
					prevAnomalousLine = null;
					
					prevData = data;
					continue;
				}
				
				
				//0				commons-collections-413-traces_NEW_ALL_100,
				//1				0,
				//2				3,
				//3				0.0,
				//4				4,
				//5				0.0,
				//6				0.99,
				//7				false,
				//8				true,
				//9				false,
				//10				/home/ribonisuppa/TKT_ANALYSIS/commons-collections-413-traces_NEW/tracesDebugging/Tracce1/../org.apache.commons.collections.bidimap.AbstractDualBidiMap.EntrySet_failing/trace.15.csv,
				//11				1,
				//12				VIOLATED_GUARD,
				//13				org.apache.commons.collections.bidimap.AbstractDualBidiMap.EntrySet.removeAll,
				//14				320016,
				//15				0<= CK253851 <= 2112,
				//16				10267
				
				
				double deltaForRangeCalculation = Double.parseDouble(content[5]);
				double normalDistributionConfidence = Double.parseDouble(content[6]);
				boolean validateAbsoluteClocks = Boolean.parseBoolean(content[7]);
				boolean includeNestedCallsTime = Boolean.parseBoolean(content[8]);
				
				Data data = getData( deltaForRangeCalculation, normalDistributionConfidence, validateAbsoluteClocks, includeNestedCallsTime );
				data.addCaseStudy( content[10] );
				
				if ( "1".equals(content[11]) ){
					if ( prevAnomalousLine != null ){
						if ( previouslyObservedGoodReason ){ //check if at least one of the reported anomalies points the developer to the fault
							prevData.rejected_forGoodReason++;
						} else {
							System.out.println("REJECTED FOR WRONG REASON: "+file+": "+line);
							prevData.rejected_forWrongReason++;
						}

						previouslyObservedGoodReason = false;
					}
				}
				
				prevAnomalousLine = line;
				
				boolean usefulFound = false;
				for ( Expectation e : expectations ){
					if ( e.matches(content) ){
						previouslyObservedGoodReason = true;
						
						data.totalUsefulAnomalies++;
						usefulFound = true;
					}
				}
				
				if ( ! usefulFound ){
					data.totalUselessAnomalies++;
				}
				
				prevData = data;
			}
			
			
			
			if ( previouslyObservedGoodReason ){
				prevData.rejected_forGoodReason++;
			} else {
				prevData.rejected_forWrongReason++;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Collection<Data> values = dataMap.values();
		dataMap= new HashMap<>();
		
		return values;
	}

	private static Data getData(double deltaForRangeCalculation, double normalDistributionConfidence,
			boolean validateAbsoluteClocks, boolean includeNestedCallsTime) {
		String key = deltaForRangeCalculation+"_"+normalDistributionConfidence+"_"+validateAbsoluteClocks+"_"+includeNestedCallsTime;
		
		Data data = dataMap.get( key );
		
		if ( data == null ){
			data = new Data();
			data.deltaForRangeCalculation = deltaForRangeCalculation;
			data.normalDistributionConfidence = normalDistributionConfidence;
			data.validateAbsoluteClocks = validateAbsoluteClocks;
			data.includeNestedCallsTime = includeNestedCallsTime;
			
			dataMap.put(key,data);
		}
		
		return data;
	}

	static HashMap<String,Data> dataMap = new HashMap<String,Data>();
	
	private static ArrayList<Expectation> loadExpectedViolations(String expectation) throws FileNotFoundException {
		// TODO Auto-generated method stub
		
		
				
		BufferedReader br = new BufferedReader(new FileReader( expectation ));
		
		String line = null;
		
		ArrayList<Expectation> expectations = new ArrayList<Expectation>();
		try {
			while ( ( line = br.readLine() ) != null ){
				String[] content = line.split(",");
				
				//0			VIOLATED_GUARD,
				//1			org.apache.commons.collections.bidimap.AbstractDualBidiMap.EntrySet.removeAll,
				//2			UPPER
				
				if ( "VIOLATED_GUARD".equals(content[0]) ){
					expectations.add( new ExpectationGuard(content[1], ViolatedBoundary.valueOf(content[2])) );
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return expectations;
	}
	


}
