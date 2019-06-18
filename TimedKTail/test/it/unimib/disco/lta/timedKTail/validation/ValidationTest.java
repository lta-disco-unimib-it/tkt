package it.unimib.disco.lta.timedKTail.validation;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import it.unimib.disco.lta.timedKTail.JTMTime.TimedAutomata;
import it.unimib.disco.lta.timedKTail.algorithm.Policy;
import it.unimib.disco.lta.timedKTail.algorithm.Policy.MergeStrategies;
import it.unimib.disco.lta.timedKTail.tests.utils.TestUtils;
import it.unimib.disco.lta.timedKTail.traces.ObserverReadTraces;
import it.unimib.disco.lta.timedKTail.traces.Parser;
import it.unimib.disco.lta.timedKTail.traces.Trace;
import it.unimib.disco.lta.timedKTail.validation.NestingValidator.ValidationError.ErrorType;
import it.unimib.disco.lta.timedKTail.validation.Validation.ValidationError;

@RunWith(Parameterized.class)
public class ValidationTest {


	@Parameters(name = "{index}: trace: {0}, ms: {1}, useCaching: {2}, recursiveValidator: {3}, validateAbsoluteClocks: {4}, checkGuards: {5}, useMethodExecutionTime: {6}, traceErrorSequences: {7}")
	public static Collection<Object[]> data() {
		ArrayList<Object[]> list = new ArrayList<Object[]>();

		boolean[] TF = new boolean[]{true,false};
		boolean[] T = new boolean[]{true};
		boolean[] F = new boolean[]{false};

		String[] traces = new String[]{
				"storageTest/TestGuardia1.manyGuardsOnRelativeClocks/trace.csv",
				"storageTest/TestSimple1/trace.csv",
				"storageTest/TestGuardia1/trace.csv",
				"storageTest/TestGuardia2/trace.csv",
				"storageTest/TestGuardia2/trace.csv",
				"storageTest/TestGuardia7/trace.csv",
				"storageTest/TestGuardia7/trace.csv",
				"storageTest/TestTransizioni5/trace.csv",
				"storageTest/TestTransizioni4/trace.csv",
				"storageTest/TestTransizioni3/trace.csv",
				"storageTest/TestTransizioni2/trace.csv",
				"storageTest/TestTransizioni6/trace.csv",
				"storageTest/TestTransizioni1/trace.csv"
		};

		for ( String trace : traces ){
			for ( MergeStrategies ms : MergeStrategies.values() ){
				for ( boolean useCaching : TF ){
					for ( boolean recursiveValidator : TF ){
						for ( boolean validateAbsoluteClocks : TF ){
							for ( boolean checkGuards : TF ){
								for ( boolean useMethodExecutionTime : TF ){
									for ( boolean traceErrorSequences : TF ){
										list.add(new Object[]{
												trace,
												ms,
												useCaching,
												recursiveValidator, 
												validateAbsoluteClocks,
												checkGuards, 
												useMethodExecutionTime,
												traceErrorSequences});
									}
								}

							}


						}


					}	
				}
			}
		}

		return list;
	}


	@Parameter(0) // first data value (0) is default
	public /* NOT private */ String trace;

	@Parameter(1) // first data value (0) is default
	public /* NOT private */ MergeStrategies mergeStartegy;

	@Parameter(2)// first data value (0) is default
	public /* NOT private */ boolean useCaching;

	@Parameter(3)// first data value (0) is default
	public /* NOT private */ boolean recursiveValidator;

	public static int k = 2;
	private Policy poli;

	@Parameter(4)// first data value (0) is default
	public boolean validateAbsoluteClocks;

	public boolean checkForGuardsNotReset = false;

	@Parameter(5)// first data value (0) is default
	public boolean checkGuards;

	@Parameter(6)// first data value (0) is default
	public boolean useOnlyExcutionTimeSpentInsideMethod;

	@Parameter(7)// first data value (0) is default
	public boolean traceErrorSequences;


	private Map<String,List<ValidationError>> expectedErrorSequences_NoNestedCalls_NoAbsoluteClocks = new HashMap<String,List<ValidationError>>();
	private Map<String,List<ValidationError>> expectedErrorSequencesTransitions = new HashMap<String,List<ValidationError>>();
	private Map<String,List<ValidationError>> expectedErrorSequences_IncludingNestedCalls_NoAbsoluteClocks = new HashMap<String,List<ValidationError>>();


