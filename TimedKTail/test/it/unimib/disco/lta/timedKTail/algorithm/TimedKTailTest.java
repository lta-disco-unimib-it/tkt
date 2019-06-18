package it.unimib.disco.lta.timedKTail.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import it.unimib.disco.lta.timedKTail.JTMTime.Activity;
import it.unimib.disco.lta.timedKTail.JTMTime.Clause;
import it.unimib.disco.lta.timedKTail.JTMTime.Clock;
import it.unimib.disco.lta.timedKTail.JTMTime.ClockFactory;
import it.unimib.disco.lta.timedKTail.JTMTime.Interval;
import it.unimib.disco.lta.timedKTail.JTMTime.Node;
import it.unimib.disco.lta.timedKTail.JTMTime.TimedAutomata;
import it.unimib.disco.lta.timedKTail.JTMTime.TimedAutomataFactory;
import it.unimib.disco.lta.timedKTail.JTMTime.Transition;
import it.unimib.disco.lta.timedKTail.algorithm.Policy.MergeStrategies;
import it.unimib.disco.lta.timedKTail.tests.utils.TestUtils;
import it.unimib.disco.lta.timedKTail.ui.VisualizeAutomata;


@RunWith(Parameterized.class)
public class TimedKTailTest {



	@Parameters(name = "{index}: strategy:{0}; useCaching:{1}; incrementalMerging{2}")
	public static Collection<Object[]> data() {
		ArrayList<Object[]> objs = new ArrayList<>();

		for ( boolean incrementalMerging : TestUtils.FT )
			for ( MergeStrategies m : MergeStrategies.values() ){
				for ( boolean useCaching : TestUtils.TF ){
					if ( incrementalMerging && useCaching ){
						continue; //not expected to be used together
					}
					objs.add(new Object[]{ m, useCaching, incrementalMerging } );
				}
			}

		return objs;
	}

	@Parameter(0) // first data value (0) is default
	public /* NOT private */ MergeStrategies mergeStartegy;

	@Parameter(1)
	public /* NOT private */ boolean useCaching;

	@Parameter(2)
	public /* NOT private */ boolean incrementalMerging;

	public static int k = 2;


	private Policy poli = new Policy(3,0,3,true,false);

	@Before
	public void setUp() throws Exception {
		poli.setUseCaching( useCaching );
		poli.setMergeStrategy(mergeStartegy);
		poli.setUseIncrementalMerging(incrementalMerging);
	}

	@After
	public void tearDown() throws Exception {
	}



	@Test
	public void testInferenceGuardia1() {
		TimedAutomata ta;
		String fTrace = "storageTest/TestGuardia1/trace.csv";
		ta = TestUtils.inferAutomata(2, poli, fTrace,false);
		TestUtils.checkInternalConsistency(ta);

		TimedAutomata expected = buildExpectedAutomataGuard1(true);
		TestUtils.checkSame( "", expected, ta, true );

	}

	@Test
	public void testInferenceGuardia1WithNestedCallsTime() {
		TimedAutomata ta;
		String fTrace = "storageTest/TestGuardia1/trace.csv";
		ta = TestUtils.inferAutomata(2, poli, fTrace,true);
		TestUtils.checkInternalConsistency(ta);

		TimedAutomata expected = buildExpectedAutomataGuard1(false);
		TestUtils.checkSame( "", expected, ta, true );

	}

	@Test
	public void testInferenceGuardia2(){

		String fTrace = "storageTest/TestGuardia2/trace.csv";
		TimedAutomata ta = TestUtils.inferAutomata(2, poli, fTrace,false);

		TestUtils.checkInternalConsistency(ta);

		TimedAutomata expected = buildExpectedAutomataGuard2(true);
		TestUtils.checkSame( "", expected, ta, true );
	}


