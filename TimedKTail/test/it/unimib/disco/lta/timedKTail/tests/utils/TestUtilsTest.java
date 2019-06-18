package it.unimib.disco.lta.timedKTail.tests.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import it.unimib.disco.lta.timedKTail.JTMTime.Activity;
import it.unimib.disco.lta.timedKTail.JTMTime.Node;
import it.unimib.disco.lta.timedKTail.JTMTime.TimedAutomata;
import it.unimib.disco.lta.timedKTail.JTMTime.TimedAutomataFactory;
import it.unimib.disco.lta.timedKTail.JTMTime.Transition;
import it.unimib.disco.lta.timedKTail.algorithm.TimedKTailTest;

public class TestUtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCheckSame() {
		TimedAutomata ta1A = ta1();
		TimedAutomata ta1B = ta1();
		
		TestUtils.checkSame("", ta1A, ta1B, false);
		
		TimedAutomata ta2A = ta2A();
		TimedAutomata ta2B = ta2B();
		
		TestUtils.checkSame("", ta2A, ta2B, false);
		
		TimedAutomata ta3 = TimedKTailTest.buildExpectedAutomataGuard1(true);
		TimedAutomata ta4 = TimedKTailTest.buildExpectedAutomataGuard1(true);
		
		TestUtils.checkSame("", ta3, ta4, false);
	}

	public TimedAutomata ta1() {
		TimedAutomataFactory taf = buildFactory();
		Node init = taf.getInitialState();
		
		Transition tr;

		Node n1 = taf.newNode(false);
		tr = taf.newTransition(init, n1, new Activity("read1.0"), Transition.BEGIN);
		
		Node n2 = taf.newNode(false);
		tr = taf.newTransition(n1, n2, new Activity("read1.0"), Transition.END);
		return taf.getTimedAutomata();
	}

	public TimedAutomataFactory buildFactory() {
		TimedAutomataFactory taf = new TimedAutomataFactory("",false);
		return taf;
	}
	
	public TimedAutomata ta2A() {
		TimedAutomataFactory taf = buildFactory();
		Node init = taf.getInitialState();
		
		Transition tr;

		Node n1 = taf.newNode(false);
		tr = taf.newTransition(init, n1, new Activity("a"), Transition.BEGIN);
		
		Node n2 = taf.newNode(false);
		tr = taf.newTransition(n1, n2, new Activity("b"), Transition.BEGIN);
		
		Node n3 = taf.newNode(false);
		tr = taf.newTransition(init, n3, new Activity("a"), Transition.BEGIN);
		
		Node n4 = taf.newNode(false);
		tr = taf.newTransition(n3, n4, new Activity("c"), Transition.BEGIN);
		
		Node n5 = taf.newNode(false);
		tr = taf.newTransition(n3, n5, new Activity("d"), Transition.BEGIN);
		
		return taf.getTimedAutomata();
	}
	
	public TimedAutomata ta2B() {
		TimedAutomataFactory taf = buildFactory();
		Node init = taf.getInitialState();
		
		Transition tr;

		
		
		Node n3 = taf.newNode(false);
		tr = taf.newTransition(init, n3, new Activity("a"), Transition.BEGIN);
		
		Node n5 = taf.newNode(false);
		tr = taf.newTransition(n3, n5, new Activity("d"), Transition.BEGIN);
		
		Node n4 = taf.newNode(false);
		tr = taf.newTransition(n3, n4, new Activity("c"), Transition.BEGIN);
		
		
		
		
		Node n1 = taf.newNode(false);
		tr = taf.newTransition(init, n1, new Activity("a"), Transition.BEGIN);
		
		Node n2 = taf.newNode(false);
		tr = taf.newTransition(n1, n2, new Activity("b"), Transition.BEGIN);
		
		return taf.getTimedAutomata();
	}

}
