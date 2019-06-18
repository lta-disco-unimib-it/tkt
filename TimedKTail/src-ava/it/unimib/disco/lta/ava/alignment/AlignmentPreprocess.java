package it.unimib.disco.lta.ava.alignment;

import java.util.ArrayList;
import java.util.List;

public class AlignmentPreprocess
{
	
	public AlignmentPreprocess(){};
	
    public AlignmentPreprocess(boolean preprocessBeforeAlignment,
            String parametersSeparator)
    {
        super();
        this.preprocessBeforeAlignment = preprocessBeforeAlignment;
        this.parametersSeparator = parametersSeparator;
    }

    private boolean preprocessBeforeAlignment=false;
    private String parametersSeparator="_";
    
    public boolean isPreprocessBeforeAlignment()
    {
        return preprocessBeforeAlignment;
    }

    public void setPreprocessBeforeAlignment(boolean preprocessBeforeAlignment)
    {
        this.preprocessBeforeAlignment = preprocessBeforeAlignment;
    }

    public String getParametersSeparator()
    {
        return parametersSeparator;
    }

    public void setParametersSeparator(String parametersSeparator)
    {
        this.parametersSeparator = parametersSeparator;
    }

    /**
     * Removes all symbol from the event
     * @param firstSequence
     * @return
     */
    public List<String> preprocessBeforeAlignment(
            List<String> observedSequence)
    {
        if(preprocessBeforeAlignment)
        {
            ArrayList<String> preprocessed=new ArrayList<String>();            
            for(String observed : observedSequence )
            {
                preprocessed.add(preprocessBeforeAlignment(observed));
            }            
            observedSequence=preprocessed;
        }
        
        return observedSequence;        
    }

    protected String preprocessBeforeAlignment(String stringToProcess)
    {
        String result=stringToProcess;
        if(preprocessBeforeAlignment)
        {
            if(stringToProcess.contains(parametersSeparator))
                result=result.substring(0, stringToProcess.indexOf(parametersSeparator));
        }
        return result;
    }
}
