package nextstep.jwp.controller;

import org.apache.http.HttpMime;
import org.apache.http.RequestEntity;
import org.apache.http.ResponseEntity;
import org.junit.jupiter.api.Test;

import static org.apache.http.HttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;

class GreetingControllerTest {

    private final Controller controller = new GreetingController();

    @Test
    void 성공코드를_반환한다() throws Exception {
        // given
        final RequestEntity requestEntity = new RequestEntity(GET, "/", null);
        final ResponseEntity expected = new ResponseEntity().contentType(HttpMime.TEXT_HTML);

        // when
        final ResponseEntity actual = controller.execute(requestEntity);

        // then
        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("contentLength", "content")
                .isEqualTo(expected);
    }
}
