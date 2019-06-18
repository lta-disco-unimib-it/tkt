package it.unimib.disco.lta.timedKTail.ui;

import static org.junit.Assert.*;

import java.io.File;
import java.util.HashMap;

import org.junit.Test;

public class FailingTestsFinderTest {

	@Test
	public void testFindFailingTests(){
		String methodUnderTest = "B";
		File faultyPrj = new File( "storageTest/FailingTestsFinder/faultyPrj" );
		File fixedPrj = new File( "storageTest/FailingTestsFinder/fixedPrj" );
		
		
		FailingTestsFinder ftf = new FailingTestsFinder( methodUnderTest );
		ftf.identifyPassingAndFailing(faultyPrj, fixedPrj);
		
		HashMap<String, Double> failing = ftf.getFailing();
		
		assertEquals( (5d-1d)/1d, failing.get("tracesTestA"), 0.001 );
		assertEquals( (7d-2d)/2d, failing.get("tracesTestB"), 0.001 );
	}
	
	
	@Test
	public void testFindFailingTests_WithExecutionError(){
		String methodUnderTest = "B";
		File faultyPrj = new File( "storageTest/FailingTestsFinder_PercentageErrorNotZero/faultyPrj" );
		File fixedPrj = new File( "storageTest/FailingTestsFinder_PercentageErrorNotZero/fixedPrj" );
		
		
		FailingTestsFinder ftf = new FailingTestsFinder( methodUnderTest );
		ftf.identifyPassingAndFailing(faultyPrj, fixedPrj);
		
		HashMap<String, Double> failing = ftf.getFailing();
		HashMap<String, Double> passing = ftf.getPassing();
		
		assertEquals( (5d-1d)/1d, failing.get("tracesTestA"), 0.001 );
		assertEquals( (7d-2d)/2d, passing.get("tracesTestB"), 0.001 );
	}
	

	
	
	@Test
	public void testFindFailingTests_DevTestsHaveOverhead(){
		String methodUnderTest = "B";
		File faultyPrj = new File( "storageTest/FailingTestsFinder_DevTestsHaveOverhead/faultyPrj" );
		File fixedPrj = new File( "storageTest/FailingTestsFinder_DevTestsHaveOverhead/fixedPrj" );
		
		
		FailingTestsFinder ftf = new FailingTestsFinder( methodUnderTest );
		ftf.identifyPassingAndFailing(faultyPrj, fixedPrj);
		
		HashMap<String, Double> failing = ftf.getFailing();
		HashMap<String, Double> passing = ftf.getPassing();
		
		assertEquals( (5d-1d)/1d, failing.get("tracesTestA"), 0.001 );
		assertEquals( (7d-2d)/2d, passing.get("tracesTestB"), 0.001 );
	}
	
}
