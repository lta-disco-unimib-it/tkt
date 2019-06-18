package it.unimib.disco.lta.ava.alignment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Align {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File l = new File( args[0]);
		File r = new File( args[0]);
		
		LinkedList<String> seqL = load( l );
		LinkedList<String> seqR = load( r );
		
		AlignmentResult aligned = StringsAlignment.align(1, 0, 0, seqL, seqR, new AlignmentPreprocess(false,"") );
	
		List<String> alignedL = aligned.getFirstSequenceAligned();
		List<String> alignedR = aligned.getSecondSequenceAligned();
		
		for ( int i = 0; i < alignedL.size(); i++ ){
			System.out.println(alignedL.get(i)+" "+alignedR.get(i));
		}
	}

	private static LinkedList<String> load(File l) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(l));
		
		String line;
		
		LinkedList<String> loaded = new LinkedList<>();
		while ( ( line = br.readLine() ) != null ){
			String[] splitted = line.split(";");
			loaded.add(splitted[1]);
		}
		
		br.close();
		
		return loaded;
	}

}
