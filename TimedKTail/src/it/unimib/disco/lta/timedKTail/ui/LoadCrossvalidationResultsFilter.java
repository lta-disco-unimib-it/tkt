package it.unimib.disco.lta.timedKTail.ui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import it.unimib.disco.lta.timedKTail.util.WordCounter;

import java.util.Set;
import java.util.Vector;

import scala.Array;

public class LoadCrossvalidationResultsFilter {

	public static class Data {

		public int acceptedAsValid;

		public int runs;

		private Set<String> subjects = new HashSet<String>();

		public String name;

		public int totalInvalid;
		public int totalValid;

		public double avgNotAccepted;

		public double avgInferenceTime;

		public double avgAccepted;

		public Data( String name ){
			this.name = name;
		}

		public double getAvgAcceptedAsValid() {
			// TODO Auto-generated method stub
			return (double)acceptedAsValid/(double)runs;
		}

		public void addSubject(String folderWithKFold) {
			subjects.add( folderWithKFold );
		}

		public String getName() {
			return name;
		}

		public double getAvgInvalid() {
			return (double)totalInvalid/(totalInvalid+totalValid);
		}

		public double getOverallAvgNotAccepted() {
			return avgNotAccepted/runs;
		}

		public double getAvgValid() {
			return (double)totalValid/(totalInvalid+totalValid);
		}
	}

	private static final String CONFIG_ARRAY_SUFFIX = "c";

	private HashMap<String,Data> prjData = new HashMap<>();
	
	private HashSet<String> observedConfigurations = new HashSet<String>(); 

	private Map<String,Map<String,List<Double>>> intMaps = new HashMap<>();

	private boolean doFiltering = true;


	private boolean expectedIncludeNestedCallsTime;
	private boolean expectedValidateAfterMerging;




	public Map<String, List<Double>> getIntegerMap( String mapKey ){
		Map<String, List<Double>> map = intMaps.get(mapKey);

		if ( map == null ){
			map = new HashMap<String, List<Double>>();
			intMaps.put(mapKey, map);
		}

		return map;
	}

	public List<Double> getFromIntegerListMap( Map<String,List<Double>> map, String key ){
		List<Double> data = map.get(key);

		if ( data == null ){
			data = new ArrayList<>();
			map.put(key, data);
		}

		return data;
	}

	public static void main(String[] args) throws IOException {

		String dest = args[0];

		LoadCrossvalidationResultsFilter lcr = new LoadCrossvalidationResultsFilter();

		File destFile = new File( dest );
		if ( destFile.exists() ){
			System.out.println("Destfile exists");
			return;
		}

		List<File> files = new ArrayList<>();
		for ( int i = 1; i < args.length; i++){
			File results = new File( args[i] );
			files.add(results);
		}

		lcr.loadDataInFiles( files );
		lcr.generateResults(destFile);


		//		
		//		getFromIntegerListMap( getIntegerMap(+prj) ,"_").add(nodes);
		//		getFromIntegerListMap( getIntegerMap("automataTransitions_"+prj) ,"_").add(transitions);

	}

	public void loadDataInFiles(List<File> files) throws FileNotFoundException, IOException {
		for( File f : files ){
			if ( f.exists() ){
				updateWithDataInFile(f);
			}
		}
	}

	boolean sensitivityData = ! Boolean.getBoolean("validateWithNewData");

	private boolean use_config_array;

	public final static boolean OLD = Boolean.getBoolean("tkt.oldCsv");

	public static final boolean ASE = Boolean.getBoolean("tkt.ASE");

	public boolean isUse_config_array() {
		return use_config_array;
	}

	public void setUse_config_array(boolean use_config_array) {
		this.use_config_array = use_config_array;
	}

