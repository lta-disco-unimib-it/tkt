/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.algorithm;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections15.multimap.MultiHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

import it.unimib.disco.lta.timedKTail.JTMTime.Clause;
import it.unimib.disco.lta.timedKTail.JTMTime.Clock;
import it.unimib.disco.lta.timedKTail.JTMTime.Guard;
import it.unimib.disco.lta.timedKTail.JTMTime.Node;
import it.unimib.disco.lta.timedKTail.JTMTime.NodeWithFuture;
import it.unimib.disco.lta.timedKTail.JTMTime.NodeWithFutureAndPendingCalls;
import it.unimib.disco.lta.timedKTail.JTMTime.NodesMerger;
import it.unimib.disco.lta.timedKTail.JTMTime.NodesMergerWithCache;
import it.unimib.disco.lta.timedKTail.JTMTime.Reset;
import it.unimib.disco.lta.timedKTail.JTMTime.TimedAutomata;
import it.unimib.disco.lta.timedKTail.JTMTime.Transition;
import it.unimib.disco.lta.timedKTail.tests.utils.TestUtils;
import it.unimib.disco.lta.timedKTail.ui.Main;
import it.unimib.disco.lta.timedKTail.validation.NestingValidator;
import it.unimib.disco.lta.timedKTail.validation.NestingValidator.Error;
import sun.security.x509.NetscapeCertTypeExtension;
/**
 *
 * @author AleX
 */
public class PhaseII {

	private TimedAutomata ta;
	private final int k;


	//private ArrayList<Pair<Clock,Clock>> fondiClock;
	private Policy poli;
	private boolean verifyModelAfterStateMerge;
	private int maxStateVisits;
	private int maxVisitDepth;
	private boolean performAdditionalChecks = Boolean.getBoolean("PhaseII.additionalChecks");
	private int ignoredMerges;
	private int performedMerges;
	private boolean storeAllAutomata = false;
	private static final Logger logger = LogManager.getLogger(PhaseII.class);
	private static final boolean ALL_MUST_BE_SAME = false;

	public PhaseII(TimedAutomata ta, int k,Policy poli){
		this.ta=ta;
		this.k=k;

		this.poli=poli;
		this.verifyModelAfterStateMerge = poli.isVerifyAfterMerging();
		//fondiClock = new ArrayList();

		maxStateVisits = poli.getMaxStatesVisits();
		maxVisitDepth = poli.getMaxVisitDepth();

		verifyMergeUsingPendingCalls = poli.isVerifyAfterMerging() && poli.isCachePendingCalls();

		logger.info("Verify after merge: "+verifyModelAfterStateMerge);
		logger.info("Max state visists: "+maxStateVisits);
		logger.info("Max visist depth: "+maxVisitDepth);
	}

	public TimedAutomata resolve(){

		NestingValidator.checkAutomata(ta);

		performStateMerging();

		//Main.saveAutomata(ta, "Merge_TransitionsDone_"+ ta.getName());

		logger.debug("Fondo clock uguali");
		//Fondo i clock uguali
		mergeClocks();



		verifyAfterMergeOperation();

		//Main.saveAutomata(ta, "Merge_ClocksDone_"+ ta.getName());

		logger.debug("applico politiche");
		//Applico politiche di inferenza K-Tail
		applyPolicies();



		verifyAfterMergeOperation();

		//Main.saveAutomata(ta, "Merge_PoliciesDone_"+ ta.getName());

		return ta;
	}

	public void performStateMerging() {
		logger.debug("Initial automata. States: "+ta.getNodes().size()+" Transitions:"+ta.getTransitions().size());

		if( poli.isUseCaching() ){
			buildKFutures();

			if ( poli.isCachePendingCalls() ){
				PendingCallsBuilder pcb = new PendingCallsBuilder();
				pcb.buildPendingCalls(ta);
			}
		}

		logger.debug("Merge states with same k-future");
		//Effettuo la fusione dei k-futuri  
		mergeNodesWithSameKFuture();

		verifyAfterMergeOperation();

		//logger.debug("Determino transizioni sovrapposte");
		mergeOverlappingTransitions(); // already done during merging




		verifyAfterMergeOperation();
	}



