package it.unimib.disco.lta.timedKTail.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoadExperimentsData {

	private static final boolean ICST = Boolean.getBoolean("ICST");;
	
	private static final boolean TIMING = Boolean.getBoolean("tkt.timing");
	
	private static String noiseKey = System.getProperty("noiseKey","");
	private static String suffix = "";
	private static String prefix = "";

	static {
		if ( ! noiseKey.isEmpty() ){
			suffix  = "."+noiseKey; 
			prefix  = noiseKey+"."; 
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {

		String dest = args[0];

		List<String> dataFolders = new ArrayList<>();
		for ( int i = 1; i < args.length; i++ ){
			dataFolders.add(args[i]);
		}
		
		System.out.println("DEST: "+dest);
		System.out.println("DATA FOLDERS: "+dataFolders);


		boolean expectedIncludeNestedCallsTimeVs[] = {true,false};
		boolean expectedValidateAfterMergingVs[] = {false};
		
		if ( ICST ){
			expectedIncludeNestedCallsTimeVs = new boolean[]{true};
			expectedValidateAfterMergingVs = new boolean[]{false};
		}
		
//		if ( true ){
//			expectedIncludeNestedCallsTimeVs = new boolean[]{false};
//			expectedValidateAfterMergingVs = new boolean[]{false};
//		}
		
		for ( boolean expectedIncludeNestedCallsTime : expectedIncludeNestedCallsTimeVs ){
			for ( boolean expectedValidateAfterMerging : expectedValidateAfterMergingVs ){
				generateSensitivityResults(dest, dataFolders, expectedIncludeNestedCallsTime, expectedValidateAfterMerging);
				generateSpecificityResults(dest, dataFolders, expectedIncludeNestedCallsTime, expectedValidateAfterMerging);
				//generateSpecificity100Results(dest, dataFolders, expectedIncludeNestedCallsTime, expectedValidateAfterMerging);				
				
				if ( TIMING ){
					generateTimingResults(dest, dataFolders, expectedIncludeNestedCallsTime, expectedValidateAfterMerging);
				}
			}
		}
	}

	public static void generateSensitivityResults(String dest, List<String> dataFolders,
			boolean expectedIncludeNestedCallsTime, boolean expectedValidateAfterMerging)
					throws FileNotFoundException, IOException {
		File destFolder = new File( dest, "expectedIncludeNestedCallsTime_"+expectedIncludeNestedCallsTime+"_expectedValidateAfterMerging_"+expectedValidateAfterMerging+suffix );
		destFolder.mkdirs();

		LoadCrossvalidationResultsFilter lcr = new LoadCrossvalidationResultsFilter();

		setUpConfigNames(lcr);

		lcr.setExpectedIncludeNestedCallsTime(expectedIncludeNestedCallsTime);
		lcr.setExpectedValidateAfterMerging(expectedValidateAfterMerging);

		File destFile = new File( destFolder, "TKT.sensitivity.R" );
		ArrayList<File> files = new ArrayList<>();
		for ( String dataFolder : dataFolders ){
			
			files.add( new File( dataFolder, "1.sensitivity.timedKTail.results.csv" ) ); 
			files.add( new File( dataFolder, "2.sensitivity.timedKTail.results.csv" ) ); 
			files.add( new File( dataFolder, "3.sensitivity.timedKTail.results.csv" ) ); 
			files.add( new File( dataFolder, "4.sensitivity.timedKTail.results.csv" ) );
			files.add( new File( dataFolder, "5.sensitivity.timedKTail.results.csv" ) );
			
		}
		
		lcr.loadDataInFiles(files);
		
		lcr.generateResults(destFile);
	}

	public static void setUpConfigNames(LoadCrossvalidationResultsFilter lcr) {
		//		{
		//			lcr.addConfigName("3_0.0_3_0.05_0.0_false");//1
		//			lcr.addConfigName("3_0.0_3_0.15_0.0_false");
		//			lcr.addConfigName("3_0.0_3_0.1_0.0_false");
		//			lcr.addConfigName("3_0.0_3_0.25_0.0_false");
		//			lcr.addConfigName("3_0.0_3_0.2_0.0_false");
		//			lcr.addConfigName("3_0.0_3_0.5_0.0_false");
		//			lcr.addConfigName("3_0.0_3_0.75_0.0_false");
		//			lcr.addConfigName("3_0.0_3_1.0_0.0_false");//8
		//			lcr.addConfigName("3_0.0_4_0.0_0.95_false");//9
		//			lcr.addConfigName("3_0.0_4_0.0_0.99_false");//10
		//		}
		{
			lcr.addConfigName("3_0.0_3_0.05_0.0_true");
			lcr.addConfigName("3_0.0_3_0.05_0.0_false");
			lcr.addConfigName("3_0.0_3_0.15_0.0_true");
			lcr.addConfigName("3_0.0_3_0.15_0.0_false");
			lcr.addConfigName("3_0.0_3_0.1_0.0_true");
			lcr.addConfigName("3_0.0_3_0.1_0.0_false");
			lcr.addConfigName("3_0.0_3_0.25_0.0_true");
			lcr.addConfigName("3_0.0_3_0.25_0.0_false");
			lcr.addConfigName("3_0.0_3_0.2_0.0_true");
			lcr.addConfigName("3_0.0_3_0.2_0.0_false");
			lcr.addConfigName("3_0.0_3_0.5_0.0_true");
			lcr.addConfigName("3_0.0_3_0.5_0.0_false");
			lcr.addConfigName("3_0.0_3_0.75_0.0_true");
			lcr.addConfigName("3_0.0_3_0.75_0.0_false");
			lcr.addConfigName("3_0.0_3_1.0_0.0_true");
			lcr.addConfigName("3_0.0_3_1.0_0.0_false");
			lcr.addConfigName("3_0.0_4_0.0_0.95_true");
			lcr.addConfigName("3_0.0_4_0.0_0.95_false");
			lcr.addConfigName("3_0.0_4_0.0_0.99_true");
			lcr.addConfigName("3_0.0_4_0.0_0.99_false");
		}
	}

	public static void generateSpecificityResults(String dest, List<String> dataFolders,
			boolean expectedIncludeNestedCallsTime, boolean expectedValidateAfterMerging)
					throws FileNotFoundException, IOException {
		File destFolder = new File( dest, "expectedIncludeNestedCallsTime_"+expectedIncludeNestedCallsTime+"_expectedValidateAfterMerging_"+expectedValidateAfterMerging+suffix );
		destFolder.mkdir();

		LoadCrossvalidationResultsFilter lcr = new LoadCrossvalidationResultsFilter();

		setUpConfigNames(lcr);

		lcr.setExpectedIncludeNestedCallsTime(expectedIncludeNestedCallsTime);
		lcr.setExpectedValidateAfterMerging(expectedValidateAfterMerging);
		lcr.setSensitivityData(false);


		File destFile = new File( destFolder, "TKT.specificity.R" );

		ArrayList<File> files = new ArrayList<>();
		for( String dataFolder : dataFolders ){
			
			files.add( new File( dataFolder, prefix+"1.specificity.timedKTail.results.csv" ) ); 
			files.add( new File( dataFolder, prefix+"2.specificity.timedKTail.results.csv" ) ); 
			files.add( new File( dataFolder, prefix+"3.specificity.timedKTail.results.csv" ) ); 
			files.add( new File( dataFolder, prefix+"4.specificity.timedKTail.results.csv" ) );
			files.add( new File( dataFolder, prefix+"5.specificity.timedKTail.results.csv" ) );
			
		}
		lcr.loadDataInFiles(files);
		
		lcr.generateResults(destFile);
	}


	public static void generateSpecificity100Results(String dest, List<String> dataFolders,
			boolean expectedIncludeNestedCallsTime, boolean expectedValidateAfterMerging)
					throws FileNotFoundException, IOException {
		File destFolder = new File( dest, "expectedIncludeNestedCallsTime_"+expectedIncludeNestedCallsTime+"_expectedValidateAfterMerging_"+expectedValidateAfterMerging+suffix );
		destFolder.mkdir();

		LoadCrossvalidationResultsFilter lcr = new LoadCrossvalidationResultsFilter();
		setUpConfigNames(lcr);
		lcr.setExpectedIncludeNestedCallsTime(expectedIncludeNestedCallsTime);
		lcr.setExpectedValidateAfterMerging(expectedValidateAfterMerging);
		lcr.setSensitivityData(false);

		File destFile = new File( destFolder, "TKT.specificity.100.R" );
		
		ArrayList<File> files = new ArrayList<>();
		for ( String dataFolder : dataFolders ){
			files.add( new File( dataFolder, "100.disturbate.results.csv" ) ); 
		}
		
		try {
			lcr.loadDataInFiles(files);
		} catch ( IOException exc ) {
			System.out.println("Cannot genrate "+destFile.getName()+" missing inputs.");
			return;
		}

		lcr.generateResults(destFile);
	}

	public static void generateTimingResults(String dest, List<String> dataFolders,
			boolean expectedIncludeNestedCallsTime, boolean expectedValidateAfterMerging)
					throws FileNotFoundException, IOException {
		File destFolder = new File( dest, "expectedIncludeNestedCallsTime_"+expectedIncludeNestedCallsTime+"_expectedValidateAfterMerging_"+expectedValidateAfterMerging+suffix );
		destFolder.mkdir();

		LoadCrossvalidationPartialResultsFilter lcr = new LoadCrossvalidationPartialResultsFilter();

		lcr.setExpectedIncludeNestedCallsTime(expectedIncludeNestedCallsTime);
		lcr.setExpectedValidateAfterMerging(expectedValidateAfterMerging);
		lcr.setSensitivityData(true);

		//TKT.time.R all.kfold.timedKTail.partial.results.csv
		File destFile = new File( destFolder, "TKT.time.csv" );
		destFile.delete();
		
		ArrayList<File> files = new ArrayList<>();
		
		
		
		for( String dataFolder : dataFolders ){
			File allFile = new File( dataFolder, "all.kfold.timedKTail.partial.results.csv" );
			
			if ( allFile.exists() ){
				files.add(allFile);
			} else {
				files.add( new File( dataFolder, "1.timing.timedKTail.results.csv" ) );
				files.add( new File( dataFolder, "2.timing.timedKTail.results.csv" ) );
				files.add( new File( dataFolder, "3.timing.timedKTail.results.csv" ) );
				files.add( new File( dataFolder, "4.timing.timedKTail.results.csv" ) );
				files.add( new File( dataFolder, "5.timing.timedKTail.results.csv" ) );
			}
		}
		
		
		lcr.loadDataInFiles(files);

		lcr.setCsvFormat(true);
		lcr.generateResults(destFile);
	}



}
