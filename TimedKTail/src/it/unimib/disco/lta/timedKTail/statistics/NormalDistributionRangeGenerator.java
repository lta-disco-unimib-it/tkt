package it.unimib.disco.lta.timedKTail.statistics;

import java.util.List;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unimib.disco.lta.timedKTail.algorithm.PhaseII;

public class NormalDistributionRangeGenerator {

	public static class Range {
		double lower;
		double upper;
		
		public Range(double lower, double upper) {
			this.lower = lower;
			this.upper = upper;
		}

		public double getLower() {
			return lower;
		}

		public double getUpper() {
			return upper;
		}
		
		
		
	}
	
	private double confidence;

	public NormalDistributionRangeGenerator( double confidence ){
		this.confidence = confidence;
	}
	
	public Range calculateRange( List<Integer> values ){
		
		double dvalues[] = new double[values.size()];
		for ( int i=0; i < dvalues.length; i++ ){
			dvalues[i]=values.get(i);
		}
		
		
		return calculateRange(dvalues);
	}

	public Range calculateRange(double[] dvalues) {
		Mean mean = new Mean();
		mean.setData(dvalues);
		double meanV = mean.evaluate();
		
		StandardDeviation std = new StandardDeviation();
		double stdV = std.evaluate(dvalues, meanV);
		
		if ( stdV == 0.0 || stdV == -0.0 ){
			return new Range( meanV, meanV );
		}
		
		
		double nrv = calculateNormalRandomVariable(meanV, stdV);
		
		double lower = meanV - ( nrv - meanV );
		if ( lower < 0 ){
			lower = 0;
		}
		
		return new Range( lower, nrv );
	}

	private static final Logger logger = LogManager.getLogger(NormalDistributionRangeGenerator.class);
	
	private double calculateNormalRandomVariable(double meanV,double stdV) {
		logger.debug("Calculating normal random variable. Mean: "+meanV+" STDV:"+stdV+" Confidence:"+confidence);
		
		NormalDistribution ndist = new NormalDistribution(meanV, stdV);
		
		double found=0;
		for ( double d=meanV; ; d+=0.1 ){
			if ( ndist.cumulativeProbability(d) > confidence ){
				
				for ( double v=d; ; v-=0.01 ){
					if ( ndist.cumulativeProbability(v) < confidence ){
						found = v+0.01;
						break;
					}
					
					
				}
				break;
			}
		}
		return found;
	}

	
}
