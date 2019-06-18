package it.unimib.disco.lta.timedKTail.validation;

import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unimib.disco.lta.timedKTail.JTMTime.Node;
import it.unimib.disco.lta.timedKTail.JTMTime.Pair;
import it.unimib.disco.lta.timedKTail.JTMTime.TimedAutomata;
import it.unimib.disco.lta.timedKTail.JTMTime.Transition;
import it.unimib.disco.lta.timedKTail.traces.Event;
import it.unimib.disco.lta.timedKTail.traces.Trace;
import it.unimib.disco.lta.timedKTail.validation.Validation.ValidationError;

public class IterativeValidator extends Validator {

	private static final Logger logger = LogManager.getLogger(IterativeValidator.class);
	

	public IterativeValidator(boolean checkGuards, boolean validateAbsoluteClocks, boolean useMethodExecutionTime, boolean checkForGuardsNotReset, TimedAutomata ta, boolean traceErrorSequences ) {
		super(checkGuards, validateAbsoluteClocks, useMethodExecutionTime, checkForGuardsNotReset, ta, traceErrorSequences );
	}

	public boolean validateEvents(Trace trace, 
			Node nodeNavigatore) {

		LinkedList<Pair<Event,Transition>> traceClock = new LinkedList();

		long risultato;

		ValidateEventsParameter firstElementVisit = new ValidateEventsParameter(trace, 0, nodeNavigatore, traceClock);
		LinkedList<ValidateEventsParameter> visitStack = new LinkedList<>();
		visitStack.add(firstElementVisit);

		while ( ! visitStack.isEmpty() ){
			boolean accepted = validaEventi(visitStack);

			if ( accepted ){
				return true;
			}
		}

		return false;
	}

	public boolean validaEventi(LinkedList<ValidateEventsParameter> visitStack){
		ValidateEventsParameter parameterObject = visitStack.getLast();

		if ( parameterObject.doPost ){
			return doPost(visitStack);
		} else {
			return doPre(visitStack);
		}
	}

	public boolean doPre(LinkedList<ValidateEventsParameter> visitStack){
		ValidateEventsParameter parameterObject = visitStack.getLast();

		Node nodeNavigatore = parameterObject.nodeN;

		if ( logger.isDebugEnabled() ) { logger.debug("Current node: "+parameterObject.nodeN.getId()); }
		
		if(parameterObject.t.inesistente(parameterObject.nEvento)){

			boolean valid = checkFinalEvent(nodeNavigatore, parameterObject.traceClock, parameterObject.t, parameterObject.nEvento );
			
			
			if ( valid && errorsObservedOnCurrentPath() ){
				if ( logger.isDebugEnabled() ) { logger.debug("Final state correct but errors observed on current path."); }
				
				parameterObject.doPost = true;
				return false;
			}
			
			if ( valid ){
				visitStack.removeLast();
			} else {
				parameterObject.doPost = true;
			}
			
			return valid;
		}


		Event e=parameterObject.t.getEvent(parameterObject.nEvento);

		if ( logger.isDebugEnabled() ){ logger.debug("Processing: "+e.toString()); }


		Transition[] ts = nodeNavigatore.getArrayTransitionsExit();
		
		boolean eventAcceptedAtLeastOnce = false;
		
		for( /*current value is ok*/ ; parameterObject.positionInVisit < ts.length; parameterObject.positionInVisit++ ){

			beforeVisitingNextExitTransition();
			
			Transition tran = ts[parameterObject.positionInVisit];

			if ( verificaEventoAutoma(tran,e) ) {

				eventAcceptedAtLeastOnce = true;
				
				//                    System.out.println("E-Evento: "+e.getAttivita()+" = "+"transizione: "+tran.getActivity().getActivity());
				//recupero la guadia e verifico se sono veriricate
				ValidationError error = verificaGuardia(tran,e,parameterObject.traceClock);
			
				if ( error != null ){
					if ( logger.isDebugEnabled() ) { logger.debug("Guardia non verificata: " + error); }
					addError(error);                      
				}
			
				if( error == null || traceErrorSequences ){ //in case we trace error sequences we do not stop after violations of guard conditions
					if (logger.isDebugEnabled() ){ logger.debug("Transizione, ID: "+tran.getId()+" attivita': "+tran.getActivity().getName()+" verificata"); }


					//aggiungo evento e e transizione alla "cronologia"
					aggiungiElementoLocalTrace(tran,e,parameterObject.traceClock);
					//nuovo nodo navigatore
					nodeNavigatore=tran.getNodeTo();


					ValidateEventsParameter nextVisitData = new ValidateEventsParameter(parameterObject.t, parameterObject.nEvento+1, nodeNavigatore, parameterObject.traceClock );
					visitStack.add(nextVisitData);
					parameterObject.positionInVisit++;
					return false;


				}
				
				


			} else {
				if ( logger.isDebugEnabled() ){ logger.debug("Analizzato Arco: "+tran.getId()+" Ma rifiutato da Traccia"); }
				//Does not match current transition, may match another one

			}

			if ( logger.isDebugEnabled() ) { logger.debug("Analizzato Arco: "+tran.getId()+"  passo al prossimo arco per validazione. Nodo corrente:" + parameterObject.nodeN.getId() +" Evento: "+e.getAttivita() ); }


		}

		if ( ! eventAcceptedAtLeastOnce ){ //this check is necessary
			
			//chiusura transizione non esistente nessuna transizione che è riconociuta dalla traccia (attività o tipologia diverse)	
			if ( logger.isDebugEnabled() ){ logger.debug("Traccia Non Acettata Nello Stato Corrente"); }

			ValidationError error = buildNotAcceptedEventError(e, parameterObject.traceClock, parameterObject.nEvento+1 );
			addError(error);		
		}

		parameterObject.doPost = true;

		return false;

	}

	

	public boolean doPost(LinkedList<ValidateEventsParameter> visitStack) {
		
		if ( logger.isDebugEnabled() ) { logger.debug("doPost"); }
		
		//POST
		ValidateEventsParameter parameterObject = visitStack.removeLast();

		if ( parameterObject.traceClock.size() > 0 ){
			parameterObject.traceClock.remove(parameterObject.traceClock.size()-1);
		}
		
		afterVisitingExitTransition( parameterObject.nEvento );

		return false;
	}

	



}