	private MultiHashMap<KFuture, NodeWithFuture> calculatedKFutures = new MultiHashMap<>();
	private MultiHashMap<Node, Transition> incomingTransitionsCache;
	private boolean verifyMergeUsingPendingCalls;
	
	private void buildKFutures() {
		
		NestingValidator.checkAutomata(ta);
		
		Collection<Node> nodes = ta.getNodes();
		
		if ( calculatedKFutures.size() > 0 ){
			
			
			if ( poli.isUseIncrementalMerging() ){
				cleanUpCalculateKFuturesFromDeletedNodes();
			}
			
//			ArrayList newNodes = new ArrayList<>();
//			
//			for ( Node node : nodes ){
//				NodeWithFuture nf = (NodeWithFuture) node;
//				if ( nf.getkFuture() == null ){
//					newNodes.add(nf);
//				}
//			}
//			
//			nodes = newNodes;
			
			updateKFutures();

			return;
		}
		
		populateKFuturesCache(nodes);
	}

	public void cleanUpCalculateKFuturesFromDeletedNodes() {
		for ( KFuture key : calculatedKFutures.keySet() ){
			Collection<NodeWithFuture> values = calculatedKFutures.get(key);
			
			LinkedList<NodeWithFuture> toRemove = new LinkedList<>();
			for ( NodeWithFuture node : values ){
				if ( node.isDeleted() ){
					toRemove.add( node );
				}
			}
			
			for ( NodeWithFuture node : toRemove ){
				calculatedKFutures.remove(key, node );
			}
		}
		
		Node init = ta.getNodeInit();
		if ( init.isDeleted() ){
			throw new IllegalStateException("Deleted init");
		}
	}
	
	private void updateKFutures() {
		Node first = ta.getNodeInit();
		
		
		
		
		Node next = first;
		
		Collection<Transition> tss = first.getTransitionsExit();
		
		for ( Transition tt : tss ){
			LinkedList<Node> stack = new LinkedList<Node>();
			LinkedList<Transition> future = new LinkedList<Transition>();
			
			stack.addLast(first);
			
			

			while ( tt != null ){
				
				next = tt.getNodeTo();
				NodeWithFuture nextWF = (NodeWithFuture) next;
				
				if ( nextWF.getkFuture() != null ){
					break;
				}
				
				future.addLast(tt);
				stack.addLast(next);

				
				
				if ( future.size() == k ){
					NodeWithFuture nodeWithFuture = (NodeWithFuture) stack.removeFirst();

					KFuture kfuture = KFutureFactory.INSTANCE.create( future );
					
					
					
					future.removeFirst();
					
					if ( nodeWithFuture.isInitialState() ){
						KFuture currentKF = nodeWithFuture.getkFuture();
						if ( currentKF != null ){
							
							if ( ! ( currentKF instanceof KFutureComposite ) ){
								LinkedList<KFuture> futures = new LinkedList<>();
								futures.add(currentKF);
								currentKF = new KFutureComposite(futures);
							}
							
							KFutureComposite kfc = 	(KFutureComposite) currentKF;
							kfc.add(kfuture);	
							kfuture = currentKF;
						} else {
							LinkedList<KFuture> futures = new LinkedList<>();
							futures.add(kfuture);
							
							kfuture = new KFutureComposite(futures);
							calculatedKFutures.put(kfuture, nodeWithFuture);
							nodeWithFuture.setkFuture(kfuture);
						}
					} else {
						//state is not initial
						calculatedKFutures.put(kfuture, nodeWithFuture);
						nodeWithFuture.setkFuture(kfuture);
					}
					
					
					
				}

				Collection<Transition> ts = next.getTransitionsExit();

				if ( ts.size() == 0 ){
					KFuture kfuture = KFutureFactory.INSTANCE.create( new LinkedList<>() );
					calculatedKFutures.put(kfuture, nextWF);
					nextWF.setkFuture(kfuture);
					tt = null;
				} else {
					tt = ts.iterator().next();
				}
				
				
			}
			
			while ( future.size() > 0 ){
				NodeWithFuture nodeWithFuture = (NodeWithFuture) stack.removeFirst();
				KFuture kfuture = KFutureFactory.INSTANCE.create( future );
				calculatedKFutures.put(kfuture, nodeWithFuture);
				nodeWithFuture.setkFuture(kfuture);
				future.removeFirst();
			}
			
			
		}
		
	}
	

