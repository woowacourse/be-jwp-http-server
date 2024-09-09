package org.apache.coyote.http;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http.request.HttpMethod;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.Path;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.HttpStatus;
import org.apache.coyote.http.response.ResponseHeader;
import org.apache.coyote.http.response.StatusLine;
import org.apache.coyote.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;

public class Dispatcher {

    private static final Logger log = LoggerFactory.getLogger(Dispatcher.class);

    private static final String STATIC = "static";
    private static final String REDIRECT = "/index.html";
    private static final String UNAUTHORIZED = "/401.html";

    private static final SessionManager sessionManager = SessionManager.getInstance();

    public String dispatch(HttpRequest request) {
        Path path = request.getPath();

        try {
            if (path.getUri().equals("/")) {
                return HttpResponse.basicResponse().toResponse();
            }
            if (path.getUri().equals("/login")) {
                return getLoginResponse(request);
            }
            if (path.getUri().equals("/register")) {
                return getRegisterResponse(request);
            }
            return gerResourceResponse(path);
        } catch (NullPointerException | IllegalArgumentException e) {
            return HttpResponse.notFoundResponses().toResponse();
        } catch (IOException e) {
            return HttpResponse.serverErrorResponses().toResponse();
        }
    }

    private String gerResourceResponse(Path path) throws IOException {
        final URL resource = getClass().getClassLoader().getResource(STATIC + path.getUri());
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        StatusLine statusLine = new StatusLine(HttpStatus.OK);
        ResponseHeader header = new ResponseHeader();
        header.setContentType(MimeType.getContentTypeFromExtension(path.getUri()));
        header.setContentLength(responseBody.getBytes().length);

        HttpResponse response = new HttpResponse(statusLine, header, responseBody);
        return response.toResponse();
    }

    private String getLoginResponse(HttpRequest request) throws IOException {
        HttpMethod method = request.getMethod();
        Path path = request.getPath();
        final URL resource = getClass().getClassLoader().getResource(STATIC + path.getUri() + MimeType.HTML.getExtension());
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        ResponseHeader header = new ResponseHeader();
        header.setContentType(MimeType.HTML.getContentType());
        header.setContentLength(responseBody.getBytes().length);

        if (request.getCookie().hasCookieName("JSESSIONID")) {
            StatusLine statusLine = new StatusLine(HttpStatus.FOUND);
            header.setLocation(REDIRECT);
            HttpResponse response = new HttpResponse(statusLine, header, responseBody);
            return response.toResponse();
        }

        if (method.equals(HttpMethod.GET)) {
            StatusLine statusLine = new StatusLine(HttpStatus.OK);
            HttpResponse response = new HttpResponse(statusLine, header, responseBody);
            return response.toResponse();
        }

        if (method.equals(HttpMethod.POST)) {
            StatusLine statusLine = new StatusLine(HttpStatus.FOUND);
            Map<String, String> parsedBody = StringUtils.separateKeyValue(request.getBody());
            return InMemoryUserRepository.findByAccount(parsedBody.get("account"))
                    .map(user -> getLoginResult(user, parsedBody, header, statusLine))
                    .orElse(new HttpResponse(new StatusLine(HttpStatus.OK), header, responseBody).toResponse());
        }
        return HttpResponse.notFoundResponses().toResponse();
    }

    private String getLoginResult(User user, Map<String, String> parsedBody, ResponseHeader header, StatusLine statusLine) {
        if (user.checkPassword(parsedBody.get("password"))) {
            log.info(user.toString());
            header.setLocation(REDIRECT);
            HttpCookie cookie = getHttpCookie(user);
            header.setCookie(cookie.toCookieResponse());
            HttpResponse response = new HttpResponse(statusLine, header, null);
            return response.toResponse();
        }
        header.setLocation(UNAUTHORIZED);
        HttpResponse response = new HttpResponse(statusLine, header, null);
        return response.toResponse();
    }

    private HttpCookie getHttpCookie(User user) {
        Session session = Session.getSession();
        session.setUser(user);
        sessionManager.add(session);
        HttpCookie cookie = new HttpCookie();
        cookie.setSessionId(session.getId());
        return cookie;
    }

    private String getRegisterResponse(HttpRequest request) throws IOException {
        HttpMethod method = request.getMethod();
        Path path = request.getPath();
        final URL resource = getClass().getClassLoader().getResource(STATIC + path.getUri() + MimeType.HTML.getExtension());
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        ResponseHeader header = new ResponseHeader();
        header.setContentType(MimeType.HTML.getContentType());
        header.setContentLength(responseBody.getBytes().length);

        if (method.equals(HttpMethod.GET)) {
            StatusLine statusLine = new StatusLine(HttpStatus.OK);
            HttpResponse response = new HttpResponse(statusLine, header, responseBody);
            return response.toResponse();
        }

        if (method.equals(HttpMethod.POST)) {
            Map<String, String> parsedBody = StringUtils.separateKeyValue(request.getBody());
            InMemoryUserRepository.save(new User(parsedBody.get("account"), parsedBody.get("password"), parsedBody.get("email")));
            StatusLine statusLine = new StatusLine(HttpStatus.FOUND);
            header.setLocation(REDIRECT);
            HttpResponse response = new HttpResponse(statusLine, header, null);
            return response.toResponse();
        }
        return HttpResponse.notFoundResponses().toResponse();
    }
}
