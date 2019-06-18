package it.unimib.disco.lta.timedKTail.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

public class UpdateInvalidEventsInResultFile {

	public static class NewData {
		int violatedGuards=0;
		int nonFinalStates=0;
		int unmatchingEvents=0;
		
		public Integer getInvalid() {
			return violatedGuards+nonFinalStates+unmatchingEvents;
		}
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File separateValidationFileTP = new File( args[0] );
		File resultsFile = new File( args[1] );
		

		//	Guava1196_T10_9,0,3,0.0,4,0.0,0.95,true,true,false,
		//	/home/pastore/TKTAIL.GUAVA.1196.final/Tracce1/../com.google.common.eventbus.EventBus_failing/trace.83.csv,
		//	VIOLATED_GUARD,com.google.common.eventbus.EventBus.register,27,0<= T <= 339,349

		HashMap<String,NewData> toUpdate=new HashMap<String,NewData>();

		identifyDataToUpdate(separateValidationFileTP, toUpdate);

		

		{
			ArrayList<String> lines = updateResults(resultsFile, toUpdate, UpdateMode.FP_as_Accepted);
			File newResultsFile = new File( args[1].replace(".csv",".FP_as_Accepted.csv" ) );
			FileUtils.writeLines(newResultsFile, lines);
		}
		
		{	
			ArrayList<String> lines = new ArrayList<>();
			
			File newResultsFile = new File( args[1].replace(".csv",".faultCorrelationAnalysis.csv" ) );
			for ( Entry<String,FaultCorrelationData> e : faultCorrelationAnalysis.entrySet() ){
				String key = e.getKey();
				FaultCorrelationData fcd = e.getValue();
				lines.add(key+","+fcd.notRelatedWithFault+","+fcd.relatedWithFault);
			}
			FileUtils.writeLines(newResultsFile, lines);
		}
	}
	
	
	public static class FaultCorrelationData {
		public int relatedWithFault;
		public int notRelatedWithFault;
	}

