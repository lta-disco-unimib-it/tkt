import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
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

public class BugReplicator {
//	Merging (3) MergeSort.mergeSort(B)T == 0;CK3:=0;;MergeSort.mergeSort(B)T == 0;CK4:=0; (243) MergeSort.mergeSort(B)T == 1;CK124:=0;;MergeSort.mergeSort(B)T == 1;CK125:=0;
	static class MyP2 extends PhaseII{

		public MyP2(TimedAutomata ta, int k, Policy poli) {
			super(ta, k, poli);
		}

		public void merge(Node nL, Node nR) {
			mergeNodes(nL, nR);
			
			boolean validMerge = verifyAutomata();
			
			System.out.println("Valid merge:" + validMerge);
		}
		
		
	}
	
	public static void main(String[] args) {
		TimedAutomata ta = Main.loadAutomata(args[0]);
		
		TimedAutomata taMerged = Main.loadAutomata(args[1]);
		
		{
			Node nL = ta.getNodeWithID(3);
			Node nR = ta.getNodeWithID(243);

			System.out.println("Nodes of original file");
			printNodes(ta, nL, nR);

			Policy poli = new Policy(3, 0, 4, true, true);
			
			poli.setMaxStatesVisits(50);
			poli.setMaxVisitDepth(92);
			
			poli.setDeltaForRangeCalculation(0.95);

			MyP2 p2 = new MyP2(ta, 2, poli);

			p2.merge(nL,nR);

			printExit(nL);
			printEnter(ta, nL);
			
			List<Error> taE = validateAutomata(ta);
			
			System.out.println("TA invalid : "+(taE.size() > 0));
		}
		
		File f = new File(args[0]+".merged");
		Main.saveAutomata(ta, f );
		
		TimedAutomata taf = Main.loadAutomata(f.getAbsolutePath());
		Node nL = taf.getNodeWithID(3);
		Node nR = taf.getNodeWithID(243);
		System.out.println("Nodes of merged file");
		printNodes(taf, nL, nR);
	
		
		Node nLM = taMerged.getNodeWithID(3);
		Node nRM = taMerged.getNodeWithID(243);
		System.out.println("Nodes of server file");
		printNodes(taMerged, nLM, nRM);
		
		
		for ( Node n : taMerged.getNodes() ){
			if ( n.visits > 0 ){
				throw new IllegalStateException("Counter above");
			}
		}
		
		
		List<Error> taE = validateAutomata(ta);
		
		List<Error> tafE = validateAutomata(taf);
		
		List<Error> tamE = validateAutomata(taMerged);
		
		System.out.println("TA invalid : "+(taE.size() > 0));
		
		System.out.println("TAf invalid : "+(tafE.size() > 0));
		
		System.out.println("TAm invalid : "+(tamE.size() > 0));
		
		//TestUtils.checkInternalConsistency(taMerged);
		
		//TestUtils.checkSame("", taf, taMerged, false);
		//WrongTerminatingEvent e = (WrongTerminatingEvent) tafE.iterator().next();
		//e.getTraversedTrace();
		
		
//		Validation v = new Validation(ta); 
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
