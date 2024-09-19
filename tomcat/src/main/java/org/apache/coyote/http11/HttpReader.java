package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestLine;

public class HttpReader {

    private final HttpRequest httpRequest;

    public HttpReader(InputStream inputStream) throws IOException {
        this.httpRequest = getRequest(inputStream);
    }

    private HttpRequest getRequest(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String firstLine = bufferedReader.readLine();

        RequestLine requestLine = getRequestLine(firstLine);
        HttpHeader headers = getHeaders(bufferedReader);
        String body = readBody(bufferedReader, headers.getContentLength());
        if (body.isBlank()) {
            return new HttpRequest(requestLine, headers, new RequestBody());
        }
        RequestBody requestBody = new RequestBody(body);
        return new HttpRequest(requestLine, headers, requestBody);
    }

    private RequestLine getRequestLine(String firstLine) {
        StringTokenizer stringTokenizer = new StringTokenizer(firstLine);

        String method = stringTokenizer.nextToken();
        String requestUrl = stringTokenizer.nextToken();
        String protocol = stringTokenizer.nextToken();
        return new RequestLine(method, requestUrl, protocol);
    }

    private HttpHeader getHeaders(BufferedReader bufferedReader) throws IOException {
        HttpHeader httpHeader = new HttpHeader();
        String line;
        while ((line = bufferedReader.readLine()) != null && !StringUtils.isEmpty(line)) {
            String[] split = line.split(": ");
            httpHeader.putHeader(split[0], split[1]);
        }
        return httpHeader;
    }

    public String readBody(BufferedReader reader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);

        return new String(buffer);
    }

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }
}
