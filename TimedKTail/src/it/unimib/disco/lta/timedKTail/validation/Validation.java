/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.validation;

import it.unimib.disco.lta.timedKTail.JTMTime.*;
import it.unimib.disco.lta.timedKTail.algorithm.PhaseI;
import it.unimib.disco.lta.timedKTail.traces.*;
import it.unimib.disco.lta.timedKTail.validation.Validation.ValidationError;
import it.unimib.disco.lta.timedKTail.validation.Validation.ValidationError.ErrorType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 *
 * @author AleX
 */
public class Validation {
	private static final Logger logger = LogManager.getLogger(Validation.class);
	//il valore da considerare si trova nella posozione 0 del vettore
	//se il valore è -1 la traccia è accettata 
	//private static ArrayList<Integer> rigaErrore;
	//il valore da considerare si trova nella posozione 0 del vettore
	//si considera solamente se il valore di rigaerrore è != da -1
	
	private TimedAutomata timedAutomata;
	private Trace t;
	private boolean validateAbsoluteClocks;
	private Long initialTime;
	private boolean checkForGuardsNotReset;
	private boolean checkGuards = true;
	private boolean useMethodExecutionTime;

	private boolean useRecursiveValidator;
	private boolean recordInvalidTraces;
	private boolean recordErrorMessage;

	private int minimalUpperBound;
	private boolean collectOnlyMainError;
	private boolean traceErrorSequences;
	
	
	
	public void setCollectOnlyMainError(boolean collectOnlyMainError) {
		this.collectOnlyMainError = collectOnlyMainError;
	}

	public void setRecordErrorMessage(boolean recordErrorMessage) {
		this.recordErrorMessage = recordErrorMessage;
	}

	public boolean isUseRecursiveValidator() {
		return useRecursiveValidator;
	}

	public void setUseRecursiveValidator(boolean useRecursiveValidator) {
		this.useRecursiveValidator = useRecursiveValidator;
	}

	public boolean isCheckGuards() {
		return checkGuards;
	}

	public void setCheckGuards(boolean checkGuards) {
		this.checkGuards = checkGuards;
	}

	private static int numTracVal = 0;
	private static int numTrace = 0;
	private static int numGuardieViolate = 0;
	private static int numEventiViolati = 0;

	
	public TimedAutomata getTimedAutomata() {
		return timedAutomata;
	}

	public static class ValidationError {
		public static enum ErrorType { VIOLATED_GUARD, UNMATCHED_EVENT, MISSING_CLOCK, NOT_FINAL }
		private int line;
		private String msg;
		private ErrorType et;
		public Clause clause;
		private String activity;
		public long actualValue;
		private boolean firstErrorOfSequence = true;
		private ArrayList<ValidationError> errorSequence;

	

		@Override
		public String toString() {
			return "[ first"+firstErrorOfSequence+"; "+et+"; "+line+"; "+clause+"; "+activity+" ]";
		}

		public ValidationError( ErrorType et, String activity, String msg, int line ){
			this.et = et;
			this.msg = msg;
			this.line = line;
			this.activity = activity;
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
		
		public String getActivity() {
			return activity;
		}

		public boolean isFirstErrorOfSequence() {
			return firstErrorOfSequence;
		}

		public void setFirstErrorOfSequence(boolean firstErrorOfSequence) {
			this.firstErrorOfSequence = firstErrorOfSequence;
		}

		public void setErrorSequence(ArrayList<ValidationError> es) {
			this.errorSequence = es;
		}
		
		public List<ValidationError> getErrorSequence() {
			return errorSequence;
		}
		
	}

	public Validation(TimedAutomata ta, boolean validateAbsoluteClocks, boolean checkForGuardsNotReset, boolean checkGuards, boolean useMethodExecutionTime, boolean traceErrorSequences){
		this.timedAutomata=ta;
		this.validateAbsoluteClocks = validateAbsoluteClocks;
		this.checkForGuardsNotReset = checkForGuardsNotReset;
		this.checkGuards = checkGuards;
		this.useMethodExecutionTime = useMethodExecutionTime;
		this.traceErrorSequences = traceErrorSequences;
	}

