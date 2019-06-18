/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.traces;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unimib.disco.lta.timedKTail.util.Pool;

/**
 *
 * @author AleX
 */
public class Parser implements ParserI {
    
    private final ObserverTraceIm o;
    private final String deli1=";";
    private final char deli2=';';
    private final char deli3='(';
    private final char deli4=')';
    public static final String start="START";
    public static final String stop = "STOP";
    private static final Logger logger = LogManager.getLogger(Parser.class);
    private long nTrace;
	private Long initialTime;
	
	private boolean firstEvent = true;
    
	private LinkedList<Event> eventsStack = new LinkedList<Event>();
	private boolean normalizeTime = true; 
	
    public boolean isNormalizeTime() {
		return normalizeTime;
	}

	public void setNormalizeTime(boolean normalizeTime) {
		this.normalizeTime = normalizeTime;
	}

	public Parser(ObserverTraceIm o){
        this.o=o;
        this.nTrace=1;
    }
    
    @Override
    public void readFile(String ind){
        logger.debug("Lettura File: "+ind);
        readTraceFile(ind);
    }
    
    @Override
    public void readFolder(String ind){
            logger.debug("Inizio lettura cartella: "+ind);
            logger.trace("LEGGO CARTELLA:"+ind);
            
            for(File fs : listTraceFiles(ind)){
            	readTraceFile(fs.getPath());
                nTrace=1;
            }   
    }
    
    @Override
    public ObserverTraceIm getObserver(){
        return this.o;
    }
    
//    private void readFiles(String ind){
//        try {
//            BufferedReader br = new BufferedReader(new FileReader(ind));
//            StreamTokenizer st = new StreamTokenizer(br);
////            st.wordChars(deli3, deli4);
//            st.wordChars(';',';');
//            st.wordChars('.','.');
////            st.whitespaceChars(';', ';');
//            while (st.nextToken()!= StreamTokenizer.TT_EOF) {
//                StringTokenizer st1=new StringTokenizer(st.sval,deli1);
//                switch(st1.countTokens())
//                {
//                    case 1:
//                        if(st1.nextToken().equals(start)){
//                            this.o.startTrace(ind,nTrace);
//                            nTrace++;
//                        }else{
//                            this.o.processTrace();
//                        }
//                        break;  
//                    default:
//                            this.o.newEvent(this.newEvent(st1));
//                        break;
//                }
//            }
//            logger.debug("Traccia letta Correttamente");
//            br.close();
//            this.o.endTrace();
//        } catch (IOException ex) {
//            logger.debug("Errore Parser Traccia: "+ind+" errore: "+ex);
//        }  
//    }
    
    protected void readTraceFile(String ind){
    	
    	eventsStack = new LinkedList<>();
    	
        String linea;
        String[] lineaSplitta;
        try {
            BufferedReader br = new BufferedReader(new FileReader(ind)); 
            firstEvent = true;
            boolean started = false;
            boolean processed=false;
            while ((linea = br.readLine()) != null) {
                lineaSplitta = linea.split(";");
                switch(lineaSplitta.length)
                {
                    case 1:
                        if(lineaSplitta[0].equals(start)){
                        	firstEvent = true;
                            startTrace(ind);
                            started=true;
                        }else{
                            if(lineaSplitta[0].equals(stop)){
                            	processed=true;
                                this.o.processTrace();
                            }
                        }
                        break;  
                    default:
                    	if ( firstEvent && ! started ){
                    		startTrace(ind);
                    		started=true;
                    	}
                        Event e = newEvent(lineaSplitta);
                        this.o.newEvent(e);
                        break;
                }
            }
            
            if ( ! processed ){
            	processed=true;
                this.o.processTrace();
            }
            
            logger.debug("File read till the end");
            br.close();
            this.o.endTrace();
        } catch (IOException ex) {
        	logger.fatal("Errore Parser Traccia: "+ind,ex);
        } catch(Exception ex1){
        	logger.fatal("Errore Parser Traccia: "+ind,ex1);
        }  
    }

	public void startTrace(String ind) {
		this.o.startTrace(ind,nTrace);
		nTrace++;
	}
     
        
    private File[] listTraceFiles(String ind){
        File f = new File(ind);
        logger.debug("Processing: "+f.getAbsolutePath());
        if (f.isDirectory()){
            return f.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					return pathname.getName().endsWith(".csv");
				}
			});
        }
        else{
            return null;
        }
    }
    
//    private Event newEvent(StringTokenizer st){        
//        Event e = new Event(st.nextToken(),
//            st.nextToken(),
//            st.nextToken(),
//            Long.valueOf(st.nextToken()),
//            st.nextToken());
//        logger.debug("evento: "+e.getAttivita()+" creato");
//        return e; 
//    }  
    
    
    public it.unimib.disco.lta.timedKTail.traces.Event newEvent(String[] s){
    	long time = Long.valueOf(s[3]);
    	
    	if ( normalizeTime ){
    		if ( firstEvent ){
    			initialTime = time;
    			time = 0L;
    			firstEvent = false;
    		} else {
    			time = time-initialTime;
    		}
    	}
    	
    	boolean isBegin;
    	if ( "B".equals(s[2]) ) {
    		isBegin=true;
    	} else if ( "E".equals(s[2]) ) {
    		isBegin=false;
    	} else {
    		throw new IllegalArgumentException("Illegal event type: "+s[2]);
    	}
    	
    	
    	it.unimib.disco.lta.timedKTail.traces.Event e;
    	
    	String idTask = StringFactory.INSTANCE.getCached(s[0]);
    	String activity = StringFactory.INSTANCE.getCached(s[1]);
        if( s.length == 4){
            e = new Event(idTask,activity,isBegin,time);
            
        }else{
            e = new Event(idTask,activity,isBegin,time,s[4]);
            
        }
        
        if ( isBegin ){
    		eventsStack.push(e);
    	} else {
    		Event correspondingBegin = eventsStack.pop();
    		e.setCorrespondingBegin(correspondingBegin);
    	}

        
        return e;
    }
    
}