	public void generateResults(File _destFile) throws IOException {
		ArrayList<Data> allData = new ArrayList<>();
		allData.addAll(prjData.values());

		allData.sort(new Comparator<Data>() {

			@Override
			public int compare(Data o1, Data o2) {
				return o1.name.compareTo(o2.name);
			}
		});

		
		BufferedWriter bw= new BufferedWriter( new FileWriter(_destFile, false) );
		try {
			if ( sensitivityData ){
				
				
				//printTableVectorsWithPrefix("config_accepted_", bw);
				
				
				printTableVectorsWithPrefix("normal_accepted_", bw);


				printTableVectorsWithPrefix("all_AbsoluteClocks_", bw);

				printTableVectorsWithPrefix("all_accepted_AbsoluteClocks_", false,bw);


				printTableVectorsWithPrefix("minMax_accepted_", bw);



				printTableVectorsWithPrefix("normal_accepted", bw);


				printTableVectorsWithPrefix("minMaxAccepted", bw);


				printTableVectorsWithPrefix("accepted_",false,bw);
				printTableVectorsWithPrefix("invalid_events_",false,bw);
				printTableVectorsWithPrefix("invalid_guards_",false,bw);
				printTableVectorsWithPrefix("missing_clocks_",false,bw);
				printTableVectorsWithPrefix("non_final_states_",false,bw);

				printConfigMetrics(bw, "");
			} else {

				printTableVectorsWithPrefix("normal_notAccepted", false, bw);		
				printTableVectorsWithPrefix("minMaxNotAccepted",false, bw);
				printTableVectorsWithPrefix("normal_notAccepted_", false,bw);

				printTableVectorsWithPrefix("all_notAccepted_AbsoluteClocks_", false,bw);

				printTableVectorsWithPrefix("minMax_notAccepted_", false,bw);

				
				String prefixToAdd = "disturbate_";
				printTableVectorsWithPrefix("accepted_",false,bw,prefixToAdd);
				printTableVectorsWithPrefix("invalid_events_",false,bw,prefixToAdd);
				printTableVectorsWithPrefix("invalid_guards_",false,bw,prefixToAdd);
				printTableVectorsWithPrefix("missing_clocks_",false,bw,prefixToAdd);
				printTableVectorsWithPrefix("non_final_states_",false,bw,prefixToAdd);
				
				printConfigMetrics(bw, prefixToAdd);
			}
		} finally {
			bw.close();
		}
		
		
		
		
		System.out.println("\n\n\n\n");

		for ( Data data : allData ){

			System.out.println(data.getName()+" "+data.getAvgInvalid()+" "+data.getAvgValid()+" "+data.runs+" "+data.subjects);
		}
	}

	public void printConfigMetrics(BufferedWriter bw, String prefixToAdd) {
		setUse_config_array(true);
		printTableVectorsWithPrefix("config_accepted_",false,bw,prefixToAdd);
		printTableVectorsWithPrefix("config_notAccepted_",false,bw,prefixToAdd);

		printTableVectorsWithPrefix("config_avgAccepted_",false,bw,prefixToAdd);
		printTableVectorsWithPrefix("config_avgNotAccepted_",false,bw,prefixToAdd);

		printTableVectorsWithPrefix("config_invalid_guards_",false,bw,prefixToAdd);

		printTableVectorsWithPrefix("config_invalid_events_",false,bw,prefixToAdd);
		printTableVectorsWithPrefix("config_invalid_guards_",false,bw,prefixToAdd);
		printTableVectorsWithPrefix("config_missing_clocks_",false,bw,prefixToAdd);
		printTableVectorsWithPrefix("config_non_final_states_",false,bw,prefixToAdd);
		
		setUse_config_array(false);
	}


