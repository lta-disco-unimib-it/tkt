package it.unimib.disco.lta.timedKTail.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections15.multimap.MultiHashMap;

import it.unimib.disco.lta.timedKTail.traces.MaxExecutionTimeFinder;
import it.unimib.disco.lta.timedKTail.traces.Parser;


public class FailingTestsFinder {

	private static final String TRACES_AD_HOC_TESTS = "Traces_AdHocTests";
	private static final String TRACES_DEV_TESTS = "Traces_DevTests";
	private static final String TRACES_DEV_TESTS_ADDITIONAL = "Traces_DevTestsAdditional";
	
	private double overHeadThreshold;
	private String methodUnderTest;
	private HashMap<String, Double> passing;
	private HashMap<String, Double> failing;
	private double errorPercentage;
	private double maxOverheadDevTests;
	
	private long minSignificantTime = 1;
	private HashMap<String, Double> undecided;
	private HashMap<String, Double> devTests; 

	private boolean testCasesCallsOnly = Boolean.parseBoolean(System.getProperty("failingTestsFinder.testCasesCallsOnly","true"));
	
	public FailingTestsFinder(String methodUnderTest) {
		this.methodUnderTest = methodUnderTest;
	}

	public static void main(String[] args) {

		String methodUnderTest = args[0];
		File faultyPrj = new File( args[1] );
		File fixedPrj = new File( args[2] );
		
		File dest = new File( args[3] );
		
		FailingTestsFinder ftf = new FailingTestsFinder( methodUnderTest );
		ftf.identifyPassingAndFailing(faultyPrj, fixedPrj);
		
		dest.mkdir();
		
		System.out.println("Overhead dev tests: "+ftf.getMaxOverheadDevTests());
		System.out.println("Error percentage: "+ftf.getErrorPercentage());
		System.out.println("Overhead threashold: "+ftf.getOverHeadThreshold());
		
		File p = new File( dest, "passingTests.csv" );
		write( ftf.getPassing(), p.getAbsolutePath(), null, false );
		
		File f = new File( dest, "failingTests.csv" );
		write( ftf.getFailing(), f.getAbsolutePath(), null, false );
		
		File u = new File( dest, "undecidedTests.csv" );
		write( ftf.getUndecided(), u.getAbsolutePath(), null, false );
		
		File a = new File( dest, "all.csv" );
		write( ftf.getPassing(), a.getAbsolutePath(), "PASS", false );
		write( ftf.getFailing(), a.getAbsolutePath(), "FAIL", true );
		write( ftf.getUndecided(), a.getAbsolutePath(), "UNKNOWN", true );
		
		File d = new File( dest, "devTests.csv" );
		write( ftf.getDevTests(), d.getAbsolutePath(), null , false );
	}

	private HashMap<String, Double> getDevTests() {
		// TODO Auto-generated method stub
		return devTests;
	}

	public double getOverHeadThreshold() {
		return overHeadThreshold;
	}

	public double getErrorPercentage() {
		return errorPercentage;
	}

	public double getMaxOverheadDevTests() {
		return maxOverheadDevTests;
	}
	
	private static void write(final Set<String> devTests, String fileName, boolean append ){
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(new File( fileName ), append));
			
