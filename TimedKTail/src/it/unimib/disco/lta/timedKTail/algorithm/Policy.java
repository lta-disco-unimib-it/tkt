/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unimib.disco.lta.timedKTail.algorithm;

import static it.unimib.disco.lta.timedKTail.traces.Statistica.incrementaMulty;
import static it.unimib.disco.lta.timedKTail.traces.Statistica.incrementaSingola;
import it.unimib.disco.lta.timedKTail.JTMTime.Clause;
import it.unimib.disco.lta.timedKTail.JTMTime.Clock;
import it.unimib.disco.lta.timedKTail.JTMTime.GreaterEqual;
import it.unimib.disco.lta.timedKTail.JTMTime.Guard;
import it.unimib.disco.lta.timedKTail.JTMTime.Interval;
import it.unimib.disco.lta.timedKTail.JTMTime.LessEqual;

import it.unimib.disco.lta.timedKTail.statistics.NormalDistributionRangeGenerator;
import it.unimib.disco.lta.timedKTail.statistics.NormalDistributionRangeGenerator.Range;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import scala.Array;


/**
 *
 * @author AleX
 */
public class Policy {
	private static final Logger logger = LogManager.getLogger(Policy.class);
	//*********POLITICHE PER UNA SINGOLA CLAUSA*********//
	// VALORE 1: POLITICA CONSERVATIVA
	// VALORE 2: POLITICA CONSERVATIVA CON PARAMETRO K -> kPoliticsOneVal
	// VALORE 3: POLITICA ELIMINAZIONE
	private final int politicsOneVal;
	private final double kPoliticsOneVal;

	//********* POLICIES FOR MULTIPLE CLAUSES *********//
	// VALORE 1: POLITICA MAGGIORANZA
	// VALORE 2: POLITICA MINORANZA
	// VALORE 3: POLITICA INTERVALLO
	// VALORE 4: POLITICA INTERVALLO NORMALIZZATO
	private final int politicsMultyVal;
	private ArrayList<Clause> clausoleEliminarePerPoliElimina;

	private double deltaForRangeCalculation = 0;
	private double normalDistributionConfidence = 0.95;
	private boolean inferAbsoluteClocks = true;

	private boolean verifyAfterMerging = false;
	private boolean includeNestedCallsTime = false;
	
	private int maxVisitDepth = 0;
	private int maxStatesVisits = 2;
	
	//DEPTH_FIRST follows the trace insertion order
	//BREADTH_FIRST 
	//LAYERS_BACKWARD merge first nodes belonging to same layer, starts from the end to reduce the number of loops t verify
	public enum MergeStrategies { DEPTH_FIRST, BREADTH_FIRST, LAYERS_BACKWARD };
	private MergeStrategies mergeStrategy = MergeStrategies.valueOf( System.getProperty("mergeStrategy", "LAYERS_BACKWARD") );
	private boolean useCaching = Boolean.parseBoolean(System.getProperty("useCaching", "true"));
	private boolean cachePendingCalls;
	private boolean useIncrementalMerging;

	
	
	public int getMaxVisitDepth() {
		return maxVisitDepth;
	}

	public void setMaxVisitDepth(int maxVisitDepth) {
		this.maxVisitDepth = maxVisitDepth;
	}

	public int getMaxStatesVisits() {
		return maxStatesVisits;
	}

	public void setMaxStatesVisits(int maxStatesVisited) {
		this.maxStatesVisits = maxStatesVisited;
	}

	public void setVerifyAfterMerging(boolean verifyAfterMerging) {
		this.verifyAfterMerging = verifyAfterMerging;
	}

	public void setIncludeNestedCallsTime(boolean includeNestedCallsTime) {
		this.includeNestedCallsTime = includeNestedCallsTime;
	}

	public boolean isVerifyAfterMerging() {
		return verifyAfterMerging;
	}

	public boolean isIncludeNestedCallsTime() {
		return includeNestedCallsTime;
	}

	public Policy(int politicsOneVal, double kPoliticsOneVal, int politicsMultyVal, boolean includeNestedCallsTime, boolean validateAfterMerging){
		this.politicsOneVal = politicsOneVal;
		this.kPoliticsOneVal=kPoliticsOneVal;
		this.politicsMultyVal = politicsMultyVal;
		clausoleEliminarePerPoliElimina = new ArrayList();
		this.includeNestedCallsTime = includeNestedCallsTime;
		this.verifyAfterMerging = validateAfterMerging;
		
		logger.info("New Policy: verifyAfterMerging"+verifyAfterMerging);
	}


	public int getPoliticsOneVal(){
		return this.politicsOneVal;
	}
	public double getKPoliticsOneVal(){
		return this.kPoliticsOneVal;
	}
	public int getPoliticsMultyVal(){
		return this.politicsMultyVal;
	}