	@Before
	public void setUp() throws Exception {
		poli = new Policy(3,0,3,true,false);
		poli.setUseCaching( useCaching );
		poli.setMergeStrategy(mergeStartegy);


		{
			ArrayList<ValidationError> list = new ArrayList<>();
			list.add( new ValidationError(ValidationError.ErrorType.UNMATCHED_EVENT, "read5.0", "", 3) );
			expectedErrorSequencesTransitions.put("storageTest/TestGuardia1/trace.csv;storageTest/TestGuardia1/trace.csv.invalidTransition.1", list);
		}

		{
			ArrayList<ValidationError> list = new ArrayList<>();
			list.add( new ValidationError(ValidationError.ErrorType.UNMATCHED_EVENT, "read6.0", "", 8) );
			expectedErrorSequencesTransitions.put("storageTest/TestGuardia1/trace.csv;storageTest/TestGuardia1/trace.csv.invalidTransition.2", list);
		}

		{
			ArrayList<ValidationError> list = new ArrayList<>();
			list.add( new ValidationError(ValidationError.ErrorType.UNMATCHED_EVENT, "read7.0", "", 9) );
			expectedErrorSequencesTransitions.put("storageTest/TestGuardia1/trace.csv;storageTest/TestGuardia1/trace.csv.invalidTransition.3", list);
		}

		{
			ArrayList<ValidationError> list = new ArrayList<>();
			list.add( new ValidationError(ValidationError.ErrorType.VIOLATED_GUARD, "read1.0", "", 8) );
			expectedErrorSequences_NoNestedCalls_NoAbsoluteClocks.put("storageTest/TestGuardia1/trace.csv;storageTest/TestGuardia1/trace.csv.invalidGuard.1", list);
		}			


		{
			ArrayList<ValidationError> list = new ArrayList<>();
			list.add( new ValidationError(ValidationError.ErrorType.VIOLATED_GUARD, "read4.0", "", 5) );
			list.add( new ValidationError(ValidationError.ErrorType.VIOLATED_GUARD, "read3.0", "", 6) );
			list.add( new ValidationError(ValidationError.ErrorType.UNMATCHED_EVENT, "read5.0", "", 8) );
			//			list.add( new ValidationError(ValidationError.ErrorType.VIOLATED_GUARD, "read1.0", "", 10) );
			expectedErrorSequences_NoNestedCalls_NoAbsoluteClocks.put("storageTest/TestGuardia1.manyGuardsOnRelativeClocks/trace.csv;storageTest/TestGuardia1.manyGuardsOnRelativeClocks/trace.csv.invalidGuard.4", list);
		}

		{
			ArrayList<ValidationError> list = new ArrayList<>();
			list.add( new ValidationError(ValidationError.ErrorType.VIOLATED_GUARD, "read2.0", "", 7) );
			list.add( new ValidationError(ValidationError.ErrorType.VIOLATED_GUARD, "read1.0", "", 8) );
			expectedErrorSequences_NoNestedCalls_NoAbsoluteClocks.put("storageTest/TestGuardia1.manyGuardsOnRelativeClocks/trace.csv;storageTest/TestGuardia1.manyGuardsOnRelativeClocks/trace.csv.invalidGuard.3", list);
		}

		{
			ArrayList<ValidationError> list = new ArrayList<>();	
			list.add( new ValidationError(ValidationError.ErrorType.VIOLATED_GUARD, "read1.0", "", 8) ); //the error is in line 8 because in the model all the read2.0 events start in different transitions, so a guard for read2.0:E is not generated
			expectedErrorSequences_NoNestedCalls_NoAbsoluteClocks.put("storageTest/TestGuardia1/trace.csv;storageTest/TestGuardia1/trace.csv.invalidGuard.3", list);
		}

		{
			ArrayList<ValidationError> list = new ArrayList<>();	
			list.add( new ValidationError(ValidationError.ErrorType.VIOLATED_GUARD, "read1.0", "", 8) );
			expectedErrorSequences_NoNestedCalls_NoAbsoluteClocks.put("storageTest/TestGuardia1/trace.csv;storageTest/TestGuardia1/trace.csv.invalidGuard.2", list);
		}




		{
			ArrayList<ValidationError> list = new ArrayList<>();
			list.add( new ValidationError(ValidationError.ErrorType.VIOLATED_GUARD, "read1.0", "", 8) );
			expectedErrorSequences_IncludingNestedCalls_NoAbsoluteClocks.put("storageTest/TestGuardia1/trace.csv;storageTest/TestGuardia1/trace.csv.invalidGuard.1", list);
		}



		{
			ArrayList<ValidationError> list = new ArrayList<>();
			list.add( new ValidationError(ValidationError.ErrorType.VIOLATED_GUARD, "read1.0", "", 8) );
			expectedErrorSequences_IncludingNestedCalls_NoAbsoluteClocks.put("storageTest/TestGuardia1/trace.csv;storageTest/TestGuardia1/trace.csv.invalidGuard.2", list);
		}


		{
			ArrayList<ValidationError> list = new ArrayList<>();
			list.add( new ValidationError(ValidationError.ErrorType.VIOLATED_GUARD, "read4.0", "", 5) );
			list.add( new ValidationError(ValidationError.ErrorType.VIOLATED_GUARD, "read3.0", "", 6) );
			list.add( new ValidationError(ValidationError.ErrorType.VIOLATED_GUARD, "read2.0", "", 7) );
			list.add( new ValidationError(ValidationError.ErrorType.VIOLATED_GUARD, "read1.0", "", 8) );
			expectedErrorSequences_IncludingNestedCalls_NoAbsoluteClocks.put("storageTest/TestGuardia1.manyGuardsOnRelativeClocks/trace.csv;storageTest/TestGuardia1.manyGuardsOnRelativeClocks/trace.csv.invalidGuard.2", list);
		}




	}

