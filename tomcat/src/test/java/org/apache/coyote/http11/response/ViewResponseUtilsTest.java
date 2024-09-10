package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Map;
import org.apache.coyote.http11.response.view.HtmlView;
import org.apache.coyote.http11.response.view.View;
import org.junit.jupiter.api.Test;

class ViewResponseUtilsTest {

    @Test
    void createResponseTest_whenTypeIsHtml() {
        View view = new HtmlView(HttpStatus.OK, Collections.EMPTY_MAP, "Hello World!");
        int expectedStatus = 200;
        Map<String, String> expectedHeaders = Map.of(
                "Content-Type", "text/html;charset=utf-8",
                "Content-Length", "12");

        HttpResponse actual = ViewResponseUtils.createResponse(view);

        assertThat(actual.getStatusCode()).isEqualTo(expectedStatus);
        assertThat(actual.getHeaders()).isEqualTo(expectedHeaders);
    }
}