	public Guard applyPolicy(Guard g){
		if(g == null){
			logger.fatal("ATTENZIONE GUARDIA VUOTA!!!!");
			return null;
		}else{
			int dim=g.getSizeClause();              
			switch (dim) {
			case 0:
				return g;
			case 1:
				//guardia con una clausola
				incrementaSingola();
				applyPolicyOneClause(g);
				return g;
			default:
				//guardia con più clausole
				incrementaMulty();
				applyPolicyPlusClause(g);
				return g;
			}
		}
	}

	public boolean applyPolicyPlusClause(Guard g){

		ArrayList<Clause> clauseDaEliminare = new ArrayList();
		ArrayList<Clock> giaConfrontate = new ArrayList();
		//ciclo su tutte le clausole
		for(Clause c:g.getClauses()){
			//verifico se c è già stata elaborata
			if ( !giaConfrontate.contains(c.getClock()) ){
				//aggiungo subito c a quelle già analizzate
				giaConfrontate.add(c.getClock());
				//recupero tutte le clausole che hanno il clock uguale a c
				ArrayList<Clause> clauseDaFondere = getClauseFondere(g, c);

				boolean infer = true;
				if ( c.getClock().isAbsoluteClock() ){
					if ( ! inferAbsoluteClocks ){
						clauseDaEliminare.add(c);
						clauseDaEliminare.addAll(clauseDaFondere);
						infer=false;
					}
				}

				if ( infer ){
					//se ci sono clausole con clock uguale c (ossia da fondere)
					if( !clauseDaFondere.isEmpty() ){
						//modifico la clausola c con i nuovi valori elaborati da clauseDaFondere
						fusionClause(clauseDaFondere,c);
						//aggiungo le clausole usate per trovare il nuovo valore di c e le metto
						//in lista da eliminare
						clauseDaEliminare.addAll(clauseDaFondere);

					}else{
						//caso in cui c'è una clausola con un singolo valore di clock! (ma sn con guardia con n Clausole)
						//a questo clock devo applicare le politiche per un singolo valore di clock
						applyOneVal(c,g);
					}
				}


			}
		}
		g.deleteClauses(clauseDaEliminare);
		if(!clausoleEliminarePerPoliElimina.isEmpty()){
			g.deleteClauses(clausoleEliminarePerPoliElimina);
		}
		return true;
	}

	public boolean applyOneVal(Clause c,Guard g){
		switch (politicsOneVal) {
		case 1:
			applicoPoliticaConservativa(c);
			return true;
		case 2:
			applicoPoliticaConservativaK(c);
			return true;
		default:
			clausoleEliminarePerPoliElimina.add(c);
			return true;
		}

	}
	public boolean fusionClause(ArrayList<Clause> clauseDaFondere, Clause c){

		switch (politicsMultyVal) {
		case 1:
			applicoPoliticoMaggioranza(clauseDaFondere,c);
			return true;
		case 2:
			applicoPoliticaMinoranza(clauseDaFondere,c);
			return true;
		case 3:
			applicoPoliticaIntervallo(clauseDaFondere,c);
			return true;
		default:
			applicoPoliticaIntervalloDistribuzioneNormale(clauseDaFondere,c);
			return true;
		}
	}

	public boolean applicoPoliticaIntervallo(ArrayList<Clause> clauseDaFondere, Clause c){
		long min=Long.MAX_VALUE;
		for(Clause c1:clauseDaFondere){
			long v = ClauseUtil.extractValueFromEqualsOperation(c1);
			if(min>v){
				min=v;
			}
		}
		
		long max=Long.MIN_VALUE;
		for(Clause c1:clauseDaFondere){
			long v = ClauseUtil.extractValueFromEqualsOperation(c1);
			if(max<v){
				max=v;
			}
		}

		max += (max * deltaForRangeCalculation);
		min -= (min * deltaForRangeCalculation);

		modifyClauseIntervallo(max,min,c);
		return true;
	}

	public boolean applicoPoliticaIntervalloDistribuzioneNormale(ArrayList<Clause> clauseDaFondere, Clause c){

		NormalDistributionRangeGenerator g = new NormalDistributionRangeGenerator(normalDistributionConfidence);
		double[] values = ClauseUtil.extractValuesAsDoubleArray( clauseDaFondere );

		double[] valuesNew = new double[values.length+1];

		System.arraycopy(values, 0, valuesNew, 0, values.length);
		valuesNew[valuesNew.length-1] = ClauseUtil.extractValueFromEqualsOperation(c);

		Range range = g.calculateRange(valuesNew);

		long min = (long)Math.floor(range.getLower());
		long max = (long)Math.ceil(range.getUpper());
		
		
		long realMin = (long) findMin( valuesNew );
		long realMax = (long) ( findMax( valuesNew )  + 0.5 );

		if ( realMin < min ){
			min = realMin;
		}
		
		if ( realMax > max ){
			max = realMax;
		}
		
		c.setOperation(new Interval(min,max));
		
		return true;
	}

