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

import scala.Array;

public class LoadCrossvalidationPartialResultsFilter {

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

	HashMap<String,Data> prjData = new HashMap<>();

	Map<String,Map<String,List<Double>>> intMaps = new HashMap<>();

	private boolean CSV;

	private boolean doFiltering = true;




	public Map<String, List<Double>> getIntegerMap( String mapKey ){
		Map<String, List<Double>> map = intMaps.get(mapKey);

		if ( map == null ){
			map = new HashMap<String, List<Double>>();
			intMaps.put(mapKey, map);
		}

		return map;
	}

	public static List<Double> getFromIntegerListMap( Map<String,List<Double>> map, String key ){
		List<Double> data = map.get(key);

		if ( data == null ){
			data = new ArrayList<>();
			map.put(key, data);
		}

		return data;
	}

	boolean sensitivityData = ! Boolean.getBoolean("validateWithNewData");

	public static void main(String[] args) throws IOException {

		String dest = args[0];


		LoadCrossvalidationPartialResultsFilter lcr = new LoadCrossvalidationPartialResultsFilter();

		File destFile = new File( dest );
		if ( destFile.exists() ){
			System.out.println("Destfile exists");
			return;
		}


		for ( int i = 1; i < args.length; i++){
			File results = new File( args[i] );
			lcr.updateWithDataInFile(results);
		}



//		ArrayList<Data> allData = new ArrayList<>();
//		allData.addAll(prjData.values());
//
//		allData.sort(new Comparator<Data>() {
//
//			@Override
//			public int compare(Data o1, Data o2) {
//				return o1.name.compareTo(o2.name);
//			}
//		});


		lcr.generateResults( destFile );

	}

	public void generateResults(File destFile) {

		if ( sensitivityData ){
			printTableVectorsWithPrefix("inferenceTime_",false,destFile,"");
		} else {
			printTableVectorsWithPrefix("inferenceTime_",false,destFile,"disturbate_");
		}


		System.out.println("\n\n\n\n");

		//		for ( Data data : allData ){
		//
		//			System.out.println(data.getName()+" "+data.getAvgInvalid()+" "+data.getAvgValid()+" "+data.runs+" "+data.subjects);
		//		}


		//printSizeInfo("automataNodes_");

		//printSizeInfo("automataTransitions_");
	}

	public void printSizeInfo(String ks) {
		List<String> mNames = extractIntegerKeys(ks);
		Collections.sort(mNames);
		for ( String nodesKey : mNames ){
			Map<String, List<Double>> map = getIntegerMap(nodesKey);
			List<Double> values = map.get("_");

			Double max = findMax(values);
			Double min = findMin(values);
			Double avg = avg(values);

			System.out.println(ks+" "+nodesKey+": "+min+" "+max+" "+avg);
			System.out.println(ks+" "+nodesKey+": "+values);
		}
	}


	private static double findMax(List<Double> values) {
		Double max = null;

		for ( Double v : values ){
			if ( max==null || v > max ){
				max = v;
			}
		}

		return max;
	}

	private static double findMin(List<Double> values) {
		Double min = null;

		for ( Double v : values ){
			if ( min == null || v < min ){
				min = v;
			}
		}

		return min;
	}

	private static double avg(List<Double> values) {
		Double max = 0.0;
		int c=0;
		for ( Double v : values ){
			max += v;
			c++;
		}

		return max/c;
	}

	private boolean expectedIncludeNestedCallsTime;

	private boolean expectedValidateAfterMerging;

