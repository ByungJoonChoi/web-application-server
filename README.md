# 실습을 위한 개발 환경 세팅
* https://github.com/slipp/web-application-server 프로젝트를 자신의 계정으로 Fork한다. Github 우측 상단의 Fork 버튼을 클릭하면 자신의 계정으로 Fork된다.
* Fork한 프로젝트를 eclipse 또는 터미널에서 clone 한다.
* Fork한 프로젝트를 eclipse로 import한 후에 Maven 빌드 도구를 활용해 eclipse 프로젝트로 변환한다.(mvn eclipse:clean eclipse:eclipse)
* 빌드가 성공하면 반드시 refresh(fn + f5)를 실행해야 한다.

# 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

# 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다.
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다.

### 요구사항 1 - http://localhost:8080/index.html로 접속시 응답
* WebServer.class 를 main 으로 실행하는 이 프로젝트는 TCP소켓 프로그래밍으로 제작되었다. TCP소켓 통신은 클라이언트와 서버간 1:1 통신이다. 그 프로세스는 다음과 같다. <br>1) 서버에서 서버소켓을 특정 포트와 연결하여 생성하고, 클라이언트의 요청 대기. <br>2) 클라이언트에서 서버의 IP 주소, 포트 정보로 소켓을 생성하고 서버에 연결 요청. <br>3) 서버에 클라이언트의 요청이 들어오면 서버에서 새로운 소켓을 생성하고 클라이언트의 소켓과 연결. <br>4) 서버에서 새로 생성된 소켓과 클라이언트 소켓이 서로 1:1 통신을 수행 (서버소켓은 관여 X).
* Socket : inputStream과 OutputStream을 가지고 있으며 이를 이용하여 프로세스 간의 통신(입출력)이 이루어진다.
* ServerSocket : port 와 연결되며 외부의 연결요청을 기다린다.(serverSocket.accept() 가 기다리는 상태임) 외부 연결 요청이 들어오면 Socket을 생성하여 소켓 간 통신이 이루어지도록 한다.
* 웹브라우져(클라이언트)에서 서버에 접속을 요청하면 inputStream을 통하여 header 가 전송된다. header 내에는 GET/POST 방식 여부, 요청 url (/index.html 과 같은...) 등의 요청정보가 들어있다.
* 서버가 클라이언트(웹브라우져)의 요청(요청url 로부터 어떤 요청을 했는지 알 수 있다.)을 수행한 뒤, 클라이언트에게 outputStream 을 통하여 응답해준다.
* 웹브라우져 요청의 경우, html 파일 내에 css, javascript 파일 참조가 있는 경우 서버로 해당 파일을 추가적으로 요청한다. (이 프로젝트의 경우도 ~/index.html 로 요청을 하면 소켓 연결이 6번(1번은 index.html 파일, 나머지는 css, javascript 파일) 되는 것을 로그를 통해 확인할 수 있다. )
* try-with-resource 문 (자동 자원 반환) : RequestHandler 클래스에 있는 try-catch 문 중에 try 옆에 ( ) 안에 stream 이 있는데, 저렇게 ( ) 안에서 객체를 생성하면 try 블럭을 벗어날 때 자동적으로 close() 가 호출되어 자원을 반환한다. 단, ( ) 안에는 AutoCloseable 인터페이스를 구현한 객체만 올 수 있다.

### 요구사항 2 - get 방식으로 회원가입
* url 이 ( A )?( B ) 형태로 들어왔을 때, A 부분을 query 라고 지칭하였고, B 부분을 param 이라고 지칭하였다.
* 한글을 입력했을 때, 서버로 전송된 한글 데이터가 깨지는 현상이 있다. 왜 이런 현상이 일어나는지 솔직히 정확하게 이해가 가지는 않는다. 단지, 인코딩 방식(UTF-8, EUC-KR 등)이 여러가지가 있는데 그게 html 파일, response header 등에 다르게 적용이 될 수가 있어서 이를 통일해야 한다고 이해했다. 그리고, 서버로 전송된 깨진 한글 데이터에 대해서는 URLDecoder로 디코딩을 하니 한글이 제대로 나오는 것을 확인할 수 있었다.
* URL을 인코딩해야 하는 2가지 이유 : 1) URL 은 ASCII char-set으로만 구성되어야 하는데 한글은 여기 해당이 안되기 때문 2) URL 내에서 의미를 갖는 문자(%,?,# 등)를 그대로 사용하면 문제가 생길 수 있기 때문(이걸로 공격도 할 수 있다는데....)
참고자료 : http://deploy.co.kr/blog/241, http://regularmotion.kr/url-encoding-url/, http://nomore7.tistory.com/entry/JAVA-Encoding-%EA%B3%BC-Decoding%EC%97%90-%EB%8C%80%ED%95%9C-%EC%A0%95%EB%A6%AC, http://hamait.tistory.com/355
* \r\n : \r 은 행의 맨 왼쪽으로 이동하기, \n 은 아래로 이동하기. 아무튼 둘이 합쳐서 쓰면 개행인듯??
참고자료 : https://kldp.org/node/28535
* (의문점) HttpRequestUtils 에 parseValues 메소드를 보면 params 를 파싱하여 map(K,V)을 생성하여 리턴하는데 stream을 사용하는 것을 볼 수 있다. 이렇게 stream을 사용해서 코드를 작성하면 좋은점은 무엇일까??
* (경험담) 마지막에 TestCase 작성이 귀찮아서 개발부터 먼저 했는데, 에러가 발견되었다. 에러가 어디서 났는지 한눈에 볼 수가 없었다. 귀찮아도... TestCase 작성 먼저 하고 개발하는 것이 나은 것 같다.
* 사용자 정보 4가지가 무조건 입력되어야 한다는 정책을 세운다면, HTML 에서만 입력을 강요하면 안된다. URL을 조작해서 서버에 정보를 전달할 수도 있기 때문이다. 이러한 정책이 있으면 반드시 서버에서 사용자 정보 4가지가 다 저장되도록 처리를 해줘야 한다. data model 에서 변수에 null 이 입력되지 않도록 강제하면 어떨까?
* 원격서버에서 메이븐으로 빌드하니 실패하였다. 로컬에서 실행은 되는데 왜 빌드가 실패하나 살펴봤더니 JunitTest가 실패했었다. 해당부분을 true가 나오도록 수정하고 다시 빌드하니 성공했다. 작성한 메서드의 기능을 변경하면, 반드시 해당 메서드를 테스트했던 테스트케이스도 수정하거나 살펴보자.

### 요구사항 3 - post 방식으로 회원가입
* 웹에서 서버로 HTTP request 를 보낼 때 POST 방식으로 데이터를 보내면 HEADER 내에 Content-Length로 몇 바이트의 BODY 를 보냈는지 알려준다.
* BODY가 저장된 위치는 HEADER가 끝나고 한줄 띈 아래이다.<br>
예)<br>
POST /user/create HTTP/1.1<br>
Host: localhost:8080<br>
Connection: keep-alive<br>
Content-Length: 59<br>
Content-Type: application/x-www-form-urlencoded<br>
Accept: \*/\*<br><br>
userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net<br>

