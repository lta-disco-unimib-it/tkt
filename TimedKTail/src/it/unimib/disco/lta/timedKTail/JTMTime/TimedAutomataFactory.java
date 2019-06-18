package it.unimib.disco.lta.timedKTail.JTMTime;

public class TimedAutomataFactory {

	private TimedAutomata ta;
	private Node initialState;
	private boolean cacheKFuture;
	private boolean cachePendingCalls;

	
	
	public TimedAutomata getTimedAutomata() {
		return ta;
	}

	public Node getInitialState() {
		return initialState;
	}

	public TimedAutomataFactory( String name, boolean cacheKFuture, boolean cachePendingCalls ){
		this.cacheKFuture = cacheKFuture; //must be first, impact on createNode
		this.cachePendingCalls = cachePendingCalls;
		initialState = createNode(true);
		ta = new TimedAutomata(name, initialState);
	}
	
//	public TimedAutomataFactory( TimedAutomata ta ){
//		this.ta = ta;
//	}

    public TimedAutomataFactory(String name, boolean cacheKFuture) {
		this(name,cacheKFuture,false);
	}

	public Transition newTransition(Node nodeFrom, Node nodeTo, Activity activity, boolean enter){
        Transition t = new Transition(nodeFrom,nodeTo,activity,enter);
        nodeFrom.addTransition(t);
        ta.addTransition(t);
        return t;
    }
    
    public Node newNode(boolean initialState){
        Node n = createNode(initialState);
        
        ta.addNode(n);
        return n;
    }

	public Node createNode(boolean initialState) {
		Node n;
        
		if ( cachePendingCalls ) {
        	n = new NodeWithFutureAndPendingCalls(initialState);
        } else if ( cacheKFuture ){
        	n = new NodeWithFuture(initialState);
        } else {
        	n = new Node(initialState);
        }
		return n;
	}

}
