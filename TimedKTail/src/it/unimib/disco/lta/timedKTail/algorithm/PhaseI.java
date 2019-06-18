/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unimib.disco.lta.timedKTail.JTMTime.Activity;
import it.unimib.disco.lta.timedKTail.JTMTime.Clause;
import it.unimib.disco.lta.timedKTail.JTMTime.Clock;
import it.unimib.disco.lta.timedKTail.JTMTime.ClockFactory;
import it.unimib.disco.lta.timedKTail.JTMTime.Equal;
import it.unimib.disco.lta.timedKTail.JTMTime.Node;
import it.unimib.disco.lta.timedKTail.JTMTime.Reset;
import it.unimib.disco.lta.timedKTail.JTMTime.TimedAutomata;
import it.unimib.disco.lta.timedKTail.JTMTime.TimedAutomataFactory;
import it.unimib.disco.lta.timedKTail.JTMTime.Transition;
import it.unimib.disco.lta.timedKTail.traces.Event;
import it.unimib.disco.lta.timedKTail.traces.Trace;
import it.unimib.disco.lta.timedKTail.validation.Validator;
/**
 * Implementa la prima fase dell'algoritmo K-Tail
 * @author AleX
 */
public class PhaseI {
	private static final Logger logger = LogManager.getLogger(PhaseI.class);
	private TimedAutomataFactory taf;
	private ActivityFactory activityFactory = new ActivityFactory();
	private boolean doMergeInExistingPath = false;
	
	public boolean isDoMergeInExistingPath() {
		return doMergeInExistingPath;
	}



	public void setDoMergeInExistingPath(boolean doMergeInExistingPath) {
		this.doMergeInExistingPath = doMergeInExistingPath;
	}



	public PhaseI( TimedAutomataFactory taf ){
		this.taf = taf;
	}
	
	private static class FunctionData {
		long lastTime;
		long executionTime;
		public Transition transition;
		public long beginTime;
		public Clock relativeClock;
	}

	public void elaborateTrace(Trace t, boolean useCumulativeTime, boolean updateTrace){
		long minTime;

		Node n1 = null;
		Node n2 = null;


		if ( t.getSize() == 0 ){
			return;
		}

		minTime=minTime(t);

		TimedAutomata ta = taf.getTimedAutomata();


		
		//LinkedList<Transition> callStack = new LinkedList<Transition>();
		LinkedList<FunctionData> timeStack = new LinkedList<FunctionData>();

		n1 = ta.getNodeInit();

		Transition lastTrans = null;
		Transition firstTrans = null;
		for(Event e:t.getEvents()){

			if(e.isBegin()){
				n2=taf.newNode(false);
				Clock ck=ClockFactory.getClockAbsolute();
				ta.addClock(ck);

				Clock ckRelative = ClockFactory.getClockRelative();
				Transition trans = taf.newTransition(n1, n2, activityFactory.newActivity(e.getAttivita()), e.isBegin());

				if ( firstTrans == null ){
					firstTrans = trans;
				}

				long time = (e.getTimestamp()-minTime);

				if ( ! timeStack.isEmpty() ){
					FunctionData caller = timeStack.peek(); 
					long executionDelta = time - caller.lastTime; 
					caller.executionTime += executionDelta;

				}



				FunctionData fd = new FunctionData();
				fd.lastTime = time;
				fd.transition = trans;
				fd.beginTime = time;
				fd.relativeClock = ckRelative;
				timeStack.push(fd);



				trans.addClause(new Clause(ck, new Equal(time)  ));
				trans.addReset(Reset.getReset(ckRelative));




				n1=n2;
				
				lastTrans = trans;
			}else{
				n2=taf.newNode(false);


				Clock ck = ClockFactory.getClockAbsolute();
				ta.addClock(ck);



				FunctionData currentFunc = timeStack.pop();
				Transition beginTransition=currentFunc.transition;

				if ( ! beginTransition.getActivity().getName().equals(e.getAttivita() ) ){
					throw new IllegalStateException("Unexpected: "+e.getAttivita()+":E expecting "+beginTransition.getActivity().getName() );
				}








				long time = (e.getTimestamp()-minTime);



				long executionTime = 0;

				if ( useCumulativeTime ){
					executionTime = time-currentFunc.beginTime;
				} else {

					if ( ! timeStack.isEmpty() ){
						FunctionData caller = timeStack.peek(); 
						caller.lastTime = time; //caller execution continues now
					}

					long lastDeltaTime = time-currentFunc.lastTime;

					executionTime = currentFunc.executionTime+lastDeltaTime;

				}

				Transition trans = taf.newTransition(n1, n2,new Activity(e.getAttivita()), e.isBegin());
				trans.addClause(new Clause(ck, new Equal(time) ));
				trans.addClause(new Clause(currentFunc.relativeClock,new Equal(executionTime) ));


				if( updateTrace ){
					e.setExecutionTime( executionTime );
				}

				n1=n2;

				lastTrans = trans;
			}

		}  


		if ( lastTrans != null ){
			lastTrans.getNodeTo().setFinalState(true);
		}
		
		if ( doMergeInExistingPath ){
			mergeInExistingPathIfSameEvents( firstTrans, (int)t.getSize() );
		}
		
	}


