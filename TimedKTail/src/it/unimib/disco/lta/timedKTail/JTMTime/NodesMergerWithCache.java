package it.unimib.disco.lta.timedKTail.JTMTime;

import java.util.Collection;

import org.apache.commons.collections15.multimap.MultiHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NodesMergerWithCache extends NodesMerger {
	private MultiHashMap<Node, Transition> incomingTransitionsCache;

	public NodesMergerWithCache( TimedAutomata ta, MultiHashMap<Node, Transition> incomingTransitionsCache ){
		super(ta);
		this.incomingTransitionsCache = incomingTransitionsCache;
	}
	
    
    private static final Logger logger = LogManager.getLogger(NodesMergerWithCache.class);

    
    @Override
    protected void modifyTransitionsToNode(Node nResta, Node nElimina){
    	Collection<Transition> ts = incomingTransitionsCache.get(nElimina);
    	
    	
    	
    	if ( ts == null ){
    		if ( nElimina.isInitialState() ){
    			logger.debug("Not removing transitions to initial node :"+nElimina);
    			//Initial state can have no incoming transitions
    			return;
    		}
    		
    		throw new IllegalStateException("Node does not have any incoming transition: "+nElimina);
    	}
    	
        for(Transition t : ts) {
        	if ( logger.isDebugEnabled() ){
        		logger.info("Modifying transition "+t);
        		logger.info("Setting node to "+nResta);
        	}
            incomingTransitions.add(t);
            t.setNodeTo(nResta);               
        }
    }

	
    @Override
	public void commit() {
		Collection<Transition> incomingToDelete = incomingTransitionsCache.get(nDelete);
		incomingTransitionsCache.putAll(nKeep, incomingToDelete);
		incomingTransitionsCache.remove(nDelete);

	}
	
    public static void updateInitialIncomingTransitionsCache( TimedAutomata ta, MultiHashMap<Node, Transition> transitionsCache ){
		for ( Transition t : ta.getTransitions() ){
			transitionsCache.put(t.getNodeTo(), t);
		}
    }
    
	public static MultiHashMap<Node, Transition> buildInitialIncomingTransitionsCache( TimedAutomata ta, boolean incremental ){
		MultiHashMap<Node, Transition> transitionsCache = new MultiHashMap<>();
		for ( Transition t : ta.getTransitions() ){
			transitionsCache.put(t.getNodeTo(), t);
		}
		
		if ( incremental == false ){
			Node initNode = ta.getNodeInit();
			if ( transitionsCache.containsKey(initNode) ){
				throw new IllegalStateException("Initial node is not expected to have incoming transitions at this stage");
			}
		}
		
		return transitionsCache;
	}
	
}
