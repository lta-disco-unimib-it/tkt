package it.unimib.disco.lta.timedKTail.ui;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import it.unimib.disco.lta.timedKTail.traces.Event;

public class JPTSObserver_Method extends JPTSObserver {
	/**
	 * 
	 */
	private final JavaPerformanceTracesSplitter javaPerformanceTracesSplitter;


	public JPTSObserver_Method(JavaPerformanceTracesSplitter javaPerformanceTracesSplitter, File destFolder) {
		super(destFolder);
		this.javaPerformanceTracesSplitter = javaPerformanceTracesSplitter;
	}

	private int stackSize;
	private List<Event> currentTrace = new LinkedList<Event>();
	

	@Override
	public void newEvent(Event e) {

		if ( e.isBegin() ){
			if ( stackSize == 0 ){
				if ( ! e.getAttivita().matches(this.javaPerformanceTracesSplitter.getComponentName()) ){
					throw new IllegalStateException("First element should belong to component");
				}

				saveTrace();
			}

			stackSize++;
		} else {
			stackSize--;
		}

		currentTrace.add( e );
	}

	@Override
	public void startTrace(String path, long nTrace) {

	}

	@Override
	public void endTrace() {

	}

	@Override
	public void Error(String s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void processTrace() {

		saveTrace();

	}

	private void saveTrace() {
		if ( currentTrace.size() == 0 ){
			return;
		}

		try {
			String methodName = currentTrace.get(0).getAttivita();

			System.out.println(methodName);
			File folder = getFolder( methodName );

			String traceName = "trace."+folder.listFiles().length+".csv";
			File newTrace = new File( folder, traceName );

			storeTrace( currentTrace, newTrace );

			currentTrace= new LinkedList<>();
		} catch ( IOException e ){

		}

	}
}