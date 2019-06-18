package it.unimib.disco.lta.timedKTail.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ProcessRunner {
	private static final Logger LOGGER = Logger.getLogger(ProcessRunner.class.getCanonicalName());

	/**
	 * Runs the main method of the given class in a standalone process.
	 * Stops the process if it takes more than maxExecutionTime seconds to finish.
	 * 
	 * @param clazz
	 * @param args
	 * @param maxExecutionTime
	 * @return 
	 * @throws IOException
	 */
	public static int run( List<String> command, final Appendable outputBuffer, final Appendable errorBuffer, int maxExecutionTime ) throws IOException{
		return run(command, outputBuffer, errorBuffer, maxExecutionTime, null);
	}
	
	public static int run( List<String> command, Appendable _outputBuffer, Appendable _errorBuffer, int maxExecutionTime, File dir) throws IOException{
		return run(command, _outputBuffer, _errorBuffer, maxExecutionTime, dir, null);
	}
	
	public static int run( List<String> command, Appendable _outputBuffer, Appendable _errorBuffer, int maxExecutionTime, File dir, Map<String, String> env ) throws IOException{
		
		if ( _outputBuffer == null ){
			_outputBuffer = new OutputStreamWriter(System.out);
		}
		
		if ( _errorBuffer == null ){
			_errorBuffer = new OutputStreamWriter(System.err);
		}
		
		final Appendable outputBuffer = _outputBuffer;
		final Appendable errorBuffer = _errorBuffer;
		
		LOGGER.info("Executing "+command.toString());
		
		String[] cmdArray = command.toArray(new String[command.size()]);
		
		ProcessBuilder pb = new ProcessBuilder(command);
		if ( dir != null ){
			pb.directory(dir);
		}
		if ( env != null ){
			pb.environment().putAll(env);
		}
		
//		Map<String, String> env = new HashMap<String, String>();
//		env.putAll( System.getenv() );
//		
//		env.put("PWD",dir.getAbsolutePath());
//		
//		List<String> envVars = new ArrayList<String>();
//		for ( Entry<String,String> e : env.entrySet() ){
//			envVars.add(e.getKey()+"="+e.getValue());
//		}
//		String envp[] = envVars.toArray(new String[envVars.size()]);
//		
//		
//		final Process p = Runtime.getRuntime().exec(cmdArray, null, dir);
		
		final Process p = pb.start();
		
		final BufferedInputStream in = new BufferedInputStream(p.getInputStream());
		final BufferedInputStream err = new BufferedInputStream(p.getErrorStream());


		
		StopperThread stopperThread=null;
		//Start time limit thread if necessary
		if ( maxExecutionTime > 0 ){
			stopperThread = new StopperThread(p,maxExecutionTime);
			stopperThread.start();
		}
		
		
		//Start daikon
		Thread t = new Thread() {

			public void run() {
				try {
					while (true) {
						int c = in.read();
						if (c < 0)
							break;
						else
							outputBuffer.append((char)c);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
		t.start();
		Thread t1 = new Thread() {

			public void run() {
				try {
					while (true) {
						int c = err.read();
						if (c < 0)
							break;
						else
							errorBuffer.append((char)c);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
		t1.start();
		try {
			int exitCode = p.waitFor();
			
			while ( t1.isAlive() || t.isAlive() ){
				Thread.sleep(100);
			}
			
			in.close();
			err.close();

			//if we are here p has been terminated, thus we can stop the stopperThead
			if ( stopperThread != null && stopperThread.isAlive() ){
				stopperThread.terminate();	
			}
			LOGGER.info("Exit code "+exitCode);
			return exitCode;
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1;
		
	}
}
