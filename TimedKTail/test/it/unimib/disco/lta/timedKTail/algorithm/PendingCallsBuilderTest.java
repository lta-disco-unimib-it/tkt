package it.unimib.disco.lta.timedKTail.algorithm;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import it.unimib.disco.lta.timedKTail.JTMTime.Activity;
import it.unimib.disco.lta.timedKTail.JTMTime.Node;
import it.unimib.disco.lta.timedKTail.JTMTime.NodeWithFutureAndPendingCalls;
import it.unimib.disco.lta.timedKTail.JTMTime.TimedAutomata;
import it.unimib.disco.lta.timedKTail.JTMTime.TimedAutomataFactory;
import it.unimib.disco.lta.timedKTail.JTMTime.Transition;

public class PendingCallsBuilderTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testBuildPendingCalls() {
		TimedAutomataFactory taf = new TimedAutomataFactory("tees", true, true);

		NodeWithFutureAndPendingCalls node0 = (NodeWithFutureAndPendingCalls) taf.getInitialState();
		NodeWithFutureAndPendingCalls node1 = (NodeWithFutureAndPendingCalls) taf.newNode(false);

		Transition t1 = taf.newTransition(node0, node1, new Activity("A"), Transition.BEGIN);

		NodeWithFutureAndPendingCalls node2 = (NodeWithFutureAndPendingCalls) taf.newNode(false);
		Transition t2 = taf.newTransition(node1, node2, new Activity("A"), Transition.END);


		NodeWithFutureAndPendingCalls node3 = (NodeWithFutureAndPendingCalls) taf.newNode(false);
		Transition t3 = taf.newTransition(node2, node3, new Activity("B"), Transition.BEGIN);

		NodeWithFutureAndPendingCalls node4 = (NodeWithFutureAndPendingCalls) taf.newNode(false);
		Transition t4 = taf.newTransition(node3, node4, new Activity("C"), Transition.BEGIN);

		NodeWithFutureAndPendingCalls node5 = (NodeWithFutureAndPendingCalls) taf.newNode(false);
		Transition t5 = taf.newTransition(node4, node5, new Activity("C"), Transition.END);

		NodeWithFutureAndPendingCalls node6 = (NodeWithFutureAndPendingCalls) taf.newNode(false);
		Transition t6 = taf.newTransition(node5, node6, new Activity("B"), Transition.END);


		TimedAutomata ta = taf.getTimedAutomata();

		PendingCallsBuilder pcb = new PendingCallsBuilder();
		pcb.buildPendingCalls(ta);

		checkPendingCalls(node0, Arrays.asList(new Transition[]{}), Arrays.asList(new Transition[]{}));


		checkPendingCalls(node1, Arrays.asList(new Transition[]{t1}), Arrays.asList(new Transition[]{t2}));

		checkPendingCalls(node2, Arrays.asList(new Transition[]{}), Arrays.asList(new Transition[]{}));

		checkPendingCalls(node3, Arrays.asList(new Transition[]{t3}), Arrays.asList(new Transition[]{t6}));

		checkPendingCalls(node4, Arrays.asList(new Transition[]{t3,t4}), Arrays.asList(new Transition[]{t5,t6}));

		checkPendingCalls(node5, Arrays.asList(new Transition[]{t3}), Arrays.asList(new Transition[]{t6}));

		checkPendingCalls(node6, Arrays.asList(new Transition[]{}), Arrays.asList(new Transition[]{}));

	}

	public void checkPendingCalls(NodeWithFutureAndPendingCalls node1, List<Transition> enterCalls,
			List<Transition> exitCalls) {

		if ( enterCalls.isEmpty() ){
			assertEquals( 0, node1.getEnteringCalls().size() );
		} else {
			PendingCallsSequence expectedtNode1Enter = PendingCallsFactory.INSTANCE.buildSequence(enterCalls);
			assertTrue( node1.getEnteringCalls().containsAll(toSet(expectedtNode1Enter)) );
			assertEquals( 1, node1.getEnteringCalls().size() );
		}

		if ( exitCalls.isEmpty() ){
			assertEquals( 0, node1.getExitingCalls().size() );
		} else {
			PendingCallsSequence expectedtNode1Exit = PendingCallsFactory.INSTANCE.buildSequence(exitCalls);
			assertTrue( node1.getExitingCalls().containsAll(toSet(expectedtNode1Exit)) );
			assertEquals( 1, node1.getExitingCalls().size() );
		}
	}

	private Collection<?> toSet(PendingCallsSequence e1) {
		return Arrays.asList(new PendingCallsSequence[]{e1});
	}

}
