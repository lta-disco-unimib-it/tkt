package it.unimib.disco.lta.timedKTail.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.collections15.multimap.MultiHashMap;

import it.unimib.disco.lta.timedKTail.traces.MaxExecutionTimeFinder;
import it.unimib.disco.lta.timedKTail.traces.Parser;

public class TraceRenamer {

	private static final String TRACES_AD_HOC_TESTS = "Traces_AdHocTests";
	private static final String TRACES_DEV_TESTS = "Traces_DevTests";
	private static final String TRACES_DEV_TESTS_ADDITIONAL = "Traces_DevTestsAdditional";
	
	private String methodUnderTest;
	
	private String replacement; 

	public TraceRenamer(String methodUnderTest, String replacement) {
		this.methodUnderTest = methodUnderTest;
		this.replacement = replacement;
	}

	public static void main(String[] args) {

		String methodUnderTest = args[0];
		String replacement = args[1];
		
		File faultyPrj = new File( args[2] );
		File fixedPrj = new File( args[3] );
		
		
		
		TraceRenamer ftf = new TraceRenamer( methodUnderTest, replacement );
		ftf.renameTraces(faultyPrj, fixedPrj);

	}

	

	public void renameTraces(File faultyPrj, File fixedPrj) {
		renameAdHocTestsForProject( new File( faultyPrj, TRACES_AD_HOC_TESTS) );
		renameAdHocTestsForProject( new File( faultyPrj, TRACES_DEV_TESTS) );
		
		renameAdHocTestsForProject( new File( fixedPrj, TRACES_AD_HOC_TESTS) );
		renameAdHocTestsForProject( new File( fixedPrj, TRACES_DEV_TESTS) );
		renameAdHocTestsForProject( new File( fixedPrj, TRACES_DEV_TESTS_ADDITIONAL) );
	}

	

	
	
	

	private void renameAdHocTestsForProject(File folderWithTestsTraces) {
		
		File[] testFolders = folderWithTestsTraces.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		
		MultiHashMap<String,Long> prjsExecutionTime = new MultiHashMap<String, Long>();
		
		if ( testFolders == null ){
			throw new IllegalStateException("Folder does not contain traces folders: "+folderWithTestsTraces.getAbsolutePath());
		}
		
		for ( File testFolder : testFolders ){
			processTestFolder( prjsExecutionTime, testFolder );
		}
		
	}

	private void processTestFolder(MultiHashMap<String,Long> prjsExecutionTime, File testFolder) {
		
		MethodRenamerObserver o = new MethodRenamerObserver( methodUnderTest, replacement );
		
		Parser p = new Parser(o);
		p.readFolder(testFolder.getAbsolutePath());
		
		
	}

}
