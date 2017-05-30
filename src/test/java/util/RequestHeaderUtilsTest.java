package util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RequestHeaderUtilsTest {
	
	private String header;
	
	@Before
	public void setup(){
		StringBuffer sb = new StringBuffer();
		sb.append("GET /index.html HTTP/1.1");
		sb.append("\n");
		sb.append("Host: localhost:8080");
		sb.append("\n");
		sb.append("Connection: keep-alive");
		sb.append("\n");
		sb.append("Pragma: no-cache");
		sb.append("\n");
		sb.append("Cache-Control: no-cache");
		sb.append("\n");
		sb.append("User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
		sb.append("\n");
		sb.append("Accept: image/webp,image/*,*/*;q=0.8");
		sb.append("\n");
		sb.append("Referer: http://localhost:8080/index.html");
		sb.append("\n");
		sb.append("Accept-Encoding: gzip, deflate, sdch, br");
		sb.append("\n");
		sb.append("Accept-Language: ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4");
		sb.append("\n");
		
		header = sb.toString();
	}
	
	@Test
	public void getFirstLineTest() throws Exception {
		assertTrue("GET /index.html HTTP/1.1".equals(RequestHeaderUtils.getFirstLine(header)));
	}
	
	@Test
	public void getUrlFromFirstLineTest() throws Exception {
		String line = RequestHeaderUtils.getFirstLine(header);
		assertTrue("/index.html".equals(RequestHeaderUtils.getUrlFromFirstLine(line)));
	}
	
	@Test
	public void getURLfromHeadertest() throws Exception {
		assertTrue("/index.html".equals(RequestHeaderUtils.getURLfromHeader(header)));
	}
	
	@After
	public void teardown(){
		header = null;
	}
}
