package it.unimib.disco.lta.timedKTail.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import it.unimib.disco.lta.timedKTail.traces.Event;
import it.unimib.disco.lta.timedKTail.traces.Parser;
import it.unimib.disco.lta.timedKTail.util.MutableInteger;


public class JPTSObserver_Object_File extends JPTSObserver {
	
	private static final int MAX_OPEN_FILES = 200;


	public JPTSObserver_Object_File(File destFolder) {
		super(destFolder);
	}

	private int stackSize;
	private Map<String,BufferedWriter> traces = new HashMap<String,BufferedWriter>();
	private Pattern pattern;
	

	public static void main(String args[]){
		File dstFolder = new File (args[0] );
		String tracePath = args[1];
		
		String patternString = null;
		if ( args.length > 2 ){
			patternString = args[2];
		}
		
		JPTSObserver_Object_File observer = new JPTSObserver_Object_File( dstFolder );
		if ( patternString != null ){
			observer.setPattern( patternString );
		}
		
		Parser p = new Parser(observer);
		p.readFile(tracePath);
		
		
	}
	
	private void setPattern(String patternString) {
		pattern = Pattern.compile(patternString);
	}

	@Override
	public void newEvent(Event e) {
		BufferedWriter bw = retrieve( e );
		try {
			writeEvent(bw, e);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	
	protected File getFolder(String typeName) {
		File folder = folders.get( typeName );
		if ( folder == null ){
			String folderName = typeName;
			folder = new File( destFolder, folderName );
//			if ( folder.exists() ){
//				folderElements.put( folder, folder.list().length );
//			} else {
//				
//			}
			folder.mkdir();
			folders.put(typeName, folder);
		}
		return folder;
	}

	LinkedList<BufferedWriter> bwList = new LinkedList<BufferedWriter>();
	HashMap<BufferedWriter,String> tracesReverse = new HashMap<>();
	
	private BufferedWriter retrieve(Event e) {
		String key = e.getId_task();
		
		if ( key.length() == 2 ){ //static, ignore
			return null;
		}
		
		BufferedWriter bw = traces.get(key);
		
		if ( bw == null ){

			closeOpenTracesIfTooMany();
			
			String type = extractType( e );
			
			if ( ! checkPattern(type) ){
				return null;
			}
			
			File folder = getFolder( type );
			String traceName = currentTraceFileName+".trace."+incrementFiles(folder)+".csv";
			File newTrace = new File( folder, traceName );
			
			
			
			System.out.println("NEW TRACE: "+newTrace.getAbsolutePath());
			
			try {
				bw = new BufferedWriter(new FileWriter(newTrace,true));
//				bw.write("START");
//				bw.newLine();
				bw.flush(); //force creation of the file
				
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
				
			tracesReverse.put(bw, key);
			traces.put(key, bw);
			bwList.addLast(bw);
		}
		
		return bw;
	}

	
	
	private HashMap<File,MutableInteger> foldersCounter = new HashMap<File,MutableInteger>();
	private String currentTraceFileName;
	private int incrementFiles(File folder) {
		MutableInteger counter = foldersCounter.get(folder);
		
		if ( counter == null ){
			counter = new MutableInteger(folder.list().length);
			
			foldersCounter.put(folder, counter);
		}
		return counter.increment();
	}

	private boolean checkPattern(String type) {
		if ( pattern == null ){
			return true;
		}
		
		return pattern.matcher(type).matches();
	}

	public void closeOpenTracesIfTooMany() {
		if ( traces.size() > MAX_OPEN_FILES ){
			int toClose = (MAX_OPEN_FILES/10);
			for ( int i = 0 ; i < toClose; i++ ){
				BufferedWriter bwToClose = bwList.removeFirst();
				try {
					bwToClose.close();
					String objId = tracesReverse.get(bwToClose);
					traces.remove(objId);
					tracesReverse.remove(objId);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
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
		
		for (  Entry<String, BufferedWriter> entries : traces.entrySet() ){
			String key = entries.getKey();
			
			BufferedWriter bw = entries.getValue();
			closeTrace(bw);
		}


		
		traces = new HashMap<>();
	}

	public void closeTrace(BufferedWriter bw) {
		try {
//			bw.write("STOP");
//			bw.newLine();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	public void writeEvent(BufferedWriter bw, Event e) throws IOException {
		if ( bw == null ){
			return;
		}
		
		bw.write(e.getId_task()+";"+e.getAttivita()+";"+e.getTipologia()+";"+e.getTimestamp());
		bw.newLine();
	} 


	private String extractType(Event firstEvent) {
		String methodName = firstEvent.getAttivita();
		int pos = methodName.lastIndexOf('.');
		return methodName.substring(0, pos);
	}
}