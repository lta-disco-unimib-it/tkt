package it.unimib.disco.lta.timedKTail.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unimib.disco.lta.timedKTail.JTMTime.Clause;
import it.unimib.disco.lta.timedKTail.JTMTime.Clock;
import it.unimib.disco.lta.timedKTail.JTMTime.Node;
import it.unimib.disco.lta.timedKTail.JTMTime.Pair;
import it.unimib.disco.lta.timedKTail.JTMTime.TimedAutomata;
import it.unimib.disco.lta.timedKTail.JTMTime.Transition;
import it.unimib.disco.lta.timedKTail.traces.Event;
import it.unimib.disco.lta.timedKTail.traces.Trace;
import it.unimib.disco.lta.timedKTail.validation.Validation.ValidationError;
import it.unimib.disco.lta.timedKTail.validation.Validation.ValidationError.ErrorType;

public abstract class Validator {
	
	private static char COL_SEPARATOR = ',';
	

	public static char getCOL_SEPARATOR() {
		return COL_SEPARATOR;
	}

	public static void setCOL_SEPARATOR(char cOL_SEPARATOR) {
		COL_SEPARATOR = cOL_SEPARATOR;
	}

	private static final Logger logger = LogManager.getLogger(Validator.class);
	private boolean validateAbsoluteClocks;
	private boolean checkGuards;
	private boolean useMethodExecutionTime;
	private boolean checkForGuardsNotReset;
	private TimedAutomata timedAutomaton;
	private boolean checkFinalState = true;
	private boolean recordErrorMessage = false;

	public boolean isRecordErrorMessage() {
		return recordErrorMessage;
	}

	public void setRecordErrorMessage(boolean recordErrorMessage) {
		this.recordErrorMessage = recordErrorMessage;
	}

	public boolean isRecordInvalidTraces() {
		return recordInvalidTraces;
	}

	public Validator(boolean checkGuards, boolean validateAbsoluteClocks, boolean useMethodExecutionTime, boolean checkForGuardsNotReset, TimedAutomata ta, boolean traceErrorSequences ) {
		this.validateAbsoluteClocks = validateAbsoluteClocks;
		this.checkForGuardsNotReset = checkForGuardsNotReset;
		this.checkGuards = checkGuards;
		this.useMethodExecutionTime = useMethodExecutionTime;
		this.timedAutomaton = ta;
		this.traceErrorSequences = traceErrorSequences;
	}

	public abstract boolean validateEvents(Trace trace, Node nodeNavigatore);

	public static boolean verificaEventoAutoma(Transition t, Event e){
		if ( (t.getActivity().getName().equals(e.getAttivita())) && (t.isBegin() == e.isBegin() ) ){
			if ( logger.isDebugEnabled() ){
				logger.debug("Evento "+e.getAttivita()+" riconosciuto da transizione ID: "+t.getId()+" attivita': "+t.getActivity().getName());
			}

			return true;
		}else{
			if ( logger.isDebugEnabled() ){
				logger.debug("Evento "+e.getAttivita()+" NON RICONOSCIUTO da transizione ID: "+t.getId()+" attivita': "+t.getActivity().getName());
			}

			return false;
		}
	}

	

	private ValidationError buildViolatedGuardError(Event e, Transition t, Clause clauseErrata, long valoreConfronto, LinkedList<Pair<Event, Transition>> traceClock, int line) {
		String errore = "";
		String tabError = "";
		
		ErrorType eType = ErrorType.VIOLATED_GUARD;

		if ( recordErrorMessage ){
			errore="Violated guard \n"
					+ "Violating event: \n"
					+ "   Activity: "+e.getAttivita()+" \n"
					+ "   Type: "+e.getTipologia()+" \n"
					+ "   Time: "+valoreConfronto+" \n"
					+ "Transition info: \n"
					+ "   Activity: "+t.getActivity().getName()+" \n"
					+ "   Type: "+t.getType()+" \n"
					+ "   Violated Clause: \n"+clauseErrata.toString()+"\n";


			if ( recordInvalidTraces ){
				errore += "Processed Events: \n" + printVisitedTrace(traceClock);
			}
			
			tabError=generateTabErrorMessage(e, traceClock, eType, clauseErrata, valoreConfronto);
		}

		ValidationError ve = new ValidationError(eType, e.getAttivita(), errore, tabError, line);
		ve.clause = clauseErrata;
		ve.actualValue = valoreConfronto;
		
		return ve;
	}



	