	public void updateWithDataInFile(File results)
			throws FileNotFoundException, IOException {
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

		int last = 0;

		WordCounter wc = new WordCounter();

		while ( ( line = br.readLine() ) != null ){

			if ( line.startsWith("#") ){
				continue;
			}

			lines++;

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


			if ( !LoadCrossvalidationResultsFilter.ASE ){
				includeNestedCallsTime = Boolean.valueOf(c[i++] );
				validateAfterMerging = Boolean.valueOf(c[i++] );
			}

//			System.out.println("Include nested calls time: "+includeNestedCallsTime+" validateAfterMerging: "+validateAfterMerging);
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
			
			Integer nonFinalStates=0;
			if ( LoadCrossvalidationResultsFilter.OLD ){

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
			if ( ! LoadCrossvalidationResultsFilter.ASE ){
				avgPerformedMerges = Double.valueOf( c[i++] );
				avgIgnoredMerges = Double.valueOf( c[i++] );
			}
			
			
			Integer totalValidEventsNoGUards = 0;
			Integer totalInvalidEventsNoGUards = 0;
			if ( LoadCrossvalidationResultsFilter.OLD ){
				
			} else {
				totalValidEventsNoGUards = Integer.valueOf( c[i++] );
				totalInvalidEventsNoGUards = Integer.valueOf( c[i++] );	
			}
			

			String runs = LoadCrossvalidationResultsFilter.getRunsFromPrjName(folderWithKFold);
			
			
			String key = runs+"_"+politicsOneVal+"_"+kPoliticsOneVal+"_"+politicsMultyVal+"_"+deltaForRangeCalculation+"_"+normalDistributionConfidence+"_"+inferAbsoluteClocks;
			//					"_"+includeNestedCallsTime+"_"+validateAfterMerging;


			String lineKey = folderWithKFold+"_"+key;
			if ( wc.increment(lineKey) > 10 ){
				System.out.println("Contains: "+lineKey);

				//throw new IllegalStateException("Duplicated "+lineKey+" "+results.getAbsolutePath()+":"+lines);
				System.err.println("More than 10: "+lineKey+" "+results.getAbsolutePath()+":"+lines);
				continue;
			}



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

			if ( avgAccepted == Double.NaN ){
				throw new IllegalArgumentException("NaN in "+results.getAbsolutePath()+" line "+line+" ");
			}

			data.avgNotAccepted += (double)totalInvalid/(totalValid+totalInvalid);
			data.avgAccepted += (double)totalValid/(totalValid+totalInvalid);

			data.avgInferenceTime += avgInferenceTime;


			data.addSubject( folderWithKFold );



			String prj = folderWithKFold.split("_")[0];


			getFromIntegerListMap( getIntegerMap("automataNodes_"+prj) ,"_").add(nodes);
			getFromIntegerListMap( getIntegerMap("automataTransitions_"+prj) ,"_").add(transitions);




			getFromIntegerListMap( getIntegerMap("all_AbsoluteClocks_"+inferAbsoluteClocks) ,"_").add(avgAccepted);
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

			getFromIntegerListMap( getIntegerMap("inferenceTime_") ,runs).add(avgInferenceTime);

			if ( politicsOneVal == 3 ){
				if ( politicsMultyVal == 4 ){

					getFromIntegerListMap( getIntegerMap("normal_accepted") ,runs).add(avgAccepted);
					getFromIntegerListMap( getIntegerMap("normal_notAccepted") ,runs).add(avgNotAccepted);

					if ( Double.isNaN(avgAccepted) ){
						throw new IllegalArgumentException("NaN in "+results.getAbsolutePath()+" line "+line+" ");
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

//		System.out.println(getFromIntegerListMap( getIntegerMap("inferenceTime_") ,"T10").size());
//		System.out.println(getFromIntegerListMap( getIntegerMap("inferenceTime_") ,"T20").size());
//		System.out.println(getFromIntegerListMap( getIntegerMap("inferenceTime_") ,"T30").size());
//		System.out.println(getFromIntegerListMap( getIntegerMap("inferenceTime_") ,"T40").size());
//		System.out.println(getFromIntegerListMap( getIntegerMap("inferenceTime_") ,"T50").size());
//		System.out.println(getFromIntegerListMap( getIntegerMap("inferenceTime_") ,"T60").size());
//		if ( getFromIntegerListMap( getIntegerMap("inferenceTime_") ,"T60").size() - last != 200 ){
//			throw new IllegalStateException("getIntegerMap(\"inferenceTime_\") ,\"T60\").size() - last != 1000 : "+getFromIntegerListMap( getIntegerMap("inferenceTime_") ,"T60").size());
//		}
//		last=getFromIntegerListMap( getIntegerMap("inferenceTime_") ,"T60").size();
		
		br.close();
	}


	private void printTableVectorsWithPrefix(String tablePrefix, File destFile) {
		printTableVectorsWithPrefix(tablePrefix,true,destFile);
	}

	private void printTableVectorsWithPrefix(String tablePrefix, boolean printSizeInName, File destFile) {
		{
			printTableVectorsWithPrefix(tablePrefix, printSizeInName, destFile, null);
		}
	}




	private void printTableVectorsWithPrefix(String tablePrefix, boolean printSizeInName, File destFile,
			String prefixToAdd) {
		// TODO Auto-generated method stub
		List<String> mNames = extractIntegerKeys(tablePrefix);

		Collections.sort(mNames,new TKeySorter());

		System.out.println("Sorted: "+mNames);

		printTableVectors(mNames,printSizeInName,destFile,prefixToAdd);
	}




	private void printTableVectors(List<String> mNames,boolean printSizeInName, File destFile, String prefixToAdd) {

		if ( prefixToAdd == null ){
			prefixToAdd = "";
		}

		try {
			BufferedWriter bw = new BufferedWriter( new FileWriter(destFile, true) );


			for ( String mName : mNames ){
				//System.out.println("Data for "+mName);
				Map<String, List<Double>> map = getIntegerMap(mName);

				Set<String> keys = map.keySet();
				ArrayList<String> sortedKeys = new ArrayList<>();
				sortedKeys.addAll(keys);
				Collections.sort(sortedKeys, new TKeySorter());

				if ( CSV ){
					printCSVcontent(prefixToAdd, bw, map, sortedKeys, mName);
				} else {
					printRcontent(printSizeInName, prefixToAdd, bw, mName, map, sortedKeys);
				}
			}

			bw.newLine();
			bw.newLine();

			bw.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void printRcontent(boolean printSizeInName, String prefixToAdd, BufferedWriter bw, String mName,
			Map<String, List<Double>> map, ArrayList<String> sortedKeys) throws IOException {
		for ( String key : sortedKeys ){
			List<Double> l = map.get(key);


			String nameKey = mName+"_"+key;
			if ( printSizeInName ){
				nameKey += "_"+l.size();
			}


				bw.append(prefixToAdd+nameKey+"=c( ");


			for ( int i = 0; i < l.size(); i++ ){
				bw.append(String.valueOf(l.get(i)));
				if ( i != l.size()-1 ){
					bw.append(",");
				}
			}


			bw.append(")");	

			bw.newLine();

		}
	}

	public void printCSVcontent(String prefixToAdd, BufferedWriter bw, Map<String, List<Double>> map,
			ArrayList<String> sortedKeys, String mapName) throws IOException {
		String firstKey = sortedKeys.get(0);
		int size = map.get(firstKey).size();
		
		{boolean first=true;
		for ( String key : sortedKeys ){

			if ( first ){
				first = false;
			} else {
				bw.append(",");
			}

			bw.append(prefixToAdd+key);
		}
		bw.newLine();
		}
		
		
		for ( int i=0; i < size; i++ ){
			
			
			boolean first = true;
			for ( String key : sortedKeys ){
				
				if ( first ){
					first = false;
				} else {
					bw.append(",");
				}
				
				List<Double> l = map.get(key);
				
				if ( l.size() <= i ){
					throw new IllegalStateException("Key "+key+"in map "+mapName+" has less than "+size+" entries");
				}
				
				bw.append(""+l.get(i) );
			}
			bw.newLine();
		}
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

	public void loadDataInFiles(ArrayList<File> files) throws FileNotFoundException, IOException {
		for( File f : files ){
			if ( ! f.exists() ){
				continue;
			}
			try {
				updateWithDataInFile(f);
			} catch ( Exception e ){
				System.err.println("Problem with: "+f.getAbsolutePath());
				throw e;
			}
		}
	}

	public void setCsvFormat(boolean b) {
		CSV=b;
	}


}
