package it.unimib.disco.lta.timedKTail.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import it.unimib.disco.lta.timedKTail.traces.Event;
import it.unimib.disco.lta.timedKTail.traces.ObserverTraceIm;

public class MethodRenamerObserver extends ObserverTraceIm {

	private String replacement;
	private String regexp;
	private Pattern pattern;

	public MethodRenamerObserver(String regexp, String replacement) {
		this.regexp = regexp;
		this.replacement = replacement;
		pattern = Pattern.compile(regexp);
	}

	List<String> events = new LinkedList<>();
	private int level;
	private String path;
	
	@Override
	public void newEvent(Event e) {
		
		boolean trace = false;
		if ( e.isBegin() ){
			level++;
			
			if ( level == 1 ){
				trace=true;
			}
			
		} else {
			
			if ( level == 1 ){
				trace=true;
			}
			
			level--;
		}
		
		if ( ! trace ){
			return;
		}
		
		String attivita = e.getAttivita();
		
		StringBuffer sb = new StringBuffer();
		sb.append(e.getId_task());
		sb.append(';');
		
		int classEnd = attivita.lastIndexOf('.');
		String clazz = attivita.substring(0, classEnd );
		String meth = attivita.substring(classEnd);
		if ( pattern.matcher(clazz).matches() ){
			sb.append(replacement+meth);
		} else {
			sb.append(attivita);
		}
		sb.append(';');
		sb.append(e.getTipologia());
		sb.append(';');
		sb.append(e.getTimestamp());
		//sb.append(';');
		
		events.add(sb.toString());
	}

	@Override
	public void startTrace(String path, long nTrace) {
		//events.add("START");
		this.path = path;
	}

	@Override
	public void endTrace() {
		
		try {
			BufferedWriter bw = new BufferedWriter( new FileWriter(path+".new.csv") );
			
			for ( String line : events ){
				bw.write(line);
				bw.newLine();
			}
			
			bw.close();
			
			File trace = new File( path );
			File backupTrace = new File( path+".bak" );
			trace.renameTo(backupTrace);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		path=null;
		events = new LinkedList<>();
	}

	@Override
	public void Error(String s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processTrace() {
		// TODO Auto-generated method stub
		
	}

}
