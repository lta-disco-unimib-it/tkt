package it.unimib.disco.lta.timedKTail.JTMTime;

import java.io.Serializable;

import it.unimib.disco.lta.timedKTail.algorithm.KFuture;

public class NodeWithFuture extends Node implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1;
	
	private transient KFuture kFuture;
	
	public NodeWithFuture(boolean initial) {
		super(initial);
	}

	public KFuture getkFuture() {
		return kFuture;
	}

	public void setkFuture(KFuture kFuture) {
		this.kFuture = kFuture;
	}

}
