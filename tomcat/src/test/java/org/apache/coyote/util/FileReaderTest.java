package org.apache.coyote.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.techcourse.exception.ResourceNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FileReaderTest {

    @DisplayName("특정 파일의 경로를 조회한다.")
    @Test
    void parseFilePath_ReturnAbsolutePathOfFile_WhenFileNameIsGiven() throws URISyntaxException {
        // given
        String targetFileName = "test-target.txt";
        String expectedFilePath = "/static/" + targetFileName;

        // when
        String actualPath = FileReader.parseFilePath(targetFileName).toString();

        // then
        assertThat(actualPath).endsWith(expectedFilePath);
    }

    @DisplayName("존재하지 않는 파일의 경로를 조회하면 예외를 던진다.")
    @Test
    void parseFilePath_ThrowsResourceNotFoundException_WhenParseNotExistFile() {
        // given
        String targetFileName = "not-exist-target.txt";

        // when & then
        assertThatThrownBy(() -> FileReader.parseFilePath(targetFileName))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @DisplayName("특정 파일의 내용을 읽어온다.")
    @Test
    void readAllLines_ShouldReturnContentsOfFile_WhenPathIsGiven() throws IOException, URISyntaxException {
        // given
        String targetFileName = "test-target.txt";
        Path targetFilePath = FileReader.parseFilePath(targetFileName);
        List<String> expectedContents = List.of("a", "A", "1", "/", "?", "&", "=", "s p a c e", "");

        // when
        List<String> actual = FileReader.readAllLines(targetFilePath);

        // then
        assertThat(actual).isEqualTo(expectedContents);
    }
}
