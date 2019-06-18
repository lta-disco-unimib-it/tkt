package it.unimib.disco.lta.timedKTail.ui;

import java.io.File;
import java.util.Set;

import it.unimib.disco.lta.timedKTail.traces.ObserverTraceIm;
import it.unimib.disco.lta.timedKTail.traces.Parser;

public class ExecutionTimeComparator {

	public static void main(String[] args) {

		File t1Folder = new File ( args[0] );
		File t2Folder = new File ( args[1] );
		
		TimeCollectorObserver tc1 = new TimeCollectorObserver();
		{
			Parser parser = new Parser(tc1);
			parser.readFolder(t1Folder.getAbsolutePath());
		}
		
		TimeCollectorObserver tc2 = new TimeCollectorObserver();
		{
			Parser parser = new Parser(tc2);
			parser.readFolder(t2Folder.getAbsolutePath());
		}
		
		Set<String> tn1 = tc1.getTraceNames();
		Set<String> tn2 = tc2.getTraceNames();
		
		if ( ! tn1.equals(tn2) ){
			for ( String t : tn1 ){
				System.out.println(t);
			}
			
			System.out.println("TN2");
			
			for ( String t : tn2 ){
				System.out.println(t);
			}
			
			throw new IllegalArgumentException("TRace names are different");
		}
		
		
		double deltaAvg = 0.0;
		for ( String trace : tn1 ){
			Long t1 = tc1.getTimeForTrace( trace );
			Long t2 = tc2.getTimeForTrace( trace );
			
			long delta = t2 - t1;
			
			if ( t1 == 0 ){
				t1=1L;
			}
			double deltaP = delta / (double) t1;
			
			deltaAvg += deltaP;
		}
		
		double avgSlowDown = deltaAvg / (double) tn1.size();
		
		System.out.println(" Average slow down: "+avgSlowDown);
	}

}
