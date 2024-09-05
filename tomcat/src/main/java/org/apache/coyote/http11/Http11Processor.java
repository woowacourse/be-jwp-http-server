package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final String REQUEST_HEADER_SUFFIX = "";
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
            final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            List<String> lines = new ArrayList<>();
            String line = br.readLine();
            if (line.isEmpty()) {
                return;
            }
            while (!REQUEST_HEADER_SUFFIX.equals(line)) {
                lines.add(line);
                line = br.readLine();
            }

            String[] startLine = lines.get(0).split(" ");
            if (startLine.length != 3) {
                return;
            }
            String httpMethod = startLine[0];
            String requestURI = startLine[1];
            String httpVersion = startLine[2];
            if (!"GET".equals(httpMethod) || !"HTTP/1.1".equals(httpVersion)) {
                return;
            }

            String contentType = "text/html";
            if (requestURI.contains(".")) {
                contentType = "text/" + requestURI.split("\\.")[1];
                requestURI = "static" + requestURI;
            }

            String responseBody = "Hello world!";
            final URL resource = getClass().getClassLoader().getResource(requestURI);
            if (resource != null) {
                final Path path = Paths.get(resource.toURI());
                responseBody = Files.readString(path);
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }
}