	public void populateKFuturesCache(Collection<Node> nodes) {
		for( Node n : nodes ){

			NodeWithFuture nf = (NodeWithFuture) n; 

			
			if ( nf.getkFuture() != null ){
				continue;
			}
			
			
			LinkedList<Transition> future = new LinkedList<>();
			LinkedList<KFuture> futures = new LinkedList<>();
			buildKFutures( n, future, k, futures );

			KFuture kfuture;
			if ( futures.size() > 1 ){
				kfuture = new KFutureComposite(futures);
			} else {
				kfuture = futures.get(0);
			}
			nf.setkFuture(kfuture);

			calculatedKFutures.put(kfuture, nf);
		}
	}

	private void buildKFutures(Node n, LinkedList<Transition> future, int k, LinkedList<KFuture> futures) {
		if ( k == 0 ){
			futures.add(KFutureFactory.INSTANCE.create( future ) );
			return;
		}

		Collection<Transition> ts = n.getTransitionsExit();
		if ( ts.isEmpty() ){
			futures.add(KFutureFactory.INSTANCE.create( future ) );
			return;
		}

		for ( Transition t : ts ){
			future.addLast(t);
			buildKFutures(t.getNodeTo(), future, k-1, futures);
			future.removeLast();
		}
	}

	public void printAutomataClauses() {
		System.out.println("Clauses of LZW.ini:");
		for ( Transition t : ta.getTransitions() ){
			if ( t.getActivity().getName().equals("LZW.ini") && t.isEnd() ){
				for ( Clause c : t.getGuard().getClauses() ){
					System.out.println(c);
				}
			}
		}
	}



	public List<List<Node>> getLayersFromStartNode() {
		Node nodeInit = ta.getNodeInit();

		Collection<Transition> traces = nodeInit.getTransitionsExit();
		ArrayList<LinkedList<Transition>> backwardTraces = new ArrayList<>(traces.size());
		for ( Transition t : traces ){
			LinkedList<Transition> backwardTrace = buildBackwardTrace( t );
			backwardTraces.add(backwardTrace);
		}





		List<List<Node>> allLevels = new ArrayList<>();

		boolean end = false;
		while ( ! end ){
			end=true;

			LinkedList<Node> level = new LinkedList<>();
			allLevels.add(level);

			for ( LinkedList<Transition> backwardTrace : backwardTraces ){
				if ( ! backwardTrace.isEmpty() ){
					end=false;
					Transition t = backwardTrace.pop();
					level.add(t.getNodeTo());	
				}
			}
		}

		LinkedList<Node> level = new LinkedList<>();
		allLevels.add(level);
		level.add( nodeInit );
		return allLevels;
	}

	public List<List<Node>> getLayersFromEnd() {
		Node nodeInit = ta.getNodeInit();

		Collection<Transition> traces = nodeInit.getTransitionsExit();
		ArrayList<LinkedList<Transition>> backwardTraces = new ArrayList<>(traces.size());
		for ( Transition t : traces ){
			LinkedList<Transition> backwardTrace = buildBackwardTrace( t );
			backwardTraces.add(backwardTrace);
		}





		List<List<Node>> allLevels = new ArrayList<>();

		boolean end = false;
		while ( ! end ){
			end=true;

			LinkedList<Node> level = new LinkedList<>();
			allLevels.add(level);

			for ( LinkedList<Transition> backwardTrace : backwardTraces ){
				if ( ! backwardTrace.isEmpty() ){
					end=false;
					Transition t = backwardTrace.pop();
					level.add(t.getNodeTo());	
				}
			}
		}

		LinkedList<Node> level = new LinkedList<>();
		allLevels.add(level);
		level.add( nodeInit );
		return allLevels;
	}

