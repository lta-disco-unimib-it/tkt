/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.JTMTime;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;




/**
 * Classe automa temporizzato
 * @author AleX
 */
public class TimedAutomata implements java.io.Serializable {
    private static final long serialVersionUID = 00000006L;
    private String name;
    
    //ArrayList dei nodi
    private HashSet<Node> nA = new HashSet<>();
    
    //ArrayList delle transizioni
    private HashSet<Transition> tA = new HashSet<>();
    
    //ArrayList dei clock
    private HashSet<Clock> cA = new HashSet<>();
    
    //nodo iniziale dell'automa
    private Node nodeInit;
    
	private int ignoredMerges;
	private int performedMerges;
    private static final Logger logger = LogManager.getLogger(TimedAutomata.class);
    
    public TimedAutomata(String name, Node nodeInit){
        this.name=name;
        nodeInit.setInitialState(true);
        nA.add(nodeInit);
        this.nodeInit=nodeInit;
    }
    
    /**
     * restituisce id dell'automa temporizzato
     * @return identificativo dell'automa
     */
    public String getName(){
        return this.name;
    }
    
    /**
     * restituisce il nodo init(iniziale) dell'automa
     * @return nodo init
     */
    public Node getNodeInit(){
        return this.nodeInit;
    }
    
    /**
     * aggiunge un nodo all'automa
     * @param n nodo da aggiungere
     * @return true o false
     */
    protected boolean addNode(Node n){
        return nA.add(n);
    }
    
    /**
     * restituisce tutti i nodi dell'automa
     * @return arrayList di nodi dell'automa
     */
    public Collection<Node> getNodes(){
        return this.nA;
    }
    
    public Node getNodeWithID(long idNode){
        for(Node n:nA){
            if(n.getId() == idNode){
                return n;
            }
        }
        logger.fatal("Attenzione, ritorno NULL, nodo ID non esiste!");
        return null;
    }
    
    
    public Transition getTransitionID(long idTransition){
        for(Transition t:tA){
            if(t.getId() == idTransition){
                return t;
            }
        }
        logger.fatal("Attenzione, ritorno NULL, transition ID non esiste!");
        return null;
    }

	public void deleteNode(Node n) {
		if ( ! n.getTransitionsExit().isEmpty() ){
			logger.debug("Deleting Ts "+n.getTransitionsExit());
			throw new IllegalArgumentException("Cannot delete a node with outgooing transitions. "+n.getTransitionsExit());
		}
		n.setDeleted(true);
		nA.remove(n);
	}
    
    /**
     * aggiunge transizione all'automa
     * @param t transizione da aggiungere
     * @return true o false
     */
    protected boolean addTransition(Transition t){
        return tA.add(t);
    }

    
    /**
     * Restituisce tutte le transizioni associate all'automa
     * @return ArrayList delle transizioni
     */
    public Collection<Transition> getTransitions(){
        return this.tA;
    }
        
    public boolean deleteTransition(Transition t){
    	t.getNodeFrom().deleteTransition(t);
    	return tA.remove(t);
    }
    
    public void deleteTransitions(Collection<Transition> ts){
    	for ( Transition t : ts ){
    		deleteTransition(t);
    	}
    }
    
    /**
     * aggiunge clock all'automa temporizzato
     * @param c clock 
     * @return true o false
     */
    public boolean addClock(Clock c){
        return cA.add(c);
    }

    

   
   

    
    public void deleteNodes(Collection<Node> ns){
    	for ( Node n : ns ){
    		deleteNode(n);
    	}
    }

	public void setName(String string) {
		name = string;
	}

	public void setIgnoredMerges(int ignoredMerges) {
		this.ignoredMerges = ignoredMerges;
	}

	public int getIgnoredMerges() {
		return ignoredMerges;
	}

	public void setPerformedMerges(int performedMerges) {
		this.performedMerges = performedMerges;
	}

	public int getPerformedMerges() {
		return performedMerges;
	}

	public Node[] getNodesArrayBreadthFirst() {
		Collection<Node> nodesCollection = getNodes();
		Node[] nodes = nodesCollection.toArray(new Node[nodesCollection.size()]);
		return nodes;
	}
	
	public LinkedList<Node> getNodesBreadthFirst() {
		Node node = nodeInit;
		
		LinkedList<Node> nodes = new LinkedList<>();
		
		LinkedList<Node> toProcess = new LinkedList<>();
		toProcess.add(node);
		
		while ( ! toProcess.isEmpty() ){
			Node n = toProcess.removeFirst();
			nodes.add(n);
			
			for ( Transition t : n.getTransitionsExit() ){
				toProcess.add(t.getNodeTo());
			}
		}
		
		return nodes;
	}
	


	/**
	 * Return an array of nodes with the initial node at position 0
	 * @return
	 */
	public Node[] getNodesArray() {

		Collection<Node> nodesCollection = getNodes();
		Node[] nodes = nodesCollection.toArray(new Node[nodesCollection.size()]);
		
		if ( nodes[0] == nodeInit ){
			return nodes;
		}
		
		for ( int i = 0; i < nodes.length; i++ ){
			if ( nodes[i] == nodeInit ){
				nodes[i] = nodes[0];
				nodes[0] = nodeInit;
				break;
			}
		}
		
		return nodes;

	}

	public void setInitialState(Node nKeep) {
		nodeInit = nKeep;
	}

	public HashSet<Transition> getTransitionsToNode(Node node) {
		HashSet<Transition> list = new HashSet<>();
		for ( Transition t : tA ){
			if ( t.getNodeTo() == node ){
				if ( ! list.add(t) ){
					throw new IllegalStateException("A same transition appears multiple times in list");
				}
			}
		}
		return list;
	}

	public void deleteTransitionsFromNode(Node n, Collection<Transition> toDelete) {
		n.deleteTransitions(toDelete);
		tA.removeAll(toDelete);
	}

	public boolean containsNode(Node node) {
		return nA.contains(node);
	}

	

	
	
}
