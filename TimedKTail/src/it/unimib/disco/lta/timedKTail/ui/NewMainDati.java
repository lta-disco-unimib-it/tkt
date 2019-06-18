/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.ui;

import static it.unimib.disco.lta.timedKTail.traces.Statistica.getStatistica;
import static it.unimib.disco.lta.timedKTail.ui.Main.saveAutomata;
import static it.unimib.disco.lta.timedKTail.ui.Main.statistica;
import static it.unimib.disco.lta.timedKTail.ui.Main.validateTrace;
import it.unimib.disco.lta.timedKTail.JTMTime.TimedAutomata;
import it.unimib.disco.lta.timedKTail.algorithm.TimedKTail;
import it.unimib.disco.lta.timedKTail.algorithm.Policy;
import it.unimib.disco.lta.timedKTail.traces.ObserverTimedAutomataTraceBuilder;
import it.unimib.disco.lta.timedKTail.traces.ObserverValidateTrace;
import it.unimib.disco.lta.timedKTail.traces.Parser;
import it.unimib.disco.lta.timedKTail.traces.Statistica;
import it.unimib.disco.lta.timedKTail.ui.Main;
import it.unimib.disco.lta.timedKTail.validation.Validation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.logging.Level;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.FileUtils;
import org.apache.commons.io.*;


/**
 *
 * @author AleX
 */
public class NewMainDati {
    
//    private static final String fTrace="storage/tracceCreazione/trace.csv";
    private static final String fTraces="storage/Tracce";
    private static final String fTracesCR="storage/Crea";
    private static final String fTracesCC="storage/Valida";
    //private static final String fTraces="storage";
    //*********POLITICHE PER UNA SINGOLA CLAUSA*********//
    // VALORE 1: POLITICA CONSERVATIVA
    // VALORE 2: POLITICA CONSERVATIVA CON PARAMETRO K -> kPoliticsOneVal
    // VALORE 3: POLITICA ELIMINAZIONE
    private static final int politicsOneVal=1;
    private static final int kPoliticsOneVal=3;
    //*********POLITICHE PER UNA MULTY CLAUSA*********//
    // VALORE 1: POLITICA MINORANZA
    // VALORE 2: POLITICA MAGGIORANZA
    // VALORE 3: POLITICA INTERVALLO
    private static final int politicsMultyVal=2;
    //path di salvataggio automi
    private static final String pathSaveTA = "save/timedAutomata/";
    private static final Logger logger = LogManager.getLogger(NewMainDati.class);
    