	private LinkedList<Transition> buildForwardTrace(Transition t) {
		LinkedList<Transition> fwdTrace = new LinkedList<>();
		while ( t != null ){
			fwdTrace.add(t);

			Node nextNode = t.getNodeTo();
			if ( nextNode == null ){
				break;
			}

			t = nextNode.getLastTransitionAdded();
		}

		return fwdTrace;
	}



	private LinkedList<Transition> buildBackwardTrace(Transition t) {
		LinkedList<Transition> backwardTrace = new LinkedList<>();
		while ( t != null ){
			backwardTrace.addLast(t);

			Node nextNode = t.getNodeTo();
			if ( nextNode == null ){
				break;
			}

			Collection<Transition> tts = nextNode.getTransitionsExit();
			if ( tts.size() > 0 ){
				t = nextNode.getLastTransitionAdded();
			} else {
				t=null;
			}
		}

		return backwardTrace;
	}



	public void verifyAfterMergeOperation() {
		if ( ! performAdditionalChecks ){
			return;
		}

		if ( verifyAutomata() == false ){
			throw new IllegalStateException("Unexpected invalid automata");
		}
	}

	protected void mergeClocks(){
		//ciclo tutte le transizioni e genero 
		//la struttura dati che contiene tutti i relativeClock dell'automa
		for(Transition t:ta.getTransitions()){

			Set<Clock> clocksToMerge = new HashSet<>();

			//Replace duplicated clocks
			Guard g=t.getGuard();
			for(Clause c:g.getClauses()){
				Clock ck = c.getClock();
				if ( ck.isRelativeClock() ){				
					clocksToMerge.add(ck);
				}
			}

			if ( clocksToMerge.size() <= 1 ){
				continue; //just one clock
			}


			for(Transition t1 : ta.getTransitions() ){
				Set<Clock> toMergeSubset = new HashSet<>();
				for ( Clock clock : clocksToMerge ){
					if ( t1.hasClockOnReset(clock) ){
						toMergeSubset.add(clock);
					}
				}


				if ( ! toMergeSubset.isEmpty() ){
					mergeClocks( t, t1, toMergeSubset );
					clocksToMerge.removeAll(toMergeSubset);
				}
			}
		}

		if ( storeAllAutomata ){
			Main.saveAutomata(ta, "Merge_Clocks_"+ ta.getName());
		}
	}


	/**
	 * Merge together clauses that work on the given set of clocks
	 * 
	 * Assumptions: 
	 * (1) all the clocks in the set appear in the reset of the transition tWithResets.
	 * (2) all the clocks in the set appear in the reset of the transition tWithGuards.
	 * 
	 * @param tWithGuards
	 * @param tWithResets
	 * @param toMergeSubset
	 */
	private void mergeClocks(Transition tWithGuards, Transition tWithResets, Set<Clock> toMergeSubset) {
		if ( toMergeSubset.size() == 1 ){
			return;
		}

		Iterator<Clock> mIt = toMergeSubset.iterator();

		Clock toKeep = mIt.next();

		for ( Clause c  :  tWithGuards.getGuard().getClauses() ){
			if ( toMergeSubset.contains( c.getClock()) ){
				c.setClock(toKeep);
			}
		}

		Set<Reset> toRemove = new HashSet<>();
		for ( Reset r : tWithResets.getResets() ){
			if ( toMergeSubset.contains(r.getClock()) ){
				if ( r.getClock() != toKeep ){
					toRemove.add(r);
				}
			}
		}

		for( Reset r : toRemove ){
			tWithResets.deleteReset(r);
		}
	}

