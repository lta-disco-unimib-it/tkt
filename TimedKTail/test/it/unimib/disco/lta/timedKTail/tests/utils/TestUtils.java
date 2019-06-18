package it.unimib.disco.lta.timedKTail.tests.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.multimap.MultiHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unimib.disco.lta.timedKTail.JTMTime.Clause;
import it.unimib.disco.lta.timedKTail.JTMTime.Guard;
import it.unimib.disco.lta.timedKTail.JTMTime.Node;
import it.unimib.disco.lta.timedKTail.JTMTime.Operation;
import it.unimib.disco.lta.timedKTail.JTMTime.TimedAutomata;
import it.unimib.disco.lta.timedKTail.JTMTime.Transition;
import it.unimib.disco.lta.timedKTail.algorithm.Policy;
import it.unimib.disco.lta.timedKTail.algorithm.TimedKTail;
import it.unimib.disco.lta.timedKTail.traces.ObserverTimedAutomataTraceBuilder;
import it.unimib.disco.lta.timedKTail.traces.Parser;
import it.unimib.disco.lta.timedKTail.ui.Main;
import it.unimib.disco.lta.timedKTail.ui.VisualizeAutomata;
import it.unimib.disco.lta.timedKTail.util.JavaRunner;
import it.unimib.disco.lta.timedKTail.validation.Validation.ValidationError;
import it.unimib.disco.lta.timedKTail.validation.Validation.ValidationError.ErrorType;


public class TestUtils {
	private static final Logger logger = LogManager.getLogger(TestUtils.class);
	public static final boolean[] TF = new boolean[]{true,false};
	public static final boolean[] FT = new boolean[]{false,true};