* 서버로 response 할 때도 이와 비슷한 형태로 header(Content-Length가 표함됨)와 body 를 보내는 것을 확인할 수 있다.

### 요구사항 4 - redirect 방식으로 이동
* HTTP response code를 redirect를 의미하는 code로 변경해주면 간단하게 클라이언트를 redirect시킬 수 있다.
* 기존에 response header에 "200 OK"라고 되어 있던 부분을 "302 Found"로 바꾸면 redirect시키겠다는 의미이다.<br><br>
예)<br>
HTTP/1.1 302 Found<br>
Location: http://www.naver.com<br>
* response 코드에는 여러종류가 있으며 대략적으로 아래와 같은 의미이다.<br>
2XX : 성공<br>
3XX : redirect <br>
4XX : 클라이언트 에러 <br>
5XX : 서버 에러 <br>

* response code 참고자료 <br>
https://en.wikipedia.org/wiki/List_of_HTTP_status_codes <br> 
https://en.wikipedia.org/wiki/HTTP_302 <br>

### 요구사항 5 - cookie
* [로그인 성공 정책]<br>User validation check 를 위해서 User class의 equals 메소드를 오버라이드 하려했다.<br>그런데 로그인할 때 필요한 정보는 id, pw 뿐인데 혹여 나중에 "id, pw 이외의 정보까지 모두 일치하는 경우에만 두 User 객체가 같다고 하자"는 정책이 추가될 상황이 올지도 모른다는 생각이 들어서 equals 메소드 오버라이딩을 하지 않기로 했다. <br>우선, id, pw 만 확인해서 같으면 로그인 성공시키기로 했다.

* [쿠키와 리다이렉션을 동시에]<br>쿠키와 리다이렉션을 동시에 진행하기 위해, 처음에는 response header 에 쿠키설정을 해서 보내고, redirect 메소드를 그냥 쓰는 삽질을 했었다. <br>HTTP/1.1 200 OK 로 쿠키 설정부분만 보낸 뒤 redirect 메소드 그냥 써서 붙이면 header 가 뭔가 문법에 안맞게 구성된다. <br>결국 브라우져는 HTTP/1.1 200 OK 로 시작했던 쿠키설정 부분만 받아들이고 그 뒤에 덫붙여 날라온 HTTP/1.1 302 Found 이하 부분은 해석하지 못하고 화면에 출력해주었다.(리다이렉션은 이루어지지 않았다.<br> 그냥 redirect 메소드를 조금 손봐서 HTTP/1.1 302 Found 부분과 url Location 설정 부분 사이에 Set-Cookie 부분을 추가해주면 되는 문제였다.

* [쿠키설정 범위]<br>요구사항 5 구현과정에서 로그인 할 때 사용했던 url 뒷부분이 /user/login 이다.<br>이때 생성된 쿠키는 /user 이하 범위인것 같다. 로그인 성공시 index.html로 돌아오는데 이때는 쿠키값이 없기 때문에 그렇게 추측해보았다.

### 요구사항 6 - 사용자 목록 출력
* 헤더에 있는 쿠키로부터 로그인 여부를 판단할 수 있다.<br>로그인된 경우 html문서를 String으로 동적으로 생성한 뒤, 바이트 단위로 읽어서 response body를 만들어 브라우져에 응답한다. 

### 요구사항 7 - stylesheet 적용
* 기존 코드에는 브라우져에게 응답을 할 때 response header의 Content-Type 이 무조건 text/html 이었다.
<br>그렇게 설정해 놓으면 css 파일 요청이 왔을 때도 브라우저는 서버로부터 받은 css 파일을 html 문서로 인식해버린다.
* 이 문제를 해결하기 위해서는 <br>1) request header 로부터 css 파일을 요청했는지 여부를 알아낸뒤, <br>2) css 파일 요청이면 response header의 Content-Type을 text/css 로 설정하여 보내면 된다.
* css 파일을 요청했는지 여부는 request header의 Accept: 이하 부분을 보면 확인할 수 있다.

### heroku 서버에 배포 후
*