	private void updateWithDataInFile(File results)
			throws FileNotFoundException, IOException {
		System.out.println("Opening: "+results.getAbsolutePath());
		BufferedReader br = new BufferedReader(new FileReader(results) );

		//folderWithKFold,executions,politicsOneVal,kPoliticsOneVal,
		//politicsMultyVal,deltaForRangeCalculation,normalDistributionConfidence,totalValid,totalInvalid,
		//avgNodes,avgTransitions,avgInferenceTime,avgValidationTime

		String line;

		boolean skipFirst = false;
		int lines = 0;
		if ( skipFirst ){
			br.readLine();
			lines++;
		}


		WordCounter wc = new WordCounter();

		while ( ( line = br.readLine() ) != null ){

			if ( line.startsWith("#") ){
				continue;
			}

			lines++;

			
			//
//			+politicsOneVal+","+kPoliticsOneVal+","+politicsMultyVal+","+deltaForRangeCalculation+",
//			"+normalDistributionConfidence+","
//			+validateAbsoluteClocks+","
//			+includeNestedCallsTime+",
//			"+validateAfterMerging+",""

//	+totalValid+","+totalInvalid+","
			
//	+unmatchedEvents+","+violatedGuards+","+missingClocks+","+nonFinalStates+","
			
//	+avgNodes+","+avgTransitions+","+avgInferenceTime+","+avgValidationTime+","+avgPerformedMerges+","
			//+avgIgnoredMerges+","
//	+totalValidNoGuards+","+totalInvalidNoGuards+","
//	
			
			
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
			
			boolean includeNestedCallsTime = true;
			boolean validateAfterMerging = false;
			
			if ( !ASE ){
				includeNestedCallsTime = Boolean.valueOf(c[i++] );
				validateAfterMerging = Boolean.valueOf(c[i++] );
			}

			if ( doFiltering ){
				if ( includeNestedCallsTime != expectedIncludeNestedCallsTime ){
					continue;
				}
				if ( validateAfterMerging != expectedValidateAfterMerging ){
					continue;
				}
			}

			Integer totalValid = Integer.valueOf( c[i++] );
			Integer totalInvalid = Integer.valueOf( c[i++] );

			Integer totalInvalidEvents = Integer.valueOf( c[i++] );
			Integer totalInvalidGuards = Integer.valueOf( c[i++] );
			Integer missingClocks = Integer.valueOf( c[i++] );
			
			Integer nonFinalStates;
			
			if ( OLD ){
				nonFinalStates = 0;
			} else {
				nonFinalStates = Integer.valueOf( c[i++] );
			}
			
			if ( totalInvalidEvents + totalInvalidGuards + missingClocks + nonFinalStates != totalInvalid ){
				throw new IllegalStateException("totalInvalidEvents + totalInvalidGuards + missingCLocks!= totalInvalid (line:"+lines+")");
			}

			Double nodes = Double.valueOf( c[i++] );
			Double transitions = Double.valueOf(c[i++] );
			Double avgInferenceTime = Double.valueOf(c[i++] );
			Double avgValidationTime = Double.valueOf( c[i++] );
			
			double avgPerformedMerges;
			double avgIgnoredMerges;
			if ( ! ASE ){
				avgPerformedMerges = Double.valueOf( c[i++] );
				avgIgnoredMerges = Double.valueOf( c[i++] );
			}
			
			Integer totalValidEventsNoGUards;
			Integer totalInvalidEventsNoGUards;
			
			if ( OLD ){
				totalValidEventsNoGUards=0;
				totalInvalidEventsNoGUards=0;
			} else {
				totalValidEventsNoGUards = Integer.valueOf( c[i++] );
				totalInvalidEventsNoGUards = Integer.valueOf( c[i++] );
			}

//			String runs = folderWithKFold.split("_")[1];
//			if ( runs.equals("all") ){
//				runs="T";
//			}
			
			
			
			
			String runs = getRunsFromPrjName( folderWithKFold );
			
			//String prj = folderWithKFold.split("_")[1];


			String key = runs+"_"+politicsOneVal+"_"+kPoliticsOneVal+"_"+politicsMultyVal+"_"+deltaForRangeCalculation+"_"+normalDistributionConfidence+"_"+inferAbsoluteClocks;


			String lineKey = folderWithKFold+"_"+key;
			if ( wc.increment(lineKey) > 1 ){
				System.out.println("Contains: "+lineKey);
				//throw new IllegalStateException("Duplicated "+lineKey+" "+results.getAbsolutePath()+":"+lines);
				System.err.println("Key already present: "+lineKey+" "+results.getAbsolutePath()+":"+lines);
				continue;
			}

			System.out.println("LOADED "+key);
			
			observedConfigurations.add( key );


			HashMap<String, Data> _prjData = prjData;

			Data data = getPrjData(key, _prjData);

			//			if ( data.subjects.contains(folderWithKFold) ){
			//				System.out.println("Already contains "+folderWithKFold+ " "+key);
			//				continue;
			//			}

			if ( totalInvalid == 0 ){
				data.acceptedAsValid++;
			}

			data.totalInvalid += totalInvalid;
			data.totalValid += totalValid;

			data.runs++;


			double avgNotAccepted = (double)totalInvalid/(totalValid+totalInvalid);
			double avgAccepted = (double)totalValid/(totalValid+totalInvalid);

			if ( Double.isNaN( avgAccepted ) ){
				IllegalArgumentException e = new IllegalArgumentException("NaN (avgAccepted) in "+results.getAbsolutePath()+" line "+line+" value: "+avgAccepted);
				//throw e;
				e.printStackTrace();
				continue;
			}

			data.avgNotAccepted += (double)totalInvalid/(totalValid+totalInvalid);
			data.avgAccepted += (double)totalValid/(totalValid+totalInvalid);

			data.avgInferenceTime += avgInferenceTime;


			data.addSubject( folderWithKFold );


			getFromIntegerListMap( getIntegerMap("all_AbsoluteClocks_"+inferAbsoluteClocks) ,"_").add(avgAccepted);

			getFromIntegerListMap( getIntegerMap("all_accepted_AbsoluteClocks_"+inferAbsoluteClocks) ,"_").add(avgNotAccepted);
			getFromIntegerListMap( getIntegerMap("all_notAccepted_AbsoluteClocks_"+inferAbsoluteClocks) ,"_").add(avgNotAccepted);

			getFromIntegerListMap( getIntegerMap("accepted_") ,runs).add((double)totalValid);
			getFromIntegerListMap( getIntegerMap("notAccepted_") ,runs).add((double)totalInvalidEvents+totalInvalidGuards+missingClocks);

			getFromIntegerListMap( getIntegerMap("invalid_events_") ,runs).add((double)totalInvalidEvents);
			getFromIntegerListMap( getIntegerMap("invalid_guards_") ,runs).add((double)totalInvalidGuards);
			getFromIntegerListMap( getIntegerMap("missing_clocks_") ,runs).add((double)missingClocks);
			getFromIntegerListMap( getIntegerMap("non_final_states_") ,runs).add((double)nonFinalStates);

			getFromIntegerListMap( getIntegerMap("config_avgAccepted_") ,key).add(avgAccepted);
			getFromIntegerListMap( getIntegerMap("config_avgNotAccepted_") ,key).add(avgNotAccepted);

			getFromIntegerListMap( getIntegerMap("config_accepted_") ,key).add((double)totalValid);
			getFromIntegerListMap( getIntegerMap("config_notAccepted_") ,key).add((double)totalInvalidEvents+totalInvalidGuards+missingClocks+nonFinalStates);
			getFromIntegerListMap( getIntegerMap("config_invalid_guards_") ,key).add((double)totalInvalidGuards);
			getFromIntegerListMap( getIntegerMap("config_invalid_events_") ,key).add((double)totalInvalidEvents);
			getFromIntegerListMap( getIntegerMap("config_missing_clocks_") ,key).add((double)missingClocks);
			getFromIntegerListMap( getIntegerMap("config_non_final_states_") ,key).add((double)nonFinalStates);
			
			System.out.println("!!!!!SIZE OF config_accepted_"+key+" "+getFromIntegerListMap( getIntegerMap("config_accepted_") ,key).size());
			
			{
				String keyT = runs+"_"+politicsOneVal+"_"+kPoliticsOneVal+"_"+politicsMultyVal+"_"+deltaForRangeCalculation+"_"+normalDistributionConfidence+"_"+true;
				getFromIntegerListMap( getIntegerMap("config_avgAccepted_") ,keyT);
				getFromIntegerListMap( getIntegerMap("config_avgNotAccepted_") ,keyT);
				getFromIntegerListMap( getIntegerMap("config_accepted_") ,keyT);
				getFromIntegerListMap( getIntegerMap("config_notAccepted_") ,keyT);
				getFromIntegerListMap( getIntegerMap("config_invalid_guards_") ,keyT);
				getFromIntegerListMap( getIntegerMap("config_invalid_events_") ,keyT);
				getFromIntegerListMap( getIntegerMap("config_missing_clocks_") ,keyT);
				getFromIntegerListMap( getIntegerMap("config_non_final_states_") ,keyT);
			}

			getFromIntegerListMap( getIntegerMap("inferenceTime_") ,runs).add(avgInferenceTime);

			if ( politicsOneVal == 3 ){
				if ( politicsMultyVal == 4 ){

					getFromIntegerListMap( getIntegerMap("normal_accepted") ,runs).add(avgAccepted);
					
					
					
					getFromIntegerListMap( getIntegerMap("normal_notAccepted") ,runs).add(avgNotAccepted);

					if ( Double.isNaN(avgAccepted) ){
						throw new IllegalArgumentException("NaN (avgAccepted) in "+results.getAbsolutePath()+" line "+line+" value: "+avgAccepted);
					}
					getFromIntegerListMap( getIntegerMap("normal_accepted_"+normalDistributionConfidence) ,"all").add(avgAccepted);
					getFromIntegerListMap( getIntegerMap("normal_notAccepted_"+normalDistributionConfidence) ,"all").add(avgNotAccepted);


					if ( normalDistributionConfidence == 0.99 ){
						getFromIntegerListMap( getIntegerMap("acceptedPerNormalized_99_"+inferAbsoluteClocks) ,runs).add(avgAccepted);
						getFromIntegerListMap( getIntegerMap("notAcceptedPerNormalized_99_"+inferAbsoluteClocks) ,runs).add(avgNotAccepted);
					} else {
						getFromIntegerListMap( getIntegerMap("acceptedPerNormalized_95_"+inferAbsoluteClocks) ,runs).add(avgAccepted);
						getFromIntegerListMap( getIntegerMap("notAcceptedPerNormalized_95_"+inferAbsoluteClocks) ,runs).add(avgNotAccepted);
					}
				}

				if ( politicsMultyVal == 3 ){

					getFromIntegerListMap( getIntegerMap("minMaxAccepted") ,runs).add(avgAccepted);
					getFromIntegerListMap( getIntegerMap("minMaxNotAccepted") ,runs).add(avgNotAccepted);

					getFromIntegerListMap( getIntegerMap("minMax_accepted_"+deltaForRangeCalculation) ,"all").add(avgAccepted);
					getFromIntegerListMap( getIntegerMap("minMax_notAccepted_"+deltaForRangeCalculation) ,"all").add(avgNotAccepted);

					getFromIntegerListMap( getIntegerMap("acceptedPerRange_"+deltaForRangeCalculation+"_"+inferAbsoluteClocks) ,runs).add(avgAccepted);
					getFromIntegerListMap( getIntegerMap("notAcceptedPerRange_"+deltaForRangeCalculation+"_"+inferAbsoluteClocks) ,runs).add(avgNotAccepted);
				}
			}



		}
		
		if ( getFromIntegerListMap( getIntegerMap("config_accepted_") ,"T100").size() != getFromIntegerListMap( getIntegerMap("config_accepted_") ,"T90").size() ){
			throw new IllegalStateException("Missing configs in: "+results);
		}

		br.close();
	}


