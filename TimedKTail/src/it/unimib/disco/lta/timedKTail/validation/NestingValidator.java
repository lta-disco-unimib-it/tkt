/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.validation;

import it.unimib.disco.lta.timedKTail.JTMTime.*;
import it.unimib.disco.lta.timedKTail.traces.*;
import it.unimib.disco.lta.timedKTail.validation.NestingValidator.ValidationError.ErrorType;
import jdk.nashorn.internal.ir.debug.PrintVisitor;
import sun.util.logging.resources.logging;

import static it.unimib.disco.lta.timedKTail.ui.Main.loadAutomata;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;


public class NestingValidator {
	private static final Logger logger = LogManager.getLogger(NestingValidator.class);
	private String msg;

	//private TimedAutomata timedAutomata;
	


	public static class ValidationError {
		public static enum ErrorType { VIOLATED_GUARD, UNMATCHED_EVENT, MISSING_CLOCK }
		private int line;
		private String msg;
		private ErrorType et;

		public ValidationError( ErrorType et, String msg, int line ){
			this.et = et;
			this.msg = msg;
			this.line = line;
		}

		public int getLine() {
			return line;
		}

		public String getMsg() {
			return msg;
		}

		public ErrorType getErrorType() {
			return et;
		}


	}




	public static void checkAutomata(TimedAutomata timedAutomata){
		if ( logger.isDebugEnabled() ){
			logger.debug("Checking automata correctness");
		}
		
		for ( Node n : timedAutomata.getNodes() ){
			for ( Transition t : n.getTransitionsExit() ){
				if ( t.getNodeFrom() != n ){
					throw new IllegalStateException("Transition: "+t.getActivity().getName()+" Expected Node: "+n.getId()+"Found: "+t.getNodeFrom()+" ArrivalNode: "+t.getNodeTo());
				}
			}
		}
		for ( Transition t : timedAutomata.getTransitions() ){
			Node n = t.getNodeFrom();
			if ( ! n.getTransitionsExit().contains(t)  ){
				throw new IllegalStateException("Transition: "+t.getActivity().getName()+" not in node: "+n.getId()+" From: "+t.getNodeFrom()+" ArrivalNode: "+t.getNodeTo());
			}
		}
		
		if ( logger.isDebugEnabled() ){
			logger.debug("Checking done");
		}
	}



	public static class Error{};
	public static class WrongTerminatingEvent extends Error {
		private Node currentState;
		private Transition tran;
		private LinkedList<Transition> traversedTrace;

		public WrongTerminatingEvent(Node currentState, Transition tran, LinkedList<Transition> traceClock) {
			this.currentState = currentState;
			this.tran = tran;
			this.traversedTrace = traceClock;
		}

		public Node getCurrentState() {
			return currentState;
		}

		public Transition getTransition() {
			return tran;
		}

		public LinkedList<Transition> getTraversedTrace() {
			return traversedTrace;
		}
	};
	public static class EmptyStack extends Error {
		private Node currentState;
		private Transition tran;
		private LinkedList<Transition> traversedTrace;

		public EmptyStack(Node currentState, Transition tran, LinkedList<Transition> traceClock) {
			this.currentState = currentState;
			this.tran = tran;
			this.traversedTrace = traceClock;
		}

		public Node getCurrentState() {
			return currentState;
		}

		public Transition getTransition() {
			return tran;
		}

		public LinkedList<Transition> getTraversedTrace() {
			return traversedTrace;
		}
	};
	

	LinkedList<Transition> stack = new LinkedList<Transition>();
	LinkedList<Transition> popped = new LinkedList<Transition>();

	List<Error> errorsFound = new LinkedList<Error>();

	private int maxVisitDepth = 20;
	private long visitedPaths = 0L;
	private boolean stopOnFirstError;

	private int maxStateVisits = 1;
	private boolean stateVisitsCheckEnabled = true; 

	{		
		updateMsg();
	}


	public void updateMsg() {
		msg = "Verifying automata. MaxVisitDepth:"+maxVisitDepth+" MaxStateVisits:"+maxStateVisits+" StopOnFirstError:"+stopOnFirstError;
	}


	public long getVisitedPaths() {
		return visitedPaths;
	}

