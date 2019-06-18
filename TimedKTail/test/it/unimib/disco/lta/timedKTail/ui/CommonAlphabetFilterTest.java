package it.unimib.disco.lta.timedKTail.ui;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CommonAlphabetFilterTest {
	
	File baseDifferent = new File("storageTest/CommonAlphabetFilterDifferentAlphabet/");
	File baseDifferentCommonOnly = new File("storageTest/CommonAlphabetFilterDifferentAlphabet_CommonTestsOnly/");
	File baseSame = new File("storageTest/CommonAlphabetFilterSameAlphabet/");
	private File passingFolderExpected;
	private File failingFolderExpected;
	private File passingFolder;
	private File failingFolder;
	
	private String[] traceNames = new String[]{"trace1.csv","trace2.csv"};
	private File failingFolderUnmodifiable;
	private File passingFolderUnmodifiable;
	private boolean sharedTestsOnly;
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSameAlphabet() throws IOException {
		
		runTest(baseSame);
	}
	
	@Test
	public void testDifferentAlphabet() throws IOException {
		
		runTest(baseDifferent);
	}
	
	@Test
	public void testDifferentAlphabetCommonOnly() throws IOException {
		
		traceNames = new String[]{
		"traces_org.apache.commons.collections.ATest1.1.trace.1473236612788.2.csv",
		"traces_org.apache.commons.collections.ListOrderedSetBugInputsFinder_1.1.trace.1473236612788.1.csv",
		"traces_org.apache.commons.collections.ListOrderedSetBugInputsFinder_2.1.trace.1473236612788.2.csv" };
		
		sharedTestsOnly=true;
		runTest(baseDifferentCommonOnly);
	}
	
	@Test
	public void testDifferentAlphabetNoModify() throws IOException {
		
		setupFolders(baseDifferent);

		executeTest(true);
		
		passingFolderExpected = passingFolderUnmodifiable;
		failingFolderExpected = failingFolderUnmodifiable; 
		checkResults();
		
		
	}

	public void runTest(File base) throws IOException {
		setupFolders(base);
		
		
		executeTest(false);
		
		checkResults();
	}

	private void checkResults() throws IOException {
		for( String traceName : traceNames ){
			{
				File modified = new File( passingFolder, traceName );
				File expected = new File( passingFolderExpected, traceName );
				
				if ( ! expected.exists() ){
					if ( ! modified.exists() ){
						continue;
					} else {
						fail("Missing trace: "+modified.getAbsolutePath());
					}
				}
				
				assertTrue("Files differ: "+modified.getAbsolutePath()+ " "+ expected.getAbsolutePath(),contentEqual(modified, expected));
			}

			{
				File modified = new File( failingFolder, traceName );
				File expected = new File( failingFolderExpected, traceName );
				
				
				if ( ! expected.exists() ){
					if ( ! modified.exists() ){
						continue;
					} else {
						fail("Missing trace: "+modified.getAbsolutePath());
					}
				}
				
				
				assertTrue("Files differ: "+modified.getAbsolutePath()+" "+expected.getAbsolutePath(),contentEqual(modified, expected) );
			}
		}
	}

	public void executeTest(boolean dontModify) throws IOException {
		CommonAlphabetFilter f = new CommonAlphabetFilter();
		f.setDontModify(dontModify);
		f.setCommmonTestsOnly( sharedTestsOnly );
		
		f.buildAlphabetAndProcessFolders(passingFolder, failingFolder);
		
		
	}

	private boolean contentEqual(File modified, File expected) throws IOException {
		List<String> modifiedLines = FileUtils.readLines(modified);
		
		List<String> expectedLines = FileUtils.readLines(expected);
		
		
		return expectedLines.equals(modifiedLines);
	}

	public void setupFolders(File base) throws IOException {
		File unmodifiableFolder = new File ( base, "unmodifiableData" );
		File modifiableFolder = new File ( base, "modifiableData" );
		modifiableFolder.mkdir();
		
		passingFolderUnmodifiable = new File( unmodifiableFolder, "passing" );
		failingFolderUnmodifiable = new File( unmodifiableFolder, "failing" );
		passingFolderExpected = new File( unmodifiableFolder, "passingExpected" );
		failingFolderExpected = new File( unmodifiableFolder, "failingExpected" );
		
		passingFolder = new File( modifiableFolder, "passing" );
		failingFolder = new File( modifiableFolder, "failing" );
		
		FileUtils.copyDirectory(passingFolderUnmodifiable, passingFolder);
		FileUtils.copyDirectory(failingFolderUnmodifiable, failingFolder);
	}

}
