package it.unimib.disco.lta.timedKTail.algorithm;

import java.util.HashMap;
import java.util.Map;

import it.unimib.disco.lta.timedKTail.JTMTime.Activity;

public class ActivityFactory {

	private Map<String,Activity> activites = new HashMap<String,Activity>();
	
	public Activity newActivity(String attivita) {
		Activity activity = activites.get(attivita);
		
		if ( activity == null ){
			activity = new Activity(attivita);
			activites.put(attivita, activity);
		}
		
		return activity;
	}

}