	public void aggiungiElementoLocalTrace(Transition t, Event e, List<Pair<Event,Transition>> traceClock){
		e.setFiredTransition( t );
		traceClock.add(new Pair(e,t));
	}



	public Pair<Event,Transition> getTransAssociateEvent(Event e, LinkedList<Pair<Event,Transition>> traceClock){
		long cont=0;
		//            System.out.println("Cerco evento associato ad: "+e.getAttivita()+" tipo: "+e.getTipologia());

		Iterator<Pair<Event, Transition>> it = traceClock.descendingIterator();
		while(it.hasNext()){
			Pair<Event, Transition> next = it.next();   
			if((e.getAttivita().equals(next.getVal1().getAttivita())) && (next.getVal2().isEnd()) ){
				cont++;
			}
			if((e.getAttivita().equals(next.getVal1().getAttivita())) && (next.getVal2().isBegin() ) && (cont==0)){ 
				return next;
			}
			if((e.getAttivita().equals(next.getVal1().getAttivita())) && (next.getVal2().isBegin() ) && (cont!=0)){
				cont--;
			}
		}
		logger.fatal("Non trovo evento associato ad evento corrente - Errore algoritmo");
		return null;
	}

	private ArrayList<ValidationError> collectedErrors = new ArrayList<>();
	private boolean recordInvalidTraces = true;
	private boolean collectOnlyMainError = Boolean.parseBoolean(System.getProperty("tkt.collectOnlyMainError","false"));

	public boolean isCollectOnlyMainError() {
		return collectOnlyMainError;
	}

	public void setCollectOnlyMainError(boolean collectOnlyMainError) {
		this.collectOnlyMainError = collectOnlyMainError;
	}

	public void setRecordInvalidTraces(boolean recordInvalidTraces) {
		this.recordInvalidTraces = recordInvalidTraces;
	}

	List<ArrayList<ValidationError>> currentErrorSequences = new ArrayList<>();
	List<ArrayList<ValidationError>> allErrorSequences = new ArrayList<>();
	ArrayList<ValidationError> currentErrorSequence;
	private boolean newErrorSequence;
	protected boolean traceErrorSequences;
	private int lastErrorSequencePos;
	
	public void addErrorToErrorSequence( ValidationError error ){
		if ( logger.isDebugEnabled() )
			logger.debug("Adding error to error sequence, event number "+error.getLine()+", current sequence: "+currentErrorSequence);
		
		if ( newErrorSequence && ( currentErrorSequence != null ) ){
			currentErrorSequence = (ArrayList<ValidationError>) currentErrorSequence.clone();
			currentErrorSequences.add( currentErrorSequence );
			allErrorSequences.add( currentErrorSequence );
		}
		
		if ( currentErrorSequence == null ){
			currentErrorSequence = new ArrayList<ValidationError>();
			currentErrorSequences.add( currentErrorSequence );
			allErrorSequences.add( currentErrorSequence );
		} else {
			error.setFirstErrorOfSequence(false);
		}
		
		lastErrorSequencePos = error.getLine();
		currentErrorSequence.add(error);
	}
	
	
	public boolean errorsObservedOnCurrentPath(){
		return (currentErrorSequence != null) || currentErrorSequences.size() > 0 ;
	}
	
	public void beforeVisitingNextExitTransition( ){
		newErrorSequence = true;
	}
	


