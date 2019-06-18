package it.unimib.disco.lta.timedKTail.ui;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashSet;

import org.apache.commons.io.FileUtils;

import it.unimib.disco.lta.timedKTail.traces.CommonDictionaryExtractorParser;

public class CommonImplementationAlphabetBuilder {

	public static void main(String[] args) {
		String faultyFolder = args[0];
		String fixedFolder = args[1];
		String dest = args[2];
		
		String[] folderNames = new String[]{"Traces_DevTests","Traces_AdHocTests"};
		
		
		File faultyParent = new File ( faultyFolder );
		File fixedParent = new File ( fixedFolder );
		
		CommonDictionaryExtractorParser fixedParser = new CommonDictionaryExtractorParser();
		CommonDictionaryExtractorParser faultyParser = new CommonDictionaryExtractorParser();
		
		for ( String folder : folderNames ){
			File faulty = new File( faultyParent, folder );
			File fixed = new File ( fixedParent, folder );
			
			processSubFolders(fixedParser, fixed);
			processSubFolders(faultyParser, faulty);
			
		}
		
		HashSet<String> passingAlphabet = fixedParser.getDictionary();
		HashSet<String> failingAlphabet = faultyParser.getDictionary();
		
		
		HashSet<String> commonAlphabet = CommonAlphabetFilter.identifyCommonAlphabet(passingAlphabet, failingAlphabet);
		
		try {
			FileUtils.writeLines(new File(dest), commonAlphabet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void processSubFolders(CommonDictionaryExtractorParser fixedParser, File fixed) {
		File[] tracesFolders = fixed.listFiles( new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				return pathname.isDirectory();
			}
		} );
		
		for ( File tfolder : tracesFolders ){
		fixedParser.readFolder( tfolder.getAbsolutePath() );
		}
	}

	
	
}
