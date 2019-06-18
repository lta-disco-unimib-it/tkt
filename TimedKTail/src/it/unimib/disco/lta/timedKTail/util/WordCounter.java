package it.unimib.disco.lta.timedKTail.util;

import java.util.HashMap;
import java.util.Map;

public class WordCounter {

	private Map<String, MutableInteger> freq = new HashMap<String, MutableInteger>();

	public int increment(String word){
		MutableInteger count = freq.get(word);
		if (count == null) {
			count = new MutableInteger(0);
			freq.put(word, count);
		}

		return count.increment();
	}
}
