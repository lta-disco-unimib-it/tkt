package it.unimib.disco.lta.timedKTail.ui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.unimib.disco.lta.timedKTail.traces.Event;
import it.unimib.disco.lta.timedKTail.traces.Parser;

public class JPTSObserver_Object extends JPTSObserver {
	
	private String currentTraceFileName;


	public JPTSObserver_Object(File destFolder) {
		super(destFolder);
	}

	private int stackSize;
	private Map<String,List<Event>> traces = new HashMap<String,List<Event>>();
	

	public static void main(String args[]){
		File dstFolder = new File (args[0] );
		String tracePath = args[1];
		JPTSObserver_Object observer = new JPTSObserver_Object( dstFolder );
		Parser p = new Parser(observer);
		p.readFile(tracePath);
		

		
		
	}
	
	@Override
	public void newEvent(Event e) {
		List<Event> currentTrace = retrieve( e );
		currentTrace.add( e );
	}
	
	
	protected File getFolder(String typeName) {
		File folder = folders.get( typeName );
		if ( folder == null ){
			String folderName = typeName.replace(".", "_");
			folder = new File( destFolder, folderName );
			folder.mkdir();
			folders.put(typeName, folder);
		}
		return folder;
	}

	private List<Event> retrieve(Event e) {
		String key = e.getId_task();
		List<Event> trace = traces.get(key);
		if ( trace == null ){
			trace = new LinkedList<>();
			traces.put(key, trace);
		}
		
		return trace;
	}

	@Override
	public void startTrace(String path, long nTrace) {
		File traceFile = new File( path );
		currentTraceFileName = traceFile.getName();
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

		saveTraces();

	}

	private void saveTraces() {
		for ( List<Event> trace : traces.values() ){
			Event firstEvent = trace.get(0);
			
			String type = extractType( firstEvent );
			File folder = getFolder( type );

			String traceName = currentTraceFileName+".trace."+folder.listFiles().length+".csv";
			File newTrace = new File( folder, traceName );

			try {
				storeTrace( trace, newTrace );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		
		traces = new HashMap<>();
	}

	private String extractType(Event firstEvent) {
		String methodName = firstEvent.getAttivita();
		int pos = methodName.lastIndexOf('.');
		return methodName.substring(0, pos);
	}
}