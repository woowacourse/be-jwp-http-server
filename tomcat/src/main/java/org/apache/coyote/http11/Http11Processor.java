package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestFactory;
import org.apache.coyote.http11.request.QueryParams;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.util.FileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String WELCOME_MESSAGE = "Hello world!";

    private final Socket connection;

    public Http11Processor(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream();
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {

            HttpResponse httpResponse = getResponse(HttpRequestFactory.create(reader));
            String response = httpResponse.parseToString();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse getResponse(HttpRequest httpRequest) throws IOException {
        try {
            if (httpRequest.isStaticFileRequest()) {
                return getStaticResourceResponse(httpRequest);
            }
            return getDynamicResourceResponse(httpRequest);
        } catch (RuntimeException e) {
            return new HttpResponse("HTTP/1.1", HttpStatus.NOT_FOUND,
                    ContentType.TEXT_HTML_CHARSET_UTF_8, "페이지를 찾을 수 없습니다.");
        }
    }

    private HttpResponse getStaticResourceResponse(HttpRequest httpRequest) {
        Optional<String> extension = httpRequest.getExtension();
        if (extension.isPresent()) {
            ContentType contentType = ContentType.from(extension.get());
            return new HttpResponse(httpRequest.getRequestLine().getProtocol(), HttpStatus.OK, contentType,
                    getStaticResourceResponse(httpRequest.getRequestLine().getUri().getPath()));
        }
        return new HttpResponse(httpRequest.getRequestLine().getProtocol(), HttpStatus.NOT_FOUND,
                ContentType.TEXT_HTML_CHARSET_UTF_8, "페이지를 찾을 수 없습니다.");
    }

    private String getStaticResourceResponse(String resourcePath) {
        return FileReader.readStaticFile(resourcePath, this.getClass());
    }

    private HttpResponse getDynamicResourceResponse(HttpRequest httpRequest) {
        String path = httpRequest.getRequestLine().getUri().getPath();
        if (path.equals("/")) {
            return new HttpResponse(httpRequest.getRequestLine().getProtocol(), HttpStatus.OK,
                    ContentType.TEXT_HTML_CHARSET_UTF_8, WELCOME_MESSAGE);
        }
        if (path.equals("/login") && httpRequest.getRequestLine().hasQueryParams()) {
            return getLoginResponseWithQueryParams(httpRequest);
        }
        if (path.equals("/login") && httpRequest.hasRequestBody()) {
            return getLoginResponseWithRequestBody(httpRequest);
        }
        String responseBody = getStaticResourceResponse(path + ".html");
        return new HttpResponse(httpRequest.getRequestLine().getProtocol(), HttpStatus.OK,
                ContentType.TEXT_HTML_CHARSET_UTF_8, responseBody);
    }

    private HttpResponse getLoginResponseWithRequestBody(final HttpRequest httpRequest) {
        RequestBody requestBody = httpRequest.getRequestBody();
        Optional<User> user = InMemoryUserRepository.findByAccount(requestBody.getValue("account"));
        if (user.isPresent()) {
            if (user.get().checkPassword(requestBody.getValue("password"))) {
                return new HttpResponse(httpRequest.getRequestLine().getProtocol(), HttpStatus.FOUND,
                        ContentType.TEXT_HTML_CHARSET_UTF_8,
                        getStaticResourceResponse("/index.html"));
            }
        }
        return new HttpResponse(httpRequest.getRequestLine().getProtocol(), HttpStatus.FOUND, "/401.html");
    }

    private HttpResponse getLoginResponseWithQueryParams(HttpRequest httpRequest) {
        QueryParams queryParams = httpRequest.getRequestLine().getUri().getQueryParams();
        Optional<User> user = InMemoryUserRepository.findByAccount(queryParams.getParameterValue("account"));
        if (user.isPresent()) {
            if (user.get().checkPassword(queryParams.getParameterValue("password"))) {
                return new HttpResponse(httpRequest.getRequestLine().getProtocol(), HttpStatus.FOUND,
                        ContentType.TEXT_HTML_CHARSET_UTF_8,
                        getStaticResourceResponse("/index.html"));
            }
        }
        return new HttpResponse(httpRequest.getRequestLine().getProtocol(), HttpStatus.FOUND, "/401.html");
    }
}
