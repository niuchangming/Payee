package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class FileReader {
	
	public static String read(InputStream in){
		StringBuffer sb = new StringBuffer();
		BufferedReader reader = null;
		try {
		    reader = new BufferedReader(new InputStreamReader(in));
		    
		    String line = "";
	        while ((line = reader.readLine()) != null) {
	        	sb.append(line);
	        }
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
		    if (reader != null) {
		         try {
		             reader.close();
		         } catch (IOException e) {
		        	 throw new RuntimeException(e);
		         }
		    }
		}
		return sb.toString();
	}
	
}
