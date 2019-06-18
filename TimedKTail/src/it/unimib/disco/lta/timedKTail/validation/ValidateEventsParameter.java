package it.unimib.disco.lta.timedKTail.validation;

import java.util.LinkedList;

import it.unimib.disco.lta.timedKTail.JTMTime.Node;
import it.unimib.disco.lta.timedKTail.JTMTime.Pair;
import it.unimib.disco.lta.timedKTail.JTMTime.Transition;
import it.unimib.disco.lta.timedKTail.traces.Event;
import it.unimib.disco.lta.timedKTail.traces.Trace;

public class ValidateEventsParameter {
	public Trace t;
	public int nEvento;
	public Node nodeN;
	public LinkedList<Pair<Event, Transition>> traceClock;
	public boolean doPost;
	public int positionInVisit;
	

	public ValidateEventsParameter(Trace t, int nEvento, Node nodeN, LinkedList<Pair<Event, Transition>> traceClock) {
		this.t = t;
		this.nEvento = nEvento;
		this.nodeN = nodeN;
		this.traceClock = traceClock;
	}
}