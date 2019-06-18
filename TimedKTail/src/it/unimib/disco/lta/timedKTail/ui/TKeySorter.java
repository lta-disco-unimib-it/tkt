package it.unimib.disco.lta.timedKTail.ui;


import java.util.Comparator;

public class TKeySorter implements Comparator<String> {

	@Override
	public int compare(String o1, String o2) {
		int p1 = o1.indexOf('T');
		int p2 = o2.indexOf('T');
		if ( p1 == -1 || p2 == -1 ){
			return o1.compareTo(o2);
		}
		
		Integer v1 = Integer.valueOf(o1.substring(p1+1));
		Integer v2 = Integer.valueOf(o2.substring(p2+1));
		return v1.compareTo(v2);
	}

}
