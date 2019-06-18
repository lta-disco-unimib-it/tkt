package it.unimib.disco.lta.timedKTail.algorithm;

import java.util.Set;

import it.unimib.disco.lta.timedKTail.JTMTime.NodeWithFutureAndPendingCalls;

public class MergeVerifierWithPendingCalls {

	public boolean canMerge(NodeWithFutureAndPendingCalls n, NodeWithFutureAndPendingCalls n1) {
		Set<PendingCallsSequence> enterN = n.getEnteringCalls();
		Set<PendingCallsSequence> enterN1 = n1.getEnteringCalls();
		
		Set<PendingCallsSequence> exitN = n.getExitingCalls();
		Set<PendingCallsSequence> exitN1 = n1.getExitingCalls();
		
//		System.out.println("");
//		System.out.println("Node: "+n);
//		System.out.println("enterN: "+enterN);
//		System.out.println("exitN: "+exitN);
//		
//		System.out.println("");
//		System.out.println("Node: "+n1);
//		System.out.println("enterN: "+enterN1);
//		System.out.println("exitN1: "+exitN1);
//		
//		System.out.println("");
//		System.out.println("");
		
		if ( exitN.isEmpty() && exitN1.isEmpty() ){
			return true;
		}
		
		if ( enterN.size() != enterN1.size() ){
			return false;
		}
		
		if ( exitN.size() != exitN1.size() ){
			return false;
		}
		
		
		
		return exitN.containsAll(exitN1); 
	}

	public void mergePendingCalls(NodeWithFutureAndPendingCalls n, NodeWithFutureAndPendingCalls n1) {
		
		Set<PendingCallsSequence> enterN1 = n1.getEnteringCalls();
		Set<PendingCallsSequence> exitN1 = n1.getExitingCalls();
		
		n.addAllEnteringCalls(enterN1);
		n.addAllExitingCalls(exitN1);
	}

	
}
