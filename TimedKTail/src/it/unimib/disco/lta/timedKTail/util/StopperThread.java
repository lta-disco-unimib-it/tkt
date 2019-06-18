package it.unimib.disco.lta.timedKTail.util;

public class StopperThread extends Thread {
	
	private Process ps;
	private int daikonLimit;
	private boolean kill = true;



	public StopperThread ( Process ps, int daikonLimit ){
		this.ps = ps;
		this.daikonLimit = daikonLimit;
	}
	
	
	public void run() {
		synchronized (this) {
			try {
				long time = System.currentTimeMillis();
				int waitDelta = daikonLimit*1000;
				long timeElapsed = 0;

				do {
					this.wait(waitDelta);
					timeElapsed += ( System.currentTimeMillis() - time );
				} while ( kill && timeElapsed < waitDelta);

				if ( kill ){
					System.err.println("Process took too much time ("+timeElapsed+" sec), killing it ("+ps.toString()+")");
					
					ps.destroy();
				}

			} catch (InterruptedException e) {

			}
		}
	}


	/**
	 * Terminate the execution of the stopper thread by notifying it
	 * 
	 */
	public void terminate() {
		synchronized (this) {
			this.kill = false;
			this.notify();
		}
	}
	

}
