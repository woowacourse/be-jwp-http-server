package org.apache.coyote.http11;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleHttpRequest {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final String requestMessage;

    public SimpleHttpRequest(final String requestMessage) {
        validateConnection(requestMessage);
        this.requestMessage = requestMessage;
    }

    private void validateConnection(final String requestMessage) {
        if (requestMessage == null) {
            throw new IllegalArgumentException("Http 요청 메시지로 null을 입력할 수 없습니다.");
        }
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public HttpMethod getHttpMethod() {
        final int httpMethodIndexNumber = 0;
        final String httpMethodValue = parseRequestLine().split(" ")[httpMethodIndexNumber];
        return HttpMethod.valueOf(httpMethodValue.toUpperCase());
    }

    public String getRequestUri() {
        final int requestUriIndexNumber = 1;
        return parseRequestLine().split(" ")[requestUriIndexNumber];
    }

    private String parseRequestLine() {
        return requestMessage.lines()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("유효하지 않은 요청 메시지여서 처리할 수 없습니다."));
    }

    public FileExtensionType parseStaticFileExtensionType() {
        final String requestUri = getRequestUri();
        log.info("requestUri: {}", requestUri);
        final String[] split = requestUri.split("\\.");
        log.info("split = {}", Arrays.toString(split));
        if (split.length != 2) {
            return null;
        }

        return FileExtensionType.fromValue(split[1]);
    }
}