	public int getMaxVisitDepth() {
		return maxVisitDepth;
	}

	public int getMaxStateVisits() {
		return maxStateVisits;
	}



	public void setMaxVisitDepth(int maxVisitDepth) {
		this.maxVisitDepth = maxVisitDepth;
		updateMsg();
	}

	public void setMaxStateVisits(int maxStateVisits) {
		this.maxStateVisits = maxStateVisits;
		updateMsg();
	}

	BufferedWriter bw = null;
	public List<Error> validate( TimedAutomata timedAutomata ){
		
		try {
			bw = new BufferedWriter( new FileWriter("paths.txt"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		logger.info(msg);
		
		
		checkAutomata(timedAutomata);

		Node node = timedAutomata.getNodeInit();

		node.visits++;
		
		LinkedList<Transition> traceClock = new LinkedList<>();
		try {
			validate(node, traceClock, 0);
		} catch (ErrorFoundException e){
			//resetNodeVisitsCounter(timedAutomata);
		}
		
		node.visits--;
		
		try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return errorsFound;
	}

	public void resetNodeVisitsCounter(TimedAutomata timedAutomata) {
		for ( Node n : timedAutomata.getNodes() ){
			n.visits=0;
		}
	}

	
	private HashSet<String> paths = new HashSet<>();
	public int validate(Node nodeNavigatore,LinkedList<Transition> traceClock, int depth) throws ErrorFoundException{
		logger.debug("Visiting Node: "+nodeNavigatore+" Depth:"+depth);
		if ( nodeNavigatore.visits < 0 || nodeNavigatore.visits > maxStateVisits ){
			logger.debug("End Visiting Node: "+nodeNavigatore+" Depth:"+depth);
			throw new IllegalStateException("More than maxStateVisits visits per state");
		}
		
		String path = getPathId(traceClock);
//		if ( paths.contains(path) ){
//			throw new IllegalStateException("Path already visited: "+path);
//		}
//		paths.add(path);
		
		if ( logger.isDebugEnabled() ){ logger.debug("Depth: "+depth+" Visiting node "+nodeNavigatore); }
		
		if(depth >= maxVisitDepth){
			if (logger.isDebugEnabled() ){ logger.debug("Max depth visists limit reached ("+maxVisitDepth+")"); logPath(path); }
			
			visitedPaths++;
			
			if ( logger.isDebugEnabled() ){ logger.debug("End Visiting Node: "+nodeNavigatore+" Depth:"+depth); }
			return depth;
		}

		if ( nodeNavigatore.getTransitionsExit().size() == 0 ){
			if (logger.isDebugEnabled() ){ logger.debug("No exit transition"); logPath(path); }
			
			visitedPaths++;
			
			if ( logger.isDebugEnabled() ){ logger.debug("End Visiting Node: "+nodeNavigatore+" Depth:"+depth); }
			return depth;
		}

		Collection<Transition> sortedExitTrans = sortTransitionsIfRequired(nodeNavigatore);
		
		int maxDepth = depth;
		for(Transition tran:sortedExitTrans ){
			
			if ( tran.getNodeFrom() != nodeNavigatore ){
				throw new IllegalStateException("Inconsistent transition");
			}

			

			int innerDepth = 0;
			
			if ( tran.isBegin() ) {

				//logger.info("PUSH "+tran.getActivity());
				stack.push(tran);

				innerDepth = visitNext(traceClock, depth, tran);

				//logger.info("POP ");
				Transition poppedT = stack.pop();

				if ( poppedT != tran ){
					printTrace(traceClock);
					if ( logger.isDebugEnabled() ){ logger.debug("End Visiting Node: "+nodeNavigatore+" Depth:"+depth); }
					throw new IllegalStateException("Should never happen");
				}

			} else {

				if ( stack.isEmpty() ){
					errorsFound.add(new EmptyStack(nodeNavigatore, tran, (LinkedList<Transition>) traceClock.clone()));
					visitedPaths++;
					
					if ( stopOnFirstError ){
						if ( logger.isDebugEnabled() ){ logger.debug("End Visiting Node: "+nodeNavigatore+" Depth:"+depth); }
						throw new ErrorFoundException();
					}
					continue;
				}
				
				Transition last = stack.peek();
				if (! ( last.isBegin() && last.getActivity().equals(tran.getActivity() ) ) ){
					errorsFound.add(new WrongTerminatingEvent(nodeNavigatore, tran, (LinkedList<Transition>) traceClock.clone() ));
					if ( stopOnFirstError ){
						if ( logger.isDebugEnabled() ){ logger.debug("End Visiting Node: "+nodeNavigatore+" Depth:"+depth); }
						throw new ErrorFoundException();
					}
					visitedPaths++;
				} else {
					//logger.info("POP ");
					stack.pop();

					popped.push(last);
					innerDepth = visitNext(traceClock, depth, tran);

					Transition ppd = popped.pop();
					//logger.info("PUSH "+ppd.getActivity());
					stack.push( ppd );
				}
				
				if ( innerDepth > maxDepth ){
					maxDepth = depth;
				}
			}
		}

		if ( logger.isDebugEnabled() ){ logger.debug("End Visiting Node: "+nodeNavigatore+" Depth:"+depth); }
		
		return maxDepth;
	}

	private void logPath(String path) {
		try {
			this.bw.append(String.valueOf(this.hashCode()));
			this.bw.append(" ");
			this.bw.append(path);
			bw.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	public Collection<Transition> sortTransitionsIfRequired(Node nodeNavigatore) {
		Collection<Transition> sortedExitTrans;
		if ( maxStateVisits > 1 ){
			sortedExitTrans = sortWithDifferentFirst(nodeNavigatore);
		} else {
			sortedExitTrans = nodeNavigatore.getTransitionsExit();
		}
		return sortedExitTrans;
	}

	public ArrayList<Transition> sortWithDifferentFirst(Node nodeNavigatore) {
		Collection<Transition> exit = nodeNavigatore.getTransitionsExit();
		ArrayList<Transition> sortedExitTrans = new ArrayList<Transition>(exit.size());
		sortedExitTrans.addAll(exit);
		
		java.util.Collections.sort(sortedExitTrans, new Comparator<Transition>() {

			@Override
			public int compare(Transition o1, Transition o2) {
				return Integer.compare( o1.getNodeTo().visits , o2.getNodeTo().visits ); 
			}
		});
		
		return sortedExitTrans;
	}

	
	HashMap<Node,Set<String>> validPrefixes = new HashMap<Node,Set<String>>();
	public int visitNext(LinkedList<Transition> traceClock, int depth, Transition tran) throws ErrorFoundException {

		if ( logger.isDebugEnabled() ) { logger.debug("Visiting transition: "+transitionString(tran)+" Depth:"+depth); }
		
		Node nextNode = tran.getNodeTo();

		if ( stateVisitsCheckEnabled ){
			if( nextNode.visits >= maxStateVisits ){
				if ( logger.isDebugEnabled() ){ logger.debug("Max state visists limit reached ("+maxStateVisits+")"); }
				
				//printTrace(traceClock);
				return depth;
			}
		}

		
		
		String prefix = getPathHash(traceClock);
		if ( retrieveValidPrefixes(nextNode).contains(prefix) ){
			return depth;
		}
		
		traceClock.add( tran );
		nextNode.visits++;

		String path = getPathId(traceClock);

		try {
			int innerDepth = validate( nextNode , traceClock, depth+1);
			
			retrieveValidPrefixes(nextNode).add(prefix);
			
			return innerDepth;
		} finally {
			nextNode.visits--;
			traceClock.removeLast();
			if ( logger.isDebugEnabled() ){  logger.debug("End visiting transition: "+transitionString(tran)+" Depth:"+depth); }
		}

	}

	public static String sha256(String base) {
	    try{
	        MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        byte[] hash = digest.digest(base.getBytes("UTF-8"));
	        StringBuffer hexString = new StringBuffer();

	        for (int i = 0; i < hash.length; i++) {
	            String hex = Integer.toHexString(0xff & hash[i]);
	            if(hex.length() == 1) hexString.append('0');
	            hexString.append(hex);
	        }

	        return hexString.toString();
	    } catch(Exception ex){
	       throw new RuntimeException(ex);
	    }
	}

	public Set<String> retrieveValidPrefixes(Node nextNode) {
		Set<String> p = validPrefixes.get(nextNode);
		if ( p == null ){
			p = new HashSet<>();
			validPrefixes.put(nextNode, p);
		}
		return p;
	}



	private String getPathId(List<Transition> traceClock){
		
		HashMap<String, Integer> map = new HashMap<String,Integer>();
		boolean error = false;
		
		StringBuffer sb = new StringBuffer();
		for (  Transition t : traceClock ){
			String id = t.getStringId();
			Integer c = map.get(id);
			if ( c == null ){
				map.put(id, 1);
			} else {
				int newVal = c+1;
				
				if ( newVal > maxStateVisits ){
					error = true;
				}
				
				map.put(id, newVal);
			}
			
			sb.append(t.getStringId()+";");
		}
		
		if (error){
			
			String pp = getPrintablePath(traceClock);
			
			IllegalStateException e = new IllegalStateException("Path with transitions traversed more than maxStateVisits: " + sb.toString()+" Path: "+pp+" MaxStateVisists: "+maxStateVisits);
			logger.warn(e.getMessage());
			
			throw e;
		}
		
		return sb.toString();
	}

	private String getPathHash(List<Transition> traceClock) {
		return sha256(getPath(traceClock));
	}
	
	private String getPath(List<Transition> traceClock) {
		StringBuffer sb = new StringBuffer();
		for (  Transition t : traceClock ){
			sb.append( t.getActivity()+":"+t.getType()+";" );
		}
		return sb.toString();
	}
	
	private String getPrintablePath(List<Transition> traceClock) {
		StringBuffer sb = new StringBuffer();
		for (  Transition t : traceClock ){
			sb.append( transitionString(t) );
		}
		return sb.toString();
	}


	public String transitionString(Transition t) {
		return t.getNodeFrom()+"->"+ t.getActivity()+":"+t.getType()+"->"+t.getNodeTo()+";";
	}


	public static void main(String args[]){
		String path = args[0];
		TimedAutomata ta = loadAutomata(path);

		System.out.println("Validating automata, states:"+ta.getNodes().size()+" transitions: "+ta.getTransitions().size()+" ignoreMerges:"+ta.getIgnoredMerges()+" performedMerges:"+ta.getPerformedMerges());
		
		NestingValidator v = new NestingValidator();
		
		String _maxStateVisits = System.getProperty("maxStateVisits");
		if ( _maxStateVisits != null ){
			v.setMaxStateVisits(Integer.valueOf(_maxStateVisits));	
		}
		
		String _maxDepth = System.getProperty("maxVisitDepth");
		if ( _maxDepth != null ){
			v.setMaxVisitDepth(Integer.valueOf(_maxDepth));	
		}
		
		String _stopOnFirst = System.getProperty("stopOnFirstError");
		if ( _stopOnFirst != null ){
			v.setStopOnFirstError(Boolean.valueOf(_stopOnFirst));	
		}
		
		List<Error> errors = v.validate(ta);

		for ( Error error : errors ){
			printError ( error );
		}
		
		if ( errors.size() == 0 ){
			System.out.println("VALID");
		} else {
			System.out.println("INVALID");
			System.exit(1);
		}

	}

	public void setStopOnFirstError(boolean value) {
		this.stopOnFirstError = value;
		updateMsg();
	}

	private static void printError(Error error) {
		if ( error instanceof WrongTerminatingEvent ){
			WrongTerminatingEvent e = (WrongTerminatingEvent) error;
			System.out.println("Unexpected Termination Event: " + e.currentState+" "+e.getTransition().getActivity().getName() );
			printTrace ( e.getTraversedTrace() );
		} else if ( error instanceof EmptyStack ){
			EmptyStack e = (EmptyStack) error;
			System.out.println("Empty Stack: " + e.currentState+" "+e.getTransition().getActivity().getName() );
			printTrace ( e.getTraversedTrace() );
		}
	}

	private static void printTrace(LinkedList<Transition> traversedTrace) {
		for ( Transition t : traversedTrace ){
			System.out.println("\t"+t.getActivity()+" "+t.getType());
		}
	}

	public boolean isStopOnFirstError() {
		return stopOnFirstError;
	}



}
