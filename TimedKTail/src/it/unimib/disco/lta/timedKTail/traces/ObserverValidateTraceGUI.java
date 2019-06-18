/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.traces;
import static it.unimib.disco.lta.timedKTail.traces.Trace.getTrace;
import it.unimib.disco.lta.timedKTail.JTMTime.Pair;
import it.unimib.disco.lta.timedKTail.validation.Validation;
import it.unimib.disco.lta.timedKTail.validation.Validation.ValidationError;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 * @author AleX
 */
public class ObserverValidateTraceGUI extends ObserverTraceIm implements ObserverTrace{
        
    private static final Logger logger = LogManager.getLogger(ObserverValidateTraceGUI.class);
    private final int id_observer;
    private Trace trace;
    private final Validation validation;
    private long valida;
    private String path;
    private long nTrace;
    private String pathSave;
    private String separator;
    File fPositivi;
    File fNegativi;
    
    public ObserverValidateTraceGUI(int id,Validation v,File pathSave){
        separator = System.getProperty("file.separator");
        this.id_observer=id;
        this.validation=v;
        this.valida=0;
        this.nTrace=0;
        this.pathSave=pathSave.getPath();
        this.fPositivi = new File(this.pathSave+this.separator+"positivi.dat");
        this.fNegativi = new File(this.pathSave+this.separator+"negativi.dat");
        
        if(!this.fNegativi.exists()){
            try {
                this.fNegativi.createNewFile();
            } catch (IOException ex) {
                logger.fatal("Impossibile la creazione dei file risultati!");
            }
        }else{
            this.fNegativi.delete();
            try {
                this.fNegativi.createNewFile();
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(ObserverValidateTraceGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(!this.fPositivi.exists()){
            try {
                this.fPositivi.createNewFile();
            } catch (IOException ex) {
                logger.fatal("Impossibile la creazione dei file risultati!");
            }
        }else{
            this.fPositivi.delete();
            try {
                this.fPositivi.createNewFile();
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(ObserverValidateTraceGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }

    public int getIDRegisterObserver(){
        return id_observer;
    }

    @Override
    public void newEvent(Event e) {
        trace.addEvent(e);
        logger.debug("Aggiungo nuovo evento alla traccia "+e.getAttivita());
    }

    @Override
    public void startTrace(String path, long nTrace) {
        //istanzio nuova Traccia
        this.path=path;
        this.nTrace=nTrace;
        trace = getTrace(path,nTrace);
        logger.debug("Inizio Nuova Traccia");
    }

    @Override
    public void endTrace() {

    }

    @Override
    public void Error(String s) {
        logger.fatal(s);
    }
    
    public long getRisultatoValidazione(){
        logger.debug("Ritorno risultato validazione: "+valida);
        return valida;
    }
    
    public void processTrace(){
        if ( logger.isDebugEnabled() ){
        	logger.debug("Inizio processo di validazione traccia n: "+nTrace);
        }
        
        List<ValidationError> ris = this.validation.validateTraceReturnAllErrors(trace);
       
        
        logger.debug("Traccia: "+valida+" con valore -1 è valida, altrimenti il valore è riga dell'errore su traccia");
        logResult(ris);
    }
    public void logResult(List<ValidationError> ris){
        
        try {
            FileWriter fwP = new FileWriter(fPositivi,true);
            FileWriter fwN = new FileWriter(fNegativi,true);
            
			if(ris == null){
                
                
                fwP.write("NOME FILE TRACCIA:\n");
                fwP.write(path);
                fwP.write("\nNUMERO TRACCIA #"+String.valueOf(nTrace)+"\n");
                fwP.write("****************************************************\n");
                fwP.flush();
            }else{
                int rigError=-2;
                
                
                this.valida++;
                fwN.write("NOME FILE TRACCIA:\n");
                fwP.write(path);
                fwN.write("\nnumero traccia #"+String.valueOf(nTrace)+"\n");
                for(ValidationError coppia:ris){
                    if(rigError == -2){
                        fwN.write("Riga errore: "+coppia.getLine()+"\n"+coppia.getErrorType());
                        rigError = coppia.getLine();
                    }

                    if(rigError < coppia.getLine()){
                        fwN.write("Riga errore: "+coppia.getLine()+"\n"+coppia.getMsg());
                        rigError = coppia.getLine();
                    }else{
                        rigError = coppia.getLine();
                    }
                }
                fwN.write("*********************************************************\n");
                fwN.flush();
            } 
            fwP.close();
            fwN.close();
        } catch (IOException ex) {
            logger.fatal("Exception while processing results for "+path, ex);
        }
    }
}