	@Test
	public void testInferenceGuardia2WithNestedCallsTime(){

		String fTrace = "storageTest/TestGuardia2/trace.csv";
		TimedAutomata ta = TestUtils.inferAutomata(2, poli, fTrace,true);

		TestUtils.checkInternalConsistency(ta);

		TimedAutomata expected = buildExpectedAutomataGuard2(false);
		TestUtils.checkSame( "", expected, ta, true );
	}





	@Test
	public void testInferenceGuardia7(){

		String fTrace = "storageTest/TestGuardia7/trace.csv";
		TimedAutomata ta = TestUtils.inferAutomata(2, poli, fTrace,false);

		TestUtils.checkInternalConsistency(ta);

		TimedAutomata expected = buildExpectedAutomataGuard7(true);
		TestUtils.checkInternalConsistency(expected);
		TestUtils.checkSame( "", expected, ta, true );
	}


	@Test
	public void testInferenceGuardia7WithNestedCallsTime(){

		String fTrace = "storageTest/TestGuardia7/trace.csv";
		TimedAutomata ta = TestUtils.inferAutomata(2, poli, fTrace,true);

		TestUtils.checkInternalConsistency(ta);

		TimedAutomata expected = buildExpectedAutomataGuard7(false);
		TestUtils.checkInternalConsistency(expected);
		TestUtils.checkSame( "", expected, ta, true );
	}

	@Test
	public void testInferenceTransitions5(){

		String fTrace = "storageTest/TestTransizioni5/trace.csv";
		TimedAutomata ta = TestUtils.inferAutomata(2, poli, fTrace,false);


		System.out.println(ta.getNodes());

		TestUtils.checkInternalConsistency(ta);

		TimedAutomata expected = buildExpectedAutomataTransition5();
		TestUtils.checkInternalConsistency(expected);
		TestUtils.checkSame( "", expected, ta, false );
	}





	@Test
	public void testInferenceTransitions4(){

		String fTrace = "storageTest/TestTransizioni4/trace.csv";
		TimedAutomata ta = TestUtils.inferAutomata(2, poli, fTrace,false);


		System.out.println(ta.getNodes());

		TestUtils.checkInternalConsistency(ta);

		TimedAutomata expected = buildExpectedAutomataTransition4();
		TestUtils.checkInternalConsistency(expected);
		TestUtils.checkSame( "", expected, ta, false );
	}


	@Test
	public void testInferenceTransitions3(){

		String fTrace = "storageTest/TestTransizioni3/trace.csv";
		TimedAutomata ta = TestUtils.inferAutomata(2, poli, fTrace,false);


		System.out.println(ta.getNodes());

		TestUtils.checkInternalConsistency(ta);

		TimedAutomata expected = buildExpectedAutomataTransition3();
		TestUtils.checkInternalConsistency(expected);
		TestUtils.checkSame( "", expected, ta, false );
	}


	@Test
	public void testInferenceTransitions2(){

		String fTrace = "storageTest/TestTransizioni2/trace.csv";
		TimedAutomata ta = TestUtils.inferAutomata(2, poli, fTrace,false);


		System.out.println(ta.getNodes());


		TestUtils.checkInternalConsistency(ta);

		TimedAutomata expected = buildExpectedAutomataTransition2();
		TestUtils.checkInternalConsistency(expected);
		TestUtils.checkSame( "", expected, ta, false );
	}


	@Test
	public void testInferenceTransitions6(){

		String fTrace = "storageTest/TestTransizioni6/trace.csv";
		TimedAutomata ta = TestUtils.inferAutomata(2, poli, fTrace,false);


		System.out.println(ta.getNodes());

		//Main.drawGraph1(ta, "");
		TestUtils.checkInternalConsistency(ta);

		TimedAutomata expected = buildExpectedAutomataTransition6();
		TestUtils.checkInternalConsistency(expected);
		TestUtils.checkSame( "", expected, ta, false );
	}