	HashMap<Transition, Integer> pathsLengths = new HashMap<Transition,Integer>();
	private boolean mergeInExistingPathIfSameEvents(Transition t, int size) {
		for( Entry<Transition, Integer> entry : pathsLengths.entrySet() ){
			Transition tr = entry.getKey();
			Integer len = entry.getValue();
			
			if ( size != len ){
				continue;
			}
			
			if( sameSequence( t, tr ) ){
				updateClocks( t, tr );
				return true;
			}
		}
		
		pathsLengths.put(t, size);
		return false;
	}



	private void updateClocks(Transition nt, Transition tr) {
		while ( nt != null ){
			for ( Clause c : nt.getGuard().getClauses() ){
				tr.addClause(c);
			}
			
			for ( Reset r : nt.getResets() ){
				tr.addReset(r);
			}
			
			
			nt = nextTransition(nt);
			tr = nextTransition( tr );
			
		}
		
		
	}



	private Transition nextTransition(Transition tr) {
		Collection<Transition> exit = tr.getNodeTo().getTransitionsExit();
		
		if ( exit.size() == 0 ){
			return null;
		}
		
		if ( exit.size() > 1 ){
			throw new IllegalStateException("Trace should be a list of transitions");
		}
		
		tr = exit.iterator().next();
		
		return tr;
	}



	private boolean sameSequence(Transition nt, Transition tr) {
		
		
		while ( nt != null ){
			
			
			if ( tr == null ){
				return false;
			}
			
			if ( ! nt.sameActivityAndType(tr) ){
				return false;
			}
			
			nt = nextTransition(nt);
			tr = nextTransition(tr);
			
		}
		
		return true;
	}



	//    public static ArrayList<Object> getClockAssociate(Transition t){
	//        try{
	//            ArrayList<Object> oc = new ArrayList();
	//            for(Reset r:t.getResets()){
	//                if(r.getClock() instanceof it.unimib.disco.lta.timedKTail.JTMTime.RelativeClock){
	//                    oc.add(r.getClock());
	//                }
	//            }
	//            for(Clause c:t.getGuard().getClauses()){
	//                if(c.getClock() instanceof it.unimib.disco.lta.timedKTail.JTMTime.AbsoluteClock){
	//                    oc.add(c.getValue());
	//                }
	//            }
	//        return oc;
	//        }catch(NullPointerException e){
	//            System.out.println("ERRORE CREAZIONE AUTOMA");
	//            System.out.println("TRACCIA NON VALIDA");
	//            System.exit(-1);
	//            return null;
	//        }
	//    }

	public static long minTime(Trace t){
		return t.getEvents().get(0).getTimestamp();
		//
		//      Fabrizio: the following does not make any sense....
		//      
		//        long minTime = 9223372036854775807L;
		//        for(Event e:t.getEvents()){
		//            if(e.getTempo() < minTime){
		//                minTime = e.getTempo();
		//            }
		//        }
		//        return minTime;
	}



}