	public Validation(TimedAutomata ta) {
		this(ta,false,false,true,false,false);
	}

	//non usare nodi uscenti xchè sono errati
	public ValidationError validateTrace(Trace trace){
		List<ValidationError> list = validateTraceReturnAllErrors(trace);
		if ( list == null ){
			return null;
		}
		return identifyError(list);
	}
	
	public List<ValidationError> validateTraceReturnAllErrors(Trace trace){
		
		logger.debug("Validating trace: "+trace.getFilePath());
		
		Node init=timedAutomata.getNodeInit();
		//Struttura dati che mi permetto di sapere il percorso che ho seguito
		//si incrementa steap by steap durante la risoluzione dell'algoritmo di validazione
		
		Node nodeNavigatore=init;
		int nEvento=0;
		
		
		
		numTrace ++;
		
		if ( useMethodExecutionTime ){
			TimedAutomataFactory taf = new TimedAutomataFactory("", true);
			PhaseI p1 = new PhaseI(taf); 
			p1.elaborateTrace(trace, false, true);
		}
		
		Validator v;
		if ( useRecursiveValidator ){
			v = new RecursiveValidator(checkGuards, validateAbsoluteClocks, useMethodExecutionTime, checkForGuardsNotReset, timedAutomata, traceErrorSequences );
		} else {
			 v = new IterativeValidator(checkGuards, validateAbsoluteClocks, useMethodExecutionTime, checkForGuardsNotReset, timedAutomata, traceErrorSequences );
		}
		v.setRecordInvalidTraces(recordInvalidTraces);
		v.setRecordErrorMessage(recordErrorMessage);
		v.setCollectOnlyMainError(collectOnlyMainError);
		
		boolean risultato = v.validateEvents(trace, nodeNavigatore);
		

		if(risultato){
			logger.info("Traccia Corretta!");
			numRisTrace(1);
			return null;
		}else{
			logger.info("Traccia Errata!");
			numRisTrace(-1);
			
			return v.getCollectedErrors();
		}

	}


	
	

	
	
	public void numRisTrace(int n){
		if(n == 1){
			numTracVal ++;
		}
	}
	public static int getNumTrace(){
		return numTrace;
	}

	public static void resettaInVal(){
		numTrace = 0;
		numTracVal = 0;
		numEventiViolati = 0;
		numGuardieViolate = 0;
	}

	public static int getNumTracVal(){
		return numTracVal;
	}

	public static int getNumGuardieViolate(){
		return numGuardieViolate;
	}

	public static int getNumEventiViolati(){
		return numEventiViolati;
	}


	//Sistemare la stampa quando si verificare la situazione che tutte le strade di ricorsione falliscono!
	//Funziona bene fin quando non ci sn errori che le strade per un cammino nn c'è e risale
	//forse la soluzione risiede nell'analisi dell'evento o della transizione che ha generato errore... da valutare!
	//forse usare anche la transizione e salvarla a parte fuori dal ciclo forse è utile
	public ValidationError identifyError(List<ValidationError> errors){
		if ( errors == null ){
			return null;
		}
		
		
		long erroreGrande = -1;
		ValidationError error = null;
		
		for(ValidationError ve:errors){
			if ( traceErrorSequences && ( ! ve.isFirstErrorOfSequence() ) ){
				continue;
			}
			if (erroreGrande <= ve.line){
				if ( erroreGrande == ve.line ){
					boolean newErrorMoreImportant = ve.getErrorType().ordinal() - error.getErrorType().ordinal()  < 0;
					if ( ! newErrorMoreImportant ){
						continue;
					}
				}
				erroreGrande = ve.line;
				error = ve;
			}
		}
		
		return error;

	}

	public void setRecordInvalidTraces(boolean v) {
		recordInvalidTraces = v;
	}

	public void setMinimalUpperBound(int minimalUpperBound) {
		this.minimalUpperBound = minimalUpperBound;
	}




}
