package it.unimib.disco.lta.timedKTail.traces;

public class MaxExecutionTimeFinder extends ObserverTraceIm {

	private String methodUnderTest;

	private int level;
	private long enterTime;
	private long maxExecutionTime = -1;

	private boolean testCasesCallsOnly;

	

	public Long getMaxExecutionTime() {
		if ( maxExecutionTime == -1 ){
			return null;
		}
		return maxExecutionTime;
	}

	public MaxExecutionTimeFinder(String methodUnderTest) {
		this.methodUnderTest = methodUnderTest;
	}

	@Override
	public void newEvent(Event e) {
		

		if ( e.getAttivita().equals(methodUnderTest) ){
			
			if ( e.isBegin() ){
				if ( level == 0 ){
					enterTime = e.getTimestamp();
				}
			} else { //is end
				if ( level == 1 ){
					
					long executionTime = e.getTimestamp()-enterTime;

					if ( executionTime > maxExecutionTime ){
						maxExecutionTime = executionTime;
					}
					
				}
			}
			
			if ( ! testCasesCallsOnly ){
				updateCurrentLevel(e);
			}

		}

		if ( testCasesCallsOnly ){
			updateCurrentLevel(e);
		}
	}

	public void updateCurrentLevel(Event e) {
		if ( e.isBegin() ){
			level++;
		} else {
			level--;
		}
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
		// TODO Auto-generated method stub

	}

	public void setTestCasesCallsOnly(boolean testCasesCallsOnly) {
		this.testCasesCallsOnly = testCasesCallsOnly;
	}

}