	public static String getRunsFromPrjName(String folderWithKFold) {
		int prjEnd = folderWithKFold.indexOf("_T");
		String runs;
		if ( prjEnd == -1 ){
			runs = "T";
		} else {
			runs = folderWithKFold.substring(prjEnd+1);
		}
		
		int pos = runs.indexOf("_");
		if ( pos > 0 ){
			runs = runs.substring(0, pos);
		}
		return runs;
	}

	public HashSet<String> getObservedConfigurations() {
		return observedConfigurations;
	}

	private void printTableVectorsWithPrefix(String tablePrefix, BufferedWriter bw) {
		printTableVectorsWithPrefix(tablePrefix,true,bw);
	}

	private void printTableVectorsWithPrefix(String tablePrefix, boolean printSizeInName, BufferedWriter bw) {
		{
			printTableVectorsWithPrefix(tablePrefix, printSizeInName, bw, null);
		}
	}




	private void printTableVectorsWithPrefix(String tablePrefix, boolean printSizeInName, BufferedWriter bw,
			String prefixToAdd) {
		// TODO Auto-generated method stub
		List<String> mNames = extractIntegerKeys(tablePrefix);

		Collections.sort(mNames);

		System.out.println("Sorted: "+mNames);

		printTableVectors(mNames,printSizeInName,bw,prefixToAdd);
	}