	private void fusionClockSimple(){
		//ciclo tutte le transizioni e genero 
		//la struttura dati che contiene tutti i relativeClock dell'automa
		for(Transition t:ta.getTransitions()){

			Set<Clock> clocksToMerge = new HashSet<>();

			//Find potentially duplicated clocks
			Guard g=t.getGuard();
			for(Clause c:g.getClauses()){
				Clock ck = c.getClock();
				if ( ck.isRelativeClock() ){
					clocksToMerge.add( ck );
				}
			}

			while ( clocksToMerge.size() > 1 ){
				Clock toKeep = clocksToMerge.iterator().next();
				clocksToMerge.remove(toKeep);


				//data la struttura su t giro automa per trovare un t1 che possiede le caratteristiche
				//desiderate
				for(Transition t1 : ta.getTransitions() ){
					if ( ! t1.hasClockOnReset(toKeep) ){
						continue;
					}

					Iterator<Reset> it = t1.getResets().iterator();
					while ( it.hasNext() ){
						Reset r = it.next();
						Clock toReplace = r.getClock();
						if ( clocksToMerge.remove(toReplace) ){ //there is just one reset per clock, if true t1 resets both "toKeep" and "toReplace"
							it.remove();

							//find the clause that has a guard with toReplace
							for(Clause c:g.getClauses()){
								Clock ck = c.getClock();
								if ( ck ==  toReplace ){
									c.setClock(toKeep);
								}
							}
						}	
					}


				}
			}

			//Fabrizio-fix: no longer needed since 15/03/2016
			//combinaClockAssoluti(t); //FONDE I CLOCK ASSOLUTI
		}
	}

	protected void applyPolicies(){
		Iterator<Transition> it = ta.getTransitions().iterator();

		while(it.hasNext()){
			Transition t = it.next();
			poli.applyPolicy(t.getGuard());
		}

		if ( storeAllAutomata ){
			Main.saveAutomata(ta, "Applied_Policies_"+ ta.getName());
		}
	}


	private void mergeNodesWithSameKFutureFollowingLayersBackward(){
		if ( poli.isUseIncrementalMerging() ){
			throw new IllegalStateException("BACKWARD_LAYER and isUseIncrementalMerging fail together");
		}
		List<List<Node>> layers = getLayersFromEnd();

		//first try to merge nodes belonging to the same layer
		for ( List<Node> layer : layers ){
			Node[] nodesArray = layer.toArray(new Node[layer.size()]);
			mergeNodesWithSameKFuture(nodesArray);
		}


		//the merge nodes of different layers
		for (  int curLayer = 0, size=layers.size(); curLayer < size; curLayer++ ){
			List<Node> layer = layers.get(curLayer);

			for ( Node ni : layer ){

				if ( ni.isDeleted() ){
					continue;
				}

				//compare with next layers
				for ( int fLayerC = curLayer+1; fLayerC < size; fLayerC++  ){
					List<Node> flayer = layers.get(fLayerC);
					for ( Node nj : flayer ){
						mergeNodesIfSameKFuture(ni, nj);
					}
				}
			}
		}

	}


	//cerco i kFuturi uguali
	private void mergeNodesWithSameKFuture(){

		if ( poli.isUseCaching() ){
			mergeNodesWithSameKFutureUsingCachedData();	
		} else {
			mergeNodesWithSameKFutureUsingVisit();
		}

		if ( performAdditionalChecks || storeAllAutomata ){
			Main.saveAutomata(ta, "Merge_DONE_"+ ta.getName());
		}
	}

	private void mergeNodesWithSameKFutureUsingCachedData() {
		KFutureComposite compositeKFutureForInitState = null;

		NestingValidator.checkAutomata(ta);
		
		for ( Entry<KFuture, Collection<NodeWithFuture>> entry : calculatedKFutures.entrySet() ){
			KFuture kFuture = entry.getKey();

			if ( kFuture instanceof KFutureComposite ){
				if ( compositeKFutureForInitState != null ){
					throw new IllegalStateException("Expecting a single composite KFuture");
				}
				compositeKFutureForInitState = (KFutureComposite) kFuture;
			}

			Collection<NodeWithFuture> nodes = entry.getValue();
			Node[] nodesArray = nodes.toArray(new Node[nodes.size()] ); 

			checkNodes(nodes);
			
			mergeNodesInArray(nodesArray);
		}

		if ( compositeKFutureForInitState != null ){

			Collection<NodeWithFuture> states = calculatedKFutures.get(compositeKFutureForInitState);
			if ( states.size() != 1 ){
				throw new IllegalStateException("Expecting a single initial state");
			}
			NodeWithFuture initState = states.iterator().next();

			if ( initState != ta.getNodeInit() ){
				throw new IllegalStateException("TA initial state is not the state with multiple Kfutures");
			}

			Node[] allNodes = ta.getNodesArray();
			for ( int i = 0; i < allNodes.length; i++ ){
				Node toMerge = allNodes[i];

				if ( toMerge == initState ){
					continue;
				}

				mergeNodesIfSameKFuture(initState, toMerge);
			}
		}
		
		

	}

