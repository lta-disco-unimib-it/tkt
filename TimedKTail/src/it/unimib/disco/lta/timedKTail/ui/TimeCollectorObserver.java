package it.unimib.disco.lta.timedKTail.ui;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

import it.unimib.disco.lta.timedKTail.traces.Event;
import it.unimib.disco.lta.timedKTail.traces.ObserverTraceIm;

public class TimeCollectorObserver extends ObserverTraceIm {

	private boolean first;
	private long begin;
	private Event lastEvent;
	private String traceName;

	@Override
	public void newEvent(Event e) {
		// TODO Auto-generated method stub
		if ( first ){
			begin = e.getTimestamp();
			first=false;
		}
		lastEvent = e;
	}

	@Override
	public void startTrace(String path, long nTrace) {
		File traceFile = new File ( path );
		traceName = traceFile.getName().replace("trace", "");
		first=true;
	}

	private HashMap<String,Long> executionTime = new HashMap<String,Long>();
	
	@Override
	public void endTrace() {
		long lastTime = lastEvent.getTimestamp();
		
		executionTime.put( traceName, lastTime-begin);
	}

	@Override
	public void Error(String s) {

	}

	@Override
	public void processTrace() {
		// TODO Auto-generated method stub

	}

	public Set<String> getTraceNames() {
		return executionTime.keySet();
	}

	public long getTimeForTrace(String trace) {
		return executionTime.get(trace);
	}

}
