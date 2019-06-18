package it.unimib.disco.lta.ava.alignment;

public class PreciseAlignmentCosts
{
    private int expectedGapScore=0;
    private int costMatch=0;
    private int costMismatch=0;
	private int observedGapScore;
    
    public int getExpectedGapScore()
    {
        return expectedGapScore;
    }
    public void setGapScore(int gapScore)
    {
        this.expectedGapScore = gapScore;
    }
    public int getCostMatch()
    {
        return costMatch;
    }
    public void setCostMatch(int costMatch)
    {
        this.costMatch = costMatch;
    }
    public int getCostMismatch()
    {
        return costMismatch;
    }
    public void setCostMismatch(int costMismatch)
    {
        this.costMismatch = costMismatch;
    }

    public PreciseAlignmentCosts(int observedGapScore,int expectedGapScore, int costMatch, int costMismatch)
    {
        this.expectedGapScore = expectedGapScore;
        this.observedGapScore = observedGapScore;
        this.costMatch = costMatch;
        this.costMismatch = costMismatch;
    }
	public int getObservedGapScore() {
		return observedGapScore;
	}
	public void setExpectedGapScore(int expectedGapScore) {
		this.expectedGapScore = expectedGapScore;
	}
	public void setObservedGapScore(int observedGapScore) {
		this.observedGapScore = observedGapScore;
	}
}