	protected void afterVisitingExitTransition(int nEvento) {
		if ( logger.isDebugEnabled() )
			logger.debug("After visiting exit transition "+nEvento);
		
		if ( currentErrorSequence != null ){
			currentErrorSequences.add( currentErrorSequence );
		}
		currentErrorSequence = null;
		
		int toRemoveIdx = -1;
		if ( lastErrorSequencePos >= nEvento ){
			for ( int i = 0, max = currentErrorSequences.size(); i < max; i++ ){
				ArrayList<ValidationError> ves = currentErrorSequences.get(i);
				if ( ves.get(ves.size()-1).getLine() >= nEvento ){
					if ( toRemoveIdx < 0 ){
						toRemoveIdx = i;
					}
				} else {
					if ( toRemoveIdx >= 0 ){
						throw new IllegalStateException("All the errors following index "+toRemoveIdx+" were expected to have happened after "+nEvento);
					}
				}
			}
		}
		
		if ( toRemoveIdx >= 0 ){
			if ( logger.isDebugEnabled() )
				logger.debug("Removing errors");
			
			while ( (currentErrorSequences.size()-1) >= toRemoveIdx ){
				currentErrorSequences.remove(currentErrorSequences.size()-1);
			}
		}
		
		if ( currentErrorSequences.size() > 0 ){
			currentErrorSequence = currentErrorSequences.get( currentErrorSequences.size() - 1 );
			lastErrorSequencePos =  currentErrorSequence.get(currentErrorSequence.size()-1).getLine();
			
			if ( logger.isDebugEnabled() )
				logger.debug("Still active errors on current path, last error pos "+lastErrorSequencePos);
		} else { 
			if ( logger.isDebugEnabled() )
				logger.debug("No more active errors on current path");
			
			currentErrorSequence = null;
			lastErrorSequencePos = -1;
		}
	}
	
	public void addError(ValidationError error) {
		if ( traceErrorSequences ){
			addErrorToErrorSequence(error);
		}
		if ( collectOnlyMainError ){
			boolean putNew = true;

			if ( collectedErrors.size() > 0 ){
				putNew = false;

				ValidationError existing = collectedErrors.get(0);
				if ( existing.getLine() == error.getLine() ){
					if ( error.getErrorType() == ErrorType.VIOLATED_GUARD ){
						putNew=true;
					}
				} else if ( existing.getLine() < error.getLine() ) {
					putNew=true;
				}

				if ( putNew ){
					collectedErrors.set(0, error);
				}
			} else {
				collectedErrors.add(error);
			}
		} else {
			collectedErrors.add(error);
		}
	}


	public ValidationError verificaGuardia(Transition tOdierna, Event eOdierno,LinkedList<Pair<Event,Transition>> traceClock){
		//Recuperato la transizione associata dato evento corrente
		if ( ! checkGuards ){
			return  null;
		}

		boolean isExit = eOdierno.isEnd();
		Event  eVecchio = null;
		Transition tVecchio = null;

		if ( isExit ){
			//			Pair<Event,Transition> coppiaAssociata = getTransAssociateEvent(eOdierno,traceClock);
			//			if( coppiaAssociata == null){
			//				throw new IllegalStateException("Associated event not found");
			//			}
			//			eVecchio=coppiaAssociata.getVal1();
			//			tVecchio=coppiaAssociata.getVal2();

			eVecchio=eOdierno.getCorrespondingBegin();
			tVecchio=eVecchio.getFiredTransition();
		}



		boolean checkedAtLeastOneGuard = false;
		boolean shouldCheckAtLeastOneGuard = false;

		ArrayList<ValidationError> errors = new ArrayList<ValidationError>();
		HashSet<Clock> validClocks = new HashSet<>();

		for(Clause c:tOdierna.getGuard().getClauses()){


			if ( logger.isDebugEnabled() ){
				logger.debug("Verifica Guardia: "+c.toString());
			}

			long timeToCheck = -1;
			if ( c.getClock().isAbsoluteClock() ){
				if ( validateAbsoluteClocks ){
					timeToCheck=eOdierno.getTimestamp();
				}
			} else {

				shouldCheckAtLeastOneGuard=true;

				if ( ! isExit ){
					throw new IllegalStateException("Unexpected local clock guard on enter");
				}
				if ( ! tVecchio.hasClockOnReset( c.getClock() ) ){
					continue;
				}
				timeToCheck = delta(eOdierno.getTimestamp(),eVecchio.getTimestamp());
			}

			if ( timeToCheck != -1 ){
				if ( logger.isDebugEnabled() ){ logger.debug("Verifica Guardia: "+c.toString()+" "+timeToCheck+" "+c.getClock().isAbsoluteClock()); }

				if ( useMethodExecutionTime ){
					if ( ! c.getClock().isAbsoluteClock() ){
						timeToCheck = eOdierno.getExecutionTime();
					}
				}

				if ( c instanceof Clause ){

				}

				if ( c.evaluateClause(timeToCheck) == false ){
					ValidationError ve;
					ve = buildViolatedGuardError(eOdierno, tOdierna, c, timeToCheck, traceClock, traceClock.size()+1);
					ve.clause = c;
					errors.add( ve );
				} else {
					validClocks.add( c.getClock() );
				}

				checkedAtLeastOneGuard=true;
			}
		}

		//errors that regard clock for which at least one clause is valid are ignored
		for ( ValidationError ve : errors ){
			if( ! validClocks.contains(ve.clause) ){
				return ve;
			}
		}

		if ( checkForGuardsNotReset && shouldCheckAtLeastOneGuard ){
			if ( ! checkedAtLeastOneGuard ){

				Set<Clock> checkedClocks = new HashSet<Clock>();
				for(Clause c:tOdierna.getGuard().getClauses()){
					checkedClocks.add(c.getClock());
				}

				if ( logger.isDebugEnabled() ){ logger.debug("Did not check any guard for transition: "+tOdierna+". \n Corresponding transition: "+tVecchio+"\n TA:"+timedAutomaton.getName()+" \n "+printVisitedTrace(traceClock)); }

				return buildMissingClockError(eOdierno, tOdierna, traceClock, traceClock.size()+1, checkedClocks);
				//throw new IllegalStateException("Did not check any guard for transition: "+tOdierna+". \n Corresponding transition: "+tVecchio+"\n TA:"+timedAutomata.getName()+" \n "+visited);
			}
		}

		if ( logger.isDebugEnabled() ){ logger.debug("Guardia Ignorata!"); }

		return null;
	}