     public static void main(String[] args) throws IOException{
        boolean stop = true;
        long nTrace = 0;
        //FILE TRACCE BASE
        File file = new File("storage/Tracce");
        //FILE TRACCE INFERIRE AUTOMA
        File fCrea = new File("storage/Crea");
        //FILE TRACCE CONVALIDARE
        File fConvalida = new File("storage/Valida");
         
        File f[] = file.listFiles();
        //passate esecuzioni
        svuotaCartelle(fCrea);
        svuotaCartelle(fConvalida);
            //GRUPPO 10
            System.out.println("ESECUZIONE GRUPPO 10--------------------------------");
            Date date = new Date();
            System.out.println(date.toString());
            int j=0;
            while(stop){
                System.out.println("GIRO NUMERO: "+j);
                for(int i=0;i<10;i++){
                    if(i!=j){
                        org.apache.commons.io.FileUtils.copyFileToDirectory(f[i], fCrea);
                    }
                }
                org.apache.commons.io.FileUtils.copyFileToDirectory(f[j], fConvalida);
                //esecuzione
                esegui(fCrea.getPath(),fConvalida.getPath());
                Statistica.reset();
                //svuotamento dello spazio
                svuotaCartelle(fCrea);
                svuotaCartelle(fConvalida);
                //incremento
                j++;
                if(j==10){
                    stop = false;
                }
            }
            //GRUPPO 20
            System.out.println("ESECUZIONE GRUPPO 20--------------------------------");
            date = new Date();
            System.out.println(date.toString());
            stop=true;
            j=0;
            int k=0;
            int kk=1;
            while(stop){
                System.out.println("GIRO NUMERO: "+j);
                for(int i=0;i<20;i++){
                    if((i!=k) && (i!=kk)){
                        org.apache.commons.io.FileUtils.copyFileToDirectory(f[i], fCrea);
                    }
                }
                org.apache.commons.io.FileUtils.copyFileToDirectory(f[k], fConvalida);
                org.apache.commons.io.FileUtils.copyFileToDirectory(f[kk], fConvalida);
                //esecuzione
                esegui(fCrea.getPath(),fConvalida.getPath());
                Statistica.reset();
                //svuotamento dello spazio
                svuotaCartelle(fCrea);
                svuotaCartelle(fConvalida);
                //incremento
                j++;
                k=k+2;
                kk=kk+2;
                if(j==10){
                    stop = false;
                }
            }
            //GRUPPO 30
            System.out.println("ESECUZIONE GRUPPO 30--------------------------------");
            date = new Date();
            System.out.println(date.toString());
            stop=true;
            j=0;
            k=0;
            kk=1;
            int kkk=2;
            while(stop){
                System.out.println("GIRO NUMERO: "+j);
                for(int i=0;i<20;i++){
                    if((i!=k) && (i!=kk) && (i!=kkk)){
                        org.apache.commons.io.FileUtils.copyFileToDirectory(f[i], fCrea);
                    }
                }
                org.apache.commons.io.FileUtils.copyFileToDirectory(f[k], fConvalida);
                org.apache.commons.io.FileUtils.copyFileToDirectory(f[kk], fConvalida);
                org.apache.commons.io.FileUtils.copyFileToDirectory(f[kkk], fConvalida);
                //esecuzione
                esegui(fCrea.getPath(),fConvalida.getPath());
                Statistica.reset();
                //svuotamento dello spazio
                svuotaCartelle(fCrea);
                svuotaCartelle(fConvalida);
                //incremento
                j++;
                k=k+3;
                kk=kk+3;
                kkk=kkk+3;
                if(j==10){
                    stop = false;
                }
            }


    }
     
     public static void svuotaCartelle(File path){
         for(File f:path.listFiles()){
             f.delete();
         }
     }
     
     public static void esegui(String pathSorgente,String pathConvalida){
        Integer k=2;
        TimedAutomata ta;
        Policy poli = new Policy(politicsOneVal,kPoliticsOneVal,politicsMultyVal,true,false);
        
        TimedKTail kt = new TimedKTail(k,poli);
        //valore primo del costruttore è identificativo dell'osservatore 
        ObserverTimedAutomataTraceBuilder o = new ObserverTimedAutomataTraceBuilder(1,kt);
	Parser parser = new Parser(o);
//	parser.readFile(fTrace);
        parser.readFolder(pathSorgente);
//        drawGraph1(kt.getTimedAutomata(),"Stadio1"+"pathSaveTA: "+fTraces);
        
        kt.resolve();
        ta=kt.getTimedAutomata();
//        drawGraph1(ta,"Stadio2"+"path: "+fTraces);
//        saveAutomata(ta);
//        drawGraph1(loadAutomata(),"caricatoFILE");
        
        //saveAutomata(ta);
        //validazione tracce
        validateTrace(ta,pathConvalida);
        statistica(ta);
        getStatistica();
        System.out.println("0000000000000000000000000000000000000000000000000000000000000000000");
     }
     
    public static void validateTrace(TimedAutomata ta,String pathConvalida){
        Validation validation = new Validation(ta,false,false,true,false,false);
        ObserverValidateTrace oValidate = new ObserverValidateTrace(1,validation);
        Parser parser2 = new Parser(oValidate);
        parser2.readFolder(pathConvalida);
    }
    
}