	@Test
	public void testInferenceTransitions1(){

		String fTrace = "storageTest/TestTransizioni1/trace.csv";
		TimedAutomata ta = TestUtils.inferAutomata(2, poli, fTrace,false);


		System.out.println(ta.getNodes());

		TestUtils.checkInternalConsistency(ta);

		TimedAutomata expected = buildExpectedAutomataTransition1();
		TestUtils.checkInternalConsistency(expected);
		TestUtils.checkSame( "", expected, ta, false );
	}

	@Test
	public void testInferenceTransitions1WithRepeatedSequences(){

		String fTrace = "storageTest/TestTransizioni1WithRepeatedSequences/trace.csv";
		TimedAutomata ta = TestUtils.inferAutomata(2, poli, fTrace,false);


		System.out.println(ta.getNodes());

		TestUtils.checkInternalConsistency(ta);

		TimedAutomata expected = buildExpectedAutomataTransition1();
		TestUtils.checkInternalConsistency(expected);
		TestUtils.checkSame( "", expected, ta, false );
	}


	@Test
	public void testInferenceLongTrace() {
		if ( incrementalMerging ){
			return;
		}
		
		TimedAutomata ta;
		String fTrace = "storageTest/TestLongTrace/trace.csv";
		ta = TestUtils.inferAutomata(2, poli, fTrace,true);
		TestUtils.checkInternalConsistency(ta);

		//		TestUtils.visualize(ta);

		TimedAutomata expected = buildExpectedAutomataLongTrace();

		//		TestUtils.visualize(expected);

		TestUtils.checkSame( "", expected, ta, false );

	}


	public static TimedAutomata buildExpectedAutomataLongTrace() {
		TimedAutomataFactory taf = new TimedAutomataFactory("",false);
		Node init = taf.getInitialState();

		Transition tr;


		Node n1 = taf.newNode(false);
		tr = taf.newTransition(init, n1, new Activity("read1.0"), Transition.BEGIN);

		tr = taf.newTransition(n1, init, new Activity("read1.0"), Transition.END);
		Node n3 = taf.newNode(false);
		tr = taf.newTransition(init, n3, new Activity("read1.0"), Transition.BEGIN);
		Node n4 = taf.newNode(false);
		tr = taf.newTransition(n3, n4, new Activity("read1.0"), Transition.END);


		n4.setFinalState(true);



		return taf.getTimedAutomata();
	}


	public static TimedAutomata buildExpectedAutomataTransition1() {
		TimedAutomataFactory taf = new TimedAutomataFactory("",false);
		Node init = taf.getInitialState();

		Transition tr;


		Node n1 = taf.newNode(false);
		tr = taf.newTransition(init, n1, new Activity("read1.0"), Transition.BEGIN);
		Node n2 = taf.newNode(false);
		tr = taf.newTransition(n1, n2, new Activity("read2.0"), Transition.BEGIN);
		Node n3 = taf.newNode(false);
		tr = taf.newTransition(n2, n3, new Activity("read3.0"), Transition.BEGIN);
		Node n4 = taf.newNode(false);
		tr = taf.newTransition(n3, n4, new Activity("read4.0"), Transition.BEGIN);
		Node n5 = taf.newNode(false);
		tr = taf.newTransition(n4, n5, new Activity("read4.0"), Transition.END);

		Node n10 = taf.newNode(false);
		tr = taf.newTransition(n1, n10, new Activity("read2.0"), Transition.BEGIN);
		Node n11 = taf.newNode(false);
		tr = taf.newTransition(n10, n11, new Activity("read3.0"), Transition.BEGIN);
		Node n12 = taf.newNode(false);
		tr = taf.newTransition(n11, n12, new Activity("read14.0"), Transition.BEGIN);

		tr = taf.newTransition(n12, n5, new Activity("read14.0"), Transition.END);


		Node n18 = taf.newNode(false);
		tr = taf.newTransition(n1, n18, new Activity("read2.0"), Transition.BEGIN);
		Node n19 = taf.newNode(false);
		tr = taf.newTransition(n18, n19, new Activity("read3.0"), Transition.BEGIN);
		Node n20 = taf.newNode(false);
		tr = taf.newTransition(n19, n20, new Activity("read24.0"), Transition.BEGIN);

		tr = taf.newTransition(n20, n5, new Activity("read24.0"), Transition.END);



		Node n6 = taf.newNode(false);
		tr = taf.newTransition(n5, n6, new Activity("read3.0"), Transition.END);
		Node n7 = taf.newNode(false);
		tr = taf.newTransition(n6, n7, new Activity("read2.0"), Transition.END);
		Node n8 = taf.newNode(false);
		tr = taf.newTransition(n7, n8, new Activity("read1.0"), Transition.END);

		n8.setFinalState(true);



		return taf.getTimedAutomata();
	}










