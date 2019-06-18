package it.unimib.disco.lta.timedKTail.ui;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.unimib.disco.lta.timedKTail.util.JavaRunner;

public class JavaPerformanceTracesSplitter {

	private String componentName;
	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	protected File destFolder;
	private boolean useFiles;
	private boolean deleteTraces;


	public JavaPerformanceTracesSplitter(File destFolder, boolean useFiles) {
		this.destFolder = destFolder;
		this.useFiles = useFiles;

	}

	public static void main(String[] args) {

		File tracesFolder = new File( args[0] );
		File destFolder = new File( args[1] );


		destFolder.mkdir();

		boolean useFiles = Boolean.parseBoolean(System.getProperty("useFiles","false"));

		boolean deleteTraces = Boolean.parseBoolean(System.getProperty("deleteTraces","false"));

		System.out.println("Use files: "+useFiles);
		System.out.println("Delete Traces:" +deleteTraces);
		JavaPerformanceTracesSplitter jpts = new JavaPerformanceTracesSplitter(destFolder,useFiles);
		jpts.setDeleteTraces( deleteTraces );
		String componentName = System.getProperty("componentName");
		if ( componentName != null ){
			jpts.setComponentName(componentName);
		}

		jpts.processTraces(  tracesFolder );

	}

	private void setDeleteTraces(boolean deleteTraces) {
		this.deleteTraces = deleteTraces;
	}

	private void processTraces(File tracesFolder) {
		File[] traces = tracesFolder.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".csv");
			}
		});

		if ( traces == null ){
			System.out.println("No traces in "+tracesFolder.getAbsolutePath());
			return;
		}

		for ( File trace : traces ){
			processTrace(trace);
		}
	}

	private void processTrace(File trace) {
		System.out.println("Processing: "+trace.getAbsolutePath());

		List<String> args = new ArrayList<String>();
		args.add( destFolder.getAbsolutePath() );
		args.add( trace.getAbsolutePath() );

		String patternString = System.getProperty("classPattern");
		if ( patternString != null ){
			args.add( patternString );	
		}

		try {

			Class clazzToRun;

			if ( componentName!=null){
				clazzToRun = JPTSObserver_Method.class;
			} else {

				if ( useFiles ){
					clazzToRun = JPTSObserver_Object_File.class;
				} else {
					clazzToRun = JPTSObserver_Object.class;
				}

			}

			JavaRunner.runMainInClass(clazzToRun, args, 0);

			if( deleteTraces ){
				System.out.println("Deleting: "+trace.getAbsolutePath());
				trace.delete();
				System.out.println("Deleted: "+(!trace.exists()));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
