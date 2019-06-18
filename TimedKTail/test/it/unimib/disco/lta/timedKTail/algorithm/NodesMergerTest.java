package it.unimib.disco.lta.timedKTail.algorithm;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import it.unimib.disco.lta.timedKTail.JTMTime.Node;
import it.unimib.disco.lta.timedKTail.JTMTime.NodesMerger;
import it.unimib.disco.lta.timedKTail.JTMTime.TimedAutomata;
import it.unimib.disco.lta.timedKTail.tests.utils.TestUtils;
import it.unimib.disco.lta.timedKTail.ui.Main;

@RunWith(Parameterized.class)
public class NodesMergerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}


	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{TimedKTailTest.buildExpectedAutomataGuard1(true),TimedKTailTest.buildExpectedAutomataGuard1(true)},
			{TimedKTailTest.buildExpectedAutomataGuard2(true),TimedKTailTest.buildExpectedAutomataGuard2(true)},
			{TimedKTailTest.buildExpectedAutomataGuard7(true),TimedKTailTest.buildExpectedAutomataGuard7(true)},
			{TimedKTailTest.buildExpectedAutomataTransition1(),TimedKTailTest.buildExpectedAutomataTransition1()},
			{TimedKTailTest.buildExpectedAutomataTransition2(),TimedKTailTest.buildExpectedAutomataTransition2()},
			{TimedKTailTest.buildExpectedAutomataTransition3(),TimedKTailTest.buildExpectedAutomataTransition3()},
			{TimedKTailTest.buildExpectedAutomataTransition4(),TimedKTailTest.buildExpectedAutomataTransition4()},
			{TimedKTailTest.buildExpectedAutomataTransition5(),TimedKTailTest.buildExpectedAutomataTransition5()},
		});
	}

	@Parameter // first data value (0) is default
	public /* NOT private */ TimedAutomata ta;

	@Parameter(value = 1)
	public /* NOT private */ TimedAutomata taExpected;

	@Test
	public void testMerge() {
		testMergeOfAllAutomataNodes(ta, taExpected);
	}


	public void testMergeOfAllAutomataNodes(TimedAutomata ta, TimedAutomata taExpected) {
		Node[] nodes = ta.getNodesArray();
		for( int i=0, len=ta.getNodes().size(); i < len; i++ ){
			for( int j=1; j<len; j++ ){
				
				String msg = "Merging: "+nodes[i].getId() +" "+ nodes[j].getId()+" ";
				System.out.println(msg);
				
				NodesMerger merger = new NodesMerger(ta);
				merger.mergeNodes( nodes[i], nodes[j] );
				merger.rollback();

				
				//Main.drawGraph1(ta, "");
				//Main.drawGraph1(taExpected, "");
				TestUtils.checkSame(msg, ta, taExpected, true);
			}	
		}

	}

}
