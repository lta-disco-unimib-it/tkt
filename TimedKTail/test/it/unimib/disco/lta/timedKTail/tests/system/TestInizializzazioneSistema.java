package it.unimib.disco.lta.timedKTail.tests.system;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static it.unimib.disco.lta.timedKTail.tests.system.TestSuite.pathProject;
import static it.unimib.disco.lta.timedKTail.tests.system.TestSuite.pathProjectTest;
import static it.unimib.disco.lta.timedKTail.tests.system.TestSuite.separator;
import static org.junit.Assert.*;

import org.junit.BeforeClass;

/**
 *
 * @author AleX
 */
public class TestInizializzazioneSistema {


    public TestInizializzazioneSistema() {  
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {

    }
   
    @Test
    public void testTracceValidare2(){
        assertTrue(pathProject.getFolderTraceValidation().exists());
    }
    @Test
    public void testCartellaTrace2(){
        assertTrue(pathProject.getFolderTrace().exists());
    }
    @Test
    public void testCartellaResult2(){
        assertTrue(pathProject.getFolderResult().exists());
    }
    @Test
    public void testCartellaTimedAutomata2(){
        assertTrue(pathProject.getFolderTA().exists());
    }
    @Test
    public void testCartellaProgetto2(){
        assertTrue(pathProject.getFolderProject().exists());
    }
    
    @Test
    public void testTracceValidare(){
        assertNotNull(pathProject.getFolderTraceValidation());
    }
    @Test
    public void testCartellaTrace(){
        assertNotNull(pathProject.getFolderTrace());
    }
    @Test
    public void testCartellaResult(){
        assertNotNull(pathProject.getFolderResult());
    }
    @Test
    public void testCartellaTimedAutomata(){
        assertNotNull(pathProject.getFolderTA());
    }
    @Test
    public void testCartellaProgetto(){
        assertNotNull(pathProject.getFolderProject());
    }
    
    @Test
    public void testTracceValidare3(){
        File pathTraveTraceValidationTest = new File(pathProjectTest+separator+"traceValidation");
        Assert.assertEquals(pathTraveTraceValidationTest, pathProject.getFolderTraceValidation());
    }
    @Test
    public void testCartellaTrace3(){
        File pathTraceTest = new File(pathProjectTest+separator+"trace");
        Assert.assertEquals(pathTraceTest, pathProject.getFolderTrace());
    }
    @Test
    public void testCartellaResult3(){
        File pathResultTest = new File(pathProjectTest+separator+"result");
        Assert.assertEquals(pathResultTest, pathProject.getFolderResult());
    }
    @Test
    public void testCartellaTimedAutomata3(){
        File pathTaTest = new File(pathProjectTest+separator+"timedAutomata");
        Assert.assertEquals(pathTaTest, pathProject.getFolderTA());
    }
    @Test
    public void testCartellaProgetto3(){
        Assert.assertEquals(pathProjectTest,pathProject.getFolderProject());
    }
        @Test
    public void testTracceValidare4(){
        assertTrue(pathProject.getFolderTraceValidation().isDirectory());
    }
    @Test
    public void testCartellaTrace4(){
        assertTrue(pathProject.getFolderTrace().isDirectory());
    }
    @Test
    public void testCartellaResult4(){
        assertTrue(pathProject.getFolderResult().isDirectory());
    }
    @Test
    public void testCartellaTimedAutomata4(){
        assertTrue(pathProject.getFolderTA().isDirectory());
    }
    @Test
    public void testCartellaProgetto4(){
        assertTrue(pathProject.getFolderProject().isDirectory());
    }
    
}
