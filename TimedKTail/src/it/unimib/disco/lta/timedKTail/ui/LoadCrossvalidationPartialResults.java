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
import java.util.Set;

import scala.Array;

public class LoadCrossvalidationPartialResults {

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

	static HashMap<String,Data> prjData = new HashMap<>();

	static Map<String,Map<String,List<Double>>> intMaps = new HashMap<>();




	public static Map<String, List<Double>> getIntegerMap( String mapKey ){
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

	public static void main(String[] args) throws IOException {

		String dest = args[0];


		File destFile = new File( dest );
		if ( destFile.exists() ){
			System.out.println("Destfile exists");
			return;
		}


		for ( int i = 1; i < args.length; i++){
			File results = new File( args[i] );
			updateWithDataInFile(results);
		}



		ArrayList<Data> allData = new ArrayList<>();
		allData.addAll(prjData.values());

		allData.sort(new Comparator<Data>() {

			@Override
			public int compare(Data o1, Data o2) {
				return o1.name.compareTo(o2.name);
			}
		});


		boolean inference = ! Boolean.getBoolean("validateWithNewData");

		if ( inference ){
			printTableVectorsWithPrefix("inferenceTime_",false,destFile,"");
		} else {
			printTableVectorsWithPrefix("inferenceTime_",false,destFile,"disturbate_");
		}


		System.out.println("\n\n\n\n");

		for ( Data data : allData ){

			System.out.println(data.getName()+" "+data.getAvgInvalid()+" "+data.getAvgValid()+" "+data.runs+" "+data.subjects);
		}
		
		
		printSizeInfo("automataNodes_");
		
		printSizeInfo("automataTransitions_");
	}

	public static void printSizeInfo(String ks) {
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

	private static void updateWithDataInFile(File results)
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


		HashSet<String> fileData = new HashSet<String>();

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
			Integer totalValid = Integer.valueOf( c[i++] );
			Integer totalInvalid = Integer.valueOf( c[i++] );

			Integer totalInvalidEvents = Integer.valueOf( c[i++] );
			Integer totalInvalidGuards = Integer.valueOf( c[i++] );
			Integer missingClocks = Integer.valueOf( c[i++] );

			if ( totalInvalidEvents + totalInvalidGuards + missingClocks != totalInvalid ){
				throw new IllegalStateException("totalInvalidEvents + totalInvalidGuards + missingCLocks!= totalInvalid (line:"+lines+")");
			}

			Double nodes = Double.valueOf( c[i++] );
			Double transitions = Double.valueOf(c[i++] );
			Double avgInferenceTime = Double.valueOf(c[i++] );
			Double avgValidationTime = Double.valueOf( c[i++] );

			String runs = folderWithKFold.split("_")[1];
			String key = runs+"_"+politicsOneVal+"_"+kPoliticsOneVal+"_"+politicsMultyVal+"_"+deltaForRangeCalculation+"_"+normalDistributionConfidence+"_"+inferAbsoluteClocks;


			String lineKey = folderWithKFold+"_"+key;
			if ( fileData.contains(lineKey) ){
				System.out.println("Contains: "+lineKey);
				throw new IllegalStateException("Duplicated "+lineKey+" "+results.getAbsolutePath()+":"+lines);
				//continue;
			}
			fileData.add(lineKey);


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

			getFromIntegerListMap( getIntegerMap("config_avgAccepted_") ,key).add(avgAccepted);
			getFromIntegerListMap( getIntegerMap("config_avgNotAccepted_") ,key).add(avgNotAccepted);

			getFromIntegerListMap( getIntegerMap("config_accepted_") ,key).add((double)totalValid);
			getFromIntegerListMap( getIntegerMap("config_notAccepted_") ,key).add((double)totalInvalidEvents+totalInvalidGuards+missingClocks);
			getFromIntegerListMap( getIntegerMap("config_invalid_guards_") ,key).add((double)totalInvalidGuards);
			getFromIntegerListMap( getIntegerMap("config_invalid_events_") ,key).add((double)totalInvalidEvents);
			getFromIntegerListMap( getIntegerMap("config_missing_clocks_") ,key).add((double)missingClocks);


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

		br.close();
	}


	private static void printTableVectorsWithPrefix(String tablePrefix, File destFile) {
		printTableVectorsWithPrefix(tablePrefix,true,destFile);
	}

	private static void printTableVectorsWithPrefix(String tablePrefix, boolean printSizeInName, File destFile) {
		{
			printTableVectorsWithPrefix(tablePrefix, printSizeInName, destFile, null);
		}
	}




	private static void printTableVectorsWithPrefix(String tablePrefix, boolean printSizeInName, File destFile,
			String prefixToAdd) {
		// TODO Auto-generated method stub
		List<String> mNames = extractIntegerKeys(tablePrefix);

		Collections.sort(mNames);

		System.out.println("Sorted: "+mNames);

		printTableVectors(mNames,printSizeInName,destFile,prefixToAdd);
	}




	private static void printTableVectors(List<String> mNames,boolean printSizeInName, File destFile, String prefixToAdd) {

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
				Collections.sort(sortedKeys);

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

			bw.newLine();
			bw.newLine();

			bw.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static List<String> extractIntegerKeys(String string) {
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

}
