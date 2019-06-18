package it.unimib.disco.lta.timedKTail.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.collections15.multimap.MultiHashMap;

public class RemoveDuplicatedTraces {

	public static void main(String[] args) {
		File folder = new File(args[0]);

		File[] files = folder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".csv");
			}
		});
		
		MultiHashMap<Long, File> map = new MultiHashMap<Long,File>();
		
		for ( File file : files ){
			map.put(file.length(), file );
		}
		
		for ( Entry<Long, Collection<File>> e : map.entrySet() ){
			Collection<File> values = e.getValue();
			
			File[] filesArray = values.toArray(new File[values.size()]);
			for ( int i=0; i < filesArray.length-1; i++ ){
				for ( int j=0; j < filesArray.length; j++ ){
					if ( filesArray[j] != null && filesArray[i] != null ){
						if ( same(filesArray[i],filesArray[j]) ){
							filesArray[j].delete();
							filesArray[j]=null;
						}
					}
				}	
			}
		}
	}

	private static boolean same(File file, File file2) {
		
		List<String> command = new ArrayList<>();
		command.add("diff");
		command.add("-q");
		command.add(file.getAbsolutePath());
		command.add(file2.getAbsolutePath());
		try {
			int ret = ProcessRunner.run(command, null, null, 0);
			if( ret == 0 ){
				return true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
