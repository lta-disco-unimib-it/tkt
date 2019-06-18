package it.unimib.disco.lta.timedKTail.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import it.unimib.disco.lta.timedKTail.traces.Event;
import it.unimib.disco.lta.timedKTail.traces.ObserverTraceIm;
import it.unimib.disco.lta.timedKTail.traces.Parser;

public class AlphabetFilterObserver extends ObserverTraceIm {

	
	private Set<String> alphabet;

	public AlphabetFilterObserver(Set<String> alphabet) {
		this.alphabet = alphabet;
	}

	
	private File path;
	private BufferedWriter bw;
	private File newTrace;
	
	@Override
	public void newEvent(Event e) {
		
		if ( SKIP ){
			return;
		}
		
		String attivita = e.getAttivita();
		if ( ! alphabet.contains(attivita) ){
			return;
		}
		
		try {
			writeEventToTrace(e);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

	public void writeEventToTrace(Event e) throws IOException {
		
		BufferedWriter sb = bw;
		sb.append(e.getId_task());
		sb.append(';');
		sb.append(e.getAttivita());
		sb.append(';');
		sb.append(e.getTipologia());
		sb.append(';');
		sb.append(String.valueOf(e.getTimestamp()));
		sb.append(';');
		
		String infoSupp = e.getInfo_supp();
		if ( infoSupp != null ){
			sb.append(infoSupp);
			//sb.append(';');
		}
		
		bw.newLine();
	}
	
	Set<String> acceptableTestNames;
	public void setAcceptableTestNames(Set<String> acceptableTestNames) {
		this.acceptableTestNames = acceptableTestNames;
	}


	private boolean SKIP = false;

	@Override
	public void startTrace(String path, long nTrace) {
		
		File trace = new File( path );
		String testName = CommonAlphabetFilter.extractTestName(trace);
		if ( acceptableTestNames != null ){
			if ( ! acceptableTestNames.contains(testName) ){
				SKIP=true;
				return;
			}
		}
		
		SKIP=false;
		
		try {
			if ( bw == null ){
				this.path = new File( path );
				this.newTrace = new File( path+".new.csv" );
				bw = new BufferedWriter( new FileWriter(newTrace) );
			}
			
			bw.append(Parser.start);
			bw.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void endTrace() {
		
		if ( SKIP ){
			return;
		}
		
		try {
			
		
			
			bw.close();
			
			
			newTrace.renameTo(path);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		path=null;
		newTrace=null;
		bw=null;
	}

	@Override
	public void Error(String s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processTrace() {
		
		if ( SKIP ){
			return;
		}
		
		try {
			bw.append(Parser.stop);
			bw.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
