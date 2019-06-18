package it.unimib.disco.lta.timedKTail.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaRunner {
	private static final String BCT_UTIL_JAVA_RUNNER_INCLUDE_CURRENT_CLASSPATH = "javaRunner.includeCurrentClasspath";
	
	private static final Logger LOGGER = Logger.getLogger(JavaRunner.class.getCanonicalName());
	public final static boolean addCurrentLogging = true;

	public static String getCurrentClassPath(){
		return System.getProperty("java.class.path");
	}
	
	/**
	 * Runs the main method of the given class in a standalone process.
	 * Stops the process if it takes more than maxExecutionTime milliseconds to finish.
	 * 
	 * @param clazz
	 * @param args
	 * @param maxExecutionTime
	 * @throws IOException
	 */
	public static void runMainInClass(Class clazz, List<String> args, int maxExecutionTime ) throws IOException{
		runMainInClass(clazz, args, maxExecutionTime, new ArrayList<String>() );
	}
	
	public static void runMainInClass(Class clazz, List<String> args, int maxExecutionTime, boolean includeVMargs ) throws IOException{
		
		List<String> vmCmds = new ArrayList<>();
		if ( includeVMargs ){
			Set<Object> keySet = System.getProperties().keySet();
			for ( Object key : keySet){
				if ( key == null ){
					continue;
				}
				if ( key.toString().startsWith("bct.") ){
					Object value = System.getProperty(key.toString());
					if ( value != null ){
						vmCmds.add("-D"+key+"="+value.toString());
					}
				}
			}
		}
		
		
		runMainInClass(clazz, vmCmds, args, maxExecutionTime, new ArrayList<String>(), true, null, null);
	}
	
	public static void runMainInClass(Class clazz, List<String> args, int maxExecutionTime, List<String> additionalPaths) throws IOException{
		
		
		
		runMainInClass(clazz, null, args, maxExecutionTime, additionalPaths, true, null, null);
	}
	
	/**
	 * Executes the method " main "  of the Class passed as input 
	 * 
	 * @param clazz		the class that contains the main function to execute
	 * @param vmArgs	the options to pass to the VM (usually each arg is like "-Darg=value" )
	 * @param args		the arguments passed to the main function
	 * @param maxExecutionTime	the max execution time (after which the process is killed, 0 not t kill it)
	 * @param additionalPaths	additional paths to be added to the classpath
	 * @param includeCurrentClassPath	indicates wether or not to pass the current classpath to the java VM
	 * @param outputBuffer	the buffer where to save the program standard output
	 * @param errorBuffer   the buffer where to save the program standard error
	 * @throws IOException	
	 */
	public static void runMainInClass(Class clazz, List<String> vmArgs, List<String> args, int maxExecutionTime, List<String> additionalPaths, boolean includeCurrentClassPath, Appendable outputBuffer, Appendable errorBuffer ) throws IOException{
		runMainInClass(clazz, vmArgs, args, maxExecutionTime, additionalPaths, includeCurrentClassPath, outputBuffer, errorBuffer, null);
	}
	
	public static int runMainInClass(Class clazz, List<String> vmArgs, List<String> args, int maxExecutionTime, List<String> additionalPaths, boolean includeCurrentClassPath, Appendable outputBuffer, Appendable errorBuffer, File workingDir ) throws IOException{
		
		List<String> command = new ArrayList<String>();
		
		//String cmd = "java -Xmx512m -classpath \"" + igs.getDaikonPath()  + "\" daikon.Daikon "+daikonAdditionalOptions+" --config \""+ EnvironmentalSetter.getBctHomeNoJAR()  + File.separator + "conf" + File.separator + "files" + File.separator + getDaikonConfigFileName(configFile)+"\"" 
		
		command.add("java");
		
		//command.add("-D");
		
		if ( addCurrentLogging ){
			if ( vmArgs == null ){
				vmArgs =  new ArrayList<String>();
			}
			
			String configPropertyName = "java.util.logging.config.file";
			String logginingPath = System.getProperty(configPropertyName);
			if ( logginingPath != null ){
				vmArgs.add("-D"+configPropertyName+"="+logginingPath);
			}
		}
		
		if ( vmArgs != null ){
			for ( String vmArg :vmArgs ){
				command.add(vmArg);
			}
		}
		
		
		if ( ! includeCurrentClassPath ){
			if ( Boolean.parseBoolean(System.getProperty(BCT_UTIL_JAVA_RUNNER_INCLUDE_CURRENT_CLASSPATH) ) ){
				includeCurrentClassPath = true;
			}
		}
		
		String path = null;
		if ( includeCurrentClassPath ){
			LOGGER.log(Level.INFO,"Including current path");
			path = getCurrentClassPath();
		}
		
		if ( additionalPaths != null ){
			for ( String p : additionalPaths ){
				if ( path == null ){
					path = p;
				} else {
					path+=File.pathSeparator+p;
				}
			}
		}
		if ( path != null ){
			command.add("-cp");
			command.add(path);
		}
		
		String cName = clazz.getCanonicalName();
		if ( clazz.isMemberClass() ){
			int lastDot = cName.lastIndexOf('.');
			char[] charName = cName.toCharArray();
			charName[lastDot] = '$';
			cName = String.valueOf(charName);
		}
		command.add(cName);
		
		command.addAll(args);
		
		LOGGER.log(Level.INFO,"Executing "+command.toString());
		
		int exitCode = ProcessRunner.run(command, outputBuffer, errorBuffer, maxExecutionTime, workingDir);
		LOGGER.info("Java Process Exit code "+exitCode);

		return exitCode;
		
	}
}
