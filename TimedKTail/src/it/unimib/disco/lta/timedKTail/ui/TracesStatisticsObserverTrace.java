package it.unimib.disco.lta.timedKTail.ui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections15.map.HashedMap;

import it.unimib.disco.lta.timedKTail.traces.Event;
import it.unimib.disco.lta.timedKTail.traces.ObserverTraceIm;

public class TracesStatisticsObserverTrace extends ObserverTraceIm {

	private Map<String,LinkedList<Long>> listsMap = new HashedMap<>();
	private Map<String,LinkedList<Long>> listsTime = new HashedMap<>();

	@Override
	public void newEvent(Event e) {
		LinkedList<Long> list = getStackList( e.getAttivita() );
		
		if ( e.isBegin() ){
			list.add( e.getTimestamp() );
		} else {
			Long last = list.pollLast();
			long delta = e.getTimestamp() - last;
			
			LinkedList<Long> listExec = getExecutionTimeList( e.getAttivita() );
			listExec.add(delta);
		}
	}

	private LinkedList<Long> getExecutionTimeList(String attivita) {
		return getList(listsTime,attivita);
	}
	
	private LinkedList<Long> getStackList(String attivita) {
		return getList(listsMap,attivita);
	}
	
	private LinkedList<Long> getList(Map<String,LinkedList<Long>> map,String attivita) {
		LinkedList<Long> list = map.get( attivita );
		if ( list == null ){
			list = new LinkedList<>();
			map.put(attivita, list);
		}
		return list;
	}

	@Override
	public void startTrace(String path, long nTrace) {
		// TODO Auto-generated method stub

	}

	@Override
	public void endTrace() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Error(String s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void processTrace() {
		for ( Entry<String, LinkedList<Long>> e : listsMap.entrySet()  ){
			if ( e.getValue().size() > 0 ){
				throw new IllegalStateException();
			}
		}
	}
	
	public Map<String,Long> getMinTable(){
		Map<String,Long> minMap = new HashMap<String,Long>();
		for ( Entry<String, LinkedList<Long>> even : listsTime.entrySet() ){
			String eventName = even.getKey();
			LinkedList<Long> listOFValues = even.getValue();
			long min = findMin( listOFValues );
			minMap.put( eventName, min );
		}
		
		return minMap;
	}
	
	public Map<String,Long> getMaxTable(){
		Map<String,Long> minMap = new HashMap<String,Long>();
		for ( Entry<String, LinkedList<Long>> even : listsTime.entrySet() ){
			String eventName = even.getKey();
			LinkedList<Long> listOFValues = even.getValue();
			long min = findMax( listOFValues );
			minMap.put( eventName, min );
		}
		
		return minMap;
	}

	private long findMin(LinkedList<Long> listOFValues) {
		Long min = null;
		for ( Long value : listOFValues ){
			if ( min  == null ){
				min = value;
			} else {
				if ( value < min ){
					min = value;
				}
			}
		}
		return min;
	}
	
	private long findMax(LinkedList<Long> listOFValues) {
		Long min = null;
		for ( Long value : listOFValues ){
			if ( min  == null ){
				min = value;
			} else {
				if ( value > min ){
					min = value;
				}
			}
		}
		return min;
	}

}
