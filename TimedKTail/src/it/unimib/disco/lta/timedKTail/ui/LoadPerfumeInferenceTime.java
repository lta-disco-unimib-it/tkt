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

public class LoadPerfumeInferenceTime {

	
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






		boolean inference = ! Boolean.getBoolean("validateWithNewData");

		if ( inference ){
			printTableVectorsWithPrefix("inferenceTime_",false,destFile,"perfume_",800);
		} else {
			printTableVectorsWithPrefix("inferenceTime_",false,destFile,"perfume_disturbate_");
		}

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



		while ( ( line = br.readLine() ) != null ){
			System.out.println(lines);
			if ( line.startsWith("#") ){
				continue;
			}

			lines++;

			String[] c = line.split(",");
			int i = 0;
			String folderWithKFold = c[i++];
			
			Double avgInferenceTime = Double.valueOf(c[i++] );
			

			String runs = folderWithKFold.split("_")[1];
			

			List<Double> kmap = getFromIntegerListMap( getIntegerMap("run_inferenceTime_") ,folderWithKFold);
			if ( kmap.size() >= 100 ){
				throw new IllegalStateException();
			}
			kmap.add( avgInferenceTime );
				
				
			getFromIntegerListMap( getIntegerMap("inferenceTime_") ,runs).add(avgInferenceTime);

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




	private static void printTableVectorsWithPrefix(String tablePrefix, boolean printSizeInName, File destFile, String prefixToAdd) {
		printTableVectorsWithPrefix(tablePrefix, printSizeInName, destFile, prefixToAdd, 0);
	}
	
	private static void printTableVectorsWithPrefix(String tablePrefix, boolean printSizeInName, File destFile, String prefixToAdd, int minSize) {
		// TODO Auto-generated method stub
		List<String> mNames = extractIntegerKeys(tablePrefix);

		Collections.sort(mNames);

		System.out.println("Sorted: "+mNames);

		printTableVectors(mNames,printSizeInName,destFile,prefixToAdd,minSize);
	}




	private static void printTableVectors(List<String> mNames,boolean printSizeInName, File destFile, String prefixToAdd, int minSize) {

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

					int inserted = 0;
					for ( int i = 0; i < l.size(); i++ ){
						
						bw.append(String.valueOf(l.get(i))); inserted++;
						
						if ( i != l.size()-1 ){
							bw.append(",");
						}
					}
					
					int s = l.size();
					while ( s < minSize ){
						if ( s > 0 ){
							bw.append(",");
						}
						bw.append("NA");  inserted++;
						s++;
					}
					
					bw.append(")");
					bw.newLine();

					if ( minSize > 0 && inserted != minSize ){
						bw.close();
						
						for ( String ss : getIntegerMap("run_inferenceTime_").keySet() ){
							System.out.println(ss +" " + getIntegerMap("run_inferenceTime_").get(ss).size() );
						}
						
						throw new IllegalStateException("More than expected:"+prefixToAdd+nameKey +" " + inserted);
					}
					
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

	

}
