package it.unimib.disco.lta.ava.alignment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AlignmentResult implements Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private float score;
    private List<String> firstSequenceAligned;
    private List<String> secondSequenceAligned;
	private int gapFirst;
	private int gapSecond;
	private int matchingElements;
	private int mismatchingElements;
    
    
    public AlignmentResult(List<String> firstSequenceAligned, 
            List<String> secondSequenceAligned,float score)
    {
        this.score = score;
        this.firstSequenceAligned = firstSequenceAligned;       
        this.secondSequenceAligned = secondSequenceAligned;
    
        
        calculatesGaps();
    }
    
    private void calculatesGaps() {
    	
    	//FIXME: remove this check, it should throw an exception, but we are usingAlignmentResults not in all the plcaes correctly  
    	if ( firstSequenceAligned.size() != secondSequenceAligned.size() ){
    		return;
    	}
    	
    	
    	
    	int size = firstSequenceAligned.size();
		for ( int i = 0 ; i < size; i++ ){
			String firstElement = firstSequenceAligned.get(i);
			String secondElement = secondSequenceAligned.get(i);
			if ( firstElement.equals("-") ){
				gapFirst++;
			} else if (secondElement.equals("-")){
				gapSecond++;
			} else if ( firstElement.equals(secondElement) ){
				matchingElements++;
			} else {
				mismatchingElements++;
			}		
		}
	}
    
	public float getScore()
    {
        return score;
    }
   
    public List<String> getFirstSequenceAligned()
    {
        return firstSequenceAligned;
    }
    
    public List<String> getSecondSequenceAligned()
    {
        return secondSequenceAligned;
    }

	public int getGapFirst() {
		return gapFirst;
	}

	public int getGapSecond() {
		return gapSecond;
	}

	public int getMatchingElements() {
		return matchingElements;
	}

	public int getMismatchingElements() {
		return mismatchingElements;
	} 
	
	/**
	 * Returns the elements expected which do not have a correspondent in observed (i.e. replaced or deleted)
	 * @return
	 */
	public List<String> getModifiedElements(){
		return getModifiedElements(secondSequenceAligned,firstSequenceAligned,false);
	}
	
	/**
	 * Returns the elements observed which do not have a correspondent in expected (i.e. inserted or replaced)
	 * @return
	 */
	public List<String> getModifyingElements(){
		return getModifiedElements(firstSequenceAligned,secondSequenceAligned,false);
	}
	
	
	/**
	 * Returns the elements that match
	 * @return
	 */
	public List<String> getMatchingElementsLis(){
		return getModifiedElements(firstSequenceAligned,secondSequenceAligned,true);
	}
	
	/**
	 * Returns the elements of referenceSequence which do not have a correspondent symbol in newSequence.
	 *
	 *  
	 * @param referenceSequence
	 * @param newSequence
	 * @return
	 */
	public List<String> getModifiedElements(List<String> referenceSequence, List<String> newSequence, boolean matching ){
		int size = referenceSequence.size();
		ArrayList<String> mod = new ArrayList<String>();
		
		for ( int i = 0 ; i < size; i++ ){
			if ( ! referenceSequence.get(i).equals("-") ){
				if ( ! referenceSequence.get(i).equals(newSequence.get(i) ) ){
					if ( ! matching ){
						mod.add(referenceSequence.get(i));
					}
				} else { //if matching put in results only if they are equals
					if ( matching ){
						mod.add(referenceSequence.get(i));
					}
				}
			}
			
		}
		
		return mod;
	}
	
	/**
	 * Return the elements deleted in the second sequence
	 * 
	 * @return
	 */
	public List<String> getDeletedElements(){
		return getElementsReplacingGaps(secondSequenceAligned, firstSequenceAligned );
	}
	
	/**
	 * Return the elements added in the second sequence
	 * 
	 * @return
	 */
	public List<String> getAddedElements(){
		return getElementsReplacingGaps(firstSequenceAligned, secondSequenceAligned);
		
	}
	
	
	public List<String> getElementsReplacingGaps(List<String> referenceSequence, List<String> newSequence ){
		
		int size = referenceSequence.size();
		ArrayList<String> mod = new ArrayList<String>();
		
		for ( int i = 0 ; i < size; i++ ){
			if ( referenceSequence.get(i).equals("-") ){
				if ( ! newSequence.get(i).equals("-") ){
					mod.add(newSequence.get(i));
				} 
			}
		}
		
		return mod;
	}

	/**
	 * Return the number of elements not matching
	 * 
	 * @return
	 */
	public int getDifferencesCount() {
		return mismatchingElements*2+gapFirst+gapSecond;
	}
	
	public String toString(){
    	StringBuffer sb = new StringBuffer();
    	sb.append("First: ");
    	for ( String symbol : firstSequenceAligned ){
    		sb.append(symbol+" ");
    	}
    	sb.append("\nSecond ");
    	for ( String symbol : secondSequenceAligned ){
    		sb.append(symbol+" ");
    	}
    	sb.append("\nScore "+score);
    	return sb.toString();
    }
	
}
