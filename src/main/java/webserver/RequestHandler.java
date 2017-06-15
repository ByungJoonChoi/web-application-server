package webserver;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.Request;
import model.User;
import util.HTMLUtil;
import util.RequestHandlerUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    
    private static final String CREATE_USER = "/user/create";
    private static final String LOGIN_USER = "/user/login";
    private static final String USER_LIST ="/user/list";
    private static final String GET = "GET";
    private static final String POST = "POST";
//    private static final String LOCAL_HOME = "http://localhost:8080/index.html";
    private static final String SERVER_HOME = "http://13.124.139.176:8080/index.html"; 
    private static final String LOGIN_FAIL = "http://localhost:8080/user/login_failed.html";
    private static final String LOGIN = "http://localhost:8080/user/login.html";
    
    private String contentType = "text/html";
   
    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
        	Request req = RequestHandlerUtils.parseRequest(in);
    		String url = RequestHandlerUtils.getURLfromHeader(req.getHeader());
    		log.debug("Request url : " + url);
    		log.debug("header : " + req.getHeader());
    		if(isCreateUser(url)){
    			DataBase.addUser(createUser(url, req));
    			redirect(out, SERVER_HOME);
//    			redirect(out, LOCAL_HOME);
    			return;
    		}
    		
    		if(isLoginUser(url)){
    			log.debug("LogingUser!!!");
    			log.debug("body : " + req.getBody());
    			if(isValidUser(req.getBody())){
//    				redirect(out, LOCAL_HOME, true);
    				redirect(out, SERVER_HOME, true);
    				return;
    			}
    			redirect(out, LOGIN_FAIL, false);
    			return;
    		}
    		
    		if(isUserList(url)){
    			if(RequestHandlerUtils.isLoggedIn(req.getHeader())){
    				String html = HTMLUtil.getUserList();
    				byte[] body = html.getBytes();
    				DataOutputStream dos = new DataOutputStream(out);
    				response200Header(dos, body.length);
    				responseBody(dos, body);
    				return;
    			}
    			redirect(out, LOGIN);
    			return;
    		}
    		
    		if(isReqStylesheet(req.getHeader())){
    			contentType = "text/css";
    		}
    		responseData(url, out); // 유저 생성요청이 아니면 url을 클라이언트가 요청한 자원 경로로 인식하여 요청 처리
    		
        } catch (IOException e) {
            log.error(e.getMessage());
        } catch (Exception e){
        	log.error(e.getMessage());
        }
    }

	private boolean isReqStylesheet(String header) {
		return RequestHandlerUtils.isReqStylesheet(header);
	}

	private boolean isValidUser(String params) throws Exception {
		return RequestHandlerUtils.isValidUser(params);
	}

	private void redirect(OutputStream out, String url) {
		DataOutputStream dos = new DataOutputStream(out);
		try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + url + " \r\n");
            dos.flush();
		} catch (IOException e) {
            log.error(e.getMessage());
        }
	}
	
	private void redirect(OutputStream out, String url, boolean isLogin) {
		DataOutputStream dos = new DataOutputStream(out);
		try {
			dos.writeBytes("HTTP/1.1 302 Found \r\n");
			dos.writeBytes("Set-Cookie: logined=" + isLogin + "\r\n");
			dos.writeBytes("Location: " + url + " \r\n");
			dos.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private User createUser(String url, Request req) throws Exception{
    	String method = RequestHandlerUtils.getMethod(req.getHeader());
    	String params = null;
    	if(GET.equals(method)){
    		params = RequestHandlerUtils.getParams(url);
    	}
    	if(POST.equals(method)){
    		params = req.getBody();
    	}
    	return createUserFromParams(params);
    }

	private User createUserFromParams(String params) throws Exception {
		if(params == null || params.isEmpty()){
			log.debug("params are not sent");
			return null;
		}
		// 웹브라우져에서 URL을 조작하여 User 데이터 저장과 상관없는 (K,V) pair를 전달할 수도 있다. 
		// 이 경우 현재는 map에 (K,V) 형태로 저장이 되며, createUser 메소드를 수행하는데 큰 영향을 주지는 않는다. 
		// 단, User model 클래스의 key 중 빠드린 것이 있어도 User 객체가 만들어지는데 해당 필드값은 null 로 설정된다.
		// User data field 중 null로 되는 것이 없게 하려면 별도의 조치를 취해주어야 한다. 
		return RequestHandlerUtils.createUser(params);
	}
	

	private boolean isUserList(String url) {
		return USER_LIST.equals(RequestHandlerUtils.getQuery(url));
	}

	private boolean isCreateUser(String url) {
		return CREATE_USER.equals(RequestHandlerUtils.getQuery(url));
	}
	
	private boolean isLoginUser(String url) {
		return LOGIN_USER.equals(RequestHandlerUtils.getQuery(url));
	}

	private void responseData(String url, OutputStream out) throws IOException {
		byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
		DataOutputStream dos = new DataOutputStream(out);
		
		response200Header(dos, body.length);
		responseBody(dos, body);
	}

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
//            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
