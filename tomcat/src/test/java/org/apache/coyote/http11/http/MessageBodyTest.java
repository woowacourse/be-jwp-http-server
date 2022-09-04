package org.apache.coyote.http11.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import support.BufferedReaderFactory;

class MessageBodyTest {

    @Test
    void length() throws IOException {
        BufferedReader bufferedReader = BufferedReaderFactory.getBufferedReader("String");
        MessageBody messageBody = MessageBody.from(bufferedReader);

        assertThat(messageBody.length()).isEqualTo("String\r\n".getBytes().length);
    }
}
