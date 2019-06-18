package it.unimib.disco.lta.timedKTail.validation;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import it.unimib.disco.lta.timedKTail.JTMTime.Activity;
import it.unimib.disco.lta.timedKTail.JTMTime.Node;
import it.unimib.disco.lta.timedKTail.JTMTime.TimedAutomata;
import it.unimib.disco.lta.timedKTail.JTMTime.TimedAutomataFactory;
import it.unimib.disco.lta.timedKTail.JTMTime.Transition;
import it.unimib.disco.lta.timedKTail.validation.NestingValidator.EmptyStack;
import it.unimib.disco.lta.timedKTail.validation.NestingValidator.Error;

public class NestingValidatorTest {

	public TimedAutomataFactory taf = new TimedAutomataFactory("test",false);
	
	@Test
	public void testValidOneStateOneLoop(){
		
        Transition tr;
        
        Node init = taf.getInitialState();
        Node n1 = taf.newNode(false);
		tr = taf.newTransition(init, n1, new Activity("a"), Transition.BEGIN);
		
		
		tr = taf.newTransition(n1, init, new Activity("a"), Transition.END);
		
		
		NestingValidator v = new NestingValidator();
		v.setMaxStateVisits(20);
		v.setMaxVisitDepth(20);
		
		List<Error> errors = v.validate(taf.getTimedAutomata());
		assertEquals( 0, errors.size() );
		
		assertEquals( 1, v.getVisitedPaths());
	}
	
	@Test
	public void testValidOneStateOneLoopError(){
		
        Transition tr;
        
        Node init = taf.getInitialState();
        Node n1 = taf.newNode(false);
		tr = taf.newTransition(init, n1, new Activity("a"), Transition.BEGIN);
		
		
		tr = taf.newTransition(n1, n1, new Activity("a"), Transition.END);
		
		
		NestingValidator v = new NestingValidator();
		v.setMaxStateVisits(20);
		v.setMaxVisitDepth(20);
		
		List<Error> errors = v.validate(taf.getTimedAutomata());
		assertEquals( 1, errors.size() );
		assertTrue ( errors.get(0) instanceof EmptyStack );
		
		assertEquals( 1, v.getVisitedPaths());
	}
	
	
	@Test
	public void testValidTwoStatesLoops(){
		Node init = taf.getInitialState();
        Transition tr;
        
        //s0-a->s1 
        //s1-aE->s0
        
        //s1-b->s2
        //s2-b:E->s0
        

        
        
        
        Node n1 = taf.newNode(false);
		tr = taf.newTransition(init, n1, new Activity("a"), Transition.BEGIN);
		
		
		tr = taf.newTransition(n1, init, new Activity("a"), Transition.END);
		
		
		Node n2 = taf.newNode(false);
		tr = taf.newTransition(n1, n2, new Activity("b"), Transition.BEGIN);
		
		
		tr = taf.newTransition(n2, init, new Activity("b"), Transition.END);
		
		{	
			NestingValidator v = new NestingValidator();
			v.setMaxStateVisits(20);
			v.setMaxVisitDepth(3);
			List<Error> errors = v.validate(taf.getTimedAutomata());
			assertEquals( 0, errors.size() );
			assertEquals( 2, v.getVisitedPaths());
		}
		
		{
			NestingValidator v = new NestingValidator();
			v.setMaxStateVisits(20);
			v.setMaxVisitDepth(4);
			List<Error> errors = v.validate(taf.getTimedAutomata());
			assertEquals( 0, errors.size() );
			assertEquals( 3, v.getVisitedPaths());
		}
		
		{
			NestingValidator v = new NestingValidator();
			v.setMaxStateVisits(20);
			v.setMaxVisitDepth(5);
			List<Error> errors = v.validate(taf.getTimedAutomata());
			assertEquals( 0, errors.size() );
			assertEquals( 4, v.getVisitedPaths());
		}
	}
}
