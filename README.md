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
* WebServer.class 를 main 으로 실행하는 이 프로젝트는 TCP소켓 프로그래밍으로 제작되었다. TCP소켓 통신은 클라이언트와 서버간 1:1 통신이다. 그 프로세스는 다음과 같다. 1) 서버에서 서버소켓을 특정 포트와 연결하여 생성하고, 클라이언트의 요청 대기. 2) 클라이언트에서 서버의 IP 주소, 포트 정보로 소켓을 생성하고 서버에 연결 요청. 3) 서버에 클라이언트의 요청이 들어오면 서버에서 새로운 소켓을 생성하고 클라이언트의 소켓과 연결. 4) 서버에서 새로 생성된 소켓과 클라이언트 소켓이 서로 1:1 통신을 수행 (서버소켓은 관여 X).
* Socket : inputStream과 OutputStream을 가지고 있으며 이를 이용하여 프로세스 간의 통신(입출력)이 이루어진다.
* ServerSocket : port 와 연결되며 외부의 연결요청을 기다린다.(serverSocket.accept() 가 기다리는 상태임) 외부 연결 요청이 들어오면 Socket을 생성하여 소켓 간 통신이 이루어지도록 한다.
* 웹브라우져(클라이언트)에서 서버에 접속을 요청하면 inputStream을 통하여 header 가 전송된다. header 내에는 GET/POST 방식 여부, 요청 url (/index.html 과 같은...) 등의 요청정보가 들어있다.
* 서버가 클라이언트(웹브라우져)의 요청(요청url 로부터 어떤 요청을 했는지 알 수 있다.)을 수행한 뒤, 클라이언트에게 outputStream 을 통하여 응답해준다.
* 웹브라우져 요청의 경우, html 파일 내에 css, javascript 파일 참조가 있는 경우 서버로 해당 파일을 추가적으로 요청한다. (이 프로젝트의 경우도 ~/index.html 로 요청을 하면 소켓 연결이 6번(1번은 index.html 파일, 나머지는 css, javascript 파일) 되는 것을 로그를 통해 확인할 수 있다. )
* try-with-resource 문 (자동 자원 반환) : RequestHandler 클래스에 있는 try-catch 문 중에 try 옆에 ( ) 안에 stream 이 있는데, 저렇게 ( ) 안에서 객체를 생성하면 try 블럭을 벗어날 때 자동적으로 close() 가 호출되어 자원을 반환한다. 단, ( ) 안에는 AutoCloseable 인터페이스를 구현한 객체만 올 수 있다. 

### 요구사항 2 - get 방식으로 회원가입
*

### 요구사항 3 - post 방식으로 회원가입
*

### 요구사항 4 - redirect 방식으로 이동
*

### 요구사항 5 - cookie
*

### 요구사항 6 - stylesheet 적용
*

### heroku 서버에 배포 후
*
