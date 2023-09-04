package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.request.HttpRequestMethod.GET;
import static org.apache.coyote.http11.request.HttpRequestMethod.POST;
import static org.apache.coyote.http11.response.HttpStatusCode.FOUND;
import static org.apache.coyote.http11.response.HttpStatusCode.NOT_FOUND;
import static org.apache.coyote.http11.response.HttpStatusCode.OK;
import static org.apache.coyote.http11.response.HttpStatusCode.UNAUTHORIZED;
import static org.apache.coyote.http11.response.ResponseHeaderType.CONTENT_LENGTH;
import static org.apache.coyote.http11.response.ResponseHeaderType.CONTENT_TYPE;
import static org.apache.coyote.http11.response.ResponseHeaderType.LOCATION;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestMethod;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.HttpResponseHeader;
import org.apache.coyote.http11.response.HttpResponseStatusLine;

public class LoginHandler implements RequestHandler {
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        String uri = httpRequest.getStartLine().getHttpRequestUri().getUri();
        HttpRequestMethod httpMethod = httpRequest.getStartLine().getHttpMethod();

        if (uri.equals("/login") && httpMethod == GET) {
            return getLoginPage(httpRequest);
        }

        if (httpMethod == POST) {
            String requestBody = httpRequest.getBody().getBody();
            Map<String, String> accountInfo = parseParms(requestBody);

            Optional<User> optionalUser = InMemoryUserRepository.findByAccount(accountInfo.get("account"));

            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                if (user.checkPassword(accountInfo.get("password"))) {
                    HttpResponseStatusLine statusLine = new HttpResponseStatusLine(
                            httpRequest.getStartLine().getHttpVersion(), FOUND);

                    HttpResponseHeader httpResponseHeader = new HttpResponseHeader();
                    httpResponseHeader.add(CONTENT_TYPE, "text/html;charset=utf-8");
                    httpResponseHeader.add(CONTENT_LENGTH, String.valueOf("".getBytes().length));
                    httpResponseHeader.add(LOCATION, "/index.html");

                    HttpResponseBody body = HttpResponseBody.from("");

                    return new HttpResponse(statusLine, httpResponseHeader, body);
                }
            }

            final URL resource = getClass().getClassLoader().getResource("static/401.html");
            var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            HttpResponseStatusLine statusLine = new HttpResponseStatusLine(
                    httpRequest.getStartLine().getHttpVersion(), UNAUTHORIZED);

            HttpResponseHeader httpResponseHeader = new HttpResponseHeader();
            httpResponseHeader.add(CONTENT_TYPE, "text/html;charset=utf-8");
            httpResponseHeader.add(CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length));

            HttpResponseBody body = HttpResponseBody.from(responseBody);

            return new HttpResponse(statusLine, httpResponseHeader, body);
        }

        return getNotFoundPage(httpRequest);
    }

    private HttpResponse getNotFoundPage(final HttpRequest httpRequest) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static/404.html");
        var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        HttpResponseStatusLine statusLine = new HttpResponseStatusLine(
                httpRequest.getStartLine().getHttpVersion(), NOT_FOUND);

        HttpResponseHeader httpResponseHeader = new HttpResponseHeader();
        httpResponseHeader.add(CONTENT_TYPE, "text/html;charset=utf-8");
        httpResponseHeader.add(CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length));

        HttpResponseBody body = HttpResponseBody.from(responseBody);

        return new HttpResponse(statusLine, httpResponseHeader, body);
    }

    private HttpResponse getLoginPage(final HttpRequest httpRequest) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        HttpResponseStatusLine statusLine = new HttpResponseStatusLine(
                httpRequest.getStartLine().getHttpVersion(), OK);

        HttpResponseHeader httpResponseHeader = new HttpResponseHeader();
        httpResponseHeader.add(CONTENT_TYPE, "text/html;charset=utf-8");
        httpResponseHeader.add(CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length));

        HttpResponseBody body = HttpResponseBody.from(responseBody);

        return new HttpResponse(statusLine, httpResponseHeader, body);
    }

    public Map<String, String> parseParms(final String body) {
        Map<String, String> parms = new HashMap<>();

        Arrays.stream(body.split("&"))
                .forEach(value -> parms.put(value.split("=")[0], value.split("=")[1]));

        return parms;
    }
}