	@Test
	public void testValidation() {
		testWithTrace(trace);
	}

	public void testWithTrace(String fTrace) {
		TimedAutomata ta;
		ta = TestUtils.inferAutomata(2, poli, fTrace,!useOnlyExcutionTimeSpentInsideMethod);

		//TestUtils.visualize(ta);

		TestUtils.checkInternalConsistency(ta);




		{	
			ObserverReadTraces oValidate = new ObserverReadTraces();
			Parser parser2 = new Parser(oValidate);
			parser2.readFile(fTrace);

			for ( Trace t : oValidate.getTraces() ){
				Validation v = new Validation(ta,validateAbsoluteClocks,checkForGuardsNotReset,checkGuards, useOnlyExcutionTimeSpentInsideMethod, traceErrorSequences);
				v.setUseRecursiveValidator(recursiveValidator);
				v.setRecordInvalidTraces(true);
				ValidationError result = v.validateTrace(t);
				assertNull( result );
			}
		}


		if ( checkGuards ){
			checkTracesWithInvalidGuards(fTrace, ta);
		}

		checkTracesWithEarlyTermination(fTrace, ta);

		checkTracesWithInvalidTransitions(fTrace, ta);
	}

	public void checkTracesWithEarlyTermination(String fTrace, TimedAutomata ta) throws AssertionError {
		{

			int idx = 1;
			while ( true ){
				String fTracef = fTrace+".earlyTermination."+idx;
				File f = new File( fTracef );
				if ( ! f.exists() ){
					break;
				}
				idx++;


				ObserverReadTraces oValidate = new ObserverReadTraces();
				Parser parser2 = new Parser(oValidate);
				parser2.readFile(fTracef);

				for ( Trace t : oValidate.getTraces() ){
					Validation v = new Validation(ta,validateAbsoluteClocks,checkForGuardsNotReset,checkGuards, useOnlyExcutionTimeSpentInsideMethod, traceErrorSequences);
					v.setUseRecursiveValidator(recursiveValidator);
					List<ValidationError> result = v.validateTraceReturnAllErrors(t);

					try {
						assertTrue("Error not found in trace: "+fTracef +" (total errors reported: "+result.size()+")", TestUtils.checkContainErrorType( Validation.ValidationError.ErrorType.NOT_FINAL, result ) );
					} catch ( AssertionError a ){
						throw a;
					}
				}

			}

		}
	}

