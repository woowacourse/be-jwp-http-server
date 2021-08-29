package nextstep.learning.http;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.framework.util.ResourceUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 웹서버는 사용자가 요청한 html 파일을 제공 할 수 있어야 한다.<br>
 * File 클래스를 사용해서 파일을 읽어오고, 사용자에게 전달한다.
 */
@DisplayName("File 클래스 학습 테스트")
class FileTest {

    /**
     * File 객체를 생성하려면 파일의 경로를 알아야 한다.<br>
     * 자바 애플리케이션은 resource 디렉터리에 정적 파일을 저장한다.<br>
     * resource 디렉터리의 경로는 어떻게 알아낼 수 있을까?
     */
    @Test
    void resource_디렉터리에_있는_파일의_경로를_찾는다() {

        // given
        final String fileName = "nextstep.txt";

        // when
        final String actual = ResourceUtils.findPathOf(fileName).toString();

        // then
        assertThat(actual).endsWith(fileName);
    }

    /**
     * 읽어온 파일의 내용을 I/O Stream을 사용해서 사용자에게 전달 해야 한다.
     * File, Files 클래스를 사용하여 파일의 내용을 읽어보자.
     */
    @Test
    void 파일의_내용을_읽는다() throws IOException {

        // given
        final String fileName = "nextstep.txt";

        // when
        final List<String> actual = Files.readAllLines(ResourceUtils.findPathOf(fileName));

        // then
        assertThat(actual).containsOnly("nextstep");
    }
}
