/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.ui;

import static it.unimib.disco.lta.timedKTail.ui.Main.loadAutomata;
import static it.unimib.disco.lta.timedKTail.ui.Main.saveAutomata;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unimib.disco.lta.timedKTail.JTMTime.TimedAutomata;
import it.unimib.disco.lta.timedKTail.traces.ObserverValidateTrace;
import it.unimib.disco.lta.timedKTail.traces.Parser;
import it.unimib.disco.lta.timedKTail.traces.Trace;
import it.unimib.disco.lta.timedKTail.validation.Validation;
import it.unimib.disco.lta.timedKTail.validation.Validation.ValidationError;


/**
 *
 * @author AleX
 */
public class Crossvalidation {

	//    private static final String fTrace="storage/tracceCreazione/trace.csv";

	//private static final String fTraces="storage";
	//*********POLITICHE PER UNA SINGOLA CLAUSA*********//
	// VALORE 1: POLITICA CONSERVATIVA
	// VALORE 2: POLITICA CONSERVATIVA CON PARAMETRO K -> kPoliticsOneVal
	// VALORE 3: POLITICA ELIMINAZIONE
	private int politicsOneVal=1;
	private double kPoliticsOneVal=0.1;
	//*********POLITICHE PER UNA MULTY CLAUSA*********//
	// VALORE 1: POLITICA MINORANZA
	// VALORE 2: POLITICA MAGGIORANZA
	// VALORE 3: POLITICA INTERVALLO
	private int politicsMultyVal=2;

	private double deltaForRangeCalculation= 0.0;
	private double normalDistributionConfidence= 0.95;

	private int totalValid;
	private int totalInvalid;

	private long totalValidationTime;
	private long totalInferenceTime;

	private int totalNodes;
	private int totalTransitions;

	private int executions;
	private boolean validateAbsoluteClocks;

	private int unmatchedEvents;
	private int violatedGuards;

	private int _unmatchedEvents;
	private int _violatedGuards;

	private int _Valid;
	private int _Invalid;

	private long _validationTime;
	private long _inferenceTime;

	private int _Nodes;
	private int _Transitions;
	private int _missingClocks;
	private int missingClocks;
	private boolean crossValidation = true;
	private String configurationKey;
	private boolean sameTrainingAndValidation;
	private boolean includeNestedCallsTime;
	private static boolean checkFolders = Boolean.getBoolean("checkFolders");

	private boolean validateAfterMerging;

	private int _ignoredMerges;
	private int totalIgnoredMerges;
	private int _performedMerges;
	private int totalPerformedMerges;
	private boolean replaceAutomata  = Boolean.getBoolean("replaceAutomata");
	private File automataDestFolder;
	private int totalInvalidNoGuards;
	private int totalValidNoGuards;
	private int _ValidNoGuards;
	private int _InvalidNoGuards;
	private int _nonFinalStates;
	private int nonFinalStates;
	private boolean checkAlsoWithoutGuards = Boolean.parseBoolean(System.getProperty("tkt.checkAlsoWithoutGuards","true") );
	private boolean traceErrorSequences  = Boolean.parseBoolean(System.getProperty("tkt.traceErrorSequences","false") );

	//path di salvataggio automi


	private static final Logger logger = LogManager.getLogger(Crossvalidation.class);
	private static final boolean inferAutomataInSeparateProcess = Boolean.parseBoolean(System.getProperty("tkt.inferAutomataInSeparateProcess","false") );



	public Crossvalidation(int politicsOneVal, double kPoliticsOneVal,
			int politicsMultyVal, double deltaForRangeCalculation,
			double normalDistributionConfidence, boolean _validateAbsoluteClocks,
			boolean _includeNestedCallsTime, boolean _validateAfterMerging) {
		super();
		this.politicsOneVal = politicsOneVal;
		this.kPoliticsOneVal = kPoliticsOneVal;
		this.politicsMultyVal = politicsMultyVal;
		this.deltaForRangeCalculation = deltaForRangeCalculation;
		this.normalDistributionConfidence = normalDistributionConfidence;
		this.validateAbsoluteClocks = _validateAbsoluteClocks;
		this.includeNestedCallsTime = _includeNestedCallsTime;
		this.validateAfterMerging = _validateAfterMerging;
	}




