package it.unimib.disco.lta.timedKTail.ui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class FilterTpFpInValidation {

	private static String replacing;
	private static String replaced;
	
	public static void main(String[] args) throws IOException {
		File validationResultFile = new File( args[0] );
		String faultyCall = args[1];
		
		BufferedReader br = new BufferedReader(new FileReader(validationResultFile));
		
		BufferedWriter tp = new BufferedWriter(new FileWriter(new File(validationResultFile.getAbsolutePath()+".TP") ));
		BufferedWriter fp = new BufferedWriter(new FileWriter(new File(validationResultFile.getAbsolutePath()+".FP") ));
		
		
		String replacementString = System.getProperty("tkt.replacement");
		
		if ( replacementString != null ){
			String[] replacementArray = replacementString.split(",");
			replaced=replacementArray[0];
			replacing=replacementArray[1];
		}
				
		String line;
		
		HashSet<String> missing = new HashSet<String>();
		
		while ( ( line = br.readLine() ) != null ){
			//TKTAIL.GUAVA.371.ALPHA_IMPL.final_T80_8,
			//6,3,0.0,3,1.0,0.0,false,true,false,
			//10 /home/pastore/TKTAIL.GUAVA.371.ALPHA_IMPL.final/traces/Tracce1/../com_google_common_collect_LinkedHashMultimap_failing/trace.78.csv,
			//11 UNMATCHED_EVENT,
			//12 com.google.common.collect.LinkedHashMultimap.removeAll,
			//13 120024,
			//null,0
			String[] splitted = line.split(",");
			
			if ( splitted.length <= 1 ){
				continue;
			}
			
			if ( splitted[1].equals("ACCEPTED") ){
				continue;
			}
			
			String file = splitted[10];
			
			file = replacePrefixIfNecessary( file );
			
			
			String issue = splitted[11];
			
			if ( issue.equals("ACCEPTED") ){
				continue;
			}
			
			
			if ( splitted.length <= 12 ){
				System.out.println(line);
			}
			
			
			String method = splitted[12];
			String lineNoStr = splitted[13];
			
			Integer lineNo = Integer.valueOf(lineNoStr);
			
			
			boolean TP=false;
			int bugStart = findFirstOccurrence( file, faultyCall );
			if ( bugStart == -1 ){
				
				missing.add( file );
				TP = false;
			} else if ( lineNo >= bugStart ){
				TP = true;
			}
			
			if ( TP ){
				tp.write(line);
				tp.newLine();
			} else {
				fp.write(line);
				fp.newLine();
			}
		}
		
		tp.close();
		fp.close();
		
		br.close();
		
		System.out.println("!!!FILES WITH MISSING CALL TO FAULTY METHOD: ");
		for ( String file : missing ){
			System.out.println(file);
		}
	}
	
	
	
	private static String replacePrefixIfNecessary(String file) {
		if ( replaced != null ){
			
			File f = new File( file );
			if ( ! f.exists() ){
				return file.replace(replaced, replacing);	
			}
			
		}
		return file;
	}



	static HashMap<String,Integer> loaded = new HashMap<String,Integer>();
	private static int findFirstOccurrence(String l, String stringToFind) throws IOException {
		Integer pos = loaded.get(l);
		
		if ( pos != null ){
			return pos;
		}
		
		
		BufferedReader br = new BufferedReader(new FileReader(l));
		
		String line;
		try {
		
		int c=0;
		while ( ( line = br.readLine() ) != null ){
			
			String[] splitted = line.split(";");
			
			if ( splitted.length <= 1 ){
				continue;
			}
			
			String method = splitted[1];
					
			if ( method.equals(stringToFind) )	{
				loaded.put(method, c);
				return c;
			}
			c++;
		}
		
		} finally {
		br.close();
		}
		
		return -1;
		//throw new IllegalStateException("Not found: "+stringToFind+" in file "+l);
	}

}
