package it.unimib.disco.lta.timedKTail.tests.system;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import it.unimib.disco.lta.timedKTail.ui.InterfacciaBase;
import it.unimib.disco.lta.timedKTail.ui.PathProject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author AleX
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({TestInizializzazioneSistema.class,TestCreazioneTimedAutomata.class,TestVisualizzazioneTimedAutomata.class,
                    TestEliminazione.class})
public class TestSuite {

    public static String separator;
    public static InterfacciaBase ib;
    public static String user;
    
    public static File pathProjectTest;
    public static PathProject pathProject;
    public static String nameTecnica;
    public static File pathTimedAutomata;
    public static String conf;
    
    @BeforeClass
    public static void setUpClass() throws Exception {
       ib = new InterfacciaBase();
       separator = System.getProperty("file.separator");  
       user = System.getProperty("user.home");
       conf="3;1;1";
       nameTecnica="kTailTime.KTail";
       
       //Imposto oggetto File di progetto di test
       pathProjectTest = new File(user+separator+"TestProject");
       //Imposto l'oggetto PathProject
       ib.setPathForLoadProject(pathProjectTest);
       //Richiedo oggetto pathProject
       pathProject=ib.getPathProject();
       //creo cartella pathProject
       pathProjectTest.mkdir();
       //Creo cartelle progetto (4 cartelle)
       ib.createProject();
       pathTimedAutomata = new File(String.valueOf(pathProject.getFolderTA())+separator+"timedAutomata.jtm");
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
                
    }

    @After
    public void tearDown() throws Exception {
    }
    
    public static void createFile(){
        try {
            FileWriter fwP = new FileWriter(pathProject.getFolderTrace()+separator+"file.csv",false);
            fwP.write("START\n");
            for(long temp=98483941;temp<98483961;temp++){
                fwP.write("S02034;read1.0;B;"+temp+";a\n");
                fwP.flush();
            }
            for(long temp=98483961;temp<98483981;temp++){
                fwP.write("S02034;read1.0;E;"+temp+";a\n");
                fwP.flush();
            }
            fwP.write("STOP");
            fwP.close();
        } catch (IOException ex) {
            Logger.getLogger(TestCreazioneTimedAutomata.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