	public static TimedAutomata buildExpectedAutomataTransition2() {
		TimedAutomataFactory taf = new TimedAutomataFactory("",false);
		Node init = taf.getInitialState();

		Transition tr;


		Node n1 = taf.newNode(false);
		tr = taf.newTransition(init, n1, new Activity("read1.0"), Transition.BEGIN);
		Node n2 = taf.newNode(false);
		tr = taf.newTransition(n1, n2, new Activity("read1.0"), Transition.END);
		Node n3 = taf.newNode(false);
		tr = taf.newTransition(n2, n3, new Activity("read2.0"), Transition.BEGIN);
		Node n4 = taf.newNode(false);
		tr = taf.newTransition(n3, n4, new Activity("read3.0"), Transition.BEGIN);
		Node n5 = taf.newNode(false);
		tr = taf.newTransition(n4, n5, new Activity("read4.0"), Transition.BEGIN);
		Node n6 = taf.newNode(false);
		tr = taf.newTransition(n5, n6, new Activity("read4.0"), Transition.END);
		Node n7 = taf.newNode(false);
		tr = taf.newTransition(n6, n7, new Activity("read3.0"), Transition.END);

		tr = taf.newTransition(n7, init, new Activity("read2.0"), Transition.END);
		Node n9 = taf.newNode(false);
		tr = taf.newTransition(init, n9, new Activity("read1.0"), Transition.BEGIN);
		Node n10 = taf.newNode(false);
		tr = taf.newTransition(n9, n10, new Activity("read1.0"), Transition.END);

		n10.setFinalState(true);


		return taf.getTimedAutomata();
	}


	public static TimedAutomata buildExpectedAutomataTransition6() {
		TimedAutomataFactory taf = new TimedAutomataFactory("",false);
		Node init = taf.getInitialState();

		Transition tr;


		Node n1 = taf.newNode(false);
		tr = taf.newTransition(init, n1, new Activity("read1.0"), Transition.BEGIN);
		Node n2 = taf.newNode(false);
		tr = taf.newTransition(n1, n2, new Activity("read1.0"), Transition.END);
		Node n3 = taf.newNode(false);




		tr = taf.newTransition(n2, n3, new Activity("read2.0"), Transition.BEGIN);
		Node n4 = taf.newNode(false);
		tr = taf.newTransition(n3, n4, new Activity("read3.0"), Transition.BEGIN);
		Node n5 = taf.newNode(false);
		tr = taf.newTransition(n4, n5, new Activity("read4.0"), Transition.BEGIN);
		Node n6 = taf.newNode(false);
		tr = taf.newTransition(n5, n6, new Activity("read4.0"), Transition.END);
		Node n7 = taf.newNode(false);
		tr = taf.newTransition(n6, n7, new Activity("read3.0"), Transition.END);

		tr = taf.newTransition(n7, init, new Activity("read2.0"), Transition.END);
		Node n9 = taf.newNode(false);
		tr = taf.newTransition(init, n9, new Activity("read1.0"), Transition.BEGIN);
		Node n10 = taf.newNode(false);
		tr = taf.newTransition(n9, n10, new Activity("read1.0"), Transition.END);

		n10.setFinalState(true);


		Node n11 = taf.newNode(false);
		tr = taf.newTransition(init, n11, new Activity("read5.0"), Transition.BEGIN);
		Node n12 = taf.newNode(false);
		tr = taf.newTransition(n11, n12, new Activity("read5.0"), Transition.END);


		n12.setFinalState(true);

		return taf.getTimedAutomata();
	}


















