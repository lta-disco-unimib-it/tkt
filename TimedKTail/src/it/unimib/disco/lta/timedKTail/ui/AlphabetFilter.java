package it.unimib.disco.lta.timedKTail.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import scala.concurrent.util.Unsafe;

public class AlphabetFilter {

	public static void main(String[] args) {
		File alphabetFile = new File ( args[0]);
		
		List<File> folders = new ArrayList<>();
		for ( int i = 1; i < args.length; i++ ){
			folders.add(new File(args[i]));
		}
		
		CommonAlphabetFilter f = new CommonAlphabetFilter();
		f.loadAlphabet(alphabetFile);
		
		f.filterTracesInFolders(folders);
	}

}