	private static HashMap<String, FaultCorrelationData> faultCorrelationAnalysis = new HashMap<String,FaultCorrelationData>();
	private enum UpdateMode { FP_as_Accepted }
	public static ArrayList<String> updateResults(File resultsFile, HashMap<String, NewData> toUpdate, UpdateMode updateMode )
			throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(resultsFile));
		String line;

		ArrayList<String> lines = new ArrayList<String>();

		
		
		while ( ( line = br.readLine() ) != null ){

			if ( line.startsWith("#") ){
				continue;
			}

			String[] c = line.split(",");
			int i = 0;
			String folderWithKFold = c[i++];
			Integer executions = Integer.valueOf( c[i++] );

			Integer politicsOneVal = Integer.valueOf(  c[i++] );
			Double kPoliticsOneVal = Double.valueOf(  c[i++] );
			Integer politicsMultyVal = Integer.valueOf( c[i++] );
			Double deltaForRangeCalculation = Double.valueOf(c[i++] );
			Double normalDistributionConfidence = Double.valueOf(c[i++] );

			boolean inferAbsoluteClocks = Boolean.valueOf(c[i++] );




			Boolean includeNestedCallsTime = Boolean.valueOf(c[i++] );
			Boolean validateAfterMerging = Boolean.valueOf(c[i++] );




			Integer totalValid = Integer.valueOf( c[i++] );
			Integer totalInvalid = Integer.valueOf( c[i++] );

			Integer totalInvalidEvents = Integer.valueOf( c[i++] );
			Integer totalInvalidGuards = Integer.valueOf( c[i++] );
			Integer missingClocks = Integer.valueOf( c[i++] );


			Integer nonFinalStates = Integer.valueOf( c[i++] );

			Double nodes = Double.valueOf( c[i++] );
			Double transitions = Double.valueOf(c[i++] );
			Double avgInferenceTime = Double.valueOf(c[i++] );
			Double avgValidationTime = Double.valueOf( c[i++] );

			Double avgPerformedMerges = Double.valueOf( c[i++] );
			Double avgIgnoredMerges = Double.valueOf( c[i++] );



			Integer totalValidEventsNoGUards = Integer.valueOf( c[i++] );
			Integer totalInvalidEventsNoGUards = Integer.valueOf( c[i++] );




			String key = folderWithKFold +"," 
					//+ executions +","
					+politicsOneVal+","+kPoliticsOneVal+","+politicsMultyVal+","+deltaForRangeCalculation+","+normalDistributionConfidence+","+inferAbsoluteClocks
					+","+ includeNestedCallsTime
					+","+ validateAfterMerging;

			System.out.println("KEY: "+key);
			NewData updated = toUpdate.get(key);
			int all = totalValid+totalInvalid;
			
			
			
			FaultCorrelationData fcd = new FaultCorrelationData(); 
			if ( updateMode == UpdateMode.FP_as_Accepted ){
				if ( updated != null ){
					
					fcd.relatedWithFault=updated.getInvalid();
					fcd.notRelatedWithFault=totalInvalid-updated.getInvalid();
					
					totalInvalid = updated.getInvalid();
					totalValid = all - totalInvalid;

					totalInvalidGuards=updated.violatedGuards;
					totalInvalidEvents=updated.unmatchingEvents;
					missingClocks=0;
					nonFinalStates=updated.nonFinalStates;

				} else { //updated data is missing, means that everything is accepted of FP
					
					fcd.notRelatedWithFault=totalInvalid;
					
					totalValid=all;
					totalInvalid=0;
					totalInvalidEvents=0;
					totalInvalidGuards=0;
					missingClocks=0;
					nonFinalStates=0;
				}
			} else {
				throw new IllegalArgumentException();
			}

			faultCorrelationAnalysis.put(key, fcd);
			
			String newLine = folderWithKFold +"," + executions +","+politicsOneVal+","+kPoliticsOneVal+","+politicsMultyVal+","+deltaForRangeCalculation+","+normalDistributionConfidence+","+inferAbsoluteClocks
					+","+ includeNestedCallsTime
					+","+ validateAfterMerging
					+","+  totalValid
					+","+  totalInvalid
					+","+  totalInvalidEvents
					+","+  totalInvalidGuards
					+","+  missingClocks
					+","+  nonFinalStates
					+","+  nodes
					+","+  transitions
					+","+  avgInferenceTime
					+","+  avgValidationTime
					+","+  avgPerformedMerges
					+","+  avgIgnoredMerges
					+","+  totalValidEventsNoGUards
					+","+  totalInvalidEventsNoGUards;

			lines.add(newLine);
		}

		br.close();
		return lines;
	}

	public static void identifyDataToUpdate(File separateValidationFileTP, HashMap<String, NewData> toUpdate)
			throws FileNotFoundException, IOException {
		{
			BufferedReader br = new BufferedReader(new FileReader(separateValidationFileTP));
			String line;





			while ( ( line = br.readLine() ) != null ){
				//TKTAIL.GUAVA.371.ALPHA_IMPL.final_T80_8,
				//6,3,0.0,3,1.0,0.0,false,true,false,
				//10 /home/pastore/TKTAIL.GUAVA.371.ALPHA_IMPL.final/traces/Tracce1/../com_google_common_collect_LinkedHashMultimap_failing/trace.78.csv,
				//11 UNMATCHED_EVENT,
				//12 com.google.common.collect.LinkedHashMultimap.removeAll,
				//13 120024,
				//null,0
				String[] splitted = line.split(",");

				if ( splitted.length <= 1 ){
					continue;
				}

				StringBuffer sb = new StringBuffer();
				for ( int i = 0; i < 10; i++ ){
					
					if ( i >1 ){
						sb.append(",");
					}
					if ( i == 0 ){
						int idx = splitted[0].lastIndexOf('_');
						sb.append(splitted[0].substring(0, idx));
					}
					else if ( i != 1 ){
						sb.append(splitted[i]);
					}
				}
				String key = sb.toString();

				NewData dataToUpdate = toUpdate.get(key);

				if ( dataToUpdate == null ){
					dataToUpdate = new NewData();
					toUpdate.put(key, dataToUpdate);
				}

				//String prj = splitted[0].split(",")[0];
				if ( splitted[11].equals("VIOLATED_GUARD") ){
					dataToUpdate.violatedGuards++;
				} else if ( splitted[11].equals("UNMATCHED_EVENT") ) {
					dataToUpdate.unmatchingEvents++;
				} else if ( splitted[11].equals("NONFINAL_STATE") ) {
					dataToUpdate.nonFinalStates++;
				}

			}

			br.close();
		}
	}

}