	public static TimedAutomata buildExpectedAutomataTransition3() {
		TimedAutomataFactory taf = new TimedAutomataFactory("",false);
		Node init = taf.getInitialState();

		Transition tr;


		Node n1 = taf.newNode(false);
		tr = taf.newTransition(init, n1, new Activity("read1.0"), Transition.BEGIN);
		Node n2 = taf.newNode(false);
		tr = taf.newTransition(n1, n2, new Activity("read1.0"), Transition.END);
		Node n3 = taf.newNode(false);
		tr = taf.newTransition(n2, n3, new Activity("read2.0"), Transition.BEGIN);
		Node n4 = taf.newNode(false);
		tr = taf.newTransition(n3, n4, new Activity("read3.0"), Transition.BEGIN);
		Node n5 = taf.newNode(false);
		tr = taf.newTransition(n4, n5, new Activity("read3.0"), Transition.END);
		Node n6 = taf.newNode(false);
		tr = taf.newTransition(n5, n6, new Activity("read2.0"), Transition.END);
		n6.setFinalState(true);



		Node n7 = taf.newNode(false);
		tr = taf.newTransition(init, n7, new Activity("read1.0"), Transition.BEGIN);
		Node n8 = taf.newNode(false);
		tr = taf.newTransition(n7, n8, new Activity("read1.0"), Transition.END);
		Node n9 = taf.newNode(false);
		tr = taf.newTransition(n8, n9, new Activity("read12.0"), Transition.BEGIN);
		Node n10 = taf.newNode(false);
		tr = taf.newTransition(n9, n10, new Activity("read13.0"), Transition.BEGIN);
		Node n11 = taf.newNode(false);
		tr = taf.newTransition(n10, n11, new Activity("read13.0"), Transition.END);
		tr = taf.newTransition(n11, n6, new Activity("read12.0"), Transition.END);



		Node n13 = taf.newNode(false);
		tr = taf.newTransition(init, n13, new Activity("read1.0"), Transition.BEGIN);
		Node n14 = taf.newNode(false);
		tr = taf.newTransition(n13, n14, new Activity("read1.0"), Transition.END);
		Node n15 = taf.newNode(false);
		tr = taf.newTransition(n14, n15, new Activity("read22.0"), Transition.BEGIN);
		Node n16 = taf.newNode(false);
		tr = taf.newTransition(n15, n16, new Activity("read23.0"), Transition.BEGIN);
		Node n17 = taf.newNode(false);
		tr = taf.newTransition(n16, n17, new Activity("read23.0"), Transition.END);
		tr = taf.newTransition(n17, n6, new Activity("read22.0"), Transition.END);



		return taf.getTimedAutomata();
	}



	public static TimedAutomata buildExpectedAutomataTransition4() {
		TimedAutomataFactory taf = new TimedAutomataFactory("",false);
		Node init = taf.getInitialState();

		Transition tr;


		Node n1 = taf.newNode(false);
		tr = taf.newTransition(init, n1, new Activity("read1.0"), Transition.BEGIN);

		Node n2 = taf.newNode(false);
		tr = taf.newTransition(n1, n2, new Activity("read1.0"), Transition.END);

		Node n3 = taf.newNode(false);
		tr = taf.newTransition(n2, n3, new Activity("read2.0"), Transition.BEGIN);

		Node n4 = taf.newNode(false);
		tr = taf.newTransition(n3, n4, new Activity("read3.0"), Transition.BEGIN);

		Node n5 = taf.newNode(false);
		tr = taf.newTransition(n4, n5, new Activity("read3.0"), Transition.END);

		Node n6 = taf.newNode(false);
		tr = taf.newTransition(n5, n6, new Activity("read2.0"), Transition.END);

		n6.setFinalState(true);

		Node n7 = taf.newNode(false);
		tr = taf.newTransition(init, n7, new Activity("read11.0"), Transition.BEGIN);


		tr = taf.newTransition(n7, n2, new Activity("read11.0"), Transition.END);

		return taf.getTimedAutomata();
	}


