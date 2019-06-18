/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.tests.system;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static it.unimib.disco.lta.timedKTail.tests.system.TestSuite.ib;
import static it.unimib.disco.lta.timedKTail.tests.system.TestSuite.pathProject;
import static it.unimib.disco.lta.timedKTail.tests.system.TestSuite.pathProjectTest;
import static it.unimib.disco.lta.timedKTail.tests.system.TestSuite.separator;
import static org.junit.Assert.*;

/**
 *
 * @author AleX
 */
public class TestEliminazione {
    public static File pathResultTest;
    public static File pathTaTest;
    public static File pathTraceTest;
    public static File pathTraveTraceValidationTest;
    
    public TestEliminazione() {
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        pathResultTest = new File(pathProjectTest+separator+"result");
        pathTaTest = new File(pathProjectTest+separator+"timedAutomata");
        pathTraceTest = new File(pathProjectTest+separator+"trace");
        pathTraveTraceValidationTest = new File(pathProjectTest+separator+"traceValidation");
        //Funzione per eliminazione del progetto
        ib.deleteProject();
    }
    @Test
    public void testDeleteCartellaProgetto(){
        assertFalse(pathProject.getFolderProject().exists());
    }
    @Test
    public void testDeleteCartelaResult(){
        assertFalse(pathResultTest.exists());
    }
    @Test
    public void testDeleteCartelaTA(){
        assertFalse(pathTaTest.exists());
    }
    @Test
    public void testDeleteCartelaTraceTrace(){
        assertFalse(pathTraceTest.exists());
    }
        @Test
    public void testDeleteCartelaTraceValidation(){
        assertFalse(pathTraveTraceValidationTest.exists());
    }
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

}
