package it.unimib.disco.lta.timedKTail.algorithm;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import it.unimib.disco.lta.timedKTail.JTMTime.Activity;
import it.unimib.disco.lta.timedKTail.JTMTime.Node;
import it.unimib.disco.lta.timedKTail.JTMTime.TimedAutomata;
import it.unimib.disco.lta.timedKTail.JTMTime.TimedAutomataFactory;
import it.unimib.disco.lta.timedKTail.JTMTime.Transition;
import it.unimib.disco.lta.timedKTail.algorithm.Policy.MergeStrategies;
import it.unimib.disco.lta.timedKTail.tests.utils.TestUtils;


@RunWith(Parameterized.class)
public class TimedKTailTest_WithValidationAfterMerge {

	@Parameters(name = "{index}: MergeStrategy: {0}, cachePendingCalls: {1}")
	public static Collection<Object[]> data() {
		ArrayList<Object[]> inputs = new ArrayList<Object[]>();

		boolean[] FT = new boolean[]{false,true};

		for ( boolean cachePendingCalls : FT ){
			for ( MergeStrategies ms : MergeStrategies.values() ){
				inputs.add(new Object[]{ms,cachePendingCalls});
			}
		}

		return inputs;
	}

	@Parameter(0) // first data value (0) is default
	public /* NOT private */ MergeStrategies mergeStartegy;

	@Parameter(1) // first data value (0) is default
	public /* NOT private */ boolean cachePendingCalls;


	public static int k = 2;
	private Policy poli = new Policy(3,0,3,true,true);

	@Before
	public void setUp() throws Exception {
		poli.setUseCaching( true );
		poli.setCachePendingCalls(cachePendingCalls);
		poli.setMergeStrategy(mergeStartegy);
	}

	@After
	public void tearDown() throws Exception {
	}



	@Test
	public void testInferenceMergeValidation1() {
		TimedAutomata ta;
		String fTrace = "storageTest/TestMergeValidation1/trace.csv";
		ta = TestUtils.inferAutomata(2, poli, fTrace,false);
		TestUtils.checkInternalConsistency(ta);

//		visualize(ta);

		TimedAutomata expected = buildExpectedAutomataMergeValidation1(true);
		TestUtils.checkSame( "", expected, ta, false );

	}
	
	@Test
	public void testInferenceMergeValidation2() {
		TimedAutomata ta;
		String fTrace = "storageTest/TestMergeValidation2/trace.csv";
		ta = TestUtils.inferAutomata(2, poli, fTrace,false);
		TestUtils.checkInternalConsistency(ta);

//		visualize(ta);

		TimedAutomata expected = buildExpectedAutomataMergeValidation2(true);
		TestUtils.checkSame( "", expected, ta, false );

	}

	
	@Test
	public void testInferenceMergeValidation2_NoMergeValidation() {
		TimedAutomata ta;
		String fTrace = "storageTest/TestMergeValidation2/trace.csv";
		poli.setVerifyAfterMerging(false);
		ta = TestUtils.inferAutomata(2, poli, fTrace,false);
		TestUtils.checkInternalConsistency(ta);

//		visualize(ta);

		TimedAutomata expected = buildExpectedAutomataMergeValidation2(true);
		TestUtils.checkNotSame( "", expected, ta, false );

	}

	
	@Test
	public void testInferenceGuardia1(){

		String fTrace = "storageTest/TestGuardia1/trace.csv";
		TimedAutomata ta = TestUtils.inferAutomata(2, poli, fTrace,false);
		
		TestUtils.checkInternalConsistency(ta);

		//visualize(ta);
		
		TimedAutomata expected = TimedKTailTest.buildExpectedAutomataGuard1(true);
		TestUtils.checkInternalConsistency(expected);
		TestUtils.checkSame( "", expected, ta, true );
	}
	