	public static TimedAutomata buildExpectedAutomataTransition5() {
		TimedAutomataFactory taf = new TimedAutomataFactory("",false);
		Node init = taf.getInitialState();

		Transition tr;


		Node n1 = taf.newNode(false);
		tr = taf.newTransition(init, n1, new Activity("read1.0"), Transition.BEGIN);


		Node n2 = taf.newNode(false);
		tr = taf.newTransition(n1, n2, new Activity("read1.0"), Transition.END);

		Node n3 = taf.newNode(false);
		tr = taf.newTransition(n2, n3, new Activity("read2.0"), Transition.BEGIN);

		Node n4 = taf.newNode(false);
		tr = taf.newTransition(n3, n4, new Activity("read3.0"), Transition.BEGIN);

		Node n5 = taf.newNode(false);
		tr = taf.newTransition(n4, n5, new Activity("read3.0"), Transition.END);


		tr = taf.newTransition(n5, n2, new Activity("read2.0"), Transition.END);


		Node n11 = taf.newNode(false);
		tr = taf.newTransition(init, n11, new Activity("read11.0"), Transition.BEGIN);
		tr = taf.newTransition(n11, n2, new Activity("read11.0"), Transition.END);



		Node n9 = taf.newNode(false);
		tr = taf.newTransition(n4, n9, new Activity("read3.0"), Transition.END);

		Node n10 = taf.newNode(false);
		tr = taf.newTransition(n9, n10, new Activity("read2.0"), Transition.END);

		n10.setFinalState(true);

		return taf.getTimedAutomata();
	}