	public void checkNodes(Collection<NodeWithFuture> nodes) {
		for ( Node node : nodes ){
			if ( ! ta.containsNode( node ) ){
				throw new IllegalStateException();
			}
			if ( node.isDeleted() ){
				throw new IllegalStateException();
			}
		}
	}


	public void mergeNodesInArray(Node[] nodes) {
		if ( verifyMergeUsingPendingCalls == false && verifyModelAfterStateMerge == false ){
			mergeNodesNoCheck(nodes);
			return;
		}
		
		int c=0;

		int missing = nodes.length;

		for(int i=0, l=nodes.length; missing > 0 &&  i<l;i++){
			Node ni = nodes[i];

			if ( ni == null ){
				continue;
			}

			for(int j=i+1; missing > 0 && j<l;j++){
				c++;			
				Node nj = nodes[j];

				if ( nj == null ){
					continue;
				}

				boolean merged = mergeNodes(ni, nj);
				if ( merged ){
					nodes[j] = null;
					missing--;
				}
			}
		} 
		ta.setIgnoredMerges( ignoredMerges );
		ta.setPerformedMerges( performedMerges );
	}

	public void mergeNodesWithSameKFutureUsingVisit() {
		Node[] nodes;

		switch ( poli.getMergeStrategy() ){
		case DEPTH_FIRST:
			nodes = ta.getNodesArray();
			mergeNodesWithSameKFuture(nodes);
			break;
		case BREADTH_FIRST:
			nodes = ta.getNodesArrayBreadthFirst();
			mergeNodesWithSameKFuture(nodes);
			break;
		case LAYERS_BACKWARD:
			mergeNodesWithSameKFutureFollowingLayersBackward();
			break;
		}
	}

	public void mergeNodesNoCheck(Node[] nodes) {
		if ( nodes.length <= 1 ){
			return;
		}

		Node n = nodes[0];
		for ( int j = 1; j < nodes.length; j++ ){
			Node n1 = nodes[j];
			
			
			NodesMerger merger = buildNodesMerger();
			merger.mergeNodes(n, n1);
			merger.commit();
		}
		
		int performed = ta.getPerformedMerges();
		ta.setPerformedMerges(performed+nodes.length-1);

	}

	public void mergeNodesWithSameKFuture(Node[] nodes) {
		int c=0;



		for(int i=0, l=nodes.length;i<l;i++){
			Node ni = nodes[i];

			if ( ni == null ){
				continue;
			}

			for(int j=i+1;j<l;j++){
				c++;			
				Node nj = nodes[j];

				if ( nj == null ){
					continue;
				}

				boolean merged = mergeNodesIfSameKFuture(ni, nj);
				if ( merged ){
					nodes[j] = null;
				}

				//saveAndCheckAfterKMergeIteration(c);

			}
		} 
		ta.setIgnoredMerges( ignoredMerges );
		ta.setPerformedMerges( performedMerges );
	}

	public boolean mergeNodesIfSameKFuture(Node ni, Node nj) {


		if ( ni.isDeleted() ){
			return false;
		}

		if ( nj.isDeleted() ){
			return false;
		}


		if (sameKFuture(ni,nj)){
			boolean validMerge = mergeNodes(ni, nj);

			checkAutomataCorrectnessIfNeeded();

			return validMerge;
		}

		return false;
	}





