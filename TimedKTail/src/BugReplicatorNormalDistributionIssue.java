import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import it.unimib.disco.lta.timedKTail.JTMTime.Node;
import it.unimib.disco.lta.timedKTail.JTMTime.TimedAutomata;
import it.unimib.disco.lta.timedKTail.JTMTime.Transition;
import it.unimib.disco.lta.timedKTail.algorithm.PhaseII;
import it.unimib.disco.lta.timedKTail.algorithm.Policy;
import it.unimib.disco.lta.timedKTail.tests.utils.TestUtils;
import it.unimib.disco.lta.timedKTail.ui.Main;
import it.unimib.disco.lta.timedKTail.validation.NestingValidator;
import it.unimib.disco.lta.timedKTail.validation.NestingValidator.Error;
import it.unimib.disco.lta.timedKTail.validation.NestingValidator.WrongTerminatingEvent;
import it.unimib.disco.lta.timedKTail.validation.Validation;

public class BugReplicatorNormalDistributionIssue {

	static class MyP2 extends PhaseII{

		public MyP2(TimedAutomata ta, int k, Policy poli) {
			super(ta, k, poli);
		}

		public void run() {
			mergeOverlappingTransitions();

			
			mergeClocks();

			applyPolicies();
		}
		
		
	}
	
	public static void main(String[] args) {
		TimedAutomata ta = Main.loadAutomata(args[0]);
		LinkedList<Node> toDelete = new LinkedList<>();
		
		for(Node n : ta.getNodes() ){
			if ( n.getTransitionsExit().size() == 0 ){
				boolean entering = false;
				
				for ( Transition t1 : ta.getTransitions() ){
					if ( t1.getNodeTo().equals(n) ){
						entering=true;
						break;
					}
				}
				
				if ( entering == false ){
					toDelete.add(n);
				}
			}
		}
		
		ta.deleteNodes(toDelete);
		
		Policy poli = new Policy(3, 0, 4, true, true);
		
		poli.setMaxStatesVisits(50);
		poli.setMaxVisitDepth(92);
		
		poli.setDeltaForRangeCalculation(0.95);
		
		MyP2 p = new MyP2(ta, 2, poli);
		p.run();
	}

	public static void printNodes(TimedAutomata taf, Node nL, Node nR) {
		printExit(nL);
		printEnter(taf, nL);

		printExit(nR);
		printEnter(taf, nR);
	}

	public static List<Error> validateAutomata(TimedAutomata taf) {
		NestingValidator vf = new NestingValidator();
		vf.setMaxStateVisits(50);
		vf.setMaxVisitDepth(92);
		List<Error> tafE = vf.validate(taf);
		return tafE;
	}

	public static void printEnter(TimedAutomata ta, Node nL) {
		System.out.println("Entering "+nL.getId());
		Iterator<Transition> it = ta.getTransitions().iterator();
        while(it.hasNext()) {
            Transition t = it.next();
            //Trovato un arco entrante in n2 e lo sposto a n1
            if(t.getNodeTo().equals(nL)){
            	System.out.println("("+t.getNodeFrom().getId()+") ->"+t+" -> ("+t.getNodeTo().getId()+")");
            }  
        }
	}

	public static void printExit(Node nL) {
		System.out.println("Exit from "+nL.getId());
		for ( Transition  t : nL.getTransitionsExit() ){
			System.out.println("("+t.getNodeFrom().getId()+") ->"+t+" -> ("+t.getNodeTo().getId()+")");
		}
	}

	

}
