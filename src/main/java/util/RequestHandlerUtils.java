package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import model.User;

public class RequestHandlerUtils {

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

	public static String getParams(String url) {
		String[] token = url.split("\\?");
		if(token.length == 1) // url 내에 "?" 뒷 부분이 없는 경우, 즉 parameter 가 없는 경우 
			return null;
		return url.split("\\?")[1];
	}
	
	public static String getQuery(String url) {
		return url.split("\\?")[0];
	}

	public static HashMap<String, String> getMapFromParams(String params) throws Exception{
		HashMap<String, String> map = new HashMap<>();
		String[] keyValArray = params.split("&");
		for(String keyValue : keyValArray){
			String[] token = keyValue.split("=");
			if(token.length == 1){ // (K,V) 형태로 데이터가 넘어오지 않은 경우 (예 : age, age= )
				String key = token[0];
				throw new Exception(key +"에 mapping되는 value 값이 전달되지 않았습니다");
			}
			map.put(token[0], token[1]);
		}
		return map;
	}

	public static User saveUserFromMap(HashMap<String, String> map) {
		// TODO : User 모델 field 값 중 null 이 되는 것이 없게 하려면 별도의 로직 적용이 필요하다. 
		return new User(map.get("userId"),map.get("password"),map.get("name"), map.get("email"));
	}

	public static User createUser(String params) throws Exception {
		return saveUserFromMap(getMapFromParams(params));
	}
}