	private void saveAndCheckAfterKMergeIteration(int c) {
		if ( !performAdditionalChecks && !storeAllAutomata ){
			return;
		}

		if ( !performAdditionalChecks ){
			return;
		}


		String incrementalAutomata = "Merge_"+c+"_"+ ta.getName();
		Main.saveAutomata(ta, incrementalAutomata );


		if ( performAdditionalChecks == false ){
			return;
		}



		TimedAutomata taL = Main.loadAutomata(incrementalAutomata+"KTail.jtm");
		boolean validAfterFileLoading = verifyAutomata(taL);
		if ( ! validAfterFileLoading ){
			throw new IllegalStateException("Unexpected inconsistency between saved automata and memory one. Automata: "+incrementalAutomata);
		}
	}

	public void checkAutomataCorrectnessIfNeeded() {
		if ( performAdditionalChecks ){
			boolean valid = verifyAutomata(); 
			if (!valid){
				logger.error("Checked automata correctness: INAVLID");
				throw new IllegalStateException("Invalid automata");
			}

			for ( Node n : ta.getNodes() ){
				if ( n.visits > 0 ){
					throw new IllegalStateException("Counter of number of visits above zero");
				}
			}


			logger.debug("Checked automata correctness: VALID");
		}
	}

	private boolean sameKFuture(Node ni, Node nj){

		if ( ni instanceof NodeWithFuture ){
			if ( nj instanceof NodeWithFuture ){
				NodeWithFuture nif = (NodeWithFuture) ni;
				NodeWithFuture njf = (NodeWithFuture) nj;

				return nif.getkFuture().intersectNotNull(njf.getkFuture());
			}
		}


		return sameKFuture(ni, nj, k);
	}

	private boolean sameKFuture(Node n1, Node n2, int v){
		if ( v == 0 ){
			return true;
		}
		Collection<Transition> n1Ts = n1.getTransitionsExit();
		Collection<Transition> n2Ts = n2.getTransitionsExit();

		if ( n1Ts.isEmpty() && n2Ts.isEmpty() ){
			return true;
		}

		if ( n1.isInitialState() || n2.isInitialState() ){
			boolean same = true;
			for ( Transition t1 : n1Ts ){
				for ( Transition t2 : n2Ts ){
					if ( t1.sameActivityAndType(t2) ){
						if ( ALL_MUST_BE_SAME ){
							if ( ! sameKFuture( t1.getNodeTo(), t2.getNodeTo(), v-1 ) ){
								return false;
							}	
						} else {
							if ( sameKFuture( t1.getNodeTo(), t2.getNodeTo(), v-1 ) ){
								return true;
							}
						}
					} else {
						same = false;
					}
				}	
			}
			if ( ALL_MUST_BE_SAME ){
				return same;
			} else {
				return false;
			}
		} else {
			for ( Transition t1 : n1Ts ){
				for ( Transition t2 : n2Ts ){
					if ( t1.sameActivityAndType(t2) ){
						return sameKFuture( t1.getNodeTo(), t2.getNodeTo(), v-1 );
					}
				}	
			}
		}

		return false;
	}

	protected boolean mergeNodes(Node n, Node n1){
		if ( logger.isDebugEnabled() ){
			String nKF = buildKFutureString(n);
			String n1KF = buildKFutureString(n1);
			logger.debug("Merging "+nKF+" "+n1KF);
		}






		boolean validMerge;


		if ( verifyMergeUsingPendingCalls ){
			validMerge = mergeAndVerifyUsingPendingCalls(n, n1);
		} else {
			validMerge = mergeAndVerfifyWithVisit(n, n1);
		}

		if ( validMerge ){
			performedMerges++;

			if ( logger.isDebugEnabled() ){
				logger.debug("Merge done");
			}
		} else {
			ignoredMerges++;

			if ( logger.isDebugEnabled() ){
				logger.debug("Merge skipped");
			}
		}

		return validMerge;
	}



	public boolean mergeAndVerfifyWithVisit(Node n, Node n1) {

		NodesMerger merger = buildNodesMerger();

		merger.mergeNodes(n, n1);

		boolean validMerge = true;

		if ( verifyModelAfterStateMerge ){
			validMerge=verifyAutomata();


			if ( validMerge == false ){
				merger.rollback();
			} else {
				merger.commit();
			}

		} else {
			merger.commit();
		}

		return validMerge;
	}

