package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final SessionManager sessionManager = SessionManager.getInstance();

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String[] httpRequestFirstLine = bufferedReader.readLine().split(" ");
            String httpMethod = httpRequestFirstLine[0];
            String uri = httpRequestFirstLine[1];
            Map<String, String> headers = getHeaders(bufferedReader);

            if ("GET".equals(httpMethod)) {
                if ("/favicon.ico".equals(uri)) {
                    handleFaviconRequest(outputStream);
                    return;
                }
                /*
                TODO: GET /login 요청이 왔을 때
                1. Cookie 확인 -> JSESSIONID가 있다면,
                2. sessionManager.findSession(String id)를 활용해서 Session 객체 조회
                3. session.isExistAttribute("user")로 세션에 저장된 유저객체가 있는지 질문
                4. true가 나온다면 로그인이 잘 됐다고 판단하고, index.html로 리다이렉트
                 */
                if ("/login".equals(uri)) {
                    HttpCookie httpCookie = new HttpCookie(headers.get("Cookie"));
                    String jsessionId = httpCookie.get("JSESSIONID");
                    if (jsessionId != null) {
                        Session session = sessionManager.findSession(jsessionId);
                        if (session != null && session.isExistAttribute("user")) {
                            writeRedirectResponse("/index.html", outputStream);
                            return;
                        }
                    }
                }
            }
            if ("POST".equals(httpMethod)) {
                String requestBody = getRequestBody(headers, bufferedReader);
                if ("/login".equals(uri)) {
                    handleLogin(requestBody, outputStream);
                    return;
                }
                if ("/register".equals(uri)) {
                    handleRegister(requestBody, outputStream);
                    return;
                }
            }

            writeStaticFileResponse(uri, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> getHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        while (true) {
            String header = bufferedReader.readLine();
            if (header.isBlank()) {
                break;
            }
            String[] splitHeader = header.split(":");
            headers.put(splitHeader[0].trim(), splitHeader[1].trim());
        }
        return headers;
    }

    private void handleFaviconRequest(OutputStream outputStream) throws IOException {
        final var response = "HTTP/1.1 204 No Content \r\n" +
                "Content-Length: 0 \r\n" +
                "\r\n";
        writeResponse(outputStream, response);
    }

    private String getJsessionId(String cookie) {
        if (cookie == null) {
            return null;
        }
        Map<String, String> cookiePairs = new HashMap<>();
        for (String rawCookiePair : cookie.split(";")) {
            String[] cookiePair = rawCookiePair.split("=");
            cookiePairs.put(cookiePair[0].trim(), cookiePair[1].trim());
        }
        return cookiePairs.get("JSESSIONID");
    }

    private String getRequestBody(Map<String, String> headers, BufferedReader bufferedReader) throws IOException {
        int contentLength = Integer.parseInt(headers.get("Content-Length"));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);

        return new String(buffer);
    }

    private void handleLogin(String requestBody, OutputStream outputStream) throws IOException {
        Map<String, String> pairs = getPairs(requestBody);

        String account = pairs.get("account");
        String password = pairs.get("password");
        if (account != null & password != null) {
            Optional<User> foundUser = InMemoryUserRepository.findByAccount(account);
            if (foundUser.isPresent()) {
                User user = foundUser.get();
                /*
                TODO: 로그인이 잘 됐으면, POST 요청이 날아올 일도 없다고 판단하자. 따라서 jsessionId == null인지 판단할 필요 없이 항상 응답에 Set-Cookie를 날린다.
                로그인에 성공하면
                1. UUID를 생성자에 넘기면서 Session 객체를 생성한다.
                2. session.setAttribute("user", user)로 유저 객체를 세션에 저장한다.
                3. writeRedirectResponseWithCookie에 UUID를 넘긴다.
                 */
                if (user.checkPassword(password)) {
                    // 근데 로그인 성공했으면 그냥 set cookie 항상 하면 되는거 아닌가? 여기서 분기처리 왜했지?
                    log.info("로그인 성공 ! 아이디 : {}", user.getAccount());
                    Session session = new Session(UUID.randomUUID().toString());
                    session.setAttribute("user", user);
                    sessionManager.add(session);
                    writeRedirectResponseWithCookie(session.getId(), "/index.html", outputStream);
                    return;
                }
            }
        }
        writeRedirectResponse("/401.html", outputStream);
    }

    private void handleRegister(String requestBody, OutputStream outputStream) throws IOException {
        Map<String, String> pairs = getPairs(requestBody);

        InMemoryUserRepository.save(new User(pairs.get("account"), pairs.get("password"), pairs.get("email")));
        writeRedirectResponse("/index.html", outputStream);
    }

    private Map<String, String> getPairs(String requestBody) {
        Map<String, String> queryStringPairs = new HashMap<>();
        for (String pairs : requestBody.split("&")) {
            String[] pair = pairs.split("=");
            if (pair.length == 2) {
                queryStringPairs.put(pair[0], pair[1]);
            }
        }

        return queryStringPairs;
    }

    private void writeRedirectResponseWithCookie(String jsessionId, String location, OutputStream outputStream) throws IOException {
        final var response = "HTTP/1.1 302 Found \r\n" +
                "Set-Cookie: JSESSIONID=" + jsessionId + " \r\n" +
                "Location: http://localhost:8080" + location + " \r\n" +
                "\r\n";
        writeResponse(outputStream, response);
    }

    private void writeRedirectResponse(String location, OutputStream outputStream) throws IOException {
        final var response = "HTTP/1.1 302 Found \r\n" +
                "Location: http://localhost:8080" + location + " \r\n" +
                "\r\n";
        writeResponse(outputStream, response);
    }

    private void writeStaticFileResponse(String uri, OutputStream outputStream) throws IOException {
        uri = addHtmlExtension(uri);
        var responseBody = getStaticFileContent(uri);
        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/" + getFileExtension(uri) + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        writeResponse(outputStream, response);
    }

    private String addHtmlExtension(String uri) {
        if (!"/".equals(uri) && !uri.contains(".")) {
            return uri + ".html";
        }
        return uri;
    }

    private String getStaticFileContent(String uri) throws IOException {
        if (Objects.equals(uri, "/")) {
            return "Hello world!";
        }
        String staticPath = "static" + uri;
        File file = new File(getClass().getClassLoader().getResource(staticPath).getPath());
        return new String(Files.readAllBytes(file.toPath()));
    }

    private String getFileExtension(String uri) {
        if (Objects.equals(uri, "/")) {
            return "html";
        }
        String[] splitPath = uri.split("\\.");
        return splitPath[splitPath.length - 1];
    }

    private void writeResponse(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
