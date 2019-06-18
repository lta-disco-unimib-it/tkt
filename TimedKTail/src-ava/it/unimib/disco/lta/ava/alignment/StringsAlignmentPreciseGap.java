package it.unimib.disco.lta.ava.alignment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;


public class StringsAlignmentPreciseGap
{
	private static class AlignmentData{
		public AlignmentData(int row,int column) {
			this.row = row;
			this.col = column;
		}
		public int row;
		public int col;
	}
   
    public static final String EMPTY_STRING = "-";
    //fields
    private List<String> s;
    private List<String> t;
    private int a[][];

    private List<String> alignS;
    private List<String> alignT;

    //alignment costs
    private PreciseAlignmentCosts alignmentCosts;
    /**
     * Initializes the class with alignment costs
     * @param alignmentCosts contains the weights for the alignment
     */
    private StringsAlignmentPreciseGap(PreciseAlignmentCosts alignmentCosts)
    {
        alignS=new LinkedList<String>();
        alignT=new LinkedList<String>();
        this.alignmentCosts=alignmentCosts;
    }
    /**
     * Aligns two string sequences using the given alignment weights
     * @param matchScore score used for the match between the two characters in the same position
     * @param mismatchScore score used for the mismatch between the two characters in the same position
     * @param gapScore score used for the gap between the two characters in the same position
     * @param observedSequence observedSequence sequence to align
     * @param expectedSequence expectedSequence sequence to align
     * @param preprocess indicates if and how the sequences to align must be processed before the alignemnt
     * @return the result of the alignment between the given strings
     */
    public static AlignmentResult align(int matchScore,int mismatchScore,int observedGapScore, int expectedGapScore,
            List<String> observedSequence,List<String> expectedSequence,AlignmentPreprocess preprocess)
    {
    
    	if ( preprocess != null ){	
    		observedSequence=preprocess.preprocessBeforeAlignment(observedSequence);
    		expectedSequence=preprocess.preprocessBeforeAlignment(expectedSequence);
    	}
    
    	return align(matchScore,mismatchScore,observedGapScore,expectedGapScore,observedSequence,expectedSequence);
    }
    
    /**
     * Aligns with the global alignment the given strings and using the specified alignment costs for the matches.
     * @param matchScore score used for the match between the two characters in the same position
     * @param mismatchScore score used for the mismatch between the two characters in the same position
     * @param gapScore score used for the gap between the two characters in the same position
     * @param observedSequence observedSequence sequence to align
     * @param expectedSequence expectedSequence sequence to align
     * @return the result of the alignment between the given strings
     */
    private static AlignmentResult align(int matchScore,int mismatchScore,int observedGapScore,int expectedGapScore, 
            List<String> observedSequence,List<String> expectedSequence)
    {   
    	//System.out.println("ALIGN "+observedSequence.size()+" "+expectedSequence.size());
        PreciseAlignmentCosts costs=new PreciseAlignmentCosts(observedGapScore,expectedGapScore,matchScore,mismatchScore);
        StringsAlignmentPreciseGap align=new StringsAlignmentPreciseGap(costs);
        
        float score= align.createAlignmentStructure(observedSequence, expectedSequence);
        align.createAlignmentSequences();
        
                
        AlignmentResult result=new AlignmentResult(align.alignT,align.alignS,score);
        
        return result;
    }
    /**
     * Creates the alignments sequence from the weights table.
     */
    private void createAlignmentSequences()
    {
        if(s.size()==0)
        {
            alignT=t;
            fillWithEmptyElements(alignS,t.size());
            return;
        } 
        if(t.size()==0)
        {
            alignS=s;
            fillWithEmptyElements(alignT,s.size());
            return;
        }

        createAlignmentSequences(a.length-1,a[0].length-1);
    }
    /**
     * Fills the given list with empty strings
     * @param listToFill list to fill
     * @param size number of cells to fill with emtpy strings
     */
    private void fillWithEmptyElements(List<String> listToFill, int size)
    {
        for(int i=0;i<size;i++)
        {
            listToFill.add(EMPTY_STRING);
        }        
    }

