package it.unimib.disco.lta.ava.alignment;

public class AlignmentCosts
{
    private int gapScore=0;
    private int costMatch=0;
    private int costMismatch=0;
    
    public int getGapScore()
    {
        return gapScore;
    }
    public void setGapScore(int gapScore)
    {
        this.gapScore = gapScore;
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

    public AlignmentCosts(int gapScore, int costMatch, int costMismatch)
    {
        this.gapScore = gapScore;
        this.costMatch = costMatch;
        this.costMismatch = costMismatch;
    }
}