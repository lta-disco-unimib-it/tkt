/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.tests.system;

import it.unimib.disco.lta.timedKTail.JTMTime.TimedAutomata;
import it.unimib.disco.lta.timedKTail.ui.PathProject;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static it.unimib.disco.lta.timedKTail.tests.system.TestSuite.ib;
import static it.unimib.disco.lta.timedKTail.tests.system.TestSuite.pathProject;
import static org.junit.Assert.*;

/**
 *
 * @author AleX
 */
public class TestVisualizzazioneTimedAutomata {
    public static TimedAutomata ta;
    public static long nVertici;
    public static long nArchi;
    public static Object grafo;
    
    public TestVisualizzazioneTimedAutomata() {
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        ib.loadAutomata();
        ib.drawGraph1("Timed Automata");
        ta=ib.getTimedAutomata();
        nVertici=ib.getNVertex();
        nArchi=ib.getNEdge();
        grafo=ib.getGUIGraph();
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

    @Test
    public void testGUIGrafo(){
        assertNotNull(grafo);
    }
    @Test
    public void testTimedAutomataNode(){
        assertNotNull(ta.getNodes());
    }
        @Test
    public void testTimedAutomataEdge(){
        assertNotNull(ta.getTransitions());
    }
    @Test
    public void testTimedAutomata(){
        assertNotNull(ta);
    }
    public void testPathTimedAutomata(){
        assertNotNull(pathProject.getFolderTA());
    }
    @Test
    public void testNVertici(){
        assertEquals(nVertici, ta.getNodes().size());
    }
    @Test
    public void testNArchi(){
        assertEquals(nArchi, ta.getTransitions().size());
    }
}
