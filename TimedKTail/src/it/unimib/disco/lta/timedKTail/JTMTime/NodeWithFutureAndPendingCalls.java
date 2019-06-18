package it.unimib.disco.lta.timedKTail.JTMTime;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import it.unimib.disco.lta.timedKTail.algorithm.PendingCallsSequence;

public class NodeWithFutureAndPendingCalls extends NodeWithFuture implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public NodeWithFutureAndPendingCalls(boolean initial) {
		super(initial);
	}
	
	private Set<PendingCallsSequence> enteringCalls = new HashSet<>();
	private Set<PendingCallsSequence> exitingCalls = new HashSet<>();
	
	
	public Set<PendingCallsSequence> getEnteringCalls() {
		return enteringCalls;
	}
	public void addEnteringCalls(PendingCallsSequence enteringCalls) {
		this.enteringCalls.add( enteringCalls );
	}
	public Set<PendingCallsSequence> getExitingCalls() {
		return exitingCalls;
	}
	public void addExitingCalls(PendingCallsSequence exitingCalls) {
		this.exitingCalls.add( exitingCalls );
	}
	public void addAllEnteringCalls(Set<PendingCallsSequence> enterCalls) {
		enteringCalls.addAll(enterCalls);
	}
	public void addAllExitingCalls(Set<PendingCallsSequence> exitN1) {
		exitingCalls.addAll(exitN1);
	}

	
	
}