	@Test
	public void testInferenceGuardia2(){

		String fTrace = "storageTest/TestGuardia2/trace.csv";
		TimedAutomata ta = TestUtils.inferAutomata(2, poli, fTrace,false);
		
		TestUtils.checkInternalConsistency(ta);

		TimedAutomata expected = TimedKTailTest.buildExpectedAutomataGuard2(true);
		TestUtils.checkInternalConsistency(expected);
		TestUtils.checkSame( "", expected, ta, true );
	}
	
	@Test
	public void testInferenceGuardia7(){

		String fTrace = "storageTest/TestGuardia7/trace.csv";
		TimedAutomata ta = TestUtils.inferAutomata(2, poli, fTrace,false);
		
		TestUtils.checkInternalConsistency(ta);

		TimedAutomata expected = TimedKTailTest.buildExpectedAutomataGuard7(true);
		TestUtils.checkInternalConsistency(expected);
		TestUtils.checkSame( "", expected, ta, true );
	}
	
	
	

	private TimedAutomata buildExpectedAutomataMergeValidation1(boolean b) {
		TimedAutomataFactory taf = new TimedAutomataFactory("",false);
		Node init = taf.getInitialState();

		Transition tr;


		Node n1 = taf.newNode(false);
		tr = taf.newTransition(init, n1, new Activity("read1.0"), Transition.BEGIN);
		Node n2 = taf.newNode(false);
		tr = taf.newTransition(n1, n2, new Activity("read2.0"), Transition.BEGIN);
		Node n3 = taf.newNode(false);
		tr = taf.newTransition(n2, n3, new Activity("read2.0"), Transition.END);
		Node n4 = taf.newNode(false);
		tr = taf.newTransition(n3, n4, new Activity("read1.0"), Transition.END);

		Node n5 = taf.newNode(false);
		tr = taf.newTransition(init, n5, new Activity("read3.0"), Transition.BEGIN);
		Node n6 = taf.newNode(false);
		tr = taf.newTransition(n5, n6, new Activity("read2.0"), Transition.BEGIN);
		Node n7 = taf.newNode(false);
		tr = taf.newTransition(n6, n7, new Activity("read2.0"), Transition.END);

		tr = taf.newTransition(n7, n4, new Activity("read3.0"), Transition.END);



		n4.setFinalState(true);


		return taf.getTimedAutomata();
	}
	
	private TimedAutomata buildExpectedAutomataMergeValidation2(boolean b) {
		TimedAutomataFactory taf = new TimedAutomataFactory("",false);
		Node init = taf.getInitialState();

		Transition tr;


		Node n1 = taf.newNode(false);
		tr = taf.newTransition(init, n1, new Activity("read1.0"), Transition.BEGIN);
		Node n2 = taf.newNode(false);
		tr = taf.newTransition(n1, n2, new Activity("read2.0"), Transition.BEGIN);
		Node n3 = taf.newNode(false);
		tr = taf.newTransition(n2, n3, new Activity("read2.0"), Transition.END);
		Node n4 = taf.newNode(false);
		tr = taf.newTransition(n3, n4, new Activity("read1.0"), Transition.END);

		Node n5 = taf.newNode(false);
		tr = taf.newTransition(init, n5, new Activity("read3.0"), Transition.BEGIN);
		Node n6 = taf.newNode(false);
		tr = taf.newTransition(n5, n6, new Activity("read2.0"), Transition.BEGIN);
		Node n7 = taf.newNode(false);
		tr = taf.newTransition(n6, n7, new Activity("read2.0"), Transition.END);

		tr = taf.newTransition(n7, n4, new Activity("read3.0"), Transition.END);
		
		
		Node n8 = taf.newNode(false);
		tr = taf.newTransition(init, n8, new Activity("read0.0"), Transition.BEGIN);
		Node n9 = taf.newNode(false);
		tr = taf.newTransition(n8, n9, new Activity("read0.0"), Transition.END);
		
		tr = taf.newTransition(n9, n1, new Activity("read1.0"), Transition.BEGIN);

		



		n4.setFinalState(true);


		return taf.getTimedAutomata();
	}


}
