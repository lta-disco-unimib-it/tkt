package it.unimib.disco.lta.timedKTail.ui;

import java.util.Map;

import it.unimib.disco.lta.timedKTail.traces.ObserverTraceIm;
import it.unimib.disco.lta.timedKTail.traces.Parser;

public class CompareTracesCharacteristics {

	public static void main(String[] args) {
		TracesStatisticsObserverTrace o = new TracesStatisticsObserverTrace();


		String tracesPath = args[0];
		Parser parser = new Parser(o);
		parser.readFolder(tracesPath);


		Map<String, Long> maxT = o.getMaxTable();
		Map<String, Long> minT = o.getMinTable();


	}

}