	private void printTableVectors(List<String> mNames,boolean printSizeInName, BufferedWriter bw, String prefixToAdd) {
		
		if ( prefixToAdd == null ){
			prefixToAdd = "";
		}

		try {
			

			if ( use_config_array ){
				declareConfigArrays(mNames, printSizeInName, prefixToAdd, bw);
			}

			for ( String mName : mNames ){
				System.out.println("Data for "+mName);
				Map<String, List<Double>> map = getIntegerMap(mName);

				Set<String> keys = map.keySet();
				ArrayList<String> sortedKeys = new ArrayList<>();
				sortedKeys.addAll(keys);
				Collections.sort(sortedKeys);

				for ( String key : sortedKeys ){
					List<Double> l = map.get(key);

					
					String nameKey = mName+"_"+key;
					if ( printSizeInName ){
						nameKey += "_"+l.size();
					}

					String vectorName = prefixToAdd+nameKey;

					if ( use_config_array ){
						vectorName=buildConfigArrayLocation(vectorName);

						if ( vectorName == null ){
							continue;
						}
					}

					bw.append(vectorName+"=c( ");
					if ( l.size() == 0 ){
						int lastDash = vectorName.lastIndexOf('[');
						String sizeKey = vectorName.substring(0, lastDash);
						
						Integer size = configArraySizes.get( sizeKey );
						if ( size == null ){
							System.out.println("Looking for "+sizeKey+" : "+mName+" "+vectorName);
							System.out.println(configArraySizes);
						}
						
						for ( int i = 0; i < size; i++ ){
							bw.append("NA");
							if ( i != size-1 ){
								bw.append(",");
							}
						}		
					} else {
						for ( int i = 0; i < l.size(); i++ ){
							bw.append(String.valueOf(l.get(i)));
							if ( i != l.size()-1 ){
								bw.append(",");
							}
						}
					}
					bw.append(")");
					bw.newLine();

				}
			}

			bw.newLine();
			bw.newLine();

			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	



	public void declareConfigArrays(List<String> mNames, boolean printSizeInName, String prefixToAdd, BufferedWriter bw)
			throws IOException {
		
		HashSet<String> filled = new HashSet<>();
		
		for ( String mName : mNames ){
			//System.out.println("Data for "+mName);
			Map<String, List<Double>> map = getIntegerMap(mName);

			Set<String> keys = map.keySet();
			ArrayList<String> sortedKeys = new ArrayList<>();
			sortedKeys.addAll(keys);
			Collections.sort(sortedKeys);

			boolean done = false;
			for ( String key : sortedKeys ){
				List<Double> l = map.get(key);


				String nameKey = mName+"_"+key;
				if ( printSizeInName ){
					nameKey += "_"+l.size();
				}

				String vectorName = prefixToAdd+nameKey;

				String configArray = identifyConfigArray(vectorName);
				if ( configArray != null ){
					if (! filled.contains(configArray) ){
						bw.append(configArray+"=matrix(,nrow="+configs.size()+",ncol="+l.size()+",byrow=TRUE)");
						//bw.append(configArray+"=rep(0,"+configs.size()+")");
						bw.newLine();
						done = true;
						filled.add(configArray);
						configArraySizes.put( configArray, l.size() );
					}
					
				}
			}
			
			if ( ! done ){
				System.out.println("Not done for "+mName);
			}
		}
	}

	HashMap<String,Integer> configArraySizes = new HashMap<String,Integer>();
	ArrayList<String> configs = new ArrayList<>();

	public void addConfigName(String configName){
		configs.add(configName);
	}

	private String identifyConfigArray(String vectorName) {
		String location = buildConfigArrayLocation(vectorName);

		if( location == null ){
			return null;
		}

		int end = location.indexOf(CONFIG_ARRAY_SUFFIX+"[");

		return location.substring(0, end)+CONFIG_ARRAY_SUFFIX;
	}

	private String buildConfigArrayLocation(String vectorName) {
		int i=0;
		for ( String config : configs ){
			i++;
			if ( vectorName.endsWith(config) ){
				String vecHeader = vectorName.substring(0, vectorName.length()-config.length());
				return vecHeader+CONFIG_ARRAY_SUFFIX+"["+i+",]";
			}
		}
		return null;
	}

	private List<String> extractIntegerKeys(String string) {
		List<String> res = new ArrayList<>();

		for ( String key : intMaps.keySet() ){
			if ( key.startsWith(string) ){
				res.add( key );
			}
		}

		return res;
	}

	private static Data getPrjData(String key, HashMap<String, Data> _prjData) {
		Data data = _prjData.get(key);
		if ( data == null ){
			data = new Data(key);
			_prjData.put(key, data);
		}
		return data;
	}

	public boolean isExpectedIncludeNestedCallsTime() {
		return expectedIncludeNestedCallsTime;
	}

	public void setExpectedIncludeNestedCallsTime(boolean expectedIncludeNestedCallsTime) {
		this.expectedIncludeNestedCallsTime = expectedIncludeNestedCallsTime;
	}

	public boolean isExpectedValidateAfterMerging() {
		return expectedValidateAfterMerging;
	}

	public void setExpectedValidateAfterMerging(boolean expectedValidateAfterMerging) {
		this.expectedValidateAfterMerging = expectedValidateAfterMerging;
	}

	public boolean isSensitivityData() {
		return sensitivityData;
	}

	public void setSensitivityData(boolean sensitivityData) {
		this.sensitivityData = sensitivityData;
	}

}
