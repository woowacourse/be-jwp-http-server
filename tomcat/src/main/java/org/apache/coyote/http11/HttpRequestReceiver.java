package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.exception.ReceivingRequestFailedException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.request.HttpRequestHeader;
import org.apache.coyote.http11.request.HttpRequestLine;

public class HttpRequestReceiver {

    HttpRequest receiveRequest(InputStream inputStream) {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            HttpRequestLine requestLine = receiveRequestLine(bufferedReader);
            HttpRequestHeader header = receiveRequestHeader(bufferedReader);
            HttpRequestBody body = receiveRequestBody(header, bufferedReader);

            return new HttpRequest(requestLine, header, body);
        } catch (IOException e) {
            throw new ReceivingRequestFailedException();
        }
    }

    private HttpRequestLine receiveRequestLine(BufferedReader bufferedReader) throws IOException {
        return new HttpRequestLine(bufferedReader.readLine());
    }

    private HttpRequestHeader receiveRequestHeader(BufferedReader bufferedReader) throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            String input = bufferedReader.readLine();
            if (input == null || input.isBlank()) {
                break;
            }
            sb.append(input).append(System.lineSeparator());
        }

        return new HttpRequestHeader(sb.toString());
    }

    private HttpRequestBody receiveRequestBody(HttpRequestHeader header,
                                               BufferedReader bufferedReader) throws IOException {
        if (!header.hasContentLength()) {
            return null;
        }

        int contentLength = header.getContentLength();
        char[] bodyChars = new char[contentLength];
        bufferedReader.read(bodyChars, 0, contentLength);

        String payload = new String(bodyChars);
        String decodedPayload = URLDecoder.decode(payload, StandardCharsets.UTF_8);

        return new HttpRequestBody(decodedPayload, header.getContentType());
    }
}