	public Crossvalidation() {
		// TODO Auto-generated constructor stub
	}


	public static class InferSingleAutomata {
		public static void main(String args[]){

			int c = 0;

			String path = args[c++];

			Integer _politicsOneVal = Integer.valueOf(args[c++]);
			Double _kPoliticsOneVal = Double.valueOf(args[c++]);
			Integer _politicsMultyVal = Integer.valueOf(args[c++]);

			Double _normalDistributionConfidence = Double.valueOf(args[c++]);
			Double _deltaForRangeCalculation = Double.valueOf(args[c++]);

			boolean _validateAbsoluteClocks = Boolean.valueOf(args[c++]);
			boolean _includeNestedCallsTime = Boolean.valueOf(args[c++]);
			boolean _validateAfterMerging = Boolean.valueOf(args[c++]);

			String output = args[c++];


			TimedAutomata ta = Main.inferAutomata(path,_politicsOneVal,_kPoliticsOneVal,_politicsMultyVal,_deltaForRangeCalculation,_normalDistributionConfidence,_validateAbsoluteClocks,_includeNestedCallsTime,_validateAfterMerging); 

			File outputFile = new File ( output );
			Main.saveAutomata(ta, outputFile);
		}
	}

	public static void main(String[] args) throws IOException{




		File folderWithKFold = new File(args[0]);

		File dest = new File(args[1]);

		if ( ! folderWithKFold.exists() ){
			System.err.println("Folder does not exists "+folderWithKFold.getAbsolutePath());
			return;
		}

		boolean sameTrainingAndValidation = Boolean.getBoolean("sameTrainingAndValidation");
		File[] experiments;

		String automataFolderPath = System.getProperty("automataFolder");
		File automataFolder;
		if ( automataFolderPath != null ){
			automataFolder = new File(automataFolderPath);
		} else {
			automataFolder = dest;
		}

		if ( sameTrainingAndValidation ){
			experiments = new File[1];
			experiments[0] = folderWithKFold;
		} else {
			experiments = folderWithKFold.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					if ( ! pathname.isDirectory() ){
						return false;
					}

					try {
						Double v = Double.valueOf(pathname.getName());
					} catch ( NumberFormatException e ){
						return false;
					}

					return true;
				}
			});
		}

		int c = 2;
		Integer _politicsOneVal = Integer.valueOf(args[c++]);
		Double _kPoliticsOneVal = Double.valueOf(args[c++]);
		Integer _politicsMultyVal = Integer.valueOf(args[c++]);
		Double _deltaForRangeCalculation = Double.valueOf(args[c++]);
		Double _normalDistributionConfidence = Double.valueOf(args[c++]);
		boolean _validateAbsoluteClocks = Boolean.valueOf(args[c++]);
		boolean _includeNestedCallsTime = Boolean.valueOf(args[c++]);
		boolean _validateAfterMerging = Boolean.valueOf(args[c++]);

		Crossvalidation cv = new Crossvalidation(_politicsOneVal,_kPoliticsOneVal,_politicsMultyVal,_deltaForRangeCalculation,_normalDistributionConfidence,_validateAbsoluteClocks,_includeNestedCallsTime, _validateAfterMerging);

		cv.crossValidation = Boolean.parseBoolean(System.getProperty("crossValidation", "true"));

		String prefix = "";

		BufferedWriter validationErrorsRecorder = null;


		String prefixString = System.getProperty("prefix");
		if( prefixString != null ){
			prefix = prefixString;
		}


		String validationFolder = System.getProperty("separateValidation");
		if ( validationFolder != null ){
			cv.crossValidation = false;
			if ( prefix.isEmpty() ){
				prefix = "separateValidation.";
			}

			validationErrorsRecorder = new BufferedWriter( new FileWriter(new File( dest,prefix+"separateValidation.timedKTail.traces.results.csv"), true) );
		}





		for ( File experimentFolder: experiments ){
			//FILE TRACCE INFERIRE AUTOMA
			File fCrea = new File(experimentFolder,"trace");

			//FILE TRACCE CONVALIDARE
			File fConvalida = new File(experimentFolder,"traceValidation");

			if ( sameTrainingAndValidation ){
				cv.sameTrainingAndValidation = sameTrainingAndValidation;
				cv.automataDestFolder = automataFolder;
				fCrea = experimentFolder;
				fConvalida = experimentFolder;
			}

			if ( validationFolder != null ){
				logger.debug("ValidationFolder "+validationFolder);
				fConvalida = new File(validationFolder);
				cv.errorMapRecorder = validationErrorsRecorder;
			}

			if ( checkFolders ){
				checkFolders(fCrea,fConvalida.getAbsolutePath());
				continue;
			}

			cv.esegui(fCrea.getPath(),fConvalida.getAbsolutePath());

			{
				String statsLine = cv.getPartialStatisticsLine(experimentFolder);
				appendLineToFile(new File( dest,prefix+"timedKTail.partial.results.csv"), statsLine);
			}


		}

		if ( validationErrorsRecorder != null ){
			validationErrorsRecorder.close();
		}


		if ( checkFolders ){
			return;
		}


		{
			String statsLine = cv.getStatisticsLine(folderWithKFold);
			appendLineToFile(new File( dest,prefix+"timedKTail.results.csv"), statsLine);
		}
	}




	private static void appendLineToFile(File dest, String statsLine)
			throws IOException {
		BufferedWriter wr = new BufferedWriter(new FileWriter(dest,true));
		wr.write(statsLine);
		wr.newLine();
		wr.close();
	}


	public String getPartialStatisticsLine(File folderWithKFold) {



		double avgValidationTime = (double) _validationTime / (double) (_Valid+_Invalid);


		if ( _Invalid != _unmatchedEvents + _violatedGuards + _missingClocks + _nonFinalStates ){
			logger.warn("While reporting partial statistics: _Invalid != _unmatchedEvents + _violatedGuards + _missingClocks + _nonFInalStates : "+_Invalid+"  "+_unmatchedEvents +" " + _violatedGuards +" " + _missingClocks+" "+_nonFinalStates);
			throw new IllegalStateException("While reporting partial statistics: _Invalid != _unmatchedEvents + _violatedGuards + _missingClocks + _nonFinalStates");
		}



		//System.out.println(folderWithKFold+",valid:"+totalValid+",invalid:"+totalInvalid+",avgNodes:"+avgNodes+",avgTransitions:"+avgTransitions+",avgInferenceTime:"+avgInferenceTime+",avgValidationTime:"+avgValidationTime);
		return (
				getExperimentKey( folderWithKFold )
				+","
				+_Valid+","+_Invalid+","
				+_unmatchedEvents+","+_violatedGuards+","+_missingClocks+","+_nonFinalStates+","
				+_Nodes+","+_Transitions+","+_inferenceTime+","+avgValidationTime+","+_performedMerges+","+_ignoredMerges+","
				+_ValidNoGuards+","+_InvalidNoGuards
				);
	}


	private String getExperimentKey(File folderWithKFold) {
		String head;
		if ( sameTrainingAndValidation ){
			head=folderWithKFold.getName()+"_100";
		} else {
			head = folderWithKFold.getParentFile().getName()+"_"+folderWithKFold.getName();
		}

		return head
				+","+executions+","
				+politicsOneVal+","+kPoliticsOneVal+","+politicsMultyVal+","+deltaForRangeCalculation+","+normalDistributionConfidence+","
				+validateAbsoluteClocks+","
				+includeNestedCallsTime+","
				+validateAfterMerging;
	}




	public String getStatisticsLine(File folderWithKFold) {
		double avgNodes = (double) totalNodes / (double) executions;
		double avgTransitions = (double) totalTransitions / (double) executions;
		double avgInferenceTime = (double) totalInferenceTime / (double) executions;
		double avgValidationTime = (double) totalValidationTime / (double) executions;
		double avgIgnoredMerges = (double) totalIgnoredMerges / (double) executions;
		double avgPerformedMerges = (double) totalPerformedMerges / (double) executions;

		if ( totalInvalid != unmatchedEvents + violatedGuards + missingClocks + nonFinalStates ){
			throw new IllegalStateException("While reporting statistics: _Invalid != _unmatchedEvents + _violatedGuards + _missingClocks + nonFinalStates");
		}

		String name = folderWithKFold.getName();
		int pos = name.indexOf("_T");

		try {
			int expectedExcutions;

			if ( pos == -1 ){
				expectedExcutions=100;
			} else {
				String numString = name.substring(pos+2);

				if ( numString.equalsIgnoreCase( "_ALL" ) ){
					expectedExcutions=100;
				} else {
					expectedExcutions = Integer.valueOf(numString);
				}
			}
			if ( expectedExcutions != totalInvalid + totalValid ){
				logger.warn("ATTENTION: While reporting statistics: expectedExcutions != totalInvalid + totalValid " + 
						expectedExcutions+ " ," + totalInvalid + ", " + totalValid );
			}
		} catch ( NumberFormatException e ){
			logger.catching(e);
		}

		//System.out.println(folderWithKFold+",valid:"+totalValid+",invalid:"+totalInvalid+",avgNodes:"+avgNodes+",avgTransitions:"+avgTransitions+",avgInferenceTime:"+avgInferenceTime+",avgValidationTime:"+avgValidationTime);
		return (
				folderWithKFold+","+executions+","
						+politicsOneVal+","+kPoliticsOneVal+","+politicsMultyVal+","+deltaForRangeCalculation+","+normalDistributionConfidence+","
						+validateAbsoluteClocks+","
						+includeNestedCallsTime+","+validateAfterMerging+","
						+totalValid+","+totalInvalid+","
						+unmatchedEvents+","+violatedGuards+","+missingClocks+","+nonFinalStates+","
						+avgNodes+","+avgTransitions+","+avgInferenceTime+","+avgValidationTime+","+avgPerformedMerges+","+avgIgnoredMerges+","
						+totalValidNoGuards+","+totalInvalidNoGuards+","

				);
	}




	public void esegui(String pathSorgente,String pathConvalida){
		File src = new File( pathSorgente );
		String taName = src.getName()+"_"+politicsOneVal+"_"+kPoliticsOneVal+"_"+politicsMultyVal+"_"+normalDistributionConfidence+"_"+deltaForRangeCalculation+"_"+validateAbsoluteClocks+"_"+includeNestedCallsTime+"_"+validateAfterMerging;
		TimedAutomata ta;
		File automataFile;

		if ( sameTrainingAndValidation ){
			configurationKey = getExperimentKey(src);
			automataFile = new File ( automataDestFolder, taName+".jtm" );
		} else {
			configurationKey = getExperimentKey(src.getParentFile());
			automataFile = new File ( src.getParentFile().getParentFile(), src.getParentFile().getName()+taName+".jtm" );
		}


		boolean inferAutomata;
		if ( crossValidation ){

			logger.debug("Inferring: "+taName+" "+automataFile.getAbsolutePath());
			inferAutomata = true;

			if ( automataFile.exists()  ){
				if (!replaceAutomata) {	//KEEP CURRENT AUTOMATA
					logger.warn("Automata file exists: "+automataFile.getAbsolutePath());
					inferAutomata=false;
				} else {
					automataFile.delete();
				}
			}
		} else {
			inferAutomata=false;
		}


		_inferenceTime = 0;

		if ( inferAutomata ){
			logger.info("Inference: "+taName+" "+automataFile.getAbsolutePath()+" "+pathSorgente);
			
			
			long inferenceBegin = System.currentTimeMillis();
			if( inferAutomataInSeparateProcess ){
				ta = InferModel.inferAutomataInSeparateProcess(automataFile.getAbsolutePath(), pathSorgente,politicsOneVal,kPoliticsOneVal,politicsMultyVal,deltaForRangeCalculation,normalDistributionConfidence,validateAbsoluteClocks,includeNestedCallsTime,validateAfterMerging);
			} else {
				ta = Main.inferAutomata(pathSorgente,politicsOneVal,kPoliticsOneVal,politicsMultyVal,deltaForRangeCalculation,normalDistributionConfidence,validateAbsoluteClocks,includeNestedCallsTime,validateAfterMerging);	
			}
			 
			long inferenceEnd = System.currentTimeMillis();
			_inferenceTime = inferenceEnd-inferenceBegin;

			ta.setName ( taName );


			saveAutomata(ta, automataFile );
		} 

		if ( ! automataFile.exists() ){
			throw new IllegalStateException("Automata file does not exist: "+automataFile.getAbsolutePath());
		}

		ta = loadAutomata(automataFile.getAbsolutePath());

		_ignoredMerges = ta.getIgnoredMerges();
		totalIgnoredMerges += _ignoredMerges;

		_performedMerges = ta.getPerformedMerges();
		totalPerformedMerges += _performedMerges;





		{

			logger.info("Validating: "+taName+" "+automataFile.getAbsolutePath()+" "+pathConvalida);
			long validationBegin = System.currentTimeMillis();
			validateTraces(ta,pathConvalida);
			long validationEnd = System.currentTimeMillis();

			_validationTime = validationEnd-validationBegin;

			totalValidationTime += _validationTime;
		}


		totalInferenceTime += _inferenceTime;

		_Nodes = ta.getNodes().size();
		totalNodes += _Nodes;

		_Transitions = ta.getTransitions().size();
		totalTransitions += _Transitions;



		executions++;
	}




	private static void checkFolders(File src, String pathConvalida) {

		String[] filesInfer= src.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return (! name.startsWith(".") ) && name.endsWith(".csv");
			}
		});

		File convalida = new File( pathConvalida);
		String[] filesValidate = convalida.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return (! name.startsWith(".") ) && name.endsWith(".csv");
			}
		});

		File kFolder = src.getParentFile();
		String prj = kFolder.getParentFile().getName();
		int tPos = prj.lastIndexOf('T');
		String executionsString = prj.substring(tPos+1);
		Integer expectedFiles = Integer.valueOf(executionsString);

		if ( expectedFiles != filesInfer.length+ filesValidate.length ){
			System.out.println("Incomplete folder: "+kFolder.getAbsolutePath() +" "+filesInfer.length+" "+filesValidate.length);
		}

		return;

	}

	public void validateTraces(TimedAutomata ta,String pathConvalida){
		validateTraces(ta, pathConvalida, true);

		if ( checkAlsoWithoutGuards ){
			validateTraces(ta, pathConvalida, false);
		}
	}

	public void validateTraces(TimedAutomata ta,String pathConvalida, boolean useGuards){

		logger.debug("PARTIAL: "+_unmatchedEvents+" "+_violatedGuards+" "+_missingClocks+" "+_nonFinalStates);
		logger.debug("GLOBAL: "+totalInvalid+" "+unmatchedEvents+" "+violatedGuards+" "+missingClocks+" "+nonFinalStates);
		logger.debug("Path convalida: "+pathConvalida);

		logger.info("Use guards: "+pathConvalida+" "+useGuards);



		Validation validation = new Validation(ta,validateAbsoluteClocks,false,useGuards, (! includeNestedCallsTime), traceErrorSequences );
		validation.setRecordInvalidTraces( false );
		validation.setCollectOnlyMainError(true);

		ObserverValidateTrace oValidate = new ObserverValidateTrace(1,validation);


		Parser parser2 = new Parser(oValidate);
		parser2.readFolder(pathConvalida);

		List<Trace> valid = oValidate.getValidTraces();
		List<Trace> invalid = oValidate.getInvalidTraces();



		File tracesToValidate = new File(pathConvalida);
		String[] toValidate = tracesToValidate.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".csv");
			}
		});

		checkIfAllTracesValidated(ta, pathConvalida, valid, invalid, toValidate);

		logger.info("Result for traces in "+pathConvalida+" "+" valid:"+valid.size()+" invalid: "+invalid.size());
		int currentValid = valid.size();
		int currentInvalid = invalid.size();


		if ( useGuards ) {
			_Valid = currentValid;
			_Invalid = currentInvalid;

			totalValid += _Valid;
			totalInvalid += _Invalid;





			logger.debug("PARTIAL: "+_Invalid);

			_nonFinalStates = 0;
			_unmatchedEvents = 0;
			_violatedGuards = 0;
			_missingClocks = 0;
			Collection<ValidationError> errors = oValidate.getMainErrors();
			for( ValidationError e : errors ){
				switch ( e.getErrorType() ){
				case UNMATCHED_EVENT:
					_unmatchedEvents++;
					break;
				case VIOLATED_GUARD:
					_violatedGuards++;
					break;
				case MISSING_CLOCK:
					_missingClocks++;
					break;
				case NOT_FINAL:
					_nonFinalStates++;
					break;
				default:
					throw new IllegalStateException("Unknown type: "+e.getErrorType());
				}
			}



			if ( errors.size() != _Invalid ){
				throw new IllegalStateException("Number of reported errors does not coincide with number of invalid traces");
			}

			if ( _unmatchedEvents + _violatedGuards + _missingClocks + _nonFinalStates != _Invalid ){
				throw new IllegalStateException("Number of counted errors does not coincide with number of invalid traces");
			}


			recordTracesInfo( valid, invalid, oValidate, tracesToValidate );

			unmatchedEvents += _unmatchedEvents;
			violatedGuards += _violatedGuards;
			missingClocks += _missingClocks;
			nonFinalStates += _nonFinalStates;

		} else {
			_ValidNoGuards = currentValid;
			_InvalidNoGuards = currentInvalid;
			totalValidNoGuards += _ValidNoGuards;
			totalInvalidNoGuards += _InvalidNoGuards;	
		}



	}



	private BufferedWriter errorMapRecorder;
	private void recordTracesInfo(List<Trace> valid, List<Trace> invalid, ObserverValidateTrace oValidate, File folderWithKFold) {
		if ( errorMapRecorder == null ){
			return;
		}

		try {
			for( Trace t : valid ){
				errorMapRecorder.write( t.getFilePath() + ",ACCEPTED," + configurationKey );
				errorMapRecorder.newLine();
			}

			for( Trace t : invalid ){
				ValidationError error = oValidate.getError(t);
				if ( traceErrorSequences ){
					int i = 1;
					for ( ValidationError errorSequenceItem : error.getErrorSequence()  ){
						recordErrorInfo(t, errorSequenceItem, i);
						i++;
					}
				} else {
					recordErrorInfo(t, error, 1);
				}
				
			}

		} catch (IOException e) {
			logger.catching(e);
		}
	}




	public void recordErrorInfo(Trace t, ValidationError error, int positinInSequence) throws IOException {
		errorMapRecorder.write( configurationKey +"," + t.getFilePath() + "," + positinInSequence + "," + error.getErrorType() +","+ error.getActivity()+ "," + error.getLine()+ "," + error.clause + "," + error.actualValue );
		errorMapRecorder.newLine();
	}




	private void checkIfAllTracesValidated(TimedAutomata ta,
			String pathConvalida, List<Trace> valid, List<Trace> invalid,
			String[] toValidate) {
		if ( toValidate.length != ( invalid.size()+valid.size() ) ){
			String msg = "valid: ";
			for ( Trace v : valid){
				msg+=v.getFilePath()+" ";
			}

			msg += " invalid:";
			for ( Trace v : invalid){
				msg+=v.getFilePath()+" ";
			}

			msg += " files:";
			for ( String f : toValidate){
				msg+=f+" ";
			}

			throw new IllegalStateException("Did not validate all traces. Path:"+pathConvalida+" "+msg+" "+ta.getName());
		}
	}

}
