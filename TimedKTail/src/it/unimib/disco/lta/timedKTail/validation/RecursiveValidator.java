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

public class RecursiveValidator extends Validator {

	private static final Logger logger = LogManager.getLogger(RecursiveValidator.class);

	public RecursiveValidator(boolean checkGuards, boolean validateAbsoluteClocks, boolean useMethodExecutionTime, boolean checkForGuardsNotReset, TimedAutomata ta, boolean traceErrorSequences) {
		super(checkGuards, validateAbsoluteClocks, useMethodExecutionTime, checkForGuardsNotReset, ta, traceErrorSequences );
	}

	public boolean validateEvents(Trace trace, Node nodeNavigatore) {
		LinkedList<Pair<Event,Transition>> traceClock = new LinkedList();

		//traceClock: storico delle transizioni e evento che hanno matchato
		return validateInternal(trace, 0, nodeNavigatore, traceClock);

	}

	public boolean validateInternal(Trace t, int nEvento, Node nodeN,LinkedList<Pair<Event,Transition>> traceClock){
		Node nodeNavigatore = nodeN;
		boolean risultato = false;

		boolean eventAcceptedAtLeastOnce = false;

		if ( logger.isDebugEnabled() ){ logger.debug("Current node: "+nodeN.getId()); }
		
		if(t.inesistente(nEvento)){
			if ( logger.isDebugEnabled() ){ logger.debug("Traccia validata!"); }
			
			boolean isFinal = checkFinalEvent(nodeNavigatore, traceClock, t, nEvento);

			if ( ! isFinal ){
				return false;
			}

			if ( errorsObservedOnCurrentPath() ){
				if ( logger.isDebugEnabled() ){ logger.debug("Errors observed on current path"); }
				
				return false;
			}

			return true;

		}else{
			Event e=t.getEvent(nEvento);

			if ( logger.isDebugEnabled() ){ logger.debug("Processing: "+e.toString()); }

			Transition tranEsterna = null;
			for(Transition tran: nodeNavigatore.getTransitionsExit() ){
				//                logger.debug("Analizzo arco: "+tran.getActivity().getActivity()+" ID: "+tran.getId());
				//verifico se evento è di tipo B e se esiste un arco uscente dal nodo navigatore che che verifica l'evento

				beforeVisitingNextExitTransition();

				if ( verificaEventoAutoma(tran,e) ) {
					
					eventAcceptedAtLeastOnce=true;
					
					//                    System.out.println("E-Evento: "+e.getAttivita()+" = "+"transizione: "+tran.getActivity().getActivity());
					//recupero la guadia e verifico se sono veriricate
					ValidationError error = verificaGuardia(tran,e,traceClock);

					if ( error != null ){
						if ( logger.isDebugEnabled() ) {  logger.debug("Guardia non verificata: " + error); }
						
						addError(error);                        
					}

					if( error == null || traceErrorSequences ){
						//                        System.out.println("Guardia Validata");
						if (logger.isDebugEnabled() ){ logger.debug("Transizione, ID: "+tran.getId()+" attivita': "+tran.getActivity().getName()+" verificata"); }


						//aggiungo evento e e transizione alla "cronologia"
						aggiungiElementoLocalTrace(tran,e,traceClock);
						//nuovo nodo navigatore
						nodeNavigatore=tran.getNodeTo();


						risultato = validateInternal(t,nEvento+1,nodeNavigatore,traceClock);
						traceClock.remove(traceClock.size()-1);
					}





				} else {
					if ( logger.isDebugEnabled() ){ logger.debug("Analizzato Arco: "+tran.getId()+" Ma rifiutato da Traccia"); }
					//Does not match current transition, may match another one

				}

				afterVisitingExitTransition(nEvento);

				if ( logger.isDebugEnabled() ) { logger.debug("Analizzato Arco: "+tran.getId()+"  passo al prossimo arco per validazione. Nodo corrente:" + nodeN.getId() +" Evento: "+e.getAttivita() ); }

				//Chiusura ciclo -> guardo Transizione successiva
				//Mi permette di bloccare la ricorsione e di nn verificare tutti gli altri rami
				if(risultato){

					if ( logger.isDebugEnabled() ){ logger.debug("Traccia VALIDA, chiudo ricorsione! "+risultato); }

					return risultato;
				}
			}

			if ( ! eventAcceptedAtLeastOnce ){ //this check is necessary
				//chiusura transizione non esistente nessuna transizione che è riconociuta dalla traccia (attività o tipologia diverse)	
				if ( logger.isDebugEnabled() ){ logger.debug("Traccia Non Acettata Nello Stato Corrente"); }

				ValidationError error = buildNotAcceptedEventError(e, traceClock, nEvento+1 );
				addError(error);		
			}

			return risultato;
		}

	}




}
