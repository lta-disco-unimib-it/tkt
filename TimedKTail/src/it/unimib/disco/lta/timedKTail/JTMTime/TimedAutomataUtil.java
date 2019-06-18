package it.unimib.disco.lta.timedKTail.JTMTime;

import java.util.Collection;

public class TimedAutomataUtil {

	public void checkFinalStatePresent(TimedAutomata ta) {
		checkFinalStatePresent(ta.getNodes());
	}
	
	public void checkFinalStatePresent(Collection<Node> nn) {
		{
			boolean finalFound = false;
			for ( Node n : nn ){
				if ( n.isFinalState() ){
					finalFound = true;
				}
			}

			if ( ! finalFound ){
				throw new AssertionError("Missing final state");
			}
		}
	}
}
