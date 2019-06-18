package it.unimib.disco.lta.timedKTail.ui;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;

import it.unimib.disco.lta.timedKTail.traces.CommonDictionaryExtractorParser;
import it.unimib.disco.lta.timedKTail.traces.Parser;

public class CommonAlphabetFilter {
	
	boolean dontModify = Boolean.getBoolean("dontModify");
	private boolean sharedFilesOnly  = Boolean.getBoolean("sharedFilesOnly");
	
	private String alphabetFile  = System.getProperty("alphabetFile");
	
	private HashSet<String> commonFileNames;
	private HashSet<String> commonAlphabet;

	public HashSet<String> getCommonAlphabet() {
		return commonAlphabet;
	}

	public void setCommonAlphabet(HashSet<String> commonAlphabet) {
		this.commonAlphabet = commonAlphabet;
	}

	public static void main(String[] args) {
		File passingFolder = new File( args[0] );
		File failingFolder = new File(args[1]);
		
		CommonAlphabetFilter f = new CommonAlphabetFilter();
		
		f.buildAlphabetAndProcessFolders(passingFolder, failingFolder);
		
		
	}

	public boolean isDontModify() {
		return dontModify;
	}

	public void setDontModify(boolean dontModify) {
		this.dontModify = dontModify;
	}

	public void buildAlphabetAndProcessFolders(File passingFolder, File failingFolder) {
		
		if ( alphabetFile != null ){
			loadAlphabet( new File( alphabetFile ) );
		} else {
			buildAlphabet(passingFolder, failingFolder);
		}
		
		if( dontModify ){
			System.out.println("NOT MODIFY OPTION SET. RETURNING.");
			return;
		}
		
		if ( commonAlphabet == null ){
			return;
		}
		
		List<File> folders = new ArrayList<File>();
		folders.add(passingFolder);
		folders.add(failingFolder);
		
		filterTracesInFolders(folders);
	}
	
	
	public void filterTracesInFolders(List<File> folders) {
		System.out.println("================");
		
		System.out.println("FILTERING.");
		
		AlphabetFilterObserver o = new AlphabetFilterObserver(commonAlphabet);
		
		if ( sharedFilesOnly ){
			o.setAcceptableTestNames(commonFileNames);
		}
		
		Parser p = new Parser(o);
		p.setNormalizeTime(false);
		for ( File folder: folders ){
			System.out.println("FILTERING FOLDER: "+folder.getAbsolutePath());
			p.readFolder(folder.getAbsolutePath());
		}
		
	}

	public void loadAlphabet(File alphabetFile) {
		try {
			List<String> lines = FileUtils.readLines( alphabetFile );
			commonAlphabet = new HashSet<>();
			commonAlphabet.addAll(lines);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public static HashSet<String> identifyCommonAlphabet(HashSet<String> passingAlphabet, HashSet<String> failingAlphabet){
		System.out.println("Alphabet not the same. ");


		HashSet<String> commonAlphabet = new HashSet<>();
		commonAlphabet.addAll( passingAlphabet );
		commonAlphabet.retainAll(failingAlphabet);

		System.out.println("Common alphabet: ");
		for ( String s : commonAlphabet ){
			System.out.println(s);
		}

		System.out.println("Only in passing: ");
		passingAlphabet.removeAll(commonAlphabet);
		for ( String s : passingAlphabet ){
			System.out.println(s);
		}

		System.out.println("Only in failing: ");
		failingAlphabet.removeAll(commonAlphabet);
		for ( String s : failingAlphabet ){
			System.out.println(s);
		}
		
		return commonAlphabet;
	}
	
	
	

	public void buildAlphabet(File passingFolder, File failingFolder) {
		if ( sharedFilesOnly ){
			readFileNames( failingFolder );
		}

		HashSet<String> passingAlphabet = extractAlphabet(passingFolder);
		HashSet<String> failingAlphabet = extractAlphabet(failingFolder);

		
		
		if ( passingAlphabet.equals(failingAlphabet) ){
			System.out.println("Same alphabet nothing to do.");
			return;
		}

		commonAlphabet = identifyCommonAlphabet(passingAlphabet, failingAlphabet);
		
	}

	private void readFileNames(File failingFolder) {
		File[] traces = failingFolder.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				return pathname.getName().endsWith(".csv");
			}
		});
		
		
		
		commonFileNames = new HashSet<String>();
		
		for ( File trace : traces ){
			String testName = extractTestName( trace );
			commonFileNames.add(testName);
		}
	}

	public static String extractTestName(File trace) {
		//assuming trace names follow this template: traces_org.apache.commons.collections.ListOrderedSetBugInputsFinder_957.1.trace.1473236612788.1.csv
		
		String traceName = trace.getName();
		int idx = traceName.indexOf(".trace.");
		
		String testName = traceName.substring(0, idx+7);
		
		return testName;
	}

	public HashSet<String> extractAlphabet(File passingFolder) {
		CommonDictionaryExtractorParser p = new CommonDictionaryExtractorParser();
		p.setCommonFileNames( commonFileNames );
		p.readFolder(passingFolder.getAbsolutePath());
		HashSet<String> passingAlphabet = p.getDictionary();
		return passingAlphabet;
	}

	public void setCommmonTestsOnly(boolean sharedTestsOnly) {
		sharedFilesOnly=sharedTestsOnly;
	}

}
