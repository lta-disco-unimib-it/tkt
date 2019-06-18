package it.unimib.disco.lta.timedKTail.statistics;

import static org.junit.Assert.*;
import it.unimib.disco.lta.timedKTail.statistics.NormalDistributionRangeGenerator;
import it.unimib.disco.lta.timedKTail.statistics.NormalDistributionRangeGenerator.Range;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.junit.Test;

/**
 * Test oracles are defined on the basis of teh results returned by
 * https://www.easycalculation.com/statistics/standard-deviation.php (to calculate stddev)
 * http://stattrek.com/online-calculator/normal.aspx  (expected z-core)
 * 
 * @author fabrizio
 *
 */
public class NormalDistributionRangeGeneratorTest {



	@Test
	public void testCalculateRange() {
		
		
		
		NormalDistributionRangeGenerator g = new NormalDistributionRangeGenerator(0.99);

		double[] v = new double[]{2.0,10.0};
		Range res = g.calculateRange(v);
		
		assertEquals(19.160 , res.upper, 0.01 );
		
		

		res = g.calculateRange(new double[]{-1.0,1.0});
		assertEquals(3.290 , res.upper, 0.01 );
		
		res = g.calculateRange(new double[]{-1.0, -0.5, 0.5, 1.0});
		assertEquals(2.124 , res.upper, 0.01 );
		
		
		res = g.calculateRange(new double[]{1,2,3,4,5,6,7,8,9,120,342,12});
		assertEquals(275.192 , res.upper, 0.01 );
		
		res = g.calculateRange(new double[]{45,55,56});
		assertEquals(66.151 , res.upper, 0.01 );
		double lowerExpected = calculateExpectedLower(66.151 ,new double[]{45,55,56});
		assertEquals( lowerExpected , res.lower, 0.01 );
		
	}
	
	/**
	 * This test shows that the range created according to normal distribution could be smaller than the sample, i.e. some values in the sample can be out of range.
	 * 
	 */
	@Test
	public void testCalculateRange_RangeSmallerThanSample() {
		
		
		
		NormalDistributionRangeGenerator g = new NormalDistributionRangeGenerator(0.99);

		double[] v = new double[]{19.0, 19.0, 19.0, 20.0, 21.0, 19.0, 19.0, 20.0, 23.0, 23.0, 21.0, 20.0, 20.0, 19.0, 20.0, 20.0, 19.0, 19.0, 24.0, 20.0, 20.0, 20.0, 20.0, 19.0, 21.0, 24.0, 20.0, 19.0, 19.0, 19.0, 19.0, 20.0, 21.0, 19.0, 19.0, 20.0, 19.0, 18.0, 19.0, 19.0, 20.0, 19.0, 19.0, 19.0, 19.0, 20.0, 19.0, 19.0, 19.0, 20.0, 20.0, 19.0, 19.0, 19.0, 20.0, 19.0, 19.0, 20.0, 20.0, 20.0, 20.0, 20.0, 20.0, 20.0, 20.0, 19.0, 21.0, 20.0, 19.0, 19.0, 19.0, 19.0, 20.0, 19.0, 19.0, 19.0, 25.0, 19.0, 19.0, 20.0, 19.0, 19.0, 24.0, 19.0, 20.0, 19.0, 19.0, 19.0 };
		Range res = g.calculateRange(v);
		
		assertEquals(22.739, res.upper, 0.01 );
		
		
	}

	private double calculateExpectedLower(double upper, double[] values) {
		Mean m = new Mean();
		m.setData(values);
		double mean = m.evaluate();
		
		double lowerExpected = mean - (upper-mean );
		return lowerExpected;
	}

}
