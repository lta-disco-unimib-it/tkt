package it.unimib.disco.lta.timedKTail.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import it.unimib.disco.lta.timedKTail.JTMTime.Node;
import it.unimib.disco.lta.timedKTail.JTMTime.NodeWithFutureAndPendingCalls;
import it.unimib.disco.lta.timedKTail.JTMTime.TimedAutomata;
import it.unimib.disco.lta.timedKTail.JTMTime.Transition;

public class PendingCallsBuilder {

	public void buildPendingCalls(TimedAutomata ta) {
		Node init = ta.getNodeInit();

		for ( Transition t : init.getTransitionsExit() ){
			LinkedList<Transition> calls = new LinkedList<>();
			LinkedList<Transition> backward = new LinkedList<>();

			Transition next = t;

			while ( next != null ){
				next = visitAndBuildPendingCalls( next, calls, backward );
			}

			calls = new LinkedList<>();
			while ( ! backward.isEmpty() ){
				next = backward.removeFirst();
				visitAndBuildPendingCallsExiting( next, calls );
			}
		}
	}

	private Transition visitAndBuildPendingCalls(Transition t, LinkedList<Transition> calls, LinkedList<Transition> backward) {
		backward.addFirst(t);

		if ( t.isBegin() ){
			calls.addLast(t);
		} else {
			calls.removeLast();
		}

		NodeWithFutureAndPendingCalls node = (NodeWithFutureAndPendingCalls) t.getNodeTo();
		if ( calls.size() > 0 ){
			node.addEnteringCalls(PendingCallsFactory.INSTANCE.buildSequence(calls));
		}

		Collection<Transition> exit = node.getTransitionsExit();
		if ( exit.size() > 1 ){
			throw new IllegalStateException("Just one exit transition expected.");
		}

		if ( exit.size() == 0 ){
			return null;
		}

		return node.getLastTransitionAdded();
	}

	private void visitAndBuildPendingCallsExiting(Transition t, LinkedList<Transition> calls) {

		if ( t.isEnd() ){
			calls.addFirst(t);
		} else {
			calls.removeFirst();
		}
		
		
		
		NodeWithFutureAndPendingCalls node = (NodeWithFutureAndPendingCalls) t.getNodeFrom();
		if ( calls.size() > 0 ){
			node.addExitingCalls(PendingCallsFactory.INSTANCE.buildSequence(calls));
		}

//		System.out.println("Adding "+calls +" TO "+node);
		

	}
}
