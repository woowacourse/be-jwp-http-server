package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
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
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            final String request = reader.readLine();
            String requestMethod = request.split(" ")[0];
            String requestUri = request.split(" ")[1];
            log.info("requestMethod = {}", requestMethod);
            log.info("requestUri = {}", requestUri);

            final Map<String, String> requestHeaders = new HashMap<>();
            String line;
            while (!"".equals(line = reader.readLine())) {
                String[] value = line.split(": ");
                requestHeaders.put(value[0], value[1]);
            }
            log.info("requestHeaders = {}", requestHeaders);

            final Map<String, String> requestBody = new HashMap<>();
            if (requestHeaders.get("Content-Length") != null) {
                int contentLength = Integer.parseInt(requestHeaders.get("Content-Length"));
                char[] buffer = new char[contentLength];
                reader.read(buffer, 0, contentLength);
                for (String body : new String(buffer).split("&")) {
                    String[] value = body.split("=");
                    requestBody.put(value[0], URLDecoder.decode(value[1], "UTF-8"));
                }
                log.info("requestBody = {}", requestBody);
            }

            var statusCode = "404";
            var contentType = "text/html;charset=utf-8";
            final URL resource404 = getClass().getClassLoader().getResource("static/404.html");
            var responseBody = new String(Files.readAllBytes(new File(resource404.getFile()).toPath()));

            if (requestUri.equals("/")) {
                statusCode = "200 OK";
                contentType = "text/html;charset=utf-8";
                responseBody = "Hello world!";

                final var response = String.join("\r\n",
                        "HTTP/1.1 " + statusCode + " ",
                        "Content-Type: " + contentType + " ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            if (requestUri.equals("/login")) {
                if (requestMethod.equals("GET")) {
                    requestUri = "login.html";
                }
                if (requestMethod.equals("POST")) {
                    statusCode = "302";
                    contentType = "text/html;charset=utf-8";
                    var location = "";

                    Optional<User> user = InMemoryUserRepository.findByAccount(requestBody.get("account"));
                    if (user.isPresent() && user.get().checkPassword(requestBody.get("password"))) {
                        location = "/index.html";
                        log.info("user: {}", user.get());
                    } else {
                        location = "/401.html";
                    }

                    final var response = String.join("\r\n",
                            "HTTP/1.1 " + statusCode + " ",
                            "Content-Type: " + contentType + " ",
                            "Location: " + location + " ",
                            "");

                    outputStream.write(response.getBytes());
                    outputStream.flush();
                    return;
                }
            }

            if (requestUri.equals("/register")) {
                if (requestMethod.equals("GET")) {
                    requestUri = "register.html";
                }
                if (requestMethod.equals("POST")) {
                    statusCode = "302";
                    var location = "/index.html";
                    final var response = String.join("\r\n",
                            "HTTP/1.1 " + statusCode + " ",
                            "Content-Type: " + contentType + " ",
                            "Location: " + location + " ",
                            "");
                    outputStream.write(response.getBytes());
                    outputStream.flush();
                    return;
                }
            }

            final String fileName = "static/" + requestUri;
            final URL resource = getClass().getClassLoader().getResource(fileName);
            if (requestHeaders.get("Accept") != null) {
                contentType = requestHeaders.get("Accept").split(",")[0];
            }
            if (resource != null) {
                statusCode = "200 OK";
                responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 " + statusCode + " ",
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
