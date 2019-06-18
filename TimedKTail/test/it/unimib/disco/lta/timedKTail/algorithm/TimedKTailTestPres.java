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



public class TimedKTailTestPres {



	


	public /* NOT private */ MergeStrategies mergeStartegy = MergeStrategies.DEPTH_FIRST;


	public /* NOT private */ boolean useCaching = false;


	public /* NOT private */ boolean incrementalMerging = false;

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
		String fTrace = "storageTest/TestPres/trace.csv";
		ta = TestUtils.inferAutomata(2, poli, fTrace,false);
		TestUtils.checkInternalConsistency(ta);

		TestUtils.visualize(ta);

	}


}
