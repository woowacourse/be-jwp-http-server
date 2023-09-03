package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHeader;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    //    @Override
//    public void process(final Socket connection) {
//        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//             final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {
//
//            RequestLine requestLine = readRequestLine(reader);
//            RequestHeader requestHeader = readHeader(reader);
//            String requestBody = readRequestBody(reader, requestHeader);
//
//            String response = ResponseGenerator.generate(requestLine, requestHeader, requestBody);
//            writer.write(response);
//            writer.flush();
//        } catch (IOException | UncheckedServletException e) {
//            log.error(e.getMessage(), e);
//        }
//    }
    private static final String DEFAULT_PAGE = "/index.html";
    private static final String DEFAULT = "html";

    @Override
    public void process(final Socket connection) {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {

            RequestLine requestLine = readRequestLine(reader);
            RequestHeader requestHeader = readHeader(reader);
            RequestBody requestBody = readRequestBody(reader, requestHeader);

            Response response = getResponse(requestLine, requestHeader, requestBody);

            // "/index.html"이나 "login.html"인 경우

            writer.write(response.toString());
            writer.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static Response getResponse(final RequestLine requestLine, final RequestHeader requestHeader, RequestBody requestBody) {
        String requestUrl = requestLine.getRequestUrl();
        // "/"인 경우
        if (requestUrl.equals("/") && requestLine.getRequestMethod().equals("GET")) {
            return responseStaticFile(DEFAULT_PAGE, requestHeader);
        }
        if (requestUrl.startsWith("/login")) {
            if (requestLine.getRequestMethod().equals("GET")) {
                return responseStaticFile("/login.html", requestHeader);
            }
            if (requestLine.getRequestMethod().equals("POST")) {
                return login(requestHeader, requestBody);
            }
        }
        if (requestUrl.startsWith("/register")) {
            if (requestLine.getRequestMethod().equals("GET")) {
                return responseStaticFile("/register.html", requestHeader);
            }
            if (requestLine.getRequestMethod().equals("POST")) {
                return register(requestBody);
            }

        }
        return responseStaticFile(requestUrl, requestHeader);
    }

    private static Response login(final RequestHeader requestHeader, final RequestBody requestBody) {
        try {
            if (requestBody == null) {
                return Response.redirection("401.html");
            }
            String account = requestBody.getContentValue("account");
            String password = requestBody.getContentValue("password");
            User user = InMemoryUserRepository.findByAccount(account).orElseThrow(() -> new IllegalArgumentException()
            );//todo : 해당하는 계정이 존재하지 않는다
            if (!user.checkPassword(password)) {
                throw new RuntimeException(); //todo :비밀번호가 일치하지 않는다.
            }
            Response response = Response.redirection("/index.html");
            if(!requestHeader.getHeaderValue("Cookie").contains("JSESSIONID")) {
                response.addHeader("Set-Cookie", "JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");
            }
            return response;
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
        }
        return Response.redirection("/401.html");
    }

    //회원가입을 버튼을 누르면 HTTP method를 GET이 아닌 POST를 사용한다.
    //회원가입을 완료하면 index.html로 리다이렉트한다.
    //로그인 페이지도 버튼을 눌렀을 때 GET 방식에서 POST 방식으로 전송하도록 변경하자.

    private static Response register(final RequestBody requestBody) {
        if (requestBody == null) {
            return Response.redirection("401.html");
        }
        String account = requestBody.getContentValue("account");
        String email = requestBody.getContentValue("email");
        String password = requestBody.getContentValue("password");
        InMemoryUserRepository.save(new User(account, password, email));
        return Response.redirection("/index.html");
    }

    //todo : 정적파일은 다 있다고 가정? 없는 파일이면?
    /*
    정적파일 요청
    url로 파일 찾음
    - 파일 있으면 : 파일 읽어서 responseBody + http 요청 생성
    - 파일 없으면 : Location 헤더만 돌려줌
     */
    private static Response responseStaticFile(String requestUri, RequestHeader requestHeader) {
        try {
            String requestedFile = ClassLoader.getSystemClassLoader().getResource("static" + requestUri).getFile();
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(requestedFile, Charset.forName("UTF-8")));
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str + "\n");
            }
            String responseBody = sb.toString();
            return Response.ok(responseBody, requestHeader);
        } catch (IOException | NullPointerException e) {
            log.error(e.getMessage(), e);
        }
        return Response.redirection("404.html");
    }

    private RequestLine readRequestLine(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        return RequestLine.from(line);
    }

    private RequestHeader readHeader(BufferedReader reader) throws IOException {
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.equals("")) {
                break;
            }
            lines.add(line);
        }
        return RequestHeader.from(lines);
    }

    private RequestBody readRequestBody(final BufferedReader reader, final RequestHeader requestHeader) throws IOException {
        if (requestHeader.getHeaderValue("Content-Type") == null) {
            return null;
        }
        int contentLength = Integer.valueOf(requestHeader.getHeaderValue("Content-Length"));
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);
        return RequestBody.from(requestBody);
    }


}