	public boolean mergeAndVerifyUsingPendingCalls(Node n, Node n1) {
		boolean validMerge;

		NodesMerger merger = buildNodesMerger();

		MergeVerifierWithPendingCalls mv = new MergeVerifierWithPendingCalls();
		if ( mv.canMerge( (NodeWithFutureAndPendingCalls) n,  (NodeWithFutureAndPendingCalls) n1 ) ){
			merger.mergeNodes(n, n1);		
			mv.mergePendingCalls( (NodeWithFutureAndPendingCalls) n,  (NodeWithFutureAndPendingCalls) n1 );
			validMerge = true;
		} else {
			validMerge = false;
		}
		return validMerge;
	}

	public NodesMerger buildNodesMerger() {

		if ( ! poli.isUseCaching() ){
			return new NodesMerger(ta);
		}

		if ( incomingTransitionsCache == null ){
			incomingTransitionsCache = NodesMergerWithCache.buildInitialIncomingTransitionsCache(ta,false);
		} else {
			if ( poli.isUseIncrementalMerging() ){
				incomingTransitionsCache = NodesMergerWithCache.buildInitialIncomingTransitionsCache(ta,true);
				//NodesMergerWithCache.updateInitialIncomingTransitionsCache(ta, incomingTransitionsCache);
			}
		}
		return new NodesMergerWithCache(ta, incomingTransitionsCache);
	}



	protected boolean verifyAutomata() {
		TimedAutomata automata = ta;

		return verifyAutomata(automata);
	}

	public boolean verifyAutomata(TimedAutomata automata) {
		NestingValidator v = new NestingValidator();
		v.setMaxStateVisits(maxStateVisits);
		v.setMaxVisitDepth(maxVisitDepth);
		v.setStopOnFirstError(true);


		List<Error> errors = v.validate(automata);

		if ( errors.size() > 0 ){
			logger.debug("Invalid automata!");
			return false;
		}

		if ( logger.isDebugEnabled() ){
			logger.debug("Paths visited in validation: "+v.getVisitedPaths());
		}

		return true;
	}

	public String buildKFutureString(Node n) {
		String nKF = "("+n.getId()+") ";

		
		Transition[] transitions = n.getArrayTransitionsExit();
		
		if ( transitions.length > 0 ){
			Transition t = transitions[0];
			nKF += t+";";
			if ( t.getNodeTo().getTransitionsExit().size() > 0){
				Node nto = t.getNodeTo();
				nKF += nto.getLastTransitionAdded();
			}
		}

		return nKF;
	}

	protected void mergeOverlappingTransitions(){
		for ( Node n : ta.getNodes() ){
			mergeOverlappingTransitions(n); 
		}

		if ( storeAllAutomata ){
			Main.saveAutomata(ta, "Merge_Overlapping_"+ ta.getName());
		}
	}

	public void mergeOverlappingTransitions(Node n) {
		HashSet<Transition> toDelete = new HashSet<Transition>();

		Transition[] transitions = n.getArrayTransitionsExit();
		for(int i=0, l=transitions.length;i<l-1;i++){
			Transition ti = transitions[i];
			if ( ti == null ){
				continue;
			}


			for(int j=i+1;j<l;j++){

				Transition tj = transitions[j];

				if ( tj == null ){
					continue;
				}

				if ( (transitionsOverlap( ti, tj ) == true) ) {

					Guard g = tj.getGuard();
					for(Clause c: g.getClauses()){
						ti.addClause(c);
					}
					for(Reset r: tj.getResets()){
						ti.addReset(r);
					}
					toDelete.add(tj);
					
					transitions[j] = null;
				}
			}
		}

		ta.deleteTransitionsFromNode(n, toDelete);
		//ta.deleteTransitions(toDelete);
	}


	private boolean transitionsOverlap(Transition t1, Transition t2){
		if ( ! t1.sameActivityAndType( t2 ) ){
			return false;
		}
		if ( (t1.getNodeFrom() == t2.getNodeFrom()) && (t1.getNodeTo() == t2.getNodeTo()) ){
			return true;
		}else{
			return false;
		}
	}



}
