package it.unimib.disco.lta.timedKTail.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import it.unimib.disco.lta.timedKTail.traces.Event;
import it.unimib.disco.lta.timedKTail.traces.ObserverTraceIm;

public abstract class JPTSObserver extends ObserverTraceIm {
	

	protected HashMap<String,File> folders = new HashMap<String,File>();
	protected File destFolder;
	
	protected void storeTrace(List<Event> trace, File newTrace) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(newTrace));
		try{
			bw.write("START");
			bw.newLine();
			for( Event e : trace ){
				bw.write(e.getId_task()+";"+e.getAttivita()+";"+e.getTipologia()+";"+e.getTimestamp());
				bw.newLine();
			}
			bw.write("STOP");
			bw.newLine();
		} finally {
			bw.close();
		}
	}
	
	public JPTSObserver( File destFolder ){
		this.destFolder = destFolder;
	}

	protected File getFolder(String methodName) {
		File folder = folders.get( methodName );
		if ( folder == null ){
			String folderName = ""+folders.size();
			folder = new File( destFolder, folderName );
			folder.mkdir();
			folders.put(methodName, folder);
		}
		return folder;
	}

}