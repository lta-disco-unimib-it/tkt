package it.unimib.disco.lta.ava.alignment;

import java.util.ArrayList;
import java.util.List;


public class StringsAlignment
{
   
    public static final String EMPTY_STRING = "-";
    //fields
    private List<String> s;
    private List<String> t;
    private int a[][];

    private List<String> alignS;
    private List<String> alignT;

    //alignment costs
    private AlignmentCosts alignmentCosts;
    /**
     * Initializes the class with alignment costs
     * @param alignmentCosts contains the weights for the alignment
     */
    private StringsAlignment(AlignmentCosts alignmentCosts)
    {
        alignS=new ArrayList<String>();
        alignT=new ArrayList<String>();
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
    public static AlignmentResult align(int matchScore,int mismatchScore,int gapScore,
            List<String> observedSequence,List<String> expectedSequence,AlignmentPreprocess preprocess)
    {
    observedSequence=preprocess.preprocessBeforeAlignment(observedSequence);
    expectedSequence=preprocess.preprocessBeforeAlignment(expectedSequence);
    return align(matchScore,mismatchScore,gapScore,observedSequence,expectedSequence);
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
    private static AlignmentResult align(int matchScore,int mismatchScore,int gapScore,
            List<String> observedSequence,List<String> expectedSequence)
    {        
        AlignmentCosts costs=new AlignmentCosts(gapScore,matchScore,mismatchScore);
        StringsAlignment align=new StringsAlignment(costs);
        
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
     * Recursive method used to create alignment sequence.
     * @param row index of the row to analyze
     * @param column index of the column to analyze
     */
    private void createAlignmentSequences(int row, int column)
    {
        if(column==0 && row==0)
        {
            return;
        }
        else
        {
            if(row>0 && a[row][column]==a[row-1][column]+alignmentCosts.getGapScore())
            {
                createAlignmentSequences(row-1,column);                
                alignS.add(s.get(row-1));
                alignT.add(EMPTY_STRING);
            }
            else
            {
                if(row>0 && column>0 && a[row][column]==a[row-1][column-1]+getScoreOfCell(row,column))
                {
                    createAlignmentSequences(row-1,column-1);                    
                    alignS.add(s.get(row-1));
                    alignT.add(t.get(column-1));
                }
                else//has to be column>0 and a[row][column]==a[row][column-1]+gapScore
                {
                    createAlignmentSequences(row,column-1);                    
                    alignS.add(EMPTY_STRING);
                    alignT.add(t.get(column-1));
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
            return transformScoreInPercentage(expectedSequence.size()*alignmentCosts.getGapScore());
        if(expectedSequence.size()==0)    
            return transformScoreInPercentage(observedSequence.size()*alignmentCosts.getGapScore());

        int rowCount=s.size()+1;
        int columnCount=t.size()+1;

        a=new int[rowCount][columnCount];
        //initialize the table with the costs
        for(int column=0;column<columnCount;column++)
        {
            a[0][column] = column * alignmentCosts.getGapScore(); 
        }
        //initialize the table with the costs
        for(int row=0;row<rowCount;row++)
        {
            a[row][0] = row * alignmentCosts.getGapScore(); 
        }  

        for(int row=1;row<a.length;row++)
        {
            for(int column=1;column<a[row].length;column++)
            {
                a[row][column]=getMaxScore(a[row-1][column]+alignmentCosts.getGapScore(),
                        a[row-1][column-1]+getScoreOfCell(row,column),
                        a[row][column-1]+alignmentCosts.getGapScore());
            }
        } 
        int score= a[rowCount-1][columnCount-1];
        return transformScoreInPercentage(score);

    }
    /**
     * Transform the alignment score in the value between 0 and 1. 
     * @param score score to normalize
     * @return value between 0 and 1 that represent the score. The value is 0 if the sequences are completely different and 1 if they are equal
     */
    private float transformScoreInPercentage(int score)
    {
       float maxScore=Math.max(s.size(), t.size())*alignmentCosts.getCostMatch();
       float result=score/maxScore;
       
       if(result<0)
           score=0;
       
       return score/maxScore;
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