    /**
     * Iterative method used to create alignment sequence.
     * @param row index of the row to analyze
     * @param column index of the column to analyze
     */
    private void createAlignmentSequences(int row, int column)
    {
    	
    	
    	
    	while ( column != 0 || row != 0 ){
    		
    		
    			if(row>0 && a[row][column]==a[row-1][column]+alignmentCosts.getObservedGapScore())
    			{
    				//createAlignmentSequences(row-1,column);
    				row=row-1;
    				
    				alignS.add(0,s.get(row));
    				alignT.add(0,EMPTY_STRING);
    			}
    			else
    			{
    				if(row>0 && column>0 && a[row][column]==a[row-1][column-1]+getScoreOfCell(row,column))
    				{
    					//createAlignmentSequences(row-1,column-1);
    					row=row-1;
    					column=column-1;
    					alignS.add(0,s.get(row));
    					alignT.add(0,t.get(column));
    				}
    				else//has to be column>0 and a[row][column]==a[row][column-1]+gapScore
    				{
    					//createAlignmentSequences(row,column-1);
    					column=column-1;
    					alignS.add(0,EMPTY_STRING);
    					alignT.add(0,t.get(column));
    				}
    			}
    		
    	}
    }
    /**
     * Creates alignment table from the given sequences to align
     * @param observedSequence observedSequence sequence to align
     * @param expectedSequence expectedSequence sequence to align
     * @return percentage that indicates how the sequences are globally similar
     */
    private float createAlignmentStructure(List<String> observedSequence,List<String> expectedSequence)
    {
        t=observedSequence;
        s=expectedSequence;
        if(observedSequence.size()==0)
            return transformScoreInPercentage(expectedSequence.size()*alignmentCosts.getObservedGapScore());
        if(expectedSequence.size()==0)    
            return transformScoreInPercentage(observedSequence.size()*alignmentCosts.getExpectedGapScore());

        int rowCount=s.size()+1;
        int columnCount=t.size()+1;

        a=new int[rowCount][columnCount];
        //initialize the table with the costs
        for(int column=0;column<columnCount;column++)
        {
            a[0][column] = column * alignmentCosts.getExpectedGapScore(); 
        }
        //initialize the table with the costs
        for(int row=0;row<rowCount;row++)
        {
            a[row][0] = row * alignmentCosts.getObservedGapScore(); 
        }  

        for(int row=1;row<a.length;row++)
        {
            for(int column=1;column<a[row].length;column++)
            {
            	//gap in expected
                a[row][column]=getMaxScore(
                		a[row-1][column]+alignmentCosts.getObservedGapScore(),
                        a[row-1][column-1]+getScoreOfCell(row,column),
                        //gap in observed
                        a[row][column-1]+alignmentCosts.getExpectedGapScore());
            }
        }
        
        
        
        
        int score= a[rowCount-1][columnCount-1];
        return score;//transformScoreInPercentage(score);

    }
    /**
     * Transform the alignment score in the value between 0 and 1. 
     * @param score score to normalize
     * @return value between 0 and 1 that represent the score. The value is 0 if the sequences are completely different and 1 if they are equal
     */
    private float transformScoreInPercentage(int score)
    {
       float maxScore=Math.max(s.size(), t.size());
       float result = score/maxScore;
       if ( result < 0 )
    	   return 0;
       return result;
       
    }

    /**
     * Returns the max score of the closest cells of the table.
     * @param leftCost score of the cell to the left
     * @param leftAboveCost score of the cost left and above
     * @param aboveCost cost of the cell above
     * @return The max value between the given values
     */
    private static int getMaxScore(int leftCost, int leftAboveCost, int aboveCost)
    {
        int max=leftCost;

        if(leftAboveCost>max)
            max=leftAboveCost;

        if(aboveCost>max)
            max=aboveCost;

        return max;
    }
    /**
     * Returns the score for the cell indicated by row index and column index
     * @param row index of row of the cells
     * @param column index of the column of the cell
     * @return score of the indicated cell
     */
    private int getScoreOfCell(int row, int column)
    {
    	
    	if(s.get(row-1).equals(t.get(column-1))){
    		return alignmentCosts.getCostMatch(); 
    	} else {
    		return alignmentCosts.getCostMismatch();
    	}
    }
}