	public static TimedAutomata buildExpectedAutomataGuard7(boolean withNestedCallsTime) {
		TimedAutomataFactory taf = new TimedAutomataFactory("",false);
		Node init = taf.getInitialState();

		Transition tr;

		Clock globalClock = ClockFactory.getClockAbsolute();
		Node n1 = taf.newNode(false);
		tr = taf.newTransition(init, n1, new Activity("read1.0"), Transition.BEGIN);
		{
			Clause g = new Clause(globalClock, new Interval(0,0) );
			tr.addClause(g);
		}

		Node n2 = taf.newNode(false);
		tr = taf.newTransition(n1, n2, new Activity("read2.0"), Transition.BEGIN);
		{
			Clause g = new Clause(globalClock, new Interval(9,15) );
			tr.addClause(g);
		}

		Node n3 = taf.newNode(false);
		tr = taf.newTransition(n2, n3, new Activity("read3.0"), Transition.BEGIN);
		{
			Clause g = new Clause(globalClock, new Interval(15,117) );
			tr.addClause(g);
		}

		Node n4 = taf.newNode(false);
		tr = taf.newTransition(n3, n4, new Activity("read4.0"), Transition.BEGIN);
		{
			Clause g = new Clause(globalClock, new Interval(20,123) );
			tr.addClause(g);
		}


		Node n5 = taf.newNode(false);
		tr = taf.newTransition(n4, n5, new Activity("read4.0"), Transition.END);
		{
			Clause g = new Clause(globalClock, new Interval(40,138) );
			tr.addClause(g);
		}
		if(withNestedCallsTime){
			Clause g = new Clause(ClockFactory.getClockRelative(), new Interval(6,20) );
			tr.addClause(g);
		} else {
			Clause g = new Clause(ClockFactory.getClockRelative(), new Interval(6,20) );
			tr.addClause(g);
		}

		Node n6 = taf.newNode(false);
		tr = taf.newTransition(n5, n6, new Activity("read3.0"), Transition.END);
		{
			Clause g = new Clause(globalClock, new Interval(52,156) );
			tr.addClause(g);
		}
		if(withNestedCallsTime){
			Clause g = new Clause(ClockFactory.getClockRelative(), new Interval(14,24) );
			tr.addClause(g);
		} else {
			Clause g = new Clause(ClockFactory.getClockRelative(), new Interval(20,40) );
			tr.addClause(g);
		}

		Node n7 = taf.newNode(false);
		tr = taf.newTransition(n6, n7, new Activity("read2.0"), Transition.END);
		{
			Clause g = new Clause(globalClock, new Interval(59,172) );
			tr.addClause(g);
		}
		if(withNestedCallsTime){
			Clause g = new Clause(ClockFactory.getClockRelative(), new Interval(10,118) );
			tr.addClause(g);
		} else {
			Clause g = new Clause(ClockFactory.getClockRelative(), new Interval(50,157) );
			tr.addClause(g);
		}

		Node n8 = taf.newNode(false);
		tr = taf.newTransition(n7, n8, new Activity("read1.0"), Transition.END);
		{
			Clause g = new Clause(globalClock, new Interval(65,175) );
			tr.addClause(g);
		}
		if(withNestedCallsTime){
			Clause g = new Clause(ClockFactory.getClockRelative(), new Interval(15,104) );
			tr.addClause(g);
		} else {
			Clause g = new Clause(ClockFactory.getClockRelative(), new Interval(65,175) );
			tr.addClause(g);
		}

		n8.setFinalState(true);

		return taf.getTimedAutomata();
	}




	public static TimedAutomata buildExpectedAutomataGuard2(boolean useMethodTime) {
		TimedAutomataFactory taf = new TimedAutomataFactory("",false);
		Node init = taf.getInitialState();

		Transition tr;

		Clock globalClock = ClockFactory.getClockAbsolute();
		Node n1 = taf.newNode(false);
		tr = taf.newTransition(init, n1, new Activity("read1.0"), Transition.BEGIN);
		{
			Clause g = new Clause(globalClock, new Interval(0,0) );
			tr.addClause(g);
		}

		Node n2 = taf.newNode(false);
		tr = taf.newTransition(n1, n2, new Activity("read2.0"), Transition.BEGIN);
		{
			Clause g = new Clause(globalClock, new Interval(10,10) );
			tr.addClause(g);
		}

		Node n3 = taf.newNode(false);
		tr = taf.newTransition(n2, n3, new Activity("read3.0"), Transition.BEGIN);
		{
			Clause g = new Clause(globalClock, new Interval(15,15) );
			tr.addClause(g);
		}

		Node n4 = taf.newNode(false);
		tr = taf.newTransition(n3, n4, new Activity("read3.0"), Transition.END);
		{
			Clause g = new Clause(globalClock, new Interval(55,55) );
			tr.addClause(g);
		}
		{
			Clause g = new Clause(ClockFactory.getClockRelative(), new Interval(40,40) );
			tr.addClause(g);
		}

		Node n5 = taf.newNode(false);
		tr = taf.newTransition(n4, n5, new Activity("read2.0"), Transition.END);
		{
			Clause g = new Clause(globalClock, new Interval(60,60) );
			tr.addClause(g);
		}
		if(useMethodTime){
			Clause g = new Clause(ClockFactory.getClockRelative(), new Interval(10,10) );
			tr.addClause(g);
		}else{
			Clause g = new Clause(ClockFactory.getClockRelative(), new Interval(50,50) );
			tr.addClause(g);
		}

		Node n6 = taf.newNode(false);
		tr = taf.newTransition(n5, n6, new Activity("read1.0"), Transition.END);
		{
			Clause g = new Clause(globalClock, new Interval(65,65) );
			tr.addClause(g);
		}
		if(useMethodTime){
			Clause g = new Clause(ClockFactory.getClockRelative(), new Interval(15,15) );
			tr.addClause(g);
		}else{
			Clause g = new Clause(ClockFactory.getClockRelative(), new Interval(65,65) );
			tr.addClause(g);
		}

		n6.setFinalState(true);

		return taf.getTimedAutomata();
	}