	protected ValidationError buildNotAcceptedEventError(Event e, List<Pair<Event, Transition>> traceClock, int line) {
		String errore = "";
		String tabError = "";
		ErrorType eType = ErrorType.UNMATCHED_EVENT;
		
		if ( recordErrorMessage ){
			errore="Invalid event \n"
					+ "Event details: \n"
					+ "   Activity: "+e.getAttivita()+" \n"
					+ "   Type: "+e.getTipologia()+" \n";

			if( recordInvalidTraces ){
				errore += "Processed events: \n" + printVisitedTrace(traceClock);
			}
			
			tabError = generateTabErrorMessage(e, traceClock, eType);

		}
		
		return new ValidationError(eType, e.getAttivita(), errore, tabError, line);
	}
	
	private String generateTabErrorMessage(Event e, LinkedList<Pair<Event, Transition>> traceClock, ErrorType eType,
			Clause violatedClause, long violatingValue) {
		
		return generateTabErrorMessage(e, traceClock, eType, "", violatedClause, violatingValue);
	}

	private String generateTabErrorMessage(Event e, List<Pair<Event, Transition>> traceClock, ErrorType eType) {
		return generateTabErrorMessage(e, traceClock, eType, "");
	}
	
	private String generateTabErrorMessage(Event e, List<Pair<Event, Transition>> traceClock, ErrorType eType,
			String clocks) {
		return generateTabErrorMessage(e, traceClock, eType, clocks, null, 0);
	}
		
		private String generateTabErrorMessage(Event e, List<Pair<Event, Transition>> traceClock, ErrorType eType,
				String clocks, Clause violatedClause, long violatingValue) {
	
		String tabError;
		String state;
		int last = traceClock.size() - 1;
		if ( last < 0 ){
			state = timedAutomaton.getNodeInit().toString();;
		} else {
			state = traceClock.get( last ).getVal2().getNodeTo().toString();
		}
		
		String clause = "";
		String violatingValueStr = "";
		
		if ( violatedClause != null ){
			clause=violatedClause.toString();
			violatingValueStr = ""+violatingValue;
		}
		
		
		tabError = eType.name()+ COL_SEPARATOR + state + COL_SEPARATOR + e.getAttivita()+":"+e.getTipologia() + COL_SEPARATOR + e.getTimestamp() + COL_SEPARATOR + clocks + COL_SEPARATOR + clause + COL_SEPARATOR + violatingValueStr;
		return tabError;
	}