	private double findMin(double[] valuesNew) {
		double min = Double.MAX_VALUE;
		
		for ( int i = 0; i < valuesNew.length; i++ ){
			if ( min > valuesNew[i] ){
				min = valuesNew[i];
			}
		}
		
		return min;
	}
	
	private double findMax(double[] valuesNew) {
		double max = Double.MIN_VALUE;
		
		for ( int i = 0; i < valuesNew.length; i++ ){
			if ( max < valuesNew[i] ){
				max = valuesNew[i];
			}
		}
		
		return max;
	}

	public void modifyClauseIntervallo(Long max,Long min,Clause c){
		long v = ClauseUtil.extractValueFromEqualsOperation(c);
		if( v > max){
			max = v;
		}
		if(v < min){
			min = v;
		}
		c.setOperation(new Interval(min,max));
	}



	public boolean applicoPoliticaMinoranza(ArrayList<Clause> clauseDaFondere, Clause c){
		long max=Long.MIN_VALUE;
		for(Clause c1:clauseDaFondere){
			long v = ClauseUtil.extractValueFromEqualsOperation(c1);
			if(v > max){
				max=v;
			}
		}
		modifyClauseMIN(max,c);
		return true;
	}

	public void modifyClauseMIN(long min, Clause c){
		long v = ClauseUtil.extractValueFromEqualsOperation(c);
		if(v < min){
			min = v;
		}
		
		c.setOperation(new LessEqual(min));
		
	}

	public boolean applicoPoliticoMaggioranza(ArrayList<Clause> clauseDaFondere, Clause c){
		long min=Long.MAX_VALUE;
		for(Clause c1:clauseDaFondere){
			if(c.getClock().getId() == 25){
			}
			long v = ClauseUtil.extractValueFromEqualsOperation(c1);
			if(v < min){
				min=v;
			}
		}
		modifyClauseMAX(min,c);
		return true;
	}

	public void modifyClauseMAX(long max, Clause c){
		long v = ClauseUtil.extractValueFromEqualsOperation(c);
		if(v > max){
			max = v;
		}
		c.setOperation(new GreaterEqual(max));
	}

	//recuper tutte le clausole che hanno lo stesso clock dato c.getClock
	public ArrayList<Clause> getClauseFondere(Guard g,Clause c){
		ArrayList<Clause> fondere = new ArrayList<Clause>();
		for(Clause c1:g.getClauses()){
			if( (c.getClock().getId() == c1.getClock().getId()) && (!c1.equals(c)) ){
				fondere.add(c1);
			}
		}
		return fondere;
	}


	//applico politiche con un singolo valore di clausola!
	public boolean applyPolicyOneClause(Guard g){
		Clause clause = g.getClauses().iterator().next();
		
		if ( ! inferAbsoluteClocks ){
			if ( clause.getClock().isAbsoluteClock() ){
				g.deleteClause(clause);
				return true;	
			}
		}

		switch (politicsOneVal) {
		case 1:
			applicoPoliticaConservativa(clause);
			return true;
		case 2:
			applicoPoliticaConservativaK(clause);
			return true;
		default:
			g.deleteClause(clause);
			return true;
		}
	}

	public void applicoPoliticaConservativaK(Clause c){
		long val = ClauseUtil.extractValueFromEqualsOperation(c);
		val=Math.round(val+val*kPoliticsOneVal);
		c.setOperation(new LessEqual(val));
	}

	public void applicoPoliticaConservativa(Clause c){
		long val = ClauseUtil.extractValueFromEqualsOperation(c);
		c.setOperation(new LessEqual(val));
	}



	public double getDeltaForRangeCalculation() {
		return deltaForRangeCalculation;
	}

	public void setDeltaForRangeCalculation(double deltaForRangeCalculation) {
		this.deltaForRangeCalculation = deltaForRangeCalculation;
	}

	public double getNormalDistributionConfidence() {
		return normalDistributionConfidence;
	}

	public void setNormalDistributionConfidence(double normalDistributionConfidence) {
		this.normalDistributionConfidence = normalDistributionConfidence;
	}

	public void setInferGuardsForAbsoluteClocks(boolean inferAbsoluteClocks) {
		this.inferAbsoluteClocks = inferAbsoluteClocks;
	}

	public MergeStrategies getMergeStrategy() {
		return mergeStrategy;
	}

	public void setMergeStrategy(MergeStrategies mergeStrategy) {
		this.mergeStrategy = mergeStrategy;
	}

	public boolean isUseCaching() {
		return useCaching;
	}

	public void setUseCaching(boolean cacheKFutures) {
		this.useCaching = cacheKFutures;
	}

	public void setCachePendingCalls(boolean cachePendingCalls) {
		this.cachePendingCalls = cachePendingCalls;
	}

	public boolean isCachePendingCalls() {
		return cachePendingCalls;
	}

	public boolean isUseIncrementalMerging() {
		return useIncrementalMerging;
	}

	public void setUseIncrementalMerging(boolean useIncrementalMerging) {
		this.useIncrementalMerging = useIncrementalMerging;
	}

}
