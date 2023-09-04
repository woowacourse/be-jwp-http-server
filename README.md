# 톰캣 구현하기

# Tasks

## 학습 테스트 목록

- 파일 입출력
- 입출력 스트림

## 기능 목록

- Tomcat
    - GET 요청에 대해 페이지를 응답할 수 있다.
        - HTTP 요청을 파싱할 수 있다.
        - 요청한 리소스를 찾아 응답할 수 있다.
        - 리소스에 맞는 Content-Type으로 응답할 수 있다.
        - 사전 정의된 경로에 대해 사전 정의된 응답을 보낼 수 있다.
        - 쿼리 스트링에 접근할 수 있다.
        - 요청한 리소스에 확장자가 없으면 html을 요청한 것으로 간주한다.
    - POST 요청을 받아 처리하고, 응답할 수 있다.
    - 쿠키를 관리한다
      - 쿠키를 파싱할 수 있다.
      - 쿠키를 추가할 수 있다.
      - 헤더로 변환할 수 있다.
      - Set-Cookie로 클라이언트에 쿠키 추가를 요청할 수 있다.
    - 세션
      - UUID로 세션을 생성한다.
      - 세션에 저장된 정보를 가져온다.
- 어플리케이션
    - 아이디 패스워드로 로그인한다.
        - 아이디 패스워드로 유저를 조회한다
        - 성공하면 index로, 실패하면 401.html로 리다이렉트한다. 
        - 세션을 저장하고, 세션ID를 쿠키에 싣는다.
        - POST 요청으로 로그인한다.
    - POST 요청으로 회원가입한다.
