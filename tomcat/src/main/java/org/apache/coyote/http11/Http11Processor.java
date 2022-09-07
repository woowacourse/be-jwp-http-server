package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.handler.LoginHandler;
import nextstep.jwp.handler.RegisterHandler;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final var request = readHttpRequest(bufferedReader);

            if (request.isEmpty()) {
                return;
            }

            final HttpRequest httpRequest = HttpRequest.from(request, request);
            final var response = getResponse(httpRequest);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readHttpRequest(final BufferedReader bufferedReader) throws IOException {
        StringBuilder header = new StringBuilder();

        final var startLine = bufferedReader.readLine();
        final var requestHeaders = readRequestHeaders(bufferedReader);
        final HttpRequest httpRequest = HttpRequest.from(startLine, requestHeaders);

//        while (bufferedReader.ready()) {
//            header.append(bufferedReader.readLine())
//                    .append("\r\n");
//        }

//        int contentLength = Integer.parseInt(httpRequestHeaders.get("Content-Length"));
//        char[] buffer = new char[contentLength];
//        bufferedReader.read(buffer, 0, contentLength);
//        String requestBody = new String(buffer);

        return header.toString();
    }

    private String readRequestHeaders(final BufferedReader bufferedReader) throws IOException {
        StringBuilder requestHeaders = new StringBuilder();
        String line;
        while (!(line = Objects.requireNonNull(bufferedReader.readLine())).isBlank()) {
            requestHeaders.append(line)
                    .append("\r\n");
        }
        return requestHeaders.toString();
    }

    private String getResponse(final HttpRequest httpRequest) throws IOException {
        return getResponse(httpRequest.getRequestUrl(), httpRequest.getRequestParams());
    }

    private String getResponse(final String url, final Map<String, String> requestParam) throws IOException {
        if ("/".equals(url)) {
            final var responseBody = "Hello world!";

            return createResponse("text/html", responseBody);
        }

        if ("/login".equals(url) && requestParam.isEmpty()) {
            return createStaticFileResponse(url + ".html");
        }

        if ("/login".equals(url)) {
            if (LoginHandler.handle(requestParam)) {
                return createUserSuccessResponse();
            }

            return createLoginFailResponse();
        }

        if ("/register".equals(url) && requestParam.isEmpty()) {
            return createStaticFileResponse(url + ".html");
        }

        if ("/register".equals(url)) {
            if (RegisterHandler.handle(requestParam)) {
                return createUserSuccessResponse();
            }
        }

        if (url.contains(".")) {
            return createStaticFileResponse(url);
        }

        throw new IllegalArgumentException("올바르지 않은 URL 요청입니다.");
    }

    private String createStaticFileResponse(final String url) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static" + url);
        final Path path = new File(resource.getFile()).toPath();
        final String responseBody = new String(Files.readAllBytes(path));

        return createResponse(Files.probeContentType(path), responseBody);
    }

    private String createResponse(final String contentType, final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String createUserSuccessResponse() throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final Path path = new File(resource.getFile()).toPath();
        final String responseBody = new String(Files.readAllBytes(path));

        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Content-Type: " + Files.probeContentType(path) + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String createLoginFailResponse() throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        final Path path = new File(resource.getFile()).toPath();
        final String responseBody = new String(Files.readAllBytes(path));

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + Files.probeContentType(path) + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