	public static void visualize(TimedAutomata ta) {
		File f = new File( "dest.jtm");
		Main.saveAutomata(ta, f );
		String[] args = new String[]{f.getAbsolutePath()};
		try {
			JavaRunner.runMainInClass(VisualizeAutomata.class,Arrays.asList(args),0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		f.delete();
	}
	
	public static TimedAutomata inferAutomata(int k, Policy poli, String fTrace, boolean useNestedCallsTime) {
		TimedAutomata ta;
		poli.setIncludeNestedCallsTime(useNestedCallsTime);
		
		TimedKTail kt = new TimedKTail(k,poli);
		ObserverTimedAutomataTraceBuilder o = new ObserverTimedAutomataTraceBuilder(2,kt);
		Parser parser = new Parser(o);
		parser.readFile(fTrace);   
		kt.resolve();
		ta=kt.getTimedAutomata();
		return ta;
	}

	public static void checkNotSame(String msg, TimedAutomata expected, TimedAutomata ta, boolean checkGuards) {
		try {
			checkSameInternal(msg,expected, ta, checkGuards);
		} catch ( AssertionError e ){
			return; //not the same
		}
		
		try {
		checkSameInternal(msg,ta, expected, checkGuards);
		} catch ( AssertionError e ){
			return; //not the same
		}
		
		throw new AssertionError(msg+"Isomorphic");
	}
	public static void checkSame(String msg, TimedAutomata expected, TimedAutomata ta, boolean checkGuards) {
		checkSameInternal(msg,expected, ta, checkGuards);
		checkSameInternal(msg,ta, expected, checkGuards);
	}

	public static void checkSameInternal(String msg, TimedAutomata expected, TimedAutomata ta, boolean checkGuards) {
		HashSet<Node> currentPath = new HashSet<Node>();
		HashSet<Node> visited = new HashSet<Node>();
		HashSet<Node> matching = new HashSet<Node>();
		checkSame( ta.getName(), currentPath, visited, matching, expected.getNodeInit(), ta.getNodeInit(), checkGuards );
		if ( ! visited.equals(matching) ){
			logger.debug("Visited: "+visited+" Matching: "+matching);
			visited.removeAll(matching);
			throw new AssertionError(msg+"Not isomorphic, missing: "+visited);
		}
	}

	private static boolean checkSame(String automata, Set<Node> currentPath, Set<Node> visited, Set<Node> matching, Node nodeExpected, Node node, boolean checkGuards) {
		
		
		if ( currentPath.contains(nodeExpected) ){
			matching.add(nodeExpected);
			return true;
		}
		try {
			currentPath.add(nodeExpected);
			visited.add(nodeExpected);

			
			if ( nodeExpected.isFinalState() ){
				if ( ! node.isFinalState() ){
					return false;
				}
			}

			if ( node.isFinalState() ){
				if ( ! nodeExpected.isFinalState() ){
					return false;
				}
			}
			
			
			if ( nodeExpected.getTransitionsExit().size() == 0 && 
					node.getTransitionsExit().size() == 0 ){
				matching.add(nodeExpected);
				return true;
			}

			boolean found = false;

			HashSet<Node> myMatching = new HashSet<Node>();

			for ( Transition te : nodeExpected.getTransitionsExit() ){
				List<Transition> ts = node.getTransitions( te.getActivity(), te.isBegin() );
				found = false;

				for ( Transition t : ts ){
					logger.debug("Current t: "+t);
					boolean guardOk = true;
					if ( checkGuards ){
						if ( ! guardEquals( te.getGuard(),t.getGuard() ) ){
							guardOk = false;
						}
					}

					if ( guardOk ){
						found = checkSame(automata, currentPath, visited, myMatching, te.getNodeTo(), t.getNodeTo(), checkGuards);
					}

					if ( found ){
						break;
					}
				}

				if ( ! found ){
					logger.debug("Not found for "+nodeExpected+" "+te);
					return false;		
				}

			}

			if ( found ){
				matching.addAll(myMatching);
				matching.add(nodeExpected);
			} else {
				logger.debug("Not found for "+nodeExpected);
			}


			return found;
		} finally {
			currentPath.remove(nodeExpected);
		}
	}
	
	public static void checkInternalConsistency(TimedAutomata ta) {
		Set<Node> nodes = new HashSet<Node>();
		Set<Transition> transitions = new HashSet<Transition>();
		
		Node init = ta.getNodeInit();
		if ( ! init.isInitialState() ){
			throw new AssertionError("Initial state not tagged as being initial.");
		}
		
		for ( Node n : ta.getNodes() ){
			if ( n.isInitialState() && n!=init ){
				throw new AssertionError("Too many initial states.");	
			}
		}
		
		
		visit(ta.getNodeInit(), nodes, transitions);

		HashSet<Node> ns = new HashSet<>();
		ns.addAll(ta.getNodes());
		
		HashSet<Transition> ts = new HashSet<>();
		ts.addAll(ta.getTransitions());
		
		{
			boolean finalFound = false;
			for ( Node n : ns ){
				if ( n.isFinalState() ){
					finalFound = true;
				}
			}

			if ( ! finalFound ){
				throw new AssertionError("Missing final state");
			}
		}
		
		boolean finalFound = false;
		for ( Node n : nodes ){
			if ( n.isFinalState() ){
				finalFound = true;
			}
		}
		if ( ! finalFound ){
			throw new AssertionError("Final state not reachable");
		}
		
		
		if ( ns.size() != nodes.size() ){
			HashSet<Node> delta = buildDelta(nodes, ns);
			
			throw new AssertionError("Different size for nodes");
		}
		
		if ( ts.size() != transitions.size() ){
			throw new AssertionError("Different size for transitions");
		}
		
		ns.removeAll(nodes);
		if ( ns.size() != 0 ){
			throw new AssertionError("Different nodes");
		}
		
		ts.removeAll(transitions);
		if ( ts.size() != 0 ){
			throw new AssertionError("Different transitions");
		}
	}

	public static <T> HashSet<T> buildDelta(Collection<T> nodes, Collection<T> ns) {
		HashSet<T> delta = new HashSet<>();
		if ( nodes.size() > ns.size()  ){
			delta.addAll(nodes);
			delta.removeAll(ns);
		} else {
			delta.addAll(ns);
			delta.removeAll(nodes);
		}
		return delta;
	}
	
	public static void visit(Node node, Set<Node> nodes, Set<Transition> transitions) {
		nodes.add(node);
		for ( Transition te : node.getTransitionsExit() ){
			if ( transitions.contains(te) ){
				continue;
			}
			System.out.println(te);
			System.out.println(te.getNodeTo().getId());
			transitions.add(te);
			visit(te.getNodeTo(),nodes,transitions);
		}
	}

	private static boolean guardEquals(Guard guard, Guard guard2) {
		List<Operation> os1 = extractOperations(guard);
		List<Operation> os2 = extractOperations(guard2);
		
		if ( os1.size() != os2.size() ){
			return false;
		}
		
		for ( Operation o1 : os1 ){
			if( ! os2.contains(o1) ){
				return false;
			}
		}
		
		return true;
	}

	public static List<Operation> extractOperations(Guard guard) {
		List<Operation> os1 = new ArrayList<Operation>();
		for ( Clause c1 : guard.getClauses() ){
			os1.add(c1.getOperation());
		}
		return os1;
	}

	public static void checkTransitionsCacheConsistency(MultiHashMap<Node, Transition> incomingTransitionsCache, TimedAutomata tA) {
		for ( Node n : incomingTransitionsCache.keySet() ){
			HashSet<Transition> expectedIncoming = tA.getTransitionsToNode(n);
			
			Collection<Transition> incomingInCache = incomingTransitionsCache.get(n);
			
			if ( incomingInCache.size() != expectedIncoming.size() ){
				HashSet<Transition> delta = buildDelta(incomingInCache, expectedIncoming);
				throw new IllegalStateException("Different sizes");
			}
			
			HashSet<Transition> cacheData = new HashSet<Transition>();
			cacheData.addAll(incomingInCache);
			
			cacheData.removeAll(expectedIncoming);
			if ( cacheData.size() > 0 ){
				throw new IllegalStateException("Missing in automata: "+cacheData);
			}
			
			expectedIncoming.removeAll(incomingInCache);
			if ( expectedIncoming.size() > 0 ){
				throw new IllegalStateException("Missing in cache: "+expectedIncoming);
			}
		}
	}

	public static boolean checkContainErrorType(ErrorType notFinal, List<ValidationError> result) {
		for( ValidationError ve : result ){
			if ( ve.getErrorType() == notFinal ){
				return true;
			}
		}
		return false;
	}

}
