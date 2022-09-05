package nextstep.jwp.controller;

import nextstep.jwp.exception.CustomNotFoundException;
import org.apache.http.HttpMime;
import org.apache.http.RequestEntity;
import org.apache.http.ResponseEntity;
import org.junit.jupiter.api.Test;

import static org.apache.http.HttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResourceControllerTest {

    private final Controller controller = new ResourceController();

    @Test
    void 해당하는_자원을_찾으면_OK_응답을_반환한다() throws Exception {
        // given
        final RequestEntity requestEntity = new RequestEntity(GET, "/index.html", null);
        final ResponseEntity expected = new ResponseEntity().contentType(HttpMime.TEXT_HTML);

        // when
        final ResponseEntity actual = controller.execute(requestEntity);

        // then
        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("contentLength", "content")
                .isEqualTo(expected);
    }

    @Test
    void 해당하는_자원을_찾지_못하면_예외가_발생한다() {
        // given
        final RequestEntity requestEntity = new RequestEntity(GET, "/notfound.html", null);

        // when, then
        assertThatThrownBy(() -> controller.execute(requestEntity))
                .isInstanceOf(CustomNotFoundException.class);
    }
}