	protected ValidationError buildNonFinalStateError(Event e, List<Pair<Event, Transition>> traceClock, int line) {
		String errore = "";
		String tabError = "";
		ErrorType eType = ErrorType.NOT_FINAL;
		
		if ( recordErrorMessage ){
			errore="Trace terminates in a non-final state \n"
					+ "Event details: \n"
					+ "   Activity: "+e.getAttivita()+" \n"
					+ "   Type: "+e.getTipologia()+" \n";

			if( recordInvalidTraces ){
				errore += "Matching trace: \n" + printVisitedTrace(traceClock);
			}
			
			tabError = generateTabErrorMessage(e, traceClock, eType);
		}

		return new ValidationError(eType, e.getAttivita(), errore, tabError, line);
	}

	private ValidationError buildMissingClockError(Event e, Transition t, List<Pair<Event, Transition>> traceClock, int line, Collection<Clock> clocksToFind) {
		ErrorType eType = ErrorType.MISSING_CLOCK;
		String clocks = "";


		for ( Clock c : clocksToFind ){
			clocks += c.getId()+":";
		}


		String errore = "";
		String tabError = "";
		if ( recordErrorMessage ){
			errore="Missing clock \n"
					+ "Event details: \n"
					+ "   Activity: "+e.getAttivita()+" \n"
					+ "   Type: "+e.getTipologia()+" \n"

				+ "Transition: \n"
				+ "   Activity: "+t.getActivity().getName()+" \n"
				+ "   Type: "+t.getType()+" \n"

				+"Clocks to find: "+clocks+"\n";

			if( recordInvalidTraces ){
				errore += "Processed events: \n" + printVisitedTrace(traceClock);
			}
			
			tabError = generateTabErrorMessage(e, traceClock, eType, clocks);
		}

		return new ValidationError(eType, e.getAttivita(), errore, tabError, line);
	}

	

	private String printVisitedTrace(
			List<Pair<Event, Transition>> traceClock ) {
		StringBuffer sb = new StringBuffer();
		for (  Pair<Event, Transition> pair : traceClock ){
			sb.append( "    "+pair.getVal1().getAttivita()+" "+pair.getVal1().getTipologia()+" " );
			sb.append(  "    ("+pair.getVal2().getNodeFrom().getId()+" -> "+pair.getVal2().getNodeTo().getId()+") \n" );
		}

		sb.append("Total events: "+traceClock.size() );

		return sb.toString();
	}


	public long delta(long valg,long valp){
		return valg-valp;
	}

	public List<ValidationError> getCollectedErrors() {
		if ( logger.isDebugEnabled() ){ logger.debug("Collected errors: "+allErrorSequences); }
		
		if ( traceErrorSequences ){
			ArrayList<ValidationError> result = new ArrayList<ValidationError>();
			for ( ValidationError error : collectedErrors ){
				if ( ! error.isFirstErrorOfSequence() ){
					continue;
				}
				
				result.add( error );
					
				int prevSize = 0;
				for ( ArrayList<ValidationError> es : allErrorSequences ){
					if ( es.get(0) == error ){
						if ( es.size() > prevSize ){
							error.setErrorSequence( es );
							prevSize = es.size();
						}
					}
				}
			}
			
			return result;
		}
		
		return collectedErrors;
	}

	public boolean checkFinalEvent(Node nodeNavigatore, LinkedList<Pair<Event, Transition>> traceClock, Trace t, int nEvento) {
		boolean accepted = true;
		if ( checkFinalState ){
			if ( ! nodeNavigatore.isFinalState() ){
				accepted = false;
			} 
		}

		if ( accepted ){
			if ( logger.isDebugEnabled() ) { logger.debug("Traccia validata!"); }
			
			return true;
		} else {

			ValidationError error = buildNonFinalStateError(t.getEvent(nEvento-1), traceClock, nEvento );
			addError(error);		

			return false;

		}
	}
}
