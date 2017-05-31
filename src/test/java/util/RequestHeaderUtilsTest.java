package util;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import model.User;

public class RequestHeaderUtilsTest {
	
	private String header;
	private String url;
	
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
		
		url = "/user/create?userId=cbj0618&password=123456&name=%EC%B5%9C%EB%B3%91%EC%A4%80&email=cbj0618%40gmail.com";
	}
	
	@Test
	public void getFirstLineTest() throws Exception {
		assertTrue("GET /index.html HTTP/1.1".equals(RequestHandlerUtils.getFirstLine(header)));
	}
	
	@Test
	public void getUrlFromFirstLineTest() throws Exception {
		String line = RequestHandlerUtils.getFirstLine(header);
		assertTrue("/index.html".equals(RequestHandlerUtils.getUrlFromFirstLine(line)));
	}
	
	@Test
	public void getURLfromHeadertest() throws Exception {
		assertTrue("/index.html".equals(RequestHandlerUtils.getURLfromHeader(header)));
	}
	
//	@Test
//	public void isFormReqTest() throws Exception {
//		String url2 = "/index.html";
//		assertTrue(RequestHandlerUtils.isFormReq(url));
//		assertTrue(!RequestHandlerUtils.isFormReq(url2));
//	}
	
	@Test
	public void getQueryDataTest() throws Exception {
		assertTrue("userId=cbj0618&password=123456&name=%EC%B5%9C%EB%B3%91%EC%A4%80&email=cbj0618%40gmail.com"
				.equals(RequestHandlerUtils.getParams(url)));
	}
	
	@Test
	public void getUserKVmapTest() throws Exception {
		String queryData = RequestHandlerUtils.getParams(url);
		HashMap<String, String> map = RequestHandlerUtils.getMapFromParams(queryData);
		assertEquals("cbj0618", map.get("userId"));
		assertEquals("123456", map.get("password"));
		assertEquals("%EC%B5%9C%EB%B3%91%EC%A4%80", map.get("name"));
		assertEquals("cbj0618%40gmail.com", map.get("email"));
	}
	
	@Test
	public void getQueryTest() throws Exception {
		assertEquals("/user/create", RequestHandlerUtils.getQuery(url));
	}
	
	@Test
	public void saveUserTest() throws Exception {
		String queryData = RequestHandlerUtils.getParams(url);
		HashMap<String, String> map = RequestHandlerUtils.getMapFromParams(queryData);
		User user = RequestHandlerUtils.saveUserFromMap(map);
		
		assertEquals("cbj0618", user.getUserId());
		assertEquals("123456", user.getPassword());
		assertEquals("%EC%B5%9C%EB%B3%91%EC%A4%80", user.getName());
		assertEquals("cbj0618%40gmail.com", user.getEmail());
	}
	
	@Test
	public void createUserTest() throws Exception {
		User user = RequestHandlerUtils.createUser(url);
		assertEquals("cbj0618", user.getUserId());
		assertEquals("123456", user.getPassword());
		assertEquals("%EC%B5%9C%EB%B3%91%EC%A4%80", user.getName());
		assertEquals("cbj0618%40gmail.com", user.getEmail());
	}
	
	@After
	public void teardown(){
		header = null;
		url = null;
	}
}
