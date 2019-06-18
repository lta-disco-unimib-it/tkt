package it.unimib.disco.lta.timedKTail.algorithm;

import it.unimib.disco.lta.timedKTail.JTMTime.Clause;
import it.unimib.disco.lta.timedKTail.JTMTime.Equal;

import java.util.ArrayList;
import java.util.List;

public class ClauseUtil {

	public static List<Long> extractValues(ArrayList<Clause> clauseDaFondere) {
		ArrayList<Long> result = new ArrayList<>(clauseDaFondere.size());
		
		for ( Clause c : clauseDaFondere ){
			result.add(extractValueFromEqualsOperation(c));
		}
		
		return result;
	}

	public static long extractValueFromEqualsOperation(Clause c) {
		return ((Equal)c.getOperation()).getExpectedValue();
	}

	public static double[] extractValuesAsDoubleArray(
			ArrayList<Clause> clauseDaFondere) {
		double result[] = new double[clauseDaFondere.size()];
		
		int i = 0;
		for ( Clause c : clauseDaFondere ){
			result[i++]=extractValueFromEqualsOperation(c);
		}
		
		return result;
	}

}
