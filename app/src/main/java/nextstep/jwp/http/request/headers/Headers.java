package nextstep.jwp.http.request.headers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.InvalidRequestHeader;

public class Headers {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int EXPECT_LINE_LENGTH = 2;
    private static final String CONTENT_LENGTH = "content-length";
    private static final String TRANSFER_ENCODING = "transfer-encoding";

    private final Map<String, String> headers;

    private Headers(Map<String, String> headers) {
        this.headers = headers;
    }

    public static Headers parse(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line = null;

        while (isNotEmpty(line)) {
            line = bufferedReader.readLine();
            String[] splitedLine = splitLine(line);

            String key = splitedLine[KEY_INDEX].toLowerCase();
            String value = splitedLine[VALUE_INDEX].toLowerCase();

            headers.put(key, value);
        }

        return new Headers(headers);
    }

    private static boolean isNotEmpty(String line) {
        return !"".equals(line);
    }

    private static String[] splitLine(String line) {
        String[] splitedLine = line.split(":", EXPECT_LINE_LENGTH);

        if (splitedLine.length != EXPECT_LINE_LENGTH) {
            throw new InvalidRequestHeader();
        }

        return splitedLine;
    }

    public boolean requestHasBody() {
        return headers.containsKey(CONTENT_LENGTH) || headers.containsKey(TRANSFER_ENCODING);
    }

    public int getContentLength() {
        return Integer.parseInt(headers.get(CONTENT_LENGTH));
    }
}
