/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.tests.system;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import static it.unimib.disco.lta.timedKTail.tests.system.TestSuite.conf;
import static it.unimib.disco.lta.timedKTail.tests.system.TestSuite.createFile;
import static it.unimib.disco.lta.timedKTail.tests.system.TestSuite.ib;
import static it.unimib.disco.lta.timedKTail.tests.system.TestSuite.nameTecnica;
import static it.unimib.disco.lta.timedKTail.tests.system.TestSuite.pathProject;
import static it.unimib.disco.lta.timedKTail.tests.system.TestSuite.separator;
import static org.junit.Assert.*;



/**
 *
 * @author AleX
 */
public class TestCreazioneTimedAutomata {
    
    public TestCreazioneTimedAutomata() {

    }
    // configurare setConfigurazione con tutto, valore di k-tail e politica
    // settare tutti i path di progetto tramite il giusto oggetto
    //Creare un file con dentro la cartella traccia il file da leggere! 
    //crerlo nel modo giusto
    @BeforeClass
    public static void setUpClass() throws Exception {
        ib.setConfigurazione(conf);
        createFile();
        ib.Inferiscitracce(nameTecnica);
    }
    @Test
    public void testSetConfig() throws Exception{
        File pathTimedAutomata = new File(String.valueOf(pathProject.getFolderTA())+separator+"timedAutomata.jtm");
        //Verifico la creazione di Timed Automata
        assertTrue(pathTimedAutomata.exists());
    }
    @Test 
    public void testCartellaTracce2(){
        //verifico se la cartella traccia esiste
        assertTrue(pathProject.getFolderTrace().exists());
    }
    @Test
    public void testCartellaTracce(){
        //verifico path delle tracce
        assertNotNull(pathProject.getFolderTrace());
    }
    @Test
    public void testPolicy(){
       //Verifico se la politica non è null
        assertNotNull(ib.getPolicy()); 
    }
    @Test
    public void testConfigurazione(){
        //Verifico se la configurazione non è nulla
        assertNotNull(ib.getConfigurazione());
    }
    @Test
    public void testCartellaTimedAutomata(){
        //verifico se non è null la cartella del timedAutomata
        assertNotNull(pathProject.getFolderTA());
    }     
    @Test
    public void testSetTecnica(){
        //Verifico se il path che contiene le tecniche di inferenza non è null
        assertNotNull(ib.getPathTecniche());
    }
    @Test
    public void testTecnica(){
        //Verifico la tecnica
        assertEquals(ib.getTecnica(), nameTecnica);
    }
    @Test
    public void testTecnica2(){
        assertNotNull(ib.getTecnica());
    }
 




}
