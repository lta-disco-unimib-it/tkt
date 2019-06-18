package it.unimib.disco.lta.timedKTail.JTMTime;

import java.util.HashMap;

import java.util.Iterator;
import java.util.LinkedList;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unimib.disco.lta.timedKTail.algorithm.PhaseII;


public class NodesMerger {

	
	protected TimedAutomata tA;
	
	protected HashMap<Transition, Node> reachingStateOfModifiedTransitions = new HashMap<Transition,Node>();

	protected boolean nKeepIsFinal;
	protected boolean nKeepIsInitial;
	protected boolean nDeleteIsFinal;
	protected boolean nDeleteIsInitial;

	//transitions that go into nDelete, or go out from nDelete
	protected LinkedList<Transition> incomingTransitions = new LinkedList<Transition>();
	protected LinkedList<Transition> outgoingTransitions = new LinkedList<Transition>();
	
	protected Node nKeep;
	protected Node nDelete;

	private boolean done;
	
	
	

	public NodesMerger( TimedAutomata ta ){
		this.tA = ta;
	}
	
    //fonde due nodi
    public void mergeNodes(Node nResta, Node nEliminare){
    	if ( nResta == nEliminare ){
    		return;
    	}
    	
    	nKeep = nResta;
    	nDelete = nEliminare;
    	nKeepIsFinal = nResta.isFinalState();
    	nKeepIsInitial = nResta.isInitialState();
    	
    	nDeleteIsFinal = nEliminare.isFinalState();
    	nDeleteIsInitial = nEliminare.isInitialState();
    	
    	if ( nEliminare.isFinalState() ){
    		nResta.setFinalState(true);
    	}
    	
    	if ( nEliminare.isInitialState() ){
    		tA.setInitialState( nKeep );
    		nKeep.setInitialState(true);
    	}
    	
    	
    	outgoingTransitions.addAll(nEliminare.getTransitionsExit());
    	
        for(Transition t:outgoingTransitions){
             modifyTransitionFromNode(nResta,nEliminare,t);
        }
        
        
        //sposto gli archi entranti di n2 a n1
        modifyTransitionsToNode(nResta,nEliminare);
        
        tA.deleteNode(nEliminare);
        
        done = true;
        
        return;
    }
    
    private static final Logger logger = LogManager.getLogger(PhaseII.class);
    
    //Dato nodo nResta, imposta sulla transizione il suo nuovo nodoFrom e aggiunge
    //all'array dei nodi uscenti la transizione t
    public void modifyTransitionFromNode(Node nResta,Node nEliminare, Transition t){
    	//System.out.println("!!! MODIFY TRANSITION: "+nResta+" "+nEliminare+" "+t);
        t.setNodeFrom(nResta);
        nResta.addTransition(t);
        nEliminare.deleteTransition(t);
        
        if ( logger.isDebugEnabled() ){
        	logger.debug("Deleting Transition "+t+" from node "+nEliminare);
        }
    }
    
    protected void modifyTransitionsToNode(Node nResta, Node nElimina){
    	Iterator<Transition> it = tA.getTransitions().iterator();
        while(it.hasNext()) {
            Transition t = it.next();
            //Trovato un arco entrante in n2 e lo sposto a n1
            if(t.getNodeTo() == nElimina ){
            	incomingTransitions.add(t);
                t.setNodeTo(nResta);               
            }  
        }
    }

	public void rollback() {
		if ( ! done ){
			return;
		}
		
		if( nDeleteIsInitial ){
			tA.setInitialState(nDelete);
			nKeep.setInitialState(false);
		}
		
		nKeep.setFinalState(nKeepIsFinal);
		nKeep.setInitialState(nKeepIsInitial);
		
		nDelete.setFinalState(nDeleteIsFinal);
		nDelete.setInitialState(nDeleteIsInitial);
		
		nDelete.setDeleted(false);
		tA.addNode(nDelete);
		
		
		
		//Proceeding backward is faster (transitions were inserted in opposite order in teh node)
		Iterator<Transition> backIt = outgoingTransitions.descendingIterator();
		while ( backIt.hasNext() ){
			Transition t = backIt.next();
			nKeep.deleteTransition(t);
			t.setNodeFrom(nDelete);
			nDelete.addTransition(t);
		}
		
		for ( Transition t : incomingTransitions ){
			t.setNodeTo(nDelete);
		}
	}

	public void commit() {
		
	}
	
}
