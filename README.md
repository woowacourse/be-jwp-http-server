# 만들면서 배우는 스프링

## 톰캣 구현하기 기능 요구사항

### 1단계 - HTTP 서버 구현하기

1. GET /index.html 응답

- [x] 인덱스 페이지(`index.html`) 보여주도록 만들기 (`GET /index.html`)
- [x] Http11ProcessorTest 테스트 클래스의 모든 테스트 통과하기

2. CSS 지원

- [x] 인덱스 페이지 열었을 때 CSS 파일도 호출하도록 만들기 (`GET /css/style.css`)

3. Query String 파싱

- [x] 로그인 페이지(`login.html`) 보여주도록 만들기 (`GET /login?account=gugu&password=password`)
- [x] 로그인 페이지에 접속했을 때 Query String 파싱하기
- [x] 파싱한 아이디, 비밀번호가 일치하면 콘솔창에 로그로 회원을 조회한 결과가 나오게 만들기

### 2단계 - 로그인 구현하기

1. HTTP Status Code 302 반환

- [x] 로그인 여부에 따라 다른 페이지로 이동시키기
    - [x] 로그인 성공 시 http status code를 302로 반환하고 `index.html`로 리다이렉트
    - [x] 로그인 실패 시 http status code를 302로 반환하고 `401.html`로 리다이렉트

2. POST 방식으로 회원가입

- [x] 회원가입 페이지(`register.html`) 보여주도록 만들기 (`GET /register`)
- [x] 회원가입 버튼을 누르면 회원가입 시키기 (`POST /register`)
    - [x] 회원가입 성공 시 `index.html`로 리다이렉트
- [x] 로그인 페이지 보여줄 때와 로그인 버튼 누를 때 API 분리하기 (`GET /login`, `POST /login`)
    - [x] `login.html` form 태그 수정

3. Cookie에 JSESSIONID 값 저장

- [x] 쿠키 활용해서 로그인 상태 유지하기 (쿠키에 JSESSIONID 값 저장)
    - [x] Cookie 클래스 추가

4. Session 구현

- [x] 세션 활용해서 로그인 상태 유지하기 (세션을 사용해서 서버에 로그인 여부를 저장)
    - [x] 쿠키에서 전달 받은 JSESSIONID 값으로 로그인 여부 체크
    - [x] SessionManager, Session 클래스 추가
    - [x] 로그인 성공 시 Session 객체의 값으로 User 객체를 저장
    - [x] 로그인된 상태에서 로그인 페이지 접근 시 index.html 페이지로 리다이렉트 (`GET /login`)

### 3단계 - 리팩터링

- [x] HttpRequest 클래스 구현하기
- [x] HttpResponse 클래스 구현하기
- [x] Controller 인터페이스 추가하기

### 4단계 - 동시성 확장하기

1. Executors로 Thread Pool 적용

- [x] Connector 클래스에서 Executors 클래스를 사용해서 ExecutorService 객체(스레드 풀) 만들기
- [x] 스레드 갯수는 maxThreads 라는 변수로 지정하기
- [x] Connector 클래스의 void process(final Socket connection) 메서드에서 매 요청마다 스레드 새로 생성하지 말고, 스레드 풀의 스레드 사용하기
- [x] 생각해보기
    - acceptCount와 maxThreads는 각각 어떤 설정일까?
        - acceptCount: 동시에 처리할 수 있는 최대 연결 대기 수 (동시에 처리할 수 있는 연결 다 차면 대기열에 들어감)
        - maxThreads: 동시에 실행할 수 있는 최대 스레드 수
    - 최대 ThreadPool의 크기는 250, 모든 Thread가 사용 중인(Busy) 상태이면 100명까지 대기 상태로 만들려면 어떻게 할까?
        - acceptCount를 100으로 설정

2. 동시성 컬렉션 사용하기

- [x] 동시성 컬렉션(Concurrent Collections)을 적용해서 Session 컬렉션에 스레드 안정성과 원자성을 보장하기
    - SessionManager 클래스에서 Session 컬렉션은 여러 스레드가 동시에 접근할 수 있음
    - Session 컬렉션에 여러 스레드가 동시에 접근하여 읽고 쓰다보면 스레드 안정성을 보장하기 어려움

## 톰캣 구현하기 가이드

### 학습목표

- 웹 서버 구현을 통해 HTTP 이해도를 높인다.
- HTTP의 이해도를 높혀 성능 개선할 부분을 찾고 적용할 역량을 쌓는다.
- 서블릿에 대한 이해도를 높인다.
- 스레드, 스레드풀을 적용해보고 동시성 처리를 경험한다.

### 시작 가이드

1. 미션을 시작하기 전에 파일, 입출력 스트림 학습 테스트를 먼저 진행합니다.
    - [File, I/O Stream](study/src/test/java/study)
    - 나머지 학습 테스트는 다음 강의 시간에 풀어봅시다.
2. 학습 테스트를 완료하면 LMS의 1단계 미션부터 진행합니다.

## 학습 테스트

1. [File, I/O Stream](study/src/test/java/study)
2. [HTTP Cache](study/src/test/java/cache)
3. [Thread](study/src/test/java/thread)