	public static TimedAutomata buildExpectedAutomataGuard1(boolean methodTime) {
		TimedAutomataFactory taf = new TimedAutomataFactory("",false);
		Node init = taf.getInitialState();

		Transition tr;

		Clock globalClock = ClockFactory.getClockAbsolute();
		Node n1 = taf.newNode(false);
		tr = taf.newTransition(init, n1, new Activity("read1.0"), Transition.BEGIN);
		{
			Clause g = new Clause(globalClock, new Interval(0,0) );
			tr.addClause(g);
		}

		Node n2 = taf.newNode(false);
		tr = taf.newTransition(n1, n2, new Activity("read2.0"), Transition.BEGIN);

		Node n3 = taf.newNode(false);
		tr = taf.newTransition(n2, n3, new Activity("read3.0"), Transition.BEGIN);

		Node n4 = taf.newNode(false);
		tr = taf.newTransition(n3, n4, new Activity("read4.0"), Transition.BEGIN);

		Node n5 = taf.newNode(false);
		tr = taf.newTransition(n4, n5, new Activity("read4.0"), Transition.END);

		Node n6 = taf.newNode(false);
		tr = taf.newTransition(n5, n6, new Activity("read3.0"), Transition.END);
		{
			Clause g = new Clause(globalClock, new Interval(55,55) );
			tr.addClause(g);
		}


		Node n7 = taf.newNode(false);
		tr = taf.newTransition(n6, n7, new Activity("read2.0"), Transition.END);
		{
			Clause g = new Clause(globalClock, new Interval(60,60) );
			tr.addClause(g);
		}


		Node n8 = taf.newNode(false);
		tr = taf.newTransition(n7, n8, new Activity("read1.0"), Transition.END);
		{
			Clause g = new Clause(globalClock, new Interval(65,65) );
			tr.addClause(g);
		}
		if ( methodTime ){
			Clock c1 = ClockFactory.getClockRelative();
			Clause g = new Clause(c1 , new Interval(15,15) );
			tr.addClause(g);
		} else {
			Clock c1 = ClockFactory.getClockRelative();
			Clause g = new Clause(c1 , new Interval(65,65) );
			tr.addClause(g);
		}

		n8.setFinalState(true);

		Node n18 = taf.newNode(false);
		tr = taf.newTransition(n1, n18, new Activity("read2.0"), Transition.BEGIN);
		Node n19 = taf.newNode(false);
		tr = taf.newTransition(n18, n19, new Activity("read3.0"), Transition.BEGIN);
		Node n20 = taf.newNode(false);
		tr = taf.newTransition(n19, n20, new Activity("read24.0"), Transition.BEGIN);
		tr = taf.newTransition(n20, n5, new Activity("read24.0"), Transition.END);

		Node n10 = taf.newNode(false);
		tr = taf.newTransition(n1, n10, new Activity("read2.0"), Transition.BEGIN);
		Node n11 = taf.newNode(false);
		tr = taf.newTransition(n10, n11, new Activity("read3.0"), Transition.BEGIN);
		Node n12 = taf.newNode(false);
		tr = taf.newTransition(n11, n12, new Activity("read14.0"), Transition.BEGIN);
		tr = taf.newTransition(n12, n5, new Activity("read14.0"), Transition.END);

		return taf.getTimedAutomata();
	}

}
