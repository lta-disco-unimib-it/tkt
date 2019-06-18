package it.unimib.disco.lta.timedKTail.util;

public class MutableInteger {
	int value = 0;
	
	public MutableInteger(int v) {
		value = v;
	}
	
	public int increment () {  return ++value; }
	public int  get ()       { return value; }
}