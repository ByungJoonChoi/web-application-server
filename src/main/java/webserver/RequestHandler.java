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

import model.Request;
import model.User;
import util.RequestHandlerUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    
    private static final String CREATE_USER = "/user/create";
    private static final String GET = "GET";
    private static final String POST = "POST";

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
    		
    		if(isCreateUser(url)){
    			createUser(url, req);
    			return;
    		}
    		
//    		System.out.println("header : " + req.getHeader());
    		responseData(url, out); // 유저 생성요청이 아니면 url을 클라이언트가 요청한 자원 경로로 인식하여 요청 처리
    		
        } catch (IOException e) {
            log.error(e.getMessage());
        } catch (Exception e){
        	log.error(e.getMessage());
        }
    }
    
    private void createUser(String url, Request req) throws Exception{
    	String method = RequestHandlerUtils.getMethod(req.getHeader());
    	String params = null;
    	if(GET.equals(method)){
    		params = RequestHandlerUtils.getParams(url);
    	}
    	if(POST.equals(method)){
    		params = req.getBody();
    	}
    	createUserFromParams(params);
    }

	private void createUserFromParams(String params) throws Exception {
		if(params == null || params.isEmpty()){
			log.debug("params are not sent");
			return;
		}
		// 웹브라우져에서 URL을 조작하여 User 데이터 저장과 상관없는 (K,V) pair를 전달할 수도 있다. 
		// 이 경우 현재는 map에 (K,V) 형태로 저장이 되며, createUser 메소드를 수행하는데 큰 영향을 주지는 않는다. 
		// 단, User model 클래스의 key 중 빠드린 것이 있어도 User 객체가 만들어지는데 해당 필드값은 null 로 설정된다.
		// User data field 중 null로 되는 것이 없게 하려면 별도의 조치를 취해주어야 한다. 
		User user = RequestHandlerUtils.createUser(params);
		log.debug("user : " + user);
	}

	private boolean isCreateUser(String url) {
		return CREATE_USER.equals(RequestHandlerUtils.getQuery(url));
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
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
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
