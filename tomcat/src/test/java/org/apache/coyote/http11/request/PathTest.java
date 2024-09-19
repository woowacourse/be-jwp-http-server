package org.apache.coyote.http11.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PathTest {
	@DisplayName("요청 헤더의 첫번째 라인에서 Path를 성공적으로 추출한다.")
	@Test
	void parseRequestToPath() {
		String requestLine = "GET / HTTP/1.1";

		Path path = Path.parseRequestToPath(requestLine);

		Assertions.assertThat(path).isEqualTo(new Path("/"));
	}
}