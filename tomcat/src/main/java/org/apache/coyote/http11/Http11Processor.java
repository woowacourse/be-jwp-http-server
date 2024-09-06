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
            // 3단계에 request를 처리해줄 클래스로 분리할 예정입니다.
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String[] httpRequestFirstLine = bufferedReader.readLine().split(" ");
            String httpMethod = httpRequestFirstLine[0];
            String uri = httpRequestFirstLine[1];
            Map<String, String> headers = getHeaders(bufferedReader);

            if ("GET".equals(httpMethod)) {
                handleGetRequest(uri, new HttpCookie(headers.get("Cookie")), outputStream);
            }
            if ("POST".equals(httpMethod)) {
                handlePostRequest(uri, getRequestBody(headers, bufferedReader), outputStream);
            }
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

    private void handleGetRequest(String uri, HttpCookie httpCookie, OutputStream outputStream) throws IOException {
        if ("/favicon.ico".equals(uri)) {
            handleFaviconRequest(outputStream);
            return;
        }
        if ("/login".equals(uri) && doesLoggedIn(httpCookie)) {
            writeRedirectResponse("/index.html", outputStream);
            return;
        }
        writeStaticFileResponse(uri, outputStream);
    }

    private void handleFaviconRequest(OutputStream outputStream) throws IOException {
        final var response = "HTTP/1.1 204 No Content \r\n" +
                "Content-Length: 0 \r\n" +
                "\r\n";
        writeResponse(outputStream, response);
    }

    private boolean doesLoggedIn(HttpCookie httpCookie) {
        Session session = sessionManager.findSession(httpCookie.get("JSESSIONID"));
        return session != null && session.doesExistAttribute("user");
    }

    public void handlePostRequest(String uri, String requestBody, OutputStream outputStream) throws IOException {
        if ("/login".equals(uri)) {
            handleLogin(requestBody, outputStream);
            return;
        }
        if ("/register".equals(uri)) {
            handleRegister(requestBody, outputStream);
        }
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
                if (user.checkPassword(password)) {
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

    private void writeRedirectResponseWithCookie(String jSessionId, String location, OutputStream outputStream) throws IOException {
        final var response = "HTTP/1.1 302 Found \r\n" +
                "Set-Cookie: JSESSIONID=" + jSessionId + " \r\n" +
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