			for ( String key : devTests ){
				bw.write(key);
				bw.newLine();
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if ( bw != null ){
				try {
					bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private static void write(final HashMap<String, Double> map, String fileName, String additional, boolean append) {
		
		ArrayList<String> sorted = new ArrayList<String>();
		sorted.addAll(map.keySet());
		
		
		Collections.sort(sorted, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return Double.compare( map.get(o1), map.get(o2) );
			}
		});
		
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(new File( fileName ), append));
			
			for ( String key : sorted ){
				bw.write(key+"\t"+map.get(key));
				if ( additional != null ){
					bw.write("\t"+additional);	
				}
				bw.newLine();
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if ( bw != null ){
				try {
					bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
				
	}

	public void identifyPassingAndFailing(File faultyPrj, File fixedPrj) {
		HashMap<String, Double> overheadAdHocTests = caclulateOverheadAdHocTests(faultyPrj, fixedPrj);
		
		HashMap<String, Double> overheadDevTests = caclulateOverheadDevTests(faultyPrj, fixedPrj);
		
		maxOverheadDevTests = maxValue( overheadDevTests );
		
		
		HashMap<String, Double> errorOverhead = calculateOverheadError(fixedPrj);
		errorPercentage = calculateAverageD( errorOverhead.values() );
		
		if ( errorPercentage < 0 ){
			errorPercentage = - errorPercentage;
		}
		
		overHeadThreshold = Math.max(errorPercentage, maxOverheadDevTests );
		
		populatePassingFailingTests( overheadAdHocTests );
		
		devTests = overheadDevTests;
		
		
	}

	private void populatePassingFailingTests(HashMap<String, Double> overheadAdHocTests) {
		System.out.println("Popoulating PASS/FAIL");
		
		passing = new HashMap<String, Double>();
		failing = new HashMap<String, Double>();
		undecided = new HashMap<String, Double>();
		
		double _overHeadThreshold = overHeadThreshold;
		double _errorPercentage = errorPercentage;
		
		
		
		if ( Double.isNaN( _overHeadThreshold ) ){
			System.out.println("_overHeadThreshold is NAN");
			_overHeadThreshold = 0.0;
		}
		
		if ( Double.isNaN( _errorPercentage ) ){
			System.out.println("_errorPercentage is NAN");
			_errorPercentage = 0.0;
		}
		
		
		for ( Entry<String, Double> e : overheadAdHocTests.entrySet() ){
			Double value = e.getValue();
			if ( value > _overHeadThreshold ){
				failing.put(e.getKey(), value);
			} else if ( value <= _errorPercentage ){
				passing.put(e.getKey(), value);
			} else {
				undecided.put(e.getKey(), value);
			}
		}
		
	}

	public HashMap<String, Double> getPassing() {
		return passing;
	}

	public HashMap<String, Double> getFailing() {
		return failing;
	}
	
	public HashMap<String, Double> getUndecided() {
		return undecided;
	}

	private static double maxValue(HashMap<String, Double> overheadDevTests) {
		double max = 0;
		
		for ( Entry<String, Double> e : overheadDevTests.entrySet() ){
			Double v = e.getValue();
			if ( v > max ){
				max = v;
			}
		}
		
		return max;
		
	}

	public HashMap<String, Double> caclulateOverheadAdHocTests(File faultyPrj, File fixedPrj ) {
		String fld = TRACES_AD_HOC_TESTS;
		
		return calculateOverhead(faultyPrj, fixedPrj, fld);
	}
	
	public HashMap<String, Double> caclulateOverheadDevTests(File faultyPrj, File fixedPrj ) {
		String fld = TRACES_DEV_TESTS;
		
		return calculateOverhead(faultyPrj, fixedPrj, fld);
	}

	
	public HashMap<String, Double> calculateOverheadError(File fixedPrj) {
		File additionalTracesFolder = new File( fixedPrj, TRACES_DEV_TESTS_ADDITIONAL );
		File referenceTracesFolder = new File( fixedPrj, TRACES_DEV_TESTS );
		
		return calculateOverhead(additionalTracesFolder, referenceTracesFolder);
	}
	
	
	
	
	public HashMap<String, Double> calculateOverhead(File faultyPrj, File fixedPrj, 
			String fld) {
		
		File faultyTracesFolder = new File( faultyPrj, fld );
		File fixedTracesFolder = new File( fixedPrj, fld );
		
		return calculateOverhead(faultyTracesFolder, fixedTracesFolder);
	}

	
	public HashMap<String, Double> calculateDelta(File faultyTracesFolder, File fixedTracesFolder) {
		MultiHashMap<String, Long> faultyPrjExecutionTime = processAdHocTestsForProject( faultyTracesFolder );
		MultiHashMap<String, Long> fixedPrjExecutionTime = processAdHocTestsForProject( fixedTracesFolder );

		if ( faultyPrjExecutionTime.isEmpty() ){
			System.out.println("!!! No traces in "+faultyTracesFolder.getAbsolutePath());
		}
		
		if ( fixedPrjExecutionTime.isEmpty() ){
			System.out.println("!!! No traces in "+fixedTracesFolder.getAbsolutePath());
		}
		
		
		HashMap<String, Double> avgFaultyPrjExecutionTime = calculateAverage( faultyPrjExecutionTime );
		HashMap<String, Double> avgFixedPrjExecutionTime = calculateAverage( fixedPrjExecutionTime );
		
		
		
		
		
		HashMap<String, Double> delta  = calculateDeltas( avgFaultyPrjExecutionTime, avgFixedPrjExecutionTime );
		
		return delta;
	}
	
	
	public HashMap<String, Double> calculateOverhead(File faultyTracesFolder, File fixedTracesFolder) {
		MultiHashMap<String, Long> faultyPrjExecutionTime = processAdHocTestsForProject( faultyTracesFolder );
		MultiHashMap<String, Long> fixedPrjExecutionTime = processAdHocTestsForProject( fixedTracesFolder );

		if ( faultyPrjExecutionTime.isEmpty() ){
			System.out.println("!!! No traces in "+faultyTracesFolder.getAbsolutePath());
		}
		
		if ( fixedPrjExecutionTime.isEmpty() ){
			System.out.println("!!! No traces in "+fixedTracesFolder.getAbsolutePath());
		}
		
		
		HashMap<String, Double> avgFaultyPrjExecutionTime = calculateAverage( faultyPrjExecutionTime );
		HashMap<String, Double> avgFixedPrjExecutionTime = calculateAverage( fixedPrjExecutionTime );
		
		printAndGetMaxExecutionTime(faultyTracesFolder, avgFaultyPrjExecutionTime);
		double maxExecutionTimeFixed = printAndGetMaxExecutionTime(fixedTracesFolder, avgFixedPrjExecutionTime);
		
		
		
		HashMap<String, Double> overhead  = calculateOverHead( avgFaultyPrjExecutionTime, avgFixedPrjExecutionTime );
		
		return overhead;
	}

	public double printAndGetMaxExecutionTime(File fixedTracesFolder, HashMap<String, Double> avgFixedPrjExecutionTime) {
		
		if ( avgFixedPrjExecutionTime.isEmpty() ){
			System.out.println("Max execution time for "+fixedTracesFolder.getName()+" : "+"No traces" );
			return 0;
		}
		
		Entry<String, Double> maxExecutionTimeEntry = findMaxExecutionTime( avgFixedPrjExecutionTime );		
		System.out.println("Max execution time for "+fixedTracesFolder.getName()+" : "+maxExecutionTimeEntry.getKey()+"\t"+maxExecutionTimeEntry.getValue() );
		
		return maxExecutionTimeEntry.getValue();
	}

	

	private Entry<String, Double> findMaxExecutionTime(HashMap<String, Double> avgFixedPrjExecutionTime) {
		Entry<String, Double> max = null;
		double maxValue = Double.MIN_VALUE;
		for( Entry<String, Double> e : avgFixedPrjExecutionTime.entrySet() ){	
			if ( e.getValue() > maxValue ){
				max = e;
				maxValue = max.getValue();
			}
		}
		
		return max;
	}
	
	
	private HashMap<String, Double> calculateDeltas(HashMap<String, Double> avgFaultyPrjExecutionTime,
			HashMap<String, Double> avgFixedPrjExecutionTime) {
		
		double minSignificantTimeD = minSignificantTime;
		
		HashMap<String,Double> result = new HashMap<>();
		for ( Entry<String, Double> e : avgFaultyPrjExecutionTime.entrySet() ){
			String key = e.getKey();
			Double faulty = e.getValue();
			Double fixed = avgFixedPrjExecutionTime.get(key);
			
			double delta = (faulty-fixed);
			
			if ( delta < 0 ){
				delta = -delta;
			}
			
			result.put(key,  delta);
			
		}
		
		return result;
	}
	
	

	private HashMap<String, Double> calculateOverHead(HashMap<String, Double> avgFaultyPrjExecutionTime,
			HashMap<String, Double> avgFixedPrjExecutionTime) {
		
		double minSignificantTimeD = minSignificantTime;
		
		HashMap<String,Double> result = new HashMap<>();
		for ( Entry<String, Double> e : avgFaultyPrjExecutionTime.entrySet() ){
			String key = e.getKey();
			Double faulty = e.getValue();
			Double fixed = avgFixedPrjExecutionTime.get(key);
			
			if ( fixed == null ){
				continue;
			}
			
			
			if ( fixed == 0.0 ){
				fixed += 1.0;
				faulty += 1.0;
			}
			
			double overhead = (faulty-fixed)/fixed;
			
			if ( Math.min(faulty, fixed) < minSignificantTimeD ){
				overhead = 0.0;
			}
			
			result.put(key,  overhead);
			
		}
		
		return result;
	}

	private HashMap<String, Double> calculateAverage(MultiHashMap<String, Long> faultyPrjExecutionTime) {
		
		HashMap<String,Double> result = new HashMap<>();
		for ( Entry<String, Collection<Long>> e : faultyPrjExecutionTime.entrySet() ){
			String k = e.getKey();
			Collection<Long> values = e.getValue();
			
			double mean = calculateAverageL( values );
			
			result.put(k, mean);
		}
		
		return result;
	}

	private double calculateAverageL(Collection<Long> values) {
		double v=0;
		
		for ( Number l : values ){
			v+=l.doubleValue();
		}
		
		return (double)v/(double)values.size();
	}
	
	
	private double calculateAverageD(Collection<Double> values) {
		double v=0;
		
		for ( Double l : values ){
			v+=l.doubleValue();
		}
		
		return (double)v/(double)values.size();
	}
	
	


	
	
	

	private MultiHashMap<String, Long> processAdHocTestsForProject(File folderWithTestsTraces) {
		
		File[] testFolders = folderWithTestsTraces.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		
		MultiHashMap<String,Long> prjsExecutionTime = new MultiHashMap<String, Long>();
		
		if ( testFolders == null ){
			throw new IllegalStateException("Folder does not contain traces folders: "+folderWithTestsTraces.getAbsolutePath());
		}
		
		for ( File testFolder : testFolders ){
			processTestFolder( prjsExecutionTime, testFolder );
		}
		
		return prjsExecutionTime;
	}

	private void processTestFolder(MultiHashMap<String,Long> prjsExecutionTime, File testFolder) {
		
		MaxExecutionTimeFinder o = new MaxExecutionTimeFinder( methodUnderTest );
		o.setTestCasesCallsOnly( testCasesCallsOnly );
		
		
		Parser p = new Parser(o);
		p.readFolder(testFolder.getAbsolutePath());
		
		String testFolderName = testFolder.getName();
		int runRunSeparator = testFolderName.lastIndexOf('_');
		
		String testName = testFolderName.substring(0, runRunSeparator );
		
		Long mt = o.getMaxExecutionTime();
		if ( mt != null ){
			prjsExecutionTime.put( testName, mt );
		}
		
		
	}

}