	public void checkTracesWithInvalidTransitions(String fTrace, TimedAutomata ta) throws AssertionError {
		int idx = 1;


		while ( true ){
			String fTracef = fTrace+".invalidTransition."+idx;
			File f = new File( fTracef );
			if ( ! f.exists() ){
				break;
			}
			idx++;

			ObserverReadTraces oValidate = new ObserverReadTraces();
			Parser parser2 = new Parser(oValidate);
			parser2.readFile(fTracef);

			for ( Trace t : oValidate.getTraces() ){
				Validation v = new Validation(ta,validateAbsoluteClocks,checkForGuardsNotReset,checkGuards, useOnlyExcutionTimeSpentInsideMethod, traceErrorSequences);
				v.setUseRecursiveValidator(recursiveValidator);
				v.setRecordInvalidTraces(true);
				v.setRecordErrorMessage(true);
				ValidationError result = v.validateTrace(t);
				assertNotNull( result );



				if ( traceErrorSequences ){

					Map<String, List<ValidationError>> map = expectedErrorSequencesTransitions;



					if ( map != null)
						checkExpectedSequence(map, fTrace, fTracef, result);
				}
			}

		}
	}

	public void checkTracesWithInvalidGuards(String fTrace, TimedAutomata ta) throws AssertionError {
		int idx = 1;


		while ( true ){
			String fTracef = fTrace+".invalidGuard."+idx;
			File f = new File( fTracef );
			if ( ! f.exists() ){
				break;
			}
			idx++;

			ObserverReadTraces oValidate = new ObserverReadTraces();
			Parser parser2 = new Parser(oValidate);
			parser2.readFile(fTracef);

			for ( Trace t : oValidate.getTraces() ){
				Validation v = new Validation(ta,validateAbsoluteClocks,checkForGuardsNotReset,checkGuards, useOnlyExcutionTimeSpentInsideMethod, traceErrorSequences);
				v.setUseRecursiveValidator(recursiveValidator);
				v.setRecordInvalidTraces(true);
				v.setRecordErrorMessage(true);
				ValidationError result = v.validateTrace(t);
				assertNotNull( result );



				if ( traceErrorSequences ){

					Map<String, List<ValidationError>> map = null;

					if ( validateAbsoluteClocks == false ){
						if ( useOnlyExcutionTimeSpentInsideMethod ){
							map = expectedErrorSequences_NoNestedCalls_NoAbsoluteClocks;
						} else {
							map = expectedErrorSequences_IncludingNestedCalls_NoAbsoluteClocks;
						}
					}

					if ( map != null)
						checkExpectedSequence(map, fTrace, fTracef, result);
				}
			}

		}
	}

	public void checkExpectedSequence(Map<String, List<ValidationError>> map, String fTrace, String fTracef,
			ValidationError result) throws AssertionError {
		List<ValidationError> actual = result.getErrorSequence();
		String key = fTrace+";"+fTracef;

		String msg = "Testing trace;checking trace:"+key;

		List<ValidationError> es = map.get(key);
		if ( es != null ){
			{
				ValidationError expectedError = es.get(0);
				ValidationError actualError = actual.get(0);

				try {
					checkErrorsEqual(msg+" MAIN ERROR: ", expectedError, actualError);
				} catch ( AssertionError a ){
					throw a;
				}
			}

			try {
				assertEquals(msg,es.size(), actual.size());
			} catch ( AssertionError a ){
				throw a;
			}
			for ( int i = 0, max = es.size(); i<max; i++ ){
				ValidationError expectedError = es.get(i);
				ValidationError actualError = actual.get(i);

				try {
					checkErrorsEqual(msg, expectedError, actualError);
				} catch ( AssertionError a ){
					throw a;
				}
			}
		}
	}

	public void checkErrorsEqual(String msg, ValidationError expectedError, ValidationError actualError) {
		try {
			assertEquals( msg, expectedError.getErrorType(), actualError.getErrorType() );
		} catch ( AssertionError a ){
			throw a;
		}
		assertEquals( msg, expectedError.getLine(), actualError.getLine() );
		assertEquals( msg, expectedError.getActivity(), actualError.getActivity() );

	}
}
