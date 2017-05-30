package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RequestHeaderUtils {

	public static String getFirstLine(String header) {
		return header.split("\n")[0];
	}

	public static String getUrlFromFirstLine(String line) {
		return line.split(" ")[1];
	}

	public static String getURLfromHeader(String header) {
		return getUrlFromFirstLine(getFirstLine(header));
	}
	
	public static String parseHeader(InputStream in) throws IOException{
    	BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
    	StringBuffer sb = new StringBuffer();
    	String line = "";
    	while(!"".equals(line = br.readLine())){
    		sb.append(line);
    		sb.append("\n");
    	}
    	return sb.toString();
    }
}
