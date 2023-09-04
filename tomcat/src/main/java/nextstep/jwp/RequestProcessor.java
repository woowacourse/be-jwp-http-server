package nextstep.jwp;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.common.ContentType;
import nextstep.jwp.common.HttpMethod;
import nextstep.jwp.common.HttpStatus;
import nextstep.jwp.common.HttpVersion;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.response.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestProcessor {

    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";
    private static final String LOCATION_HEADER = "Location";
    private static final String DEFAULT_FILE_LOCATION = "static/";
    private static final String INDEX_PAGE = "index.html";
    private static final String UNAUTHORIZED_PAGE = "401.html";

    private static final Logger log = LoggerFactory.getLogger(RequestProcessor.class);

    public ResponseEntity from(final HttpRequest httpRequest) throws URISyntaxException, IOException {

        HttpVersion version = httpRequest.getHttpVersion();
        HttpMethod method = httpRequest.getHttpMethod();
        String requestUri = httpRequest.getRequestUri();
        Map<String, String> queryParams = httpRequest.getQueryParams();
        String content = "Hello World";

        if (method.equals(HttpMethod.GET)) {
            if (requestUri.isEmpty()) {
                return ResponseEntity.of(version, HttpStatus.OK, content,
                        Map.of(CONTENT_TYPE_HEADER, ContentType.HTML.getType(),
                                CONTENT_LENGTH_HEADER, String.valueOf(content.getBytes().length)));
            }

            if (requestUri.equals("login") && !queryParams.isEmpty()) {
                String redirectedPage = UNAUTHORIZED_PAGE;
                final String account = queryParams.get("account");
                final String password = queryParams.get("password");

                final Optional<User> findedUser = InMemoryUserRepository.findByAccount(account);
                if (findedUser.isPresent()) {
                    User user = findedUser.get();
                    if (user.checkPassword(password)) {
                        log.debug(user.toString());
                        redirectedPage = INDEX_PAGE;
                    }
                }

                return ResponseEntity.of(version, HttpStatus.FOUND, content, Map.of(LOCATION_HEADER, redirectedPage));
            }

            content = makeResponseBody(requestUri);

            if (requestUri.endsWith(".html") || requestUri.equals("login")) {
                return ResponseEntity.of(version, HttpStatus.OK, content,
                        Map.of(CONTENT_TYPE_HEADER, ContentType.HTML.getType(),
                                CONTENT_LENGTH_HEADER, String.valueOf(content.getBytes().length)));
            }

            if (requestUri.endsWith(".css")) {
                return ResponseEntity.of(version, HttpStatus.OK, content,
                        Map.of(CONTENT_TYPE_HEADER, ContentType.CSS.getType(),
                                CONTENT_LENGTH_HEADER, String.valueOf(content.getBytes().length)));
            }

            if (requestUri.endsWith(".js")) {
                return ResponseEntity.of(version, HttpStatus.OK, content,
                        Map.of(CONTENT_TYPE_HEADER, ContentType.JS.getType(),
                                CONTENT_LENGTH_HEADER, String.valueOf(content.getBytes().length)));
            }

            if (requestUri.endsWith(".svg")) {
                return ResponseEntity.of(version, HttpStatus.OK, content,
                        Map.of(CONTENT_TYPE_HEADER, ContentType.SVG.getType(),
                                CONTENT_LENGTH_HEADER, String.valueOf(content.getBytes().length)));
            }
        }

        return ResponseEntity.of(version, HttpStatus.NOT_FOUND, content,
                Map.of(CONTENT_TYPE_HEADER, ContentType.HTML.getType(),
                        CONTENT_LENGTH_HEADER, String.valueOf(content.getBytes().length)));
    }

    private String makeResponseBody(String requestUri) throws URISyntaxException, IOException {
        final URL url;

        if (requestUri.equals("login")) {
            url = getClass().getClassLoader().getResource(DEFAULT_FILE_LOCATION + requestUri + ".html");
            final var path = Paths.get(url.toURI());
            return Files.readString(path);
        }

        url = getClass().getClassLoader().getResource(DEFAULT_FILE_LOCATION + requestUri);
        if (url == null) {
            URL notFoundUrl = getClass().getClassLoader().getResource(DEFAULT_FILE_LOCATION + "404.html");
            final var path = Paths.get(notFoundUrl.toURI());
            return Files.readString(path);
        }
        final var path = Paths.get(url.toURI());
        return Files.readString(path);
    }
}