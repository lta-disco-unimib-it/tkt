package it.unimib.disco.lta.timedKTail.traces;

import java.io.File;
import java.util.HashSet;

import it.unimib.disco.lta.timedKTail.ui.CommonAlphabetFilter;

public class CommonDictionaryExtractorParser extends Parser {

	public CommonDictionaryExtractorParser() {
		super(new NullObserver());
	}
	
	HashSet<String> dictionary = new HashSet<String>();
	private HashSet<String> commonFileNames;
	
	

	@Override
	protected void readTraceFile(String ind){
		File trace = new File( ind );
		String testName = CommonAlphabetFilter.extractTestName(trace);
		if ( commonFileNames != null ){
			if ( ! commonFileNames.contains(testName) ){
				return;
			}
		}
		
		super.readTraceFile(ind);
	}

	@Override
	public Event newEvent(String[] s) {
		String activity = s[1];
		
		dictionary.add(activity);
		return null;
	}

	public HashSet<String> getDictionary() {
		return dictionary;
	}

	public void setCommonFileNames(HashSet<String> commonFileNames) {
		this.commonFileNames = commonFileNames;
	}
	
	

}